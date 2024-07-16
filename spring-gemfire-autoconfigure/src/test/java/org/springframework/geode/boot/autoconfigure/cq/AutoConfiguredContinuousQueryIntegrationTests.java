/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.cq;

import static org.assertj.core.api.Assertions.assertThat;
import com.vmware.gemfire.testcontainers.GemFireCluster;
import example.geode.query.cq.event.TemperatureReading;
import example.geode.query.cq.event.TemperatureReadingsContinuousQueriesHandler;
import jakarta.annotation.Resource;
import java.io.IOException;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.EnablePdx;
import org.springframework.geode.config.annotation.ClusterAwareConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the auto-configuration of Apache Geode Continuous Query.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.CacheLoader
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.context.annotation.AnnotationConfigApplicationContext
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Import
 * @see org.springframework.data.gemfire.GemfireTemplate
 * @see org.springframework.data.gemfire.config.annotation.EnablePdx
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.geode.boot.autoconfigure.ContinuousQueryAutoConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @see example.geode.query.cq.event.TemperatureReading
 * @see example.geode.query.cq.event.TemperatureReadingsContinuousQueriesHandler
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = AutoConfiguredContinuousQueryIntegrationTests.GemFireClientConfiguration.class,
	webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@SuppressWarnings("unused")
public class AutoConfiguredContinuousQueryIntegrationTests {

	@BeforeClass
	public static void startGemFireServer() throws IOException {

		ClusterAwareConfiguration.ClusterAwareCondition.reset();

		String dockerImage = System.getProperty("spring.test.gemfire.docker.image");

		GemFireCluster gemFireCluster = new GemFireCluster(dockerImage, 1, 1);
		gemFireCluster.withPdx("example\\.geode\\.query\\.cq\\.event\\..*", true)
				.withClasspath(GemFireCluster.ALL_GLOB, System.getProperty("TEST_JAR_PATH"));

		gemFireCluster.acceptLicense().start();

		gemFireCluster.gfsh(false,"create region --name=TemperatureReadings --type=REPLICATE --cache-loader=org.springframework.geode.boot.autoconfigure.cq.TemperatureReadingsCacheLoader");

		System.setProperty("spring.data.gemfire.pool.locators", "localhost[" + gemFireCluster.getLocatorPort() + "]");
		System.setProperty("spring.data.gemfire.management.http.port",
				String.valueOf(gemFireCluster.getHttpPorts().get(0)));
	}

	@Autowired
	private GemfireTemplate temperatureReadingsTemplate;

	@Resource(name = "TemperatureReadings")
	private Region<Long, TemperatureReading> temperatureReadings;

	@Autowired
	private TemperatureReadingsContinuousQueriesHandler temperatureReadingsHandler;

	@Before
	public void setup() {
		assertThat(this.temperatureReadingsTemplate.<Long, TemperatureReading>get(1L))
			.isEqualTo(TemperatureReading.of(99));

		assertThat(this.temperatureReadings.sizeOnServer()).isEqualTo(8);

		this.temperatureReadings.keySetOnServer().forEach(key ->
			assertThat(this.temperatureReadings.get(key)).isNotEqualTo(0));
	}

	@Test
	public void assertTemperatureReadingsAreCorrect() {

		assertThat(this.temperatureReadingsHandler.getTemperatureReadingCount()).isEqualTo(4);
		assertThat(this.temperatureReadingsHandler.getBoilingTemperatures()).contains(300, 242);
		assertThat(this.temperatureReadingsHandler.getFreezingTemperatures()).contains(16, -51);
	}

	@SpringBootApplication
	@EnablePdx
	public static class GemFireClientConfiguration {

		@Bean("TemperatureReadings")
		public ClientRegionFactoryBean<Long, TemperatureReading> temperatureReadingsRegion(ClientCache gemfireCache) {

			ClientRegionFactoryBean<Long, TemperatureReading> temperatureReadingsRegion =
				new ClientRegionFactoryBean<>();

			temperatureReadingsRegion.setCache(gemfireCache);
			temperatureReadingsRegion.setShortcut(ClientRegionShortcut.PROXY);

			return temperatureReadingsRegion;
		}

		@Bean
		@DependsOn("TemperatureReadings")
		GemfireTemplate temperatureReadingsTemplate(ClientCache gemfireCache) {
			return new GemfireTemplate(gemfireCache.getRegion("/TemperatureReadings"));
		}

		@Bean
		@DependsOn("TemperatureReadings")
		TemperatureReadingsContinuousQueriesHandler temperatureReadingsHandler() {
			return new TemperatureReadingsContinuousQueriesHandler();
		}
	}
}
