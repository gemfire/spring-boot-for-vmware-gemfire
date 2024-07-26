/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.security.auth.cloud;

import static com.vmware.gemfire.testcontainers.GemFireCluster.ALL_GLOB;
import com.vmware.gemfire.testcontainers.GemFireCluster;
import java.io.IOException;
import java.util.Properties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
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
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.builder.SpringApplicationBuilder
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.core.io.ClassPathResource
 * @see org.springframework.geode.boot.autoconfigure.ClientSecurityAutoConfiguration
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

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void runGemFireServer() throws IOException {
		String dockerImage = System.getProperty("spring.test.gemfire.docker.image");
		gemFireCluster = new GemFireCluster(dockerImage,1,1)
		.withClasspath(GemFireCluster.ALL_GLOB, System.getProperty("TEST_JAR_PATH"))
		.withGemFireProperty(ALL_GLOB, "security-manager", TestSecurityManager.class.getName())
		.withGemFireProperty(ALL_GLOB, "security-username", "cluster_operator_CQhqoDaEIT1gobjLryfpBg")
		.withGemFireProperty(ALL_GLOB, "security-password", "vaxAi8UuJkBp9csgDvJ5YA");
		gemFireCluster.acceptLicense().start();

		gemFireCluster.gfshBuilder().withCredentials("cluster_operator_CQhqoDaEIT1gobjLryfpBg", "vaxAi8UuJkBp9csgDvJ5YA").build().run("create region --name=Echo --type=REPLICATE",
				"put --key=Hello --value=Hello --region=Echo",
				"put --key=Test --value=Test --region=Echo",
				"put --key=Good-Bye --value=Good-Bye --region=Echo");

		loadVcapApplicationProperties(gemFireCluster.getLocatorPort());
	}

	private static void loadVcapApplicationProperties(int locatorPort) throws IOException {

		vcapApplicationProperties.load(new ClassPathResource(VCAP_APPLICATION_PROPERTIES).getInputStream());

		vcapApplicationProperties.stringPropertyNames().forEach(propertyName -> {

			String propertyValue = String.valueOf(vcapApplicationProperties.getProperty(propertyName))
				.replaceAll(LOCATOR_PORT_PLACEHOLDER_REGEX, String.valueOf(locatorPort));

			System.setProperty(propertyName, propertyValue);
		});
	}

	@AfterClass
	public static void cleanUpUsedResources() {
		vcapApplicationProperties.stringPropertyNames().forEach(System::clearProperty);
	}

	@SpringBootApplication
	static class GemFireClientConfiguration extends BaseGemFireClientConfiguration {
		@Bean
		ClientCacheConfigurer configurer() {
			return (beanName, bean) -> bean.addLocators(new ConnectionEndpoint("localhost", gemFireCluster.getLocatorPort()));
		}
	}

	public static class TestSecurityManager extends AbstractTestSecurityManager {

		protected String getUsername() {
			return "cluster_operator_CQhqoDaEIT1gobjLryfpBg";
		}

		protected String getPassword() {
			return "vaxAi8UuJkBp9csgDvJ5YA";
		}
	}
}
