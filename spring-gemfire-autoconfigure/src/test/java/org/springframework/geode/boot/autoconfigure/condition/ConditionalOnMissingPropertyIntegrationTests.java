/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.condition;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.function.Function;

import org.junit.Test;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.tests.integration.SpringBootApplicationIntegrationTestsSupport;

/**
 * Integration tests for {@link ConditionalOnMissingProperty} and {@link OnMissingPropertyCondition}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.boot.builder.SpringApplicationBuilder
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.data.gemfire.tests.integration.SpringBootApplicationIntegrationTestsSupport
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class ConditionalOnMissingPropertyIntegrationTests extends SpringBootApplicationIntegrationTestsSupport {

	private Function<SpringApplicationBuilder, SpringApplicationBuilder> springApplicationBuilderFunction =
		Function.identity();

	private final Function<SpringApplicationBuilder, SpringApplicationBuilder> allMatchingPropertiesFunction =
		builder -> {
			builder.properties(Collections.singletonMap("example.app.config.propOne", ""));
			builder.properties(Collections.singletonMap("example.app.config.propTwo", "test"));
			return builder;
		};

	private final Function<SpringApplicationBuilder, SpringApplicationBuilder> nonMatchingPropertyFunction =
		builder -> {
			builder.properties(Collections.singletonMap("example.app.cfg.propertyOne", ""));
			return builder;
		};

	private final Function<SpringApplicationBuilder, SpringApplicationBuilder> singleMatchingPropertyFunction =
		builder -> {
			builder.properties(Collections.singletonMap("example.app.config.propTwo", "test"));
			return builder;
		};

	@Override
	protected SpringApplicationBuilder processBeforeBuild(SpringApplicationBuilder springApplicationBuilder) {
		return this.springApplicationBuilderFunction.apply(springApplicationBuilder);
	}

	private void assertBeanDefinitions(ApplicationContext applicationContext, boolean conditionalBeanExists) {

		assertThat(applicationContext.containsBean("conditionalBean")).isEqualTo(conditionalBeanExists);
		assertThat(applicationContext.containsBean("unconditionalBean")).isTrue();
	}

	@Test
	public void allBeansExist() {
		assertBeanDefinitions(newApplicationContext(TestConfiguration.class), true);
	}

	@Test
	public void allBeansExistWithNonMatchingProperties() {

		this.springApplicationBuilderFunction = this.nonMatchingPropertyFunction;

		assertBeanDefinitions(newApplicationContext(TestConfiguration.class), true);
	}

	@Test
	public void unconditionalBeanExistsWithAllMatchingProperties() {

		this.springApplicationBuilderFunction = this.allMatchingPropertiesFunction;

		assertBeanDefinitions(newApplicationContext(TestConfiguration.class), false);
	}

	@Test
	public void unconditionalBeanExistsWithSingleMatchingProperty() {

		this.springApplicationBuilderFunction = this.singleMatchingPropertyFunction;

		assertBeanDefinitions(newApplicationContext(TestConfiguration.class), false);
	}

	@Configuration
	static class TestConfiguration {

		@Bean
		@ConditionalOnMissingProperty(prefix = "example.app.config", name = { "propOne", "propTwo" })
		Object conditionalBean() {
			return "conditional";
		}

		@Bean
		Object unconditionalBean() {
			return "unconditional";
		}
	}
}
