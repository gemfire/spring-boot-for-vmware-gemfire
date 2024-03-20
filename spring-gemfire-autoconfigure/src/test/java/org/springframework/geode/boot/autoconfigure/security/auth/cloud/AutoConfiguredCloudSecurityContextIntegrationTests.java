/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.security.auth.cloud;

import java.io.IOException;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.gemfire.config.annotation.CacheServerApplication;
import org.springframework.data.gemfire.config.annotation.EnableLocator;
import org.springframework.geode.boot.autoconfigure.security.auth.AbstractAutoConfiguredSecurityContextIntegrationTests;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the auto-configuration of Apache Geode Security (authentication/authorization) in a cloud,
 * managed context (e.g. CloudFoundry).
 *
 * @author John Blum
 * @see java.util.Properties
 * @see org.junit.Test
 * @see org.apache.geode.cache.GemFireCache
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.builder.SpringApplicationBuilder
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.core.io.ClassPathResource
 * @see org.springframework.data.gemfire.config.annotation.CacheServerApplication
 * @see org.springframework.data.gemfire.config.annotation.EnableLocator
 * @see org.springframework.geode.boot.autoconfigure.ClientSecurityAutoConfiguration
 * @see org.springframework.geode.boot.autoconfigure.PeerSecurityAutoConfiguration
 * @see org.springframework.geode.boot.autoconfigure.security.auth.AbstractAutoConfiguredSecurityContextIntegrationTests
 * @see org.springframework.test.annotation.DirtiesContext
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.0.0
 */
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = AutoConfiguredCloudSecurityContextIntegrationTests.GemFireClientConfiguration.class,
	webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@SuppressWarnings("unused")
public class AutoConfiguredCloudSecurityContextIntegrationTests
		extends AbstractAutoConfiguredSecurityContextIntegrationTests {

	private static final String LOCATOR_PORT_PLACEHOLDER_REGEX = "%LOCATOR_PORT%";
	private static final String VCAP_APPLICATION_PROPERTIES = "application-vcap-cloud.properties";

	private static final Properties vcapApplicationProperties = new Properties();

	@BeforeClass
	public static void startGemFireServer() throws IOException {

		int locatorPort = findAvailablePort();

		startGemFireServer(GemFireServerConfiguration.class,
			String.format("-Dspring.data.gemfire.locator.port=%d", locatorPort),
			"-Dspring.profiles.active=security-cloud");

		loadVcapApplicationProperties(locatorPort);

		unsetTestAutoConfiguredPoolServersPortSystemProperty();
	}

	private static void loadVcapApplicationProperties(int locatorPort) throws IOException {

		vcapApplicationProperties.load(new ClassPathResource(VCAP_APPLICATION_PROPERTIES).getInputStream());

		vcapApplicationProperties.stringPropertyNames().forEach(propertyName -> {

			String propertyValue = String.valueOf(vcapApplicationProperties.getProperty(propertyName))
				.replaceAll(LOCATOR_PORT_PLACEHOLDER_REGEX, String.valueOf(locatorPort));

			System.setProperty(propertyName, propertyValue);
		});
	}

	private static void unsetTestAutoConfiguredPoolServersPortSystemProperty() {
		System.clearProperty(GEMFIRE_POOL_SERVERS_PROPERTY);
	}

	@AfterClass
	public static void cleanUpUsedResources() {
		vcapApplicationProperties.stringPropertyNames().forEach(System::clearProperty);
	}

	@SpringBootApplication
	static class GemFireClientConfiguration extends BaseGemFireClientConfiguration { }

	@SpringBootApplication
	@EnableLocator
	@CacheServerApplication(name = "AutoConfiguredCloudSecurityContextIntegrationTestsServer")
	static class GemFireServerConfiguration extends BaseGemFireServerConfiguration {

		public static void main(String[] args) {

			new SpringApplicationBuilder(GemFireServerConfiguration.class)
				.web(WebApplicationType.NONE)
				.build()
				.run(args);
		}
	}
}
