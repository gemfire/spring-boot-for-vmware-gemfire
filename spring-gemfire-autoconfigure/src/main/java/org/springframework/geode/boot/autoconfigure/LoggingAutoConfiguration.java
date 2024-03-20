/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure;

import org.apache.geode.cache.GemFireCache;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.EnableLogging;

/**
 * Spring Boot {@link EnableAutoConfiguration auto-Configuration} for Apache Geode logging.
 *
 * @author John Blum
 * @see SpringBootConfiguration
 * @see EnableAutoConfiguration
 * @see ConditionalOnBean
 * @see ConditionalOnClass
 * @see ConditionalOnMissingBean
 * @see CacheFactoryBean
 * @see EnableLogging
 * @since 1.1.0
 */
@SpringBootConfiguration
@ConditionalOnBean(GemFireCache.class)
@ConditionalOnClass(CacheFactoryBean.class)
@ConditionalOnMissingBean(name = {
	"org.springframework.data.gemfire.config.annotation.LoggingConfiguration.ClientGemFirePropertiesConfigurer",
	"org.springframework.data.gemfire.config.annotation.LoggingConfiguration.LocatorGemFirePropertiesConfigurer",
	"org.springframework.data.gemfire.config.annotation.LoggingConfiguration.PeerGemFirePropertiesConfigurer",
})
@EnableLogging
@SuppressWarnings("unused")
// TODO Find a more reliable way to refer to the LoggingConfiguration Configurer beans defined above other than by name!
public class LoggingAutoConfiguration {

}
