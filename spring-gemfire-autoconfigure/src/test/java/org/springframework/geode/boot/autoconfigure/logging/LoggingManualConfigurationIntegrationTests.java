/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.logging;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.GemFireCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.data.gemfire.config.annotation.EnableLogging;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.geode.config.annotation.UseMemberName;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the manual configuration of Apache Geode Logging.
 *
 * @author John Blum
 * @see java.util.Properties
 * @see org.junit.Test
 * @see org.apache.geode.cache.GemFireCache
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.data.gemfire.config.annotation.EnableLogging
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.3.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
	properties = {
		"spring.data.gemfire.logging.level=warn",
		"spring.main.allow-bean-definition-overriding=false"
	},
	webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@SuppressWarnings("unused")
public class LoggingManualConfigurationIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private Environment environment;

	@Autowired
	private GemFireCache cache;

	@Before
	public void setup() {
		assertThat(this.environment.getProperty("spring.main.allow-bean-definition-overriding",
			Boolean.class, true)).isFalse();
	}

	@Test
	public void manualLogConfigurationIsCorrect() {

		assertThat(this.cache).isNotNull();
		assertThat(this.cache.getName()).isEqualTo(LoggingManualConfigurationIntegrationTests.class.getSimpleName());

		Properties gemfireProperties = this.cache.getDistributedSystem().getProperties();

		assertThat(gemfireProperties).isNotNull();
		assertThat(gemfireProperties.getProperty("log-level")).isEqualTo("warn");
	}

	@SpringBootApplication
	@EnableGemFireMockObjects
	@EnableLogging
	@UseMemberName("LoggingManualConfigurationIntegrationTests")
	static class TestConfigurationOne { }

	/*
	@Configuration
	@EnableLogging(logLevel = "info")
	static class TestConfigurationTwo { }
	*/

}
