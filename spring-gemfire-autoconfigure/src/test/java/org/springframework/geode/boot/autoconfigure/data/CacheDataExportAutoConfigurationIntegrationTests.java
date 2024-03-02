/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.data;

import static org.assertj.core.api.Assertions.assertThat;

import example.app.golf.model.Golfer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.StreamSupport;

import org.apache.geode.cache.Region;
import org.awaitility.Awaitility;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.tests.integration.ClientServerIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.process.ProcessWrapper;
import org.springframework.data.gemfire.tests.util.FileUtils;
import org.springframework.geode.boot.autoconfigure.DataImportExportAutoConfiguration;
import org.springframework.geode.config.annotation.ClusterAwareConfiguration;
import org.springframework.geode.config.annotation.EnableClusterAware;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Integration Tests for {@link DataImportExportAutoConfiguration}, which specifically tests the export of
 * {@link Region} values (data) to JSON on Spring Boot application (JVM) shutdown.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see com.fasterxml.jackson.databind.ObjectMapper
 * @see org.apache.geode.cache.Region
 * @see org.springframework.boot.ApplicationRunner
 * @see org.springframework.boot.SpringApplication
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.builder.SpringApplicationBuilder
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Profile
 * @see org.springframework.data.gemfire.GemfireTemplate
 * @see org.springframework.data.gemfire.tests.process.ProcessWrapper
 * @see org.springframework.data.gemfire.tests.integration.ClientServerIntegrationTestsSupport
 * @see org.springframework.geode.boot.autoconfigure.DataImportExportAutoConfiguration
 * @since 1.3.0
 */
public class CacheDataExportAutoConfigurationIntegrationTests extends ClientServerIntegrationTestsSupport {

	@Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File workingDirectory;

	private static ProcessWrapper process;

	private static final String DATA_GOLFERS_JSON = "data-golfers.json";

	public CacheDataExportAutoConfigurationIntegrationTests() throws IOException {}

	@BeforeClass
	@AfterClass
	public static void resetClusterAwareCondition() {
		ClusterAwareConfiguration.ClusterAwareCondition.reset();
	}

	@Before
	public void runGeodeProcess() throws IOException {
		workingDirectory = temporaryFolder.newFolder(String.format("cache-data-export-%d", System.currentTimeMillis()));
		process = run(workingDirectory, TestGeodeConfiguration.class, "-Dspring.profiles.active=EXPORT",
				"-Dspring.boot.data.gemfire.cache.data.export.enabled=true");

		assertThat(process).isNotNull();

		waitOn(() -> !process.isRunning(), Duration.ofSeconds(20).toMillis(), Duration.ofSeconds(2).toMillis());
	}

	@After
	public void cleanup() {
		stop(process);
	}

	@Test
	public void exportedJsonIsCorrect() throws Exception {

		Path filePath = workingDirectory.toPath().resolve(DATA_GOLFERS_JSON);
		assertThat(workingDirectory.exists()).isTrue();
		File dataGolferJson = filePath.toFile();

		Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> assertThat(dataGolferJson).isFile());

		String actualJson = FileUtils.read(dataGolferJson);

		Set<Golfer> expectedGolfers = mapFromJsonToGolfers(actualJson);

		assertThat(expectedGolfers).isNotNull();
		assertThat(expectedGolfers).hasSize(2);
		assertContains(expectedGolfers, Golfer.newGolfer(1L, "John Blum").withHandicap(9));
		assertContains(expectedGolfers, Golfer.newGolfer(2L, "Moe Haroon").withHandicap(10));
	}

	private void assertContains(Iterable<Golfer> golfers, Golfer golfer) {

		assertThat(StreamSupport.stream(golfers.spliterator(), false).anyMatch(it -> it.getId().equals(golfer.getId())
				&& it.getName().equals(golfer.getName()) && it.getHandicap().equals(golfer.getHandicap()))).isTrue();
	}

	private Set<Golfer> mapFromJsonToGolfers(String json) throws Exception {
		return new HashSet<>(newObjectMapper().readerForListOf(Golfer.class).readValue(json));
	}

	private ObjectMapper newObjectMapper() {

		return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Profile("EXPORT")
	@SpringBootApplication
	@EnableClusterAware
	@EnableEntityDefinedRegions(basePackageClasses = Golfer.class)
	@SuppressWarnings("unused")
	static class TestGeodeConfiguration {

		public static void main(String[] args) {

			new SpringApplicationBuilder(TestGeodeConfiguration.class).web(WebApplicationType.NONE).build().run(args);
		}

		private static void log(String message, Object... args) {
			System.out.printf(String.format("%s%n", message), args);
			System.out.flush();
		}

		@Bean
		ApplicationRunner runner(GemfireTemplate golfersTemplate) {

			return args -> {

				save(golfersTemplate, Golfer.newGolfer(1L, "John Blum").withHandicap(9));
				save(golfersTemplate, Golfer.newGolfer(2L, "Moe Haroon").withHandicap(10));

				log("FORE!");
			};
		}

		private Golfer save(GemfireTemplate golfersTemplate, Golfer golfer) {
			golfersTemplate.put(golfer.getId(), golfer);
			return golfer;
		}
	}
}
