/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.client.ClientCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.geode.boot.autoconfigure.EnvironmentSourcedGemFirePropertiesAutoConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link EnvironmentSourcedGemFirePropertiesAutoConfiguration}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.context.annotation.Profile
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.geode.boot.autoconfigure.EnvironmentSourcedGemFirePropertiesAutoConfiguration
 * @see org.springframework.test.annotation.DirtiesContext
 * @see org.springframework.test.context.ActiveProfiles
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.3.0
 */
@ActiveProfiles("application-gemfire-properties")
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
	"gemfire.distributed-system-id=123",
	"gemfire.enable-time-statistics=true",
	"gemfire.invalid-property=TEST",
	"gemfire.name=TestName",
	"geode.remote-locators=hellbox[10666]",
	"locators=skullbox[12345]",
	"spring.application.name=TestApp",
})
@SuppressWarnings("unused")
public class GemFirePropertiesFromEnvironmentAutoConfigurationIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ClientCache clientCache;

	@Autowired
	private GemFireProperties gemfireProperties;

	@Test
	public void gemfirePropertiesArePresent() {

		assertThat(this.clientCache).isNotNull();
		assertThat(this.clientCache.getName()).isEqualTo("TestApp");
		assertThat(this.clientCache.getDistributedSystem()).isNotNull();

		Properties gemfireProperties = this.clientCache.getDistributedSystem().getProperties();

		assertThat(gemfireProperties).isNotNull();
		assertThat(gemfireProperties)
			.containsKeys("distributed-system-id", "enable-time-statistics", "locators", "name", "remote-locators");
		assertThat(gemfireProperties).doesNotContainKeys("invalid-property", "spring.application.name");
		assertThat(gemfireProperties.getProperty("distributed-system-id")).isEqualTo("123");
		assertThat(gemfireProperties.getProperty("enable-time-statistics")).isEqualTo("true");
		assertThat(gemfireProperties.getProperty("invalid-property")).isNull();
		assertThat(gemfireProperties.getProperty("locators")).isEmpty();
		assertThat(gemfireProperties.getProperty("name")).isEqualTo("TestApp");
		assertThat(gemfireProperties.getProperty("remote-locators")).isEmpty();
		assertThat(gemfireProperties.getProperty("spring.application.name")).isNull();
	}

	@Test
	public void gemfirePropertiesClassDoesNotCaptureGemFireProperties() {

		assertThat(this.gemfireProperties).isNotNull();
		assertThat(this.gemfireProperties.getCache().getName()).isNotEqualTo("TestApp");
		assertThat(this.gemfireProperties.getCache().getName()).isNotEqualTo("TestName");
		assertThat(this.gemfireProperties.getLocator().getHost()).isNotEqualTo("skullbox");
		assertThat(this.gemfireProperties.getLocator().getPort()).isNotEqualTo(12345);
	}

	@SpringBootApplication
	@Profile("application-gemfire-properties")
	static class GeodeConfiguration { }

}
