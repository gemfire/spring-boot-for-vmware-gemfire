/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.security.auth.hybrid;

import static com.vmware.gemfire.testcontainers.GemFireCluster.ALL_GLOB;
import com.vmware.gemfire.testcontainers.GemFireCluster;
import java.io.IOException;
import java.util.Properties;
import org.apache.geode.cache.client.ClientCache;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.geode.boot.autoconfigure.ClientSecurityAutoConfiguration;
import org.springframework.geode.boot.autoconfigure.security.auth.AbstractAutoConfiguredSecurityContextIntegrationTests;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the functionality and behavior of {@link ClientSecurityAutoConfiguration} when a
 * Spring Boot app is deployed (pushed) to CloudFoundry, however, the app has not be bound to a Pivotal Cloud Cache
 * (PCC) service instance.
 *
 * This Use Case is common when users want to deploy their Spring Boot, Apache Geode {@link ClientCache} apps to
 * CloudFoundry however, want to connect those apps to an external Apache Geode cluster.
 *
 * @author John Blum
 * @see java.util.Properties
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.builder.SpringApplicationBuilder
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.core.io.ClassPathResource
 * @see org.springframework.geode.boot.autoconfigure.ClientSecurityAutoConfiguration
 * @see org.springframework.geode.boot.autoconfigure.security.auth.AbstractAutoConfiguredSecurityContextIntegrationTests
 * @see org.springframework.test.annotation.DirtiesContext
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.1.0
 */
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = AutoConfiguredHybridSecurityContextIntegrationTests.GemFireClientConfiguration.class,
	properties = {
		"spring.data.gemfire.security.username=phantom",
		"spring.data.gemfire.security.password=s3cr3t"
	},
	webEnvironment = SpringBootTest.WebEnvironment.NONE
)
public class AutoConfiguredHybridSecurityContextIntegrationTests
		extends AbstractAutoConfiguredSecurityContextIntegrationTests {

	private static final String LOCATOR_PORT_PLACEHOLDER_REGEX = "%LOCATOR_PORT%";
	private static final String VCAP_APPLICATION_PROPERTIES = "application-vcap-hybrid.properties";

	private static final Properties vcapApplicationProperties = new Properties();

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void runGemFireServer() throws IOException {
		String dockerImage = System.getProperty("spring.test.gemfire.docker.image");
		gemFireCluster = new GemFireCluster(dockerImage,1,1)
				.withClasspath(GemFireCluster.ALL_GLOB, System.getProperty("TEST_JAR_PATH"))
				.withGemFireProperty(ALL_GLOB, "security-manager", TestSecurityManager.class.getName())
				.withGemFireProperty(ALL_GLOB, "security-username", "phantom")
				.withGemFireProperty(ALL_GLOB, "security-password", "s3cr3t");

		gemFireCluster.acceptLicense().start();

		gemFireCluster.gfshBuilder()
				.withCredentials("phantom", "s3cr3t").build()
				.run("create region --name=Echo --type=REPLICATE",
				"put --key=Hello --value=Hello --region=Echo",
				"put --key=Test --value=Test --region=Echo",
				"put --key=Good-Bye --value=Good-Bye --region=Echo");

		System.setProperty("spring.data.gemfire.pool.locators", "localhost[" + gemFireCluster.getLocatorPort() + "]");

		loadVcapApplicationProperties(gemFireCluster.getLocatorPort());

		setTestSecuritySystemProperties(gemFireCluster.getLocatorPort());

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

	private static void setTestSecuritySystemProperties(int locatorPort) {
		System.setProperty("test.security.hybrid.gemfire.pool.locators.port", String.valueOf(locatorPort));
	}

	private static void unsetTestAutoConfiguredPoolServersPortSystemProperty() {
		System.clearProperty(GEMFIRE_POOL_SERVERS_PROPERTY);
	}

	@AfterClass
	public static void cleanUpUsedResources() {

		vcapApplicationProperties.stringPropertyNames().forEach(System::clearProperty);
		System.clearProperty("test.security.hybrid.gemfire.pool.locators.port");
	}

	@SpringBootApplication
	static class GemFireClientConfiguration extends BaseGemFireClientConfiguration { }

	public static class TestSecurityManager extends AbstractTestSecurityManager {

		protected String getUsername() {
			return "phantom";
		}

		protected String getPassword() {
			return "s3cr3t";
		}
	}
}
