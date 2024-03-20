/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.function.Function;

import org.junit.Test;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.Pool;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.config.annotation.PeerCacheApplication;
import org.springframework.data.gemfire.tests.integration.SpringBootApplicationIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.geode.boot.autoconfigure.CacheNameAutoConfiguration;
import org.springframework.geode.config.annotation.UseMemberName;

/**
 * Integration Tests for {@link CacheNameAutoConfiguration}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.builder.SpringApplicationBuilder
 * @see org.springframework.data.gemfire.tests.integration.SpringBootApplicationIntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.geode.boot.autoconfigure.CacheNameAutoConfiguration
 * @since 1.0.0
 */
public class CacheNameAutoConfigurationIntegrationTests extends SpringBootApplicationIntegrationTestsSupport {

	private Function<SpringApplicationBuilder, SpringApplicationBuilder> springApplicationBuilderFunction =
		Function.identity();

	private final Function<SpringApplicationBuilder, SpringApplicationBuilder> springApplicationNamePropertyFunction =
		builder -> {
			builder.properties(Collections.singletonMap("spring.application.name", "SpringApplicationNameTest"));
			return builder;
		};

	private final Function<SpringApplicationBuilder, SpringApplicationBuilder> springDataGemFireNamePropertyFunction =
		builder -> {
			builder.properties(Collections.singletonMap("spring.data.gemfire.name", "SpringDataGemFireNameTest"));
			return builder;
		};

	@Override
	protected SpringApplicationBuilder processBeforeBuild(SpringApplicationBuilder springApplicationBuilder) {
		return this.springApplicationBuilderFunction.apply(springApplicationBuilder);
	}

	private void assertGemFireCache(GemFireCache gemfireCache, String expectedName) {

		assertThat(gemfireCache).isNotNull();
		assertThat(gemfireCache.getDistributedSystem()).isNotNull();
		assertThat(gemfireCache.getDistributedSystem().getProperties()).isNotNull();
		assertThat(gemfireCache.getDistributedSystem().getProperties().getProperty("name")).isEqualTo(expectedName);
	}

	@Test
	public void cacheNameUsesAnnotationNameAttribute() {
		assertGemFireCache(newApplicationContext(AnnotationNameAttributeTestConfiguration.class)
				.getBean(GemFireCache.class), "AnnotationNameTest");
	}

	@Test
	public void cacheNameUsesMemberNameAttribute() {
		assertGemFireCache(newApplicationContext(MemberNameAttributeTestConfiguration.class)
			.getBean(GemFireCache.class), "MemberNameTest");
	}

	@Test
	public void cacheNameUsesSpringApplicationNameProperty() {

		this.springApplicationBuilderFunction = this.springApplicationNamePropertyFunction;

		assertGemFireCache(newApplicationContext(AnnotationNameAttributeTestConfiguration.class)
			.getBean(GemFireCache.class), "SpringApplicationNameTest");
	}

	@Test
	public void cacheNameUsesSpringDataGemFireNameProperty() {

		this.springApplicationBuilderFunction = this.springApplicationNamePropertyFunction
			.andThen(this.springDataGemFireNamePropertyFunction);

		assertGemFireCache(newApplicationContext(MemberNameAttributeTestConfiguration.class)
			.getBean(GemFireCache.class), "SpringDataGemFireNameTest");
	}

	@Configuration
	@EnableAutoConfiguration
	@EnableGemFireMockObjects
	@PeerCacheApplication(name = "AnnotationNameTest")
	static class AnnotationNameAttributeTestConfiguration { }

	@Configuration
	@EnableAutoConfiguration
	@EnableGemFireMockObjects
	@UseMemberName("MemberNameTest")
	@SuppressWarnings("unused")
	static class MemberNameAttributeTestConfiguration {

		@Bean("DEFAULT")
		Pool defaultPool() {
			return mock(Pool.class);
		}
	}
}
