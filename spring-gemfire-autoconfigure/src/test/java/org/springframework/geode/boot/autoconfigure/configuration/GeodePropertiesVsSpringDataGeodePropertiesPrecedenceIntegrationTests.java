/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.client.ClientCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests asserting and testing the precedence of Apache Geode, Spring and Spring Data for Apache Geode (SDG)
 * {@link Properties} precedence.
 *
 * Essentially, SDG {@link Properties} should take precedence over (i.e. override) both Spring and Apache Geode
 * {@link Properties}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.context.annotation.Profile
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.annotation.DirtiesContext
 * @see org.springframework.test.context.ActiveProfiles
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.3.0
 */
@ActiveProfiles("spring-gemfire-property-precedence")
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
	"spring.data.gemfire.cache.client.durable-client-id=987",
	"spring.data.gemfire.name=MockName",
	"spring.application.name=TestName",
	"gemfire.durable-client-id=123",
	"gemfire.name=NoName"
})
@SuppressWarnings("unused")
public class GeodePropertiesVsSpringDataGeodePropertiesPrecedenceIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ClientCache clientCache;

	@Test
	public void springDataGeodePropertiesTakePrecedenceOverSpringAndGeodeProperties() {

		assertThat(this.clientCache).isNotNull();
		assertThat(this.clientCache.getName()).isEqualTo("MockName");
		assertThat(this.clientCache.getDistributedSystem()).isNotNull();

		Properties gemfireProperties = this.clientCache.getDistributedSystem().getProperties();

		assertThat(gemfireProperties).isNotNull();
		assertThat(gemfireProperties).containsKeys("durable-client-id", "name");
		assertThat(gemfireProperties.getProperty("durable-client-id")).isEqualTo("987");
		assertThat(gemfireProperties.getProperty("name")).isEqualTo("MockName");
	}

	@SpringBootApplication
	@EnableGemFireMockObjects
	@Profile("spring-gemfire-property-precedence")
	static class TestGeodeConfiguration { }

}
