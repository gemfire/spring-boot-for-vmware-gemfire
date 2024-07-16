/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.template;

import static org.assertj.core.api.Assertions.assertThat;
import com.vmware.gemfire.testcontainers.GemFireCluster;
import java.util.stream.Collectors;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.config.annotation.EnableClusterDefinedRegions;
import org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.geode.boot.autoconfigure.RegionTemplateAutoConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link RegionTemplateAutoConfiguration} using SDG's {@link EnableClusterDefinedRegions}
 * annotation to define {@link Region Regions} and associated Templates.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.annotation.AnnotationConfigApplicationContext
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.data.gemfire.GemfireTemplate
 * @see org.springframework.data.gemfire.config.annotation.EnableClusterDefinedRegions
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.geode.boot.autoconfigure.RegionTemplateAutoConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = ServerDefinedRegionTemplateAutoConfigurationIntegrationTests.GemFireClientConfiguration.class,
	webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@SuppressWarnings("unused")
public class ServerDefinedRegionTemplateAutoConfigurationIntegrationTests
		extends ForkingClientServerIntegrationTestsSupport {

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void runGemFireServer() {
		String dockerImage = System.getProperty("spring.test.gemfire.docker.image");
		gemFireCluster = new GemFireCluster(dockerImage,1,1)
				.withGfsh(false, "create region --name=ExampleServerRegion --type=REPLICATE");
		gemFireCluster.acceptLicense().start();

		System.setProperty("spring.data.gemfire.pool.locators", "localhost[" + gemFireCluster.getLocatorPort() + "]");
	}

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ClientCache clientCache;

	@Autowired
	private GemfireTemplate exampleServerRegionTemplate;

	@Test
	public void clientCacheContainsExampleServerRegion() {

		assertThat(this.clientCache).isNotNull();

		assertThat(CollectionUtils.nullSafeSet(this.clientCache.rootRegions()).stream()
			.map(Region::getName)
			.collect(Collectors.toSet())).containsExactly("ExampleServerRegion");
	}

	@Test
	public void exampleServerRegionExistsAsClientRegionBean() {

		Region<?, ?> exampleServerRegion = this.applicationContext.getBean("ExampleServerRegion", Region.class);

		assertThat(exampleServerRegion).isNotNull();
		assertThat(exampleServerRegion.getName()).isEqualTo("ExampleServerRegion");
		assertThat(exampleServerRegion.getAttributes()).isNotNull();
		assertThat(exampleServerRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.EMPTY);
	}

	@Test
	public void exampleServerRegionTemplateIsPresent() {

		assertThat(this.exampleServerRegionTemplate).isNotNull();
		assertThat(this.exampleServerRegionTemplate.getRegion()).isNotNull();
		assertThat(this.exampleServerRegionTemplate.getRegion().getName()).isEqualTo("ExampleServerRegion");
	}

	@SpringBootApplication
	@EnableClusterDefinedRegions
	static class GemFireClientConfiguration {

		@Bean("TestBean")
		Object testBean(@Qualifier("exampleServerRegionTemplate") GemfireTemplate exampleServerRegionTemplate) {
			return "TEST";
		}
	}
}
