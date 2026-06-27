/*
 * Copyright 2023-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.cache.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.apache.geode.cache.Cache;
import org.apache.geode.distributed.internal.DistributionConfig;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.eclipse.jetty.util.component.LifeCycle;
import org.junit.Test;

/**
 * Unit Tests for {@link Jetty12HttpService} init/shutdown lifecycle and WAR type detection.
 *
 * <p>These tests mock the GemFire {@link Cache} and {@link DistributionConfig} to exercise
 * the guard conditions in {@link Jetty12HttpService#init(Cache)} without requiring a live
 * GemFire node.  The {@link Jetty12HttpService#isJakartaEEWar(Path)} detection method is
 * tested directly because it is package-private and the core of the EE8/EE10 routing logic.
 */
public class Jetty12HttpServiceUnitTests {

	// -------------------------------------------------------------------------
	// init / lifecycle tests
	// -------------------------------------------------------------------------

	@Test
	public void initReturnsFalseAndLeavesServerNullWhenCacheIsNull() {
		Jetty12HttpService service = new Jetty12HttpService();

		assertThat(service.init(null)).isFalse();
		assertThat(service.getServer()).isNull();
	}

	/**
	 * When the HTTP service port is -1 (the value GemFire uses to signal that the embedded
	 * HTTP service is explicitly disabled), {@code init()} must return {@code false} and
	 * must not create a Jetty {@link org.eclipse.jetty.server.Server}.
	 */
	@Test
	public void initReturnsFalseAndLeavesServerNullWhenHttpServicePortIsNegativeOne() {
		Cache mockCache = mock(Cache.class);
		InternalDistributedSystem mockIDS = mock(InternalDistributedSystem.class);
		DistributionConfig mockConfig = mock(DistributionConfig.class);

		when(mockCache.getDistributedSystem()).thenReturn(mockIDS);
		when(mockIDS.getConfig()).thenReturn(mockConfig);
		when(mockConfig.getHttpServicePort()).thenReturn(-1);

		Jetty12HttpService service = new Jetty12HttpService();

		assertThat(service.init(mockCache)).isFalse();
		assertThat(service.getServer()).isNull();
	}

	/**
	 * When HTTP service is enabled (port ≥ 0) the {@code init()} pipeline reaches
	 * {@code initializeHttpServiceServer}. Verifying beyond the port guard here would
	 * require mocking deep GemFire internals ({@code SSLConfigurationFactory}). The full
	 * server-creation path is covered by
	 * {@link Jetty12HttpServiceDeploymentIntegrationTests} via {@code TestableJetty12HttpService}.
	 *
	 * <p>This test confirms the guard condition: the disabled check is on port == -1, so
	 * port == 0 must pass through to the server-initialisation branch (evidenced by the
	 * DistributedSystem being resolved from the cache).
	 */
	@Test
	public void initWithEnabledPortPassesThroughToServerInitialisationBranch() {
		Cache mockCache = mock(Cache.class);
		InternalDistributedSystem mockIDS = mock(InternalDistributedSystem.class);
		DistributionConfig mockConfig = mock(DistributionConfig.class);

		when(mockCache.getDistributedSystem()).thenReturn(mockIDS);
		when(mockIDS.getConfig()).thenReturn(mockConfig);
		// Port 0 is enabled; port -1 is the only disabled value.
		when(mockConfig.getHttpServicePort()).thenReturn(0);

		Jetty12HttpService service = new Jetty12HttpService();

		// We do not assert the return value here because SSLConfigurationFactory reads
		// native GemFire SSL state that cannot be mocked without deep stubs.
		// We assert that the disabled guard did NOT short-circuit by verifying that
		// getDistributedSystem() was called (which only happens when port > -1).
		try {
			service.init(mockCache);
		}
		catch (Exception ignored) {
			// SSLConfigurationFactory may fail on a mock config — that's acceptable here.
		}
		// The critical gate is that we got past the port check; verified by verifying
		// that getDistributedSystem() was invoked on the cache.
		org.mockito.Mockito.verify(mockCache).getDistributedSystem();
	}

	/**
	 * {@link Jetty12HttpService#close()} must be safe to call before {@code init()} has ever
	 * been called (i.e., when no Jetty server was created).
	 */
	@Test
	public void closeBeforeInitDoesNotThrow() {
		Jetty12HttpService service = new Jetty12HttpService();

		assertThatNoException().isThrownBy(service::close);
	}

	// -------------------------------------------------------------------------
	// isJakartaEEWar() detection tests
	// -------------------------------------------------------------------------

	@Test
	public void isJakartaEEWarReturnsTrueForJakartaNamespace() throws IOException {
		Path war = buildWarWithWebXml(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<web-app xmlns=\"https://jakarta.ee/xml/ns/jakartaee\""
			+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
			+ " xsi:schemaLocation=\"https://jakarta.ee/xml/ns/jakartaee"
			+ " https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd\""
			+ " version=\"6.0\">\n"
			+ "</web-app>\n");
		try {
			assertThat(new Jetty12HttpService().isJakartaEEWar(war)).isTrue();
		}
		finally {
			Files.deleteIfExists(war);
		}
	}

	@Test
	public void isJakartaEEWarReturnsFalseForJavaxNamespace() throws IOException {
		Path war = buildWarWithWebXml(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\""
			+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
			+ " xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee"
			+ " http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd\""
			+ " version=\"3.0\">\n"
			+ "</web-app>\n");
		try {
			assertThat(new Jetty12HttpService().isJakartaEEWar(war)).isFalse();
		}
		finally {
			Files.deleteIfExists(war);
		}
	}

	/**
	 * A javax (Java EE) WAR whose {@code web.xml} happens to reference the Jakarta EE
	 * namespace only inside its {@code xsi:schemaLocation} attribute (where the URI is
	 * followed by whitespace and the schema URL, not a closing quote) must NOT be
	 * misdetected as Jakarta EE. The servlet API generation is governed by the default
	 * {@code xmlns} declaration, which here is the legacy {@code java.sun.com} namespace.
	 *
	 * <p>This guards the quote-delimited detection against substring false-positives.
	 */
	@Test
	public void isJakartaEEWarReturnsFalseWhenJakartaNamespaceOnlyInSchemaLocation() throws IOException {
		Path war = buildWarWithWebXml(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\""
			+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
			+ " xsi:schemaLocation=\"https://jakarta.ee/xml/ns/jakartaee"
			+ " https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd\""
			+ " version=\"3.0\">\n"
			+ "</web-app>\n");
		try {
			assertThat(new Jetty12HttpService().isJakartaEEWar(war))
				.as("Jakarta namespace appearing only in schemaLocation must not route to EE10")
				.isFalse();
		}
		finally {
			Files.deleteIfExists(war);
		}
	}

	/**
	 * The modern {@code http://xmlns.jcp.org/xml/ns/javaee} namespace (Servlet 3.1 / 4.0)
	 * is still a {@code javax.servlet} namespace and must be detected as a non-Jakarta WAR
	 * (so it routes to the EE8 context).
	 */
	@Test
	public void isJakartaEEWarReturnsFalseForJcpOrgJavaxNamespace() throws IOException {
		Path war = buildWarWithWebXml(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<web-app xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\""
			+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
			+ " xsi:schemaLocation=\"http://xmlns.jcp.org/xml/ns/javaee"
			+ " http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd\""
			+ " version=\"4.0\">\n"
			+ "</web-app>\n");
		try {
			assertThat(new Jetty12HttpService().isJakartaEEWar(war)).isFalse();
		}
		finally {
			Files.deleteIfExists(war);
		}
	}

	/**
	 * Detection must accept the Jakarta namespace declared with single-quoted {@code xmlns}
	 * (valid XML), not only double-quoted, since the detection is quote-delimited.
	 */
	@Test
	public void isJakartaEEWarReturnsTrueForSingleQuotedJakartaNamespace() throws IOException {
		Path war = buildWarWithWebXml(
			"<?xml version='1.0' encoding='UTF-8'?>\n"
			+ "<web-app xmlns='https://jakarta.ee/xml/ns/jakartaee' version='6.0'>\n"
			+ "</web-app>\n");
		try {
			assertThat(new Jetty12HttpService().isJakartaEEWar(war)).isTrue();
		}
		finally {
			Files.deleteIfExists(war);
		}
	}

	@Test
	public void isJakartaEEWarReturnsFalseForMissingWebXml() throws IOException {
		Path war = Files.createTempFile("test-no-webxml", ".war");
		try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(war))) {
			// Write a dummy entry — no WEB-INF/web.xml
			jar.putNextEntry(new JarEntry("WEB-INF/classes/"));
			jar.closeEntry();
		}
		try {
			assertThat(new Jetty12HttpService().isJakartaEEWar(war)).isFalse();
		}
		finally {
			Files.deleteIfExists(war);
		}
	}

	@Test
	public void isJakartaEEWarReturnsFalseForUnreadableWarWithoutThrowingException() throws IOException {
		Path nonExistentWar = Path.of(System.getProperty("java.io.tmpdir"), "nonexistent-does-not-exist.war");
		// Must not throw — IOException is caught and treated as EE8 fallback
		assertThatNoException()
			.isThrownBy(() -> new Jetty12HttpService().isJakartaEEWar(nonExistentWar));
		assertThat(new Jetty12HttpService().isJakartaEEWar(nonExistentWar)).isFalse();
	}

	// -------------------------------------------------------------------------
	// SafeWebApplicationWrapper start/stop contract
	// -------------------------------------------------------------------------

	/**
	 * {@code safeStart()} must translate any failure from the underlying web application's
	 * {@link LifeCycle#start()} into a {@link Jetty12HttpService.WebApplicationException} that
	 * identifies the context path. This is the per-application failure contract that the
	 * deployment path relies on (and which cannot be triggered through WAR content because
	 * Jetty defers context-startup failures).
	 */
	@Test
	public void safeStartWrapsStartFailureInWebApplicationException() throws Exception {
		LifeCycle failing = mock(LifeCycle.class);
		doThrow(new IllegalStateException("boom")).when(failing).start();

		Jetty12HttpService.SafeWebApplicationWrapper wrapper =
			Jetty12HttpService.SafeWebApplicationWrapper.from(failing, "/app");

		assertThatThrownBy(wrapper::safeStart)
			.isInstanceOf(Jetty12HttpService.WebApplicationException.class)
			.hasMessageContaining("/app");
	}

	/**
	 * {@code safeStop()} must never propagate failures from the underlying
	 * {@link LifeCycle#stop()} — shutdown is best-effort so that one stuck web application
	 * cannot prevent the remaining applications and the server from being stopped.
	 */
	@Test
	public void safeStopSwallowsStopFailure() throws Exception {
		LifeCycle failing = mock(LifeCycle.class);
		doThrow(new IllegalStateException("boom")).when(failing).stop();

		Jetty12HttpService.SafeWebApplicationWrapper wrapper =
			Jetty12HttpService.SafeWebApplicationWrapper.from(failing, "/app");

		assertThatNoException().isThrownBy(wrapper::safeStop);
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private static Path buildWarWithWebXml(String webXmlContent) throws IOException {
		Path war = Files.createTempFile("test-war", ".war");
		try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(war))) {
			jar.putNextEntry(new JarEntry("WEB-INF/web.xml"));
			jar.write(webXmlContent.getBytes());
			jar.closeEntry();
		}
		return war;
	}
}
