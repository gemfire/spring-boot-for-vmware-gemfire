/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure;

import org.apache.geode.cache.client.ClientCache;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.EnableLogging;

/**
 * Spring Boot {@link EnableAutoConfiguration auto-Configuration} for Apache Geode logging.
 *
 * @author John Blum
 * @see org.springframework.boot.SpringBootConfiguration
 * @see org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * @see org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 * @see org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 * @see org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 * @see org.springframework.data.gemfire.client.ClientCacheFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.EnableLogging
 * @since 1.1.0
 */
@SpringBootConfiguration
@ConditionalOnBean(ClientCache.class)
@ConditionalOnClass(ClientCacheFactoryBean.class)
@ConditionalOnMissingBean(name = {
	"org.springframework.data.gemfire.config.annotation.LoggingConfiguration.ClientGemFirePropertiesConfigurer",
})
@EnableLogging
@SuppressWarnings("unused")
// TODO Find a more reliable way to refer to the LoggingConfiguration Configurer beans defined above other than by name!
public class LoggingAutoConfiguration {

}
