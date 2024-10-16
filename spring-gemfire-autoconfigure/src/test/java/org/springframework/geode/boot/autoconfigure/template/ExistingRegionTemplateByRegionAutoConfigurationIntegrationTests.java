/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.template;

import static org.assertj.core.api.Assertions.assertThat;
import jakarta.annotation.Resource;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.geode.boot.autoconfigure.RegionTemplateAutoConfiguration;
import org.springframework.geode.core.util.SpringExtensions;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link RegionTemplateAutoConfiguration} using explicitly declared {@link Region}
 * bean definition and existing {@link GemfireTemplate} {@link Region} in a Spring {@link ApplicationContext}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.data.gemfire.GemfireTemplate
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.geode.boot.autoconfigure.RegionTemplateAutoConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@SuppressWarnings("unused")
public class ExistingRegionTemplateByRegionAutoConfigurationIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private GemfireTemplate testTemplate;

	@Resource(name = "Example")
	private Region<Object, Object> example;

	private void printBeanDefinitionMetadata(String beanName) {

		System.err.printf("BEAN DEFINITION METADATA [%s]%n",
			SpringExtensions.getBeanDefinitionMetadata("exampleTemplate", this.applicationContext));
	}

	@Test
	public void exampleTemplateIsPresentButIsNotAGemfireTemplate() {

		assertThat(this.applicationContext.containsBean("exampleTemplate")).isTrue();
		assertThat(this.applicationContext.getBean("exampleTemplate")).isNotInstanceOf(GemfireTemplate.class);

		//printBeanDefinitionMetadata("exampleTemplate");
	}

	@Test
	public void onlyOneBeanOfTypeGemfireTemplateExists() {
		assertThat(this.applicationContext.getBeanNamesForType(GemfireTemplate.class)).hasSize(1);
	}

	@Test
	public void testTemplateIsPresent() {

		assertThat(this.example).isNotNull();
		assertThat(this.testTemplate).isNotNull();
		assertThat(this.testTemplate.getRegion()).isEqualTo(this.example);
	}

	@SpringBootApplication
	@EnableGemFireMockObjects
	static class TestConfiguration {

		@Bean("Example")
		public ClientRegionFactoryBean<Object, Object> exampleRegion(ClientCache gemfireCache) {

			ClientRegionFactoryBean<Object, Object> clientRegion = new ClientRegionFactoryBean<>();

			clientRegion.setCache(gemfireCache);
			clientRegion.setShortcut(ClientRegionShortcut.LOCAL);

			return clientRegion;
		}

		@Bean("TestTemplate")
		@DependsOn("Example")
		GemfireTemplate testTemplate(ClientCache gemfireCache) {
			return new GemfireTemplate(gemfireCache.getRegion("/Example"));
		}
	}
}
