/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.logging;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.GemFireCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.geode.util.GeodeConstants;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests asserting the configuration and behavior of Apache Geode logging when configured with
 * Spring Boot auto-configuration.
 *
 * @author John Blum
 * @see java.util.Properties
 * @see org.junit.Test
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.distributed.internal.DistributionConfig
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
	properties = {
		"spring.data.gemfire.logging.level=fine",
		"spring.data.gemfire.logging.log-disk-space-limit=4096",
		"spring.data.gemfire.logging.log-file=/path/to/gemfire.log",
		"spring.data.gemfire.logging.log-file-size-limit=512"
	},
	webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@SuppressWarnings("unused")
public class LoggingAutoConfigurationIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private GemFireCache gemfireCache;

	@Test
	public void loggingConfigurationWasApplied() {

		assertThat(this.gemfireCache).isNotNull();
		assertThat(this.gemfireCache.getDistributedSystem()).isNotNull();

		Properties distributedSystemProperties = this.gemfireCache.getDistributedSystem().getProperties();

		assertThat(distributedSystemProperties.getProperty(GeodeConstants.LOG_DISK_SPACE_LIMIT)).isEqualTo("4096");
		assertThat(distributedSystemProperties.getProperty(GeodeConstants.LOG_FILE)).isEqualTo("/path/to/gemfire.log");
		assertThat(distributedSystemProperties.getProperty(GeodeConstants.LOG_FILE_SIZE_LIMIT)).isEqualTo("512");
		assertThat(distributedSystemProperties.getProperty(GeodeConstants.LOG_LEVEL)).isEqualTo("fine");
	}

	@SpringBootApplication
	@EnableGemFireMockObjects
	static class TestConfiguration { }

}
