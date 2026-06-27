/*
 * Copyright 2023-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.cache.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
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
import java.util.Map;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.apache.geode.distributed.internal.DistributionConfig;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.junit.After;
import org.junit.Test;

/**
 * Integration tests verifying that {@link Jetty12HttpService} correctly deploys both
 * {@code javax.servlet}-based WARs (EE8) and {@code jakarta.servlet}-based WARs (EE10),
 * routing each to the appropriate Jetty {@link org.eclipse.jetty.ee8.webapp.WebAppContext}
 * or {@link org.eclipse.jetty.ee10.webapp.WebAppContext} transparently.
 *
 * <p>The WAR type is detected automatically from the {@code WEB-INF/web.xml} namespace —
 * {@code https://jakarta.ee/xml/ns/jakartaee} → EE10, everything else → EE8.
 *
 * <p>The EE8 tests are the primary regression guard for the fix that switched from
 * {@code org.eclipse.jetty.ee10.webapp.WebAppContext} (which requires {@code jakarta.servlet.*})
 * to {@code org.eclipse.jetty.ee8.webapp.WebAppContext} (which natively understands
 * {@code javax.servlet.*} WARs — the format GemFire's internal WARs use).
 */
public class Jetty12HttpServiceDeploymentIntegrationTests {

	private TestableJetty12HttpService service;
	private Path testWar;
	private Path secondWar;

	@After
	public void tearDown() {
		if (service != null) {
			service.close();
		}
		deleteQuietly(testWar);
		deleteQuietly(secondWar);
	}

	// =========================================================================
	// EE8 (javax.servlet) tests
	// =========================================================================

	/**
	 * Primary regression test: a WAR whose servlet class extends
	 * {@code javax.servlet.http.HttpServlet} must deploy into an available (non-failed)
	 * EE8 WebAppContext.
	 *
	 * <p>The {@code <load-on-startup>1</load-on-startup>} directive in the WAR's
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

		List<Object> webApps = service.getDeployedWebApplications();
		assertThat(webApps).hasSize(1);

		org.eclipse.jetty.ee8.webapp.WebAppContext ctx =
			(org.eclipse.jetty.ee8.webapp.WebAppContext) webApps.get(0);
		assertThat(ctx.isFailed())
			.as("WebAppContext must not be in failed state — javax.servlet must load under EE8")
			.isFalse();
		assertThat(ctx.isAvailable()).isTrue();
	}

	/**
	 * Verifies that {@link Jetty12HttpService} creates an {@code org.eclipse.jetty.ee8.webapp.WebAppContext}
	 * and not the EE10 equivalent for a javax.servlet WAR. Both classes are on the classpath;
	 * this test guards against accidentally regressing to EE10.
	 */
	@Test
	public void addWebApplicationCreatesEe8WebAppContextNotEe10() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJavaxServletWar();

		service.addWebApplication("/app", testWar, Collections.emptyMap());

		List<Object> webApps = service.getDeployedWebApplications();
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

		HttpResponse<String> response = httpGet("http://localhost:" + port + "/app/hello");

		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(response.body()).isEqualTo(HelloServlet.RESPONSE);
	}

	/**
	 * Verifies that the {@code org.eclipse.jetty.websocket.javax} attribute is explicitly
	 * set to {@code false} on each deployed EE8 {@link org.eclipse.jetty.ee8.webapp.WebAppContext}.
	 *
	 * <p>Without this attribute Jetty EE8 scans the WAR for {@code javax.websocket} endpoints
	 * and attempts to initialise its WebSocket runtime, which can conflict with the
	 * GemFire management WARs. This test guards against the attribute being silently
	 * dropped in a future refactor.
	 */
	@Test
	public void addWebApplicationDisablesJavaxWebSocketOnEe8WebAppContext() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJavaxServletWar();

		service.addWebApplication("/app", testWar, Collections.emptyMap());

		List<Object> webApps = service.getDeployedWebApplications();
		assertThat(webApps).hasSize(1);

		org.eclipse.jetty.ee8.webapp.WebAppContext ctx =
			(org.eclipse.jetty.ee8.webapp.WebAppContext) webApps.get(0);
		assertThat(ctx.getAttribute("org.eclipse.jetty.websocket.javax"))
			.as("javax WebSocket must be explicitly disabled to prevent Jetty EE8 from scanning the WAR")
			.isEqualTo(Boolean.FALSE);
	}

	/**
	 * Verifies that a non-empty {@code attributeNameValuePairs} map passed to
	 * {@code addWebApplication} is actually applied to the deployed EE8 context. Every other
	 * test passes {@link Collections#emptyMap()}, so this guards the attribute-propagation
	 * path (the {@code nullSafeMap(extraAttributes).forEach(ctx::setAttribute)} call).
	 */
	@Test
	public void addWebApplicationAppliesCustomAttributesToEe8WebAppContext() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJavaxServletWar();

		service.addWebApplication("/app", testWar,
			Map.of("custom.attribute.one", "value-1", "custom.attribute.two", 42));

		org.eclipse.jetty.ee8.webapp.WebAppContext ctx =
			(org.eclipse.jetty.ee8.webapp.WebAppContext) service.getDeployedWebApplications().get(0);
		assertThat(ctx.getAttribute("custom.attribute.one")).isEqualTo("value-1");
		assertThat(ctx.getAttribute("custom.attribute.two")).isEqualTo(42);
	}

	/**
	 * Security regression guard: directory listing must be disabled. The
	 * {@code org.eclipse.jetty.servlet.Default.dirAllowed} init parameter is set to
	 * {@code "false"} on every deployed EE8 context, and a GET against a directory in the
	 * WAR must therefore be rejected with 403 rather than returning a directory listing.
	 */
	@Test
	public void ee8DirectoryListingIsForbidden() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJavaxServletWarWithStaticDirectory();

		service.addWebApplication("/app", testWar, Collections.emptyMap());

		org.eclipse.jetty.ee8.webapp.WebAppContext ctx =
			(org.eclipse.jetty.ee8.webapp.WebAppContext) service.getDeployedWebApplications().get(0);
		assertThat(ctx.getInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed"))
			.as("directory listing must be disabled")
			.isEqualTo("false");

		// The static file is served, but the directory containing it must not be listed.
		assertThat(httpGet("http://localhost:" + port + "/app/static/data.txt").statusCode()).isEqualTo(200);
		assertThat(httpGet("http://localhost:" + port + "/app/static/").statusCode())
			.as("requesting a directory must return 403, not a listing")
			.isEqualTo(403);
	}

	/**
	 * Verifies that calling {@code close()} on a service with an EE8 deployment stops all
	 * web applications and the underlying server without throwing.
	 */
	@Test
	public void closeStopsEe8DeployedWebApplicationsAndServer() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJavaxServletWar();

		service.addWebApplication("/app", testWar, Collections.emptyMap());

		assertThat(service.getDeployedWebApplications()).hasSize(1);
		service.close();
		service = null; // suppress @After double-close
	}

	// =========================================================================
	// EE10 (jakarta.servlet) tests
	// =========================================================================

	/**
	 * A WAR whose servlet class extends {@code jakarta.servlet.http.HttpServlet} must deploy
	 * into an available (non-failed) EE10 WebAppContext.
	 */
	@Test
	public void jakartaServletWarDeploysIntoAvailableContextWithoutClassNotFoundException() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJakartaServletWar();

		service.addWebApplication("/app", testWar, Collections.emptyMap());

		List<Object> webApps = service.getDeployedWebApplications();
		assertThat(webApps).hasSize(1);

		org.eclipse.jetty.ee10.webapp.WebAppContext ctx =
			(org.eclipse.jetty.ee10.webapp.WebAppContext) webApps.get(0);
		assertThat(ctx.isFailed())
			.as("WebAppContext must not be in failed state — jakarta.servlet must load under EE10")
			.isFalse();
		assertThat(ctx.isAvailable()).isTrue();
	}

	/**
	 * Verifies that a {@code jakarta.servlet} WAR creates an EE10 WebAppContext (not EE8).
	 * Both classes are on the classpath; this is the EE10 equivalent of the EE8 regression guard.
	 */
	@Test
	public void addWebApplicationCreatesEe10WebAppContextForJakartaServletWar() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJakartaServletWar();

		service.addWebApplication("/app", testWar, Collections.emptyMap());

		List<Object> webApps = service.getDeployedWebApplications();
		assertThat(webApps).hasSize(1);

		Object ctx = webApps.get(0);
		assertThat(ctx).isInstanceOf(org.eclipse.jetty.ee10.webapp.WebAppContext.class);
		assertThat(ctx).isNotInstanceOf(org.eclipse.jetty.ee8.webapp.WebAppContext.class);
	}

	/**
	 * End-to-end: verifies that an EE10 WAR is served correctly over HTTP after deployment.
	 */
	@Test
	public void deployedJakartaServletWarServesHttpRequests() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJakartaServletWar();

		service.addWebApplication("/api", testWar, Collections.emptyMap());

		HttpResponse<String> response = httpGet("http://localhost:" + port + "/api/hello");

		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(response.body()).isEqualTo(JakartaHelloServlet.RESPONSE);
	}

	/**
	 * Verifies that the {@code org.eclipse.jetty.websocket.jakarta} attribute is set to
	 * {@code false} on an EE10 WebAppContext to suppress Jakarta WebSocket scanning.
	 */
	@Test
	public void addWebApplicationDisablesJakartaWebSocketOnEe10WebAppContext() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJakartaServletWar();

		service.addWebApplication("/app", testWar, Collections.emptyMap());

		List<Object> webApps = service.getDeployedWebApplications();
		assertThat(webApps).hasSize(1);

		org.eclipse.jetty.ee10.webapp.WebAppContext ctx =
			(org.eclipse.jetty.ee10.webapp.WebAppContext) webApps.get(0);
		assertThat(ctx.getAttribute("org.eclipse.jetty.websocket.jakarta"))
			.as("jakarta WebSocket must be explicitly disabled to prevent Jetty EE10 from scanning the WAR")
			.isEqualTo(Boolean.FALSE);
	}

	/**
	 * Verifies that a non-empty {@code attributeNameValuePairs} map is applied to the
	 * deployed EE10 context (the EE10 counterpart of the EE8 attribute-propagation test).
	 */
	@Test
	public void addWebApplicationAppliesCustomAttributesToEe10WebAppContext() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJakartaServletWar();

		service.addWebApplication("/app", testWar,
			Map.of("custom.attribute.one", "value-1", "custom.attribute.two", 42));

		org.eclipse.jetty.ee10.webapp.WebAppContext ctx =
			(org.eclipse.jetty.ee10.webapp.WebAppContext) service.getDeployedWebApplications().get(0);
		assertThat(ctx.getAttribute("custom.attribute.one")).isEqualTo("value-1");
		assertThat(ctx.getAttribute("custom.attribute.two")).isEqualTo(42);
	}

	/**
	 * Verifies that calling {@code close()} on a service with an EE10 deployment stops all
	 * web applications and the underlying server without throwing.
	 */
	@Test
	public void closeStopsEe10DeployedWebApplicationsAndServer() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJakartaServletWar();

		service.addWebApplication("/app", testWar, Collections.emptyMap());

		assertThat(service.getDeployedWebApplications()).hasSize(1);
		service.close();
		service = null; // suppress @After double-close
	}

	// =========================================================================
	// Detection / routing tests
	// =========================================================================

	/**
	 * A WAR with a {@code java.sun.com} namespace in web.xml must be routed to an EE8 context.
	 */
	@Test
	public void javaxServletWarRoutesToEe8Context() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJavaxServletWar();

		service.addWebApplication("/app", testWar, Collections.emptyMap());

		Object ctx = service.getDeployedWebApplications().get(0);
		assertThat(ctx).isInstanceOf(org.eclipse.jetty.ee8.webapp.WebAppContext.class);
	}

	/**
	 * A WAR with a {@code jakarta.ee} namespace in web.xml must be routed to an EE10 context.
	 */
	@Test
	public void jakartaServletWarRoutesToEe10Context() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJakartaServletWar();

		service.addWebApplication("/app", testWar, Collections.emptyMap());

		Object ctx = service.getDeployedWebApplications().get(0);
		assertThat(ctx).isInstanceOf(org.eclipse.jetty.ee10.webapp.WebAppContext.class);
	}

	/**
	 * A WAR without a {@code WEB-INF/web.xml} must fall back to the EE8 context (the safe
	 * default, since GemFire's own WARs are javax.servlet-based).
	 */
	@Test
	public void warWithoutWebXmlDefaultsToEe8Context() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildWarWithNoWebXml();

		service.addWebApplication("/app", testWar, Collections.emptyMap());

		Object ctx = service.getDeployedWebApplications().get(0);
		assertThat(ctx).isInstanceOf(org.eclipse.jetty.ee8.webapp.WebAppContext.class);
	}

	/**
	 * A WAR with an empty {@code web.xml} (no namespace declaration) must fall back to
	 * the EE8 context.
	 */
	@Test
	public void warWithEmptyWebXmlDefaultsToEe8Context() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildWarWithWebXml("<!-- no namespace declaration -->\n<web-app/>\n");

		service.addWebApplication("/app", testWar, Collections.emptyMap());

		Object ctx = service.getDeployedWebApplications().get(0);
		assertThat(ctx).isInstanceOf(org.eclipse.jetty.ee8.webapp.WebAppContext.class);
	}

	// =========================================================================
	// Mixed-deployment test
	// =========================================================================

	/**
	 * Deploys one EE8 WAR and one EE10 WAR to the same server instance and verifies that
	 * both respond correctly to HTTP requests — the key end-to-end proof that transparent
	 * routing works with live WARs on a shared server.
	 */
	@Test
	public void mixedEe8AndEe10WarsDeployedToSameServerBothServeRequests() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJavaxServletWar();
		secondWar = buildJakartaServletWar();

		service.addWebApplication("/javax", testWar, Collections.emptyMap());
		service.addWebApplication("/jakarta", secondWar, Collections.emptyMap());

		List<Object> webApps = service.getDeployedWebApplications();
		assertThat(webApps).hasSize(2);
		assertThat(webApps.get(0)).isInstanceOf(org.eclipse.jetty.ee8.webapp.WebAppContext.class);
		assertThat(webApps.get(1)).isInstanceOf(org.eclipse.jetty.ee10.webapp.WebAppContext.class);

		assertThat(httpGet("http://localhost:" + port + "/javax/hello").body())
			.isEqualTo(HelloServlet.RESPONSE);
		assertThat(httpGet("http://localhost:" + port + "/jakarta/hello").body())
			.isEqualTo(JakartaHelloServlet.RESPONSE);
	}

	// =========================================================================
	// Edge-case / multi-WAR tests
	// =========================================================================

	/**
	 * Two EE8 WARs deployed to different context paths must both be available independently.
	 */
	@Test
	public void multipleEe8WarsDeployToSeparateContextPaths() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJavaxServletWar();
		secondWar = buildJavaxServletWar();

		service.addWebApplication("/app1", testWar, Collections.emptyMap());
		service.addWebApplication("/app2", secondWar, Collections.emptyMap());

		assertThat(service.getDeployedWebApplications()).hasSize(2);
		assertThat(httpGet("http://localhost:" + port + "/app1/hello").statusCode()).isEqualTo(200);
		assertThat(httpGet("http://localhost:" + port + "/app2/hello").statusCode()).isEqualTo(200);
	}

	/**
	 * Two EE10 WARs deployed to different context paths must both be available independently.
	 */
	@Test
	public void multipleEe10WarsDeployToSeparateContextPaths() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJakartaServletWar();
		secondWar = buildJakartaServletWar();

		service.addWebApplication("/api1", testWar, Collections.emptyMap());
		service.addWebApplication("/api2", secondWar, Collections.emptyMap());

		assertThat(service.getDeployedWebApplications()).hasSize(2);
		assertThat(httpGet("http://localhost:" + port + "/api1/hello").statusCode()).isEqualTo(200);
		assertThat(httpGet("http://localhost:" + port + "/api2/hello").statusCode()).isEqualTo(200);
	}

	/**
	 * {@code close()} on a service with a mixed EE8+EE10 deployment must stop all contexts
	 * and the server without throwing.
	 */
	@Test
	public void closeWithMixedDeploymentStopsAllContexts() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJavaxServletWar();
		secondWar = buildJakartaServletWar();

		service.addWebApplication("/javax", testWar, Collections.emptyMap());
		service.addWebApplication("/jakarta", secondWar, Collections.emptyMap());

		assertThat(service.getDeployedWebApplications()).hasSize(2);
		service.close();
		service = null; // suppress @After double-close
	}

	// =========================================================================
	// Failure / negative-path tests
	// =========================================================================

	/**
	 * Fault isolation: deploying a broken WAR (malformed {@code web.xml}) must not take down
	 * a previously-deployed healthy WAR. Jetty defers context-startup failures and marks the
	 * broken context unavailable rather than propagating, so {@code addWebApplication} of the
	 * broken WAR completes without disturbing the running server; the healthy WAR must keep
	 * serving requests. (The exception-wrapping contract of the per-application start path is
	 * covered directly in {@link Jetty12HttpServiceUnitTests}.)
	 */
	@Test
	public void healthyWarKeepsServingWhenAnotherWarFailsToStart() throws Exception {
		int port = findFreePort();
		service = new TestableJetty12HttpService(port);
		testWar = buildJavaxServletWar();
		secondWar = buildWarWithMalformedWebXml();

		// First (healthy) deployment starts the server.
		service.addWebApplication("/good", testWar, Collections.emptyMap());

		// Second (broken) deployment: tolerate either contained failure or a wrapped
		// WebApplicationException — both are acceptable; the point is the healthy WAR survives.
		try {
			service.addWebApplication("/bad", secondWar, Collections.emptyMap());
		}
		catch (Jetty12HttpService.WebApplicationException ignored) {
			// acceptable: failure surfaced via the per-application start path
		}

		assertThat(httpGet("http://localhost:" + port + "/good/hello").statusCode())
			.as("a broken co-deployed WAR must not stop a healthy WAR from serving")
			.isEqualTo(200);
		assertThat(httpGet("http://localhost:" + port + "/good/hello").body())
			.isEqualTo(HelloServlet.RESPONSE);
	}

	/**
	 * When the embedded HTTP service is not enabled (no Jetty {@link Server} present),
	 * {@code addWebApplication} must be a no-op: it must not throw and must not register a
	 * web application. This exercises the {@code orElseGet} branch of {@code addWebApplication}.
	 */
	@Test
	public void addWebApplicationIsNoOpWhenServerIsAbsent() throws Exception {
		NoServerJetty12HttpService noServerService = new NoServerJetty12HttpService();
		testWar = buildJavaxServletWar();

		assertThat(noServerService.getServerForTest()).isNull();
		assertThatNoException()
			.isThrownBy(() -> noServerService.addWebApplication("/app", testWar, Collections.emptyMap()));
		assertThat(noServerService.getDeployedWebApplications()).isEmpty();
	}

	// =========================================================================
	// Helpers — WAR builders
	// =========================================================================

	/**
	 * Builds a minimal WAR containing {@link HelloServlet} (a {@code javax.servlet}-based
	 * servlet) and a {@code web.xml} that maps it to {@code /hello} with eager loading.
	 */
	private Path buildJavaxServletWar() throws IOException {
		Path war = Files.createTempFile("test-javax-servlet", ".war");

		String servletClassName = HelloServlet.class.getName();
		String classEntry = servletClassName.replace('.', '/') + ".class";

		try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(war))) {
			jar.putNextEntry(new JarEntry("WEB-INF/web.xml"));
			jar.write(buildJavaxWebXml(servletClassName).getBytes());
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

	/**
	 * Builds a minimal WAR containing {@link JakartaHelloServlet} (a {@code jakarta.servlet}-based
	 * servlet) and a {@code web.xml} using the Jakarta EE namespace.
	 */
	private Path buildJakartaServletWar() throws IOException {
		Path war = Files.createTempFile("test-jakarta-servlet", ".war");

		String servletClassName = JakartaHelloServlet.class.getName();
		String classEntry = servletClassName.replace('.', '/') + ".class";

		try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(war))) {
			jar.putNextEntry(new JarEntry("WEB-INF/web.xml"));
			jar.write(buildJakartaWebXml(servletClassName).getBytes());
			jar.closeEntry();

			jar.putNextEntry(new JarEntry("WEB-INF/classes/" + classEntry));
			try (var in = JakartaHelloServlet.class.getClassLoader().getResourceAsStream(classEntry)) {
				assertThat(in)
					.as("JakartaHelloServlet.class must be locatable on the test classpath")
					.isNotNull();
				in.transferTo(jar);
			}
			jar.closeEntry();
		}

		return war;
	}

	/**
	 * Builds a WAR whose {@code web.xml} is malformed XML (unbalanced tags), forcing a fatal
	 * descriptor parse error — and therefore a propagated lifecycle failure — when the
	 * context is started. The {@code java.sun.com} default namespace routes it to the EE8
	 * context.
	 */
	private Path buildWarWithMalformedWebXml() throws IOException {
		Path war = Files.createTempFile("test-malformed-webxml", ".war");
		try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(war))) {
			jar.putNextEntry(new JarEntry("WEB-INF/web.xml"));
			jar.write(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" version=\"3.0\">\n"
				+ "  <servlet>\n"
				+ "    <servlet-name>broken</servlet-name>\n"
				+ "  <!-- intentionally unbalanced: missing </servlet> and </web-app> -->\n")
				.getBytes());
			jar.closeEntry();
		}
		return war;
	}

	/**
	 * Builds a javax.servlet WAR (with {@link HelloServlet}) that also contains a static
	 * resource directory {@code static/} holding {@code data.txt}, used to verify that
	 * directory listing is forbidden while the file itself is served.
	 */
	private Path buildJavaxServletWarWithStaticDirectory() throws IOException {
		Path war = Files.createTempFile("test-static-dir", ".war");

		String servletClassName = HelloServlet.class.getName();
		String classEntry = servletClassName.replace('.', '/') + ".class";

		try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(war))) {
			jar.putNextEntry(new JarEntry("WEB-INF/web.xml"));
			jar.write(buildJavaxWebXml(servletClassName).getBytes());
			jar.closeEntry();

			jar.putNextEntry(new JarEntry("WEB-INF/classes/" + classEntry));
			try (var in = HelloServlet.class.getClassLoader().getResourceAsStream(classEntry)) {
				assertThat(in)
					.as("HelloServlet.class must be locatable on the test classpath")
					.isNotNull();
				in.transferTo(jar);
			}
			jar.closeEntry();

			jar.putNextEntry(new JarEntry("static/data.txt"));
			jar.write("static-file-content".getBytes());
			jar.closeEntry();
		}

		return war;
	}

	/** Builds a WAR with no {@code WEB-INF/web.xml} entry (only a placeholder directory). */
	private Path buildWarWithNoWebXml() throws IOException {
		Path war = Files.createTempFile("test-no-webxml", ".war");
		try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(war))) {
			jar.putNextEntry(new JarEntry("WEB-INF/classes/"));
			jar.closeEntry();
		}
		return war;
	}

	/** Builds a WAR whose {@code web.xml} contains the given raw content. */
	private Path buildWarWithWebXml(String webXmlContent) throws IOException {
		Path war = Files.createTempFile("test-custom-webxml", ".war");
		try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(war))) {
			jar.putNextEntry(new JarEntry("WEB-INF/web.xml"));
			jar.write(webXmlContent.getBytes());
			jar.closeEntry();
		}
		return war;
	}

	private static String buildJavaxWebXml(String servletClassName) {
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

	private static String buildJakartaWebXml(String servletClassName) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<web-app xmlns=\"https://jakarta.ee/xml/ns/jakartaee\""
			+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
			+ " xsi:schemaLocation=\"https://jakarta.ee/xml/ns/jakartaee"
			+ " https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd\""
			+ " version=\"6.0\">\n"
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

	// =========================================================================
	// Helpers — HTTP / port
	// =========================================================================

	private static HttpResponse<String> httpGet(String url) throws IOException, InterruptedException {
		return HttpClient.newHttpClient().send(
			HttpRequest.newBuilder().uri(URI.create(url)).GET().build(),
			HttpResponse.BodyHandlers.ofString());
	}

	private static int findFreePort() throws IOException {
		try (ServerSocket socket = new ServerSocket(0)) {
			socket.setReuseAddress(true);
			return socket.getLocalPort();
		}
	}

	private static void deleteQuietly(Path path) {
		if (path != null) {
			try {
				Files.deleteIfExists(path);
			}
			catch (IOException ignored) {}
		}
	}

	// =========================================================================
	// Test subclass — bypasses GemFire init() and wires in a real Jetty server
	// =========================================================================

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

		List<Object> getDeployedWebApplications() {
			return getWebApplications();
		}
	}

	/**
	 * A service that has never been initialised, so no Jetty {@link Server} exists. Used to
	 * exercise the "HTTP service not enabled" branch of {@code addWebApplication}.
	 */
	private static class NoServerJetty12HttpService extends Jetty12HttpService {

		Server getServerForTest() {
			return getServer();
		}

		List<Object> getDeployedWebApplications() {
			return getWebApplications();
		}
	}
}
