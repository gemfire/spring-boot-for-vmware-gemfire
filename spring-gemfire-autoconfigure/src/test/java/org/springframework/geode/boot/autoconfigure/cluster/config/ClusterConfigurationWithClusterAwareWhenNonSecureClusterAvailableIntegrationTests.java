/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.cluster.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.gemfire.config.annotation.ClusterConfigurationConfiguration;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.geode.config.annotation.ClusterAwareConfiguration;
import org.springframework.geode.config.annotation.EnableClusterAware;
import org.springframework.geode.core.util.ObjectUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for testing the {@link EnableClusterAware} annotation and expected configuration applied by SBDG
 * when the Apache Geode cluster is NOT secure.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.core.env.Environment
 * @see org.springframework.data.gemfire.config.annotation.ClusterConfigurationConfiguration
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.geode.config.annotation.EnableClusterAware
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.2.0
 */
@ActiveProfiles("cluster-configuration-with-non-secure-cluster")
@RunWith(SpringRunner.class)
@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.NONE,
	properties = { "spring.boot.data.gemfire.cluster.condition.match=true" }
)
@SuppressWarnings("unused")
public class ClusterConfigurationWithClusterAwareWhenNonSecureClusterAvailableIntegrationTests
		extends IntegrationTestsSupport {

	@BeforeClass @AfterClass
	public static void resetClusterAwareCondition() {
		ClusterAwareConfiguration.ClusterAwareCondition.reset();
	}

	@Autowired
	private Environment environment;

	@Autowired
	private ClusterConfigurationConfiguration configuration;

	@Test
	public void configurationStatesManagementRestApiDoesNotRequireHttps() {

		boolean configurationRequiresHttps =
			ObjectUtils.invoke(this.configuration, "resolveManagementRequireHttps");

		assertThat(configurationRequiresHttps).isFalse();
	}

	@Test
	public void environmentStatesManagementRestApiDoesNotRequireHttps() {

		boolean environmentRequiresHttps =
			this.environment.containsProperty("spring.data.gemfire.management.require-https");

		assertThat(environmentRequiresHttps).isFalse();
	}

	@SpringBootApplication
	@EnableClusterAware
	@EnableGemFireMockObjects
	@Profile("cluster-configuration-with-non-secure-cluster")
	static class TestConfiguration { }

}
