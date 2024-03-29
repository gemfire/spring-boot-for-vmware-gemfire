/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.security.tls;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.distributed.ConfigurationProperties;
import org.apache.geode.distributed.DistributedSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;

/**
 * Abstract Integration Test base class containing tests for TLS configuration in a Cloud Platform Environment/Context.
 *
 * @author John Blum
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.distributed.ConfigurationProperties
 * @see org.apache.geode.distributed.DistributedSystem
 * @see org.springframework.core.env.Environment
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @since 1.3.0
 */
@SuppressWarnings("unused")
public abstract class AbstractTlsEnabledAutoConfigurationIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	protected ClientCache clientCache;

	@Autowired
	protected Environment environment;

	@Before
	public void setup() {

		assertThat(this.clientCache).describedAs("ClientCache was not configured").isNotNull();
		assertThat(this.environment).describedAs("Environment was not configured").isNotNull();
	}

	@Test
	public void environmentContainsSpringDataGeodeSecuritySslUseDefaultContextProperty() {

		assertThat(this.environment.getProperty("spring.data.gemfire.security.ssl.use-default-context",
			Boolean.class, false)).isTrue();
	}

	@Test
	public void geodeClientIsConfiguredWithSslUsingDefaultContext() {

		DistributedSystem distributedSystem = this.clientCache.getDistributedSystem();

		assertThat(distributedSystem).isNotNull();
		assertThat(distributedSystem.getProperties()).isNotNull();
		assertThat(distributedSystem.getProperties().getProperty(ConfigurationProperties.SSL_USE_DEFAULT_CONTEXT, "false"))
			.isEqualTo("true");
	}
}
