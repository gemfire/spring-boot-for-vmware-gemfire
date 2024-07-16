/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.security.auth.local;

import static com.vmware.gemfire.testcontainers.GemFireCluster.ALL_GLOB;
import com.vmware.gemfire.testcontainers.GemFireCluster;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.geode.boot.autoconfigure.security.auth.AbstractAutoConfiguredSecurityContextIntegrationTests;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the auto-configuration of Apache Geode Security (authentication/authorization) in a local,
 * non-managed context.
 *
 * @author John Blum
 * @see java.security.Principal
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.builder.SpringApplicationBuilder
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.geode.boot.autoconfigure.ClientSecurityAutoConfiguration
 * @see org.springframework.geode.boot.autoconfigure.security.auth.AbstractAutoConfiguredSecurityContextIntegrationTests
 * @see org.springframework.test.annotation.DirtiesContext
 * @see org.springframework.test.context.ActiveProfiles
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.0.0
 */
@ActiveProfiles("security-local-client")
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = AutoConfiguredLocalSecurityContextIntegrationTests.GemFireClientConfiguration.class,
	webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@SuppressWarnings("unused")
public class AutoConfiguredLocalSecurityContextIntegrationTests
		extends AbstractAutoConfiguredSecurityContextIntegrationTests {

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void runGemFireServer() {
		String dockerImage = System.getProperty("spring.test.gemfire.docker.image");
		gemFireCluster = new GemFireCluster(dockerImage, 1, 1)
				.withClasspath(GemFireCluster.ALL_GLOB, System.getProperty("TEST_JAR_PATH"))
				.withGemFireProperty(ALL_GLOB, "security-manager", TestSecurityManager.class.getName())
				.withGemFireProperty(ALL_GLOB, "security-username", "ghostrider")
				.withGemFireProperty(ALL_GLOB, "security-password", "p@55w0rd");

		gemFireCluster.acceptLicense().start();

		gemFireCluster.gfshBuilder()
				.withCredentials("ghostrider", "p@55w0rd").build()
				.run("create region --name=Echo --type=REPLICATE",
						"put --key=Hello --value=Hello --region=Echo",
						"put --key=Test --value=Test --region=Echo",
						"put --key=Good-Bye --value=Good-Bye --region=Echo");

		System.setProperty("spring.data.gemfire.pool.locators", "localhost[" + gemFireCluster.getLocatorPort() + "]");
	}

	@SpringBootApplication
	static class GemFireClientConfiguration extends BaseGemFireClientConfiguration { }

	public static class TestSecurityManager extends AbstractTestSecurityManager {

		protected String getUsername() {
			return "ghostrider";
		}

		protected String getPassword() {
			return "p@55w0rd";
		}
	}
}
