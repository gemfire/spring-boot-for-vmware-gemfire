/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.template;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.config.annotation.EnableGemFireProperties;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.geode.boot.autoconfigure.RegionTemplateAutoConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link RegionTemplateAutoConfiguration} using natively declared {@link Region} definitions
 * in Apache Geode {@literal cache.xml}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.data.gemfire.config.annotation.EnableGemFireProperties
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.geode.boot.autoconfigure.RegionTemplateAutoConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@SuppressWarnings("unused")
public class NativeDefinedRegionTemplateAutoConfigurationIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private GemFireCache cache;

	@Autowired
	private GemfireTemplate exampleTemplate;

	@Test
	public void cacheContainsExampleRegion() {

		assertThat(this.cache).isNotNull();

		assertThat(CollectionUtils.nullSafeSet(this.cache.rootRegions()).stream()
			.map(Region::getName)
			.collect(Collectors.toSet())).containsExactly("Example");
	}

	@Test(expected = NoSuchBeanDefinitionException.class)
	public void exampleRegionBeanIsNotPresent() {
		this.applicationContext.getBean("Example", Region.class);
	}

	@Test
	public void exampleRegionTemplateIsPresent() {

		assertThat(this.exampleTemplate).isNotNull();
		assertThat(this.exampleTemplate.getRegion()).isNotNull();
		assertThat(this.exampleTemplate.getRegion().getName()).isEqualTo("Example");
	}

	@SpringBootApplication
	@EnableGemFireProperties(cacheXmlFile = "template-cache.xml")
	static class TestConfiguration {

		@Bean("TestBean")
		@DependsOn("gemfireCache")
		Object testBean(@Qualifier("exampleTemplate") GemfireTemplate exampleTemplate) {
			return "TEST";
		}
	}
}
