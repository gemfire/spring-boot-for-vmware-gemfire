/*
 * Copyright 2023-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.cache.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.apache.geode.distributed.internal.DistributionConfig;
import org.eclipse.jetty.ee8.webapp.WebAppContext;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.junit.After;
import org.junit.Test;

/**
 * Integration tests verifying that {@link Jetty12HttpService} deploys
 * {@code javax.servlet}-based WARs using the Jetty EE8 environment.
 *
 * <p>These tests are the primary regression guard for the fix that switched from
 * {@code org.eclipse.jetty.ee10.webapp.WebAppContext} (which requires {@code jakarta.servlet.*})
 * to {@code org.eclipse.jetty.ee8.webapp.WebAppContext} (which natively understands
 * {@code javax.servlet.*} WARs — the format GemFire's internal WARs use).
 *
 * <p>Before the fix, the EE10 environment was used and a bytecode migration step was
 * attempted at deploy time, which proved unreliable: it could not consistently rewrite
 * all string-based class references, stale-cached migrated WARs survived across GemFire
 * upgrades, and the depth of bundled JARs meant missed references surfaced as runtime
 * failures rather than startup errors.
 */
public class Jetty12HttpServiceDeploymentIntegrationTests {

	private TestableJetty12HttpService service;
	private Path testWar;

	@After
	public void tearDown() {
		if (service != null) {
			service.close();
		}
		try {
			if (testWar != null) {
				Files.deleteIfExists(testWar);
			}
		}
		catch (IOException ignored) {}
	}

	/**
	 * Primary regression test: a WAR whose servlet class extends
	 * {@code javax.servlet.http.HttpServlet} must deploy into an available (non-failed)
	 * WebAppContext.
	 *
	 * <p>The {@code &lt;load-on-startup&gt;1&lt;/load-on-startup&gt;} directive in the WAR's
	 * {@code web.xml} forces the servlet to be loaded eagerly during context initialisation
	 * rather than lazily on first request. Any {@code ClassNotFoundException} for
	 * {@code javax.servlet.http.HttpServlet} therefore surfaces immediately as a startup
	 * failure — exactly the failure mode that occurred under the old EE10 environment.
	 */
	@Test
	public void javaxServletWarDeploysIntoAvailableContextWithoutClassNotFoundException() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJavaxServletWar();

		service.addWebApplication("/app", testWar, Collections.emptyMap());

		List<WebAppContext> webApps = service.getDeployedWebApplications();
		assertThat(webApps).hasSize(1);
		assertThat(webApps.get(0).isFailed())
			.as("WebAppContext must not be in failed state — javax.servlet must load under EE8")
			.isFalse();
		assertThat(webApps.get(0).isAvailable()).isTrue();
	}

	/**
	 * Verifies that {@link Jetty12HttpService} creates an {@code org.eclipse.jetty.ee8.webapp.WebAppContext}
	 * and not the EE10 equivalent. Both classes are on the classpath; this test guards against
	 * accidentally regressing to EE10.
	 */
	@Test
	public void addWebApplicationCreatesEe8WebAppContextNotEe10() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJavaxServletWar();

		service.addWebApplication("/app", testWar, Collections.emptyMap());

		List<WebAppContext> webApps = service.getDeployedWebApplications();
		assertThat(webApps).hasSize(1);

		Object ctx = webApps.get(0);
		assertThat(ctx).isInstanceOf(org.eclipse.jetty.ee8.webapp.WebAppContext.class);
		assertThat(ctx).isNotInstanceOf(org.eclipse.jetty.ee10.webapp.WebAppContext.class);
	}

	/**
	 * End-to-end: verifies that after deployment the embedded Jetty server responds to
	 * HTTP requests on the context path and that the {@code javax.servlet}-based servlet
	 * actually executes and writes a response body.
	 */
	@Test
	public void deployedJavaxServletWarServesHttpRequests() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJavaxServletWar();

		service.addWebApplication("/app", testWar, Collections.emptyMap());

		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> response = client.send(
			HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:" + port + "/app/hello"))
				.GET()
				.build(),
			HttpResponse.BodyHandlers.ofString());

		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(response.body()).isEqualTo(HelloServlet.RESPONSE);
	}

	/**
	 * Verifies that the {@code org.eclipse.jetty.websocket.javax} attribute is explicitly
	 * set to {@code false} on each deployed {@link WebAppContext}.
	 *
	 * <p>Without this attribute Jetty EE8 scans the WAR for {@code javax.websocket} endpoints
	 * and attempts to initialise its WebSocket runtime, which can conflict with the
	 * GemFire management WARs. This test guards against the attribute being silently
	 * dropped in a future refactor.
	 */
	@Test
	public void addWebApplicationDisablesJavaxWebSocketOnWebAppContext() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJavaxServletWar();

		service.addWebApplication("/app", testWar, Collections.emptyMap());

		List<WebAppContext> webApps = service.getDeployedWebApplications();
		assertThat(webApps).hasSize(1);
		assertThat(webApps.get(0).getAttribute("org.eclipse.jetty.websocket.javax"))
			.as("javax WebSocket must be explicitly disabled to prevent Jetty EE8 from scanning the WAR for WebSocket endpoints")
			.isEqualTo(Boolean.FALSE);
	}

	/**
	 * Verifies that calling {@code close()} on a service that has an active deployment
	 * stops all web applications and the underlying server without throwing.
	 */
	@Test
	public void closeStopsDeployedWebApplicationsAndServer() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJavaxServletWar();

		service.addWebApplication("/app", testWar, Collections.emptyMap());

		assertThat(service.getDeployedWebApplications()).hasSize(1);
		service.close();
		service = null; // prevent double-close in tearDown
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	/**
	 * Builds a minimal WAR containing {@link HelloServlet} (a {@code javax.servlet}-based
	 * servlet) and a {@code web.xml} that maps it to {@code /hello} with eager loading.
	 *
	 * <p>The servlet's {@code .class} file is read from the current classpath (where it
	 * was compiled by Gradle) and written into {@code WEB-INF/classes/}.
	 */
	private Path buildJavaxServletWar() throws IOException {
		Path war = Files.createTempFile("test-javax-servlet", ".war");

		String servletClassName = HelloServlet.class.getName();
		String classEntry = servletClassName.replace('.', '/') + ".class";

		try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(war))) {
			jar.putNextEntry(new JarEntry("WEB-INF/web.xml"));
			jar.write(buildWebXml(servletClassName).getBytes());
			jar.closeEntry();

			jar.putNextEntry(new JarEntry("WEB-INF/classes/" + classEntry));
			try (var in = HelloServlet.class.getClassLoader().getResourceAsStream(classEntry)) {
				assertThat(in)
					.as("HelloServlet.class must be locatable on the test classpath")
					.isNotNull();
				in.transferTo(jar);
			}
			jar.closeEntry();
		}

		return war;
	}

	private static String buildWebXml(String servletClassName) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" version=\"3.0\">\n"
			+ "  <servlet>\n"
			+ "    <servlet-name>hello</servlet-name>\n"
			+ "    <servlet-class>" + servletClassName + "</servlet-class>\n"
			+ "    <load-on-startup>1</load-on-startup>\n"
			+ "  </servlet>\n"
			+ "  <servlet-mapping>\n"
			+ "    <servlet-name>hello</servlet-name>\n"
			+ "    <url-pattern>/hello</url-pattern>\n"
			+ "  </servlet-mapping>\n"
			+ "</web-app>\n";
	}

	private static int findFreePort() throws IOException {
		try (ServerSocket socket = new ServerSocket(0)) {
			socket.setReuseAddress(true);
			return socket.getLocalPort();
		}
	}

	// -------------------------------------------------------------------------
	// Test subclass — bypasses GemFire init() and wires in a real Jetty server
	// -------------------------------------------------------------------------

	/**
	 * Overrides {@link Jetty12HttpService#getServer()} and
	 * {@link Jetty12HttpService#getOptionalServer()} to return a pre-configured Jetty
	 * {@link Server}, bypassing the GemFire {@link org.apache.geode.cache.Cache} /
	 * {@link DistributionConfig} initialisation path. This allows the WAR-deployment
	 * logic to be exercised in isolation without a running GemFire node.
	 */
	private static class TestableJetty12HttpService extends Jetty12HttpService {

		private final Server testServer;

		TestableJetty12HttpService(int port) {
			DistributionConfig mockConfig = mock(DistributionConfig.class);
			when(mockConfig.getHttpServicePort()).thenReturn(port);
			when(mockConfig.getHttpServiceBindAddress()).thenReturn("localhost");

			testServer = new Server();
			ServerConnector connector = new ServerConnector(testServer, new HttpConnectionFactory());
			connector.setHost("localhost");
			connector.setPort(port);
			testServer.addConnector(connector);
			testServer.setHandler(new ContextHandlerCollection(true));
			testServer.setAttribute("apache.geode.cache.configuration", mockConfig);
		}

		@Override
		protected Server getServer() {
			return testServer;
		}

		@Override
		protected Optional<Server> getOptionalServer() {
			return Optional.of(testServer);
		}

		List<WebAppContext> getDeployedWebApplications() {
			return getWebApplications();
		}
	}
}
