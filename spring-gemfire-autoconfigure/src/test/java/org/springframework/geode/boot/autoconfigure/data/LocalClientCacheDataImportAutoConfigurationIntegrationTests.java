/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.geode.boot.autoconfigure.DataImportExportAutoConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import example.app.golf.model.Golfer;

/**
 * Integration Tests for {@link DataImportExportAutoConfiguration}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.context.annotation.Profile
 * @see org.springframework.data.gemfire.GemfireTemplate
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.geode.boot.autoconfigure.DataImportExportAutoConfiguration
 * @see org.springframework.test.context.ActiveProfiles
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.3.0
 */
@ActiveProfiles("IMPORT-LOCAL")
@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = LocalClientCacheDataImportAutoConfigurationIntegrationTests.TestGeodeClientConfiguration.class,
	properties = {
		"spring.application.name=LocalClientCacheDataImportAutoConfigurationIntegrationTests",
		"spring.boot.data.gemfire.cache.data.import.active-profiles=IMPORT-LOCAL"
	}
)
public class LocalClientCacheDataImportAutoConfigurationIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@SuppressWarnings("unused")
	private GemfireTemplate golfersTemplate;

	@Test
	public void golfersWereLoaded() {

		assertThat(this.golfersTemplate).isNotNull();
		assertThat(this.golfersTemplate.getRegion()).isNotNull();
		assertThat(this.golfersTemplate.getRegion().getName()).isEqualTo("Golfers");
		assertThat(this.golfersTemplate.getRegion()).hasSize(1);

		Object value = this.golfersTemplate.get(1L);

		assertThat(value).isInstanceOf(Golfer.class);

		Golfer golfer = (Golfer) value;

		assertThat(golfer).isNotNull();
		assertThat(golfer.getId()).isEqualTo(1L);
		assertThat(golfer.getName()).isEqualTo("John Blum");
		assertThat(golfer.getHandicap()).isEqualTo(9);
	}

	@Profile("IMPORT-LOCAL")
	@SpringBootApplication
	@EnableEntityDefinedRegions(basePackageClasses = Golfer.class, clientRegionShortcut = ClientRegionShortcut.LOCAL)
	static class TestGeodeClientConfiguration { }

}
