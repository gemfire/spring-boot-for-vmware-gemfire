/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure;

import org.apache.geode.cache.GemFireCache;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.geode.boot.autoconfigure.configuration.GemFireProperties;

/**
 * Spring Boot {@link EnableAutoConfiguration auto-configuration} class used to configure Spring Boot
 * {@link ConfigurationProperties} classes and beans from the Spring {@link Environment} containing Apache Geode
 * configuration properties.
 *
 * @author John Blum
 * @see org.apache.geode.cache.GemFireCache
 * @see org.springframework.boot.SpringBootConfiguration
 * @see org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * @see org.springframework.boot.context.properties.ConfigurationProperties
 * @see org.springframework.boot.context.properties.EnableConfigurationProperties
 * @see org.springframework.core.env.Environment
 * @see org.springframework.data.gemfire.CacheFactoryBean
 * @see org.springframework.geode.boot.autoconfigure.configuration.GemFireProperties
 * @since 1.0.0
 */
@SpringBootConfiguration
@ConditionalOnBean(GemFireCache.class)
@ConditionalOnClass({ GemFireCache.class, CacheFactoryBean.class })
@EnableConfigurationProperties({ GemFireProperties.class })
@SuppressWarnings("unused")
public class GemFirePropertiesAutoConfiguration {

}
