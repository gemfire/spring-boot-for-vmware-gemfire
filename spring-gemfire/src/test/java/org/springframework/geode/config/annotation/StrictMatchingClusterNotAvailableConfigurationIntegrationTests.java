/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.AfterClass;
import org.junit.Test;

import org.apache.geode.cache.client.ClientCache;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.tests.integration.SpringBootApplicationIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;

/**
 * Integration Tests for {@link EnableClusterAware} and {@link ClusterAwareConfiguration}
 * as well as {@link ClusterAvailableConfiguration} when {@code strictMode} is {@literal true}
 * using the {@link EnableClusterAware#strictMatch()} annotation attribute when no Apache Geode Cluster
 * was provisioned and made available to service Apache Geode {@link ClientCache clients}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.data.gemfire.config.annotation.ClientCacheApplication
 * @see org.springframework.data.gemfire.tests.integration.SpringBootApplicationIntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.geode.config.annotation.EnableClusterAware
 * @since 1.4.1
 */
public class StrictMatchingClusterNotAvailableConfigurationIntegrationTests
		extends SpringBootApplicationIntegrationTestsSupport {

	@AfterClass
	public static void tearDown() {
		ClusterAwareConfiguration.ClusterAwareCondition.reset();
	}

	@Test(expected = ClusterNotAvailableException.class)
	public void clusterNotAvailableExceptionThrownWhenClusterIsNotAvailableAndStrictMatchIsTrue() throws Throwable {

		try {
			newApplicationContext(TestGeodeClientConfiguration.class);
		}
		catch (Throwable expected) {

			expected = NestedExceptionUtils.getMostSpecificCause(expected);

			assertThat(expected).isInstanceOf(ClusterNotAvailableException.class);

			assertThat(expected).hasMessage("Failed to find available cluster in [%s] when strictMatch was [true]",
				ClusterAwareConfiguration.ClusterAwareCondition.RUNTIME_ENVIRONMENT_NAME);

			assertThat(expected).hasNoCause();

			throw expected;
		}
		finally {
			assertThat(System.getProperties())
				.doesNotContainKeys(ClusterAwareConfiguration.SPRING_DATA_GEMFIRE_CACHE_CLIENT_REGION_SHORTCUT_PROPERTY);
		}
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnableClusterAware(strictMatch = true)
	static class TestGeodeClientConfiguration { }

}
