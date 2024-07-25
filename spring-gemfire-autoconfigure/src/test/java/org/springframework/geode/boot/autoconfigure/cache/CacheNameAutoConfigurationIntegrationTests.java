/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.cache;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Collections;
import java.util.function.Function;
import org.apache.geode.cache.client.ClientCache;
import org.junit.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.tests.integration.SpringBootApplicationIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.geode.boot.autoconfigure.CacheNameAutoConfiguration;

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

	private void assertGemFireCache(ClientCache gemfireCache, String expectedName) {

		assertThat(gemfireCache).isNotNull();
		assertThat(gemfireCache.getDistributedSystem()).isNotNull();
		assertThat(gemfireCache.getDistributedSystem().getProperties()).isNotNull();
		assertThat(gemfireCache.getDistributedSystem().getProperties().getProperty("name")).isEqualTo(expectedName);
	}

	@Test
	public void cacheNameUsesAnnotationNameAttribute() {
		assertGemFireCache(newApplicationContext(AnnotationNameAttributeTestConfiguration.class)
				.getBean(ClientCache.class), "AnnotationNameTest");
	}

	@Test
	public void cacheNameUsesSpringApplicationNameProperty() {

		this.springApplicationBuilderFunction = this.springApplicationNamePropertyFunction;

		assertGemFireCache(newApplicationContext(AnnotationNameAttributeTestConfiguration.class)
			.getBean(ClientCache.class), "SpringApplicationNameTest");
	}


	@Configuration
	@EnableAutoConfiguration
	@EnableGemFireMockObjects
	@ClientCacheApplication(name = "AnnotationNameTest")
	static class AnnotationNameAttributeTestConfiguration { }
}
