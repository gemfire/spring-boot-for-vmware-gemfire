/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.pdx;

import static org.assertj.core.api.Assertions.assertThat;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.pdx.PdxSerializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.mapping.MappingPdxSerializer;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.geode.boot.autoconfigure.PdxSerializationAutoConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit Tests for {@link PdxSerializationAutoConfiguration} asserting that SDG's {@link MappingPdxSerializer}
 * is {@literal auto-configured} on the cache when a custom, user-defined {@link PdxSerializer} is not declared.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.pdx.PdxSerializer
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.context.annotation.Profile
 * @see org.springframework.data.gemfire.mapping.MappingPdxSerializer
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.geode.boot.autoconfigure.PdxSerializationAutoConfiguration
 * @see org.springframework.test.context.ActiveProfiles
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.3.0
 */
@ActiveProfiles("MAPPING_PDX_SERIALIZER")
@RunWith(SpringRunner.class)
@SpringBootTest(
	properties = "spring.application.name=MappingPdxSerializerPdxSerializationAutoConfigurationIntegrationTests",
	webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@SuppressWarnings("unused")
public class MappingPdxSerializerPdxSerializationAutoConfigurationIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ClientCache cache;

	@Test
	public void cacheIsAutoConfiguredWithMappingPdxSerializer() {

		assertThat(this.cache).isNotNull();
		assertThat(this.cache.getName())
			.isEqualTo(MappingPdxSerializerPdxSerializationAutoConfigurationIntegrationTests.class.getSimpleName());
		assertThat(this.cache.getPdxSerializer()).isInstanceOf(MappingPdxSerializer.class);
	}

	@SpringBootApplication
	@EnableGemFireMockObjects
	@Profile("MAPPING_PDX_SERIALIZER")
	static class TestGeodeConfiguration { }

}
