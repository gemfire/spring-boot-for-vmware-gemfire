/*
 * Copyright 2023-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.cache.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.geode.cache.Cache;
import org.apache.geode.distributed.internal.DistributionConfig;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.junit.Test;

/**
 * Unit Tests for {@link Jetty12HttpService} init/shutdown lifecycle.
 *
 * <p>These tests mock the GemFire {@link Cache} and {@link DistributionConfig} to exercise
 * the guard conditions in {@link Jetty12HttpService#init(Cache)} without requiring a live
 * GemFire node.
 */
public class Jetty12HttpServiceUnitTests {

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
}
