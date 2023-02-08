/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.junit.AfterClass;
import org.junit.Test;

import org.apache.geode.cache.client.ClientCache;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.tests.integration.SpringBootApplicationIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;

/**
 * Integration Tests for {@link EnableClusterAware} and {@link ClusterAwareConfiguration}
 * as well as {@link ClusterAvailableConfiguration} when {@code strictMode} is {@literal true}
 * configured in Spring Boot {@literal application.properties}
 * using the {@literal spring.boot.data.gemfire.cluster.condition.match.strict} property
 * when no Apache Geode Cluster was provisioned and made available to service Apache Geode
 * {@link ClientCache clients}.
 *
 * @author John Blum
 * @see java.util.Properties
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.boot.builder.SpringApplicationBuilder
 * @see org.springframework.data.gemfire.config.annotation.ClientCacheApplication
 * @see org.springframework.data.gemfire.tests.integration.SpringBootApplicationIntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.geode.config.annotation.EnableClusterAware
 * @since 1.4.1
 */
public class PropertyConfiguredStrictMatchingClusterNotAvailableConfigurationIntegrationTests
		extends SpringBootApplicationIntegrationTestsSupport {

	@AfterClass
	public static void tearDown() {
		ClusterAwareConfiguration.ClusterAwareCondition.reset();
	}

	@Override
	protected SpringApplicationBuilder processBeforeBuild(SpringApplicationBuilder springApplicationBuilder) {

		Properties testProperties = new Properties();

		testProperties
			.setProperty(ClusterAwareConfiguration.SPRING_BOOT_DATA_GEMFIRE_CLUSTER_CONDITION_MATCH_STRICT_PROPERTY,
				Boolean.TRUE.toString());

		return springApplicationBuilder.properties(testProperties);
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
	@EnableClusterAware(strictMatch = false)
	@EnableGemFireMockObjects
	@SuppressWarnings("all")
	static class TestGeodeClientConfiguration { }

}
