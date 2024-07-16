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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.geode.boot.autoconfigure.configuration.SpringSessionProperties;
import org.springframework.session.SessionRepository;
import org.springframework.session.data.gemfire.config.annotation.web.http.GemFireHttpSessionConfiguration;

/**
 * Spring Boot {@link EnableAutoConfiguration auto-configuration} class used to configure Spring Boot
 * {@link ConfigurationProperties} classes and beans from the Spring {@link Environment} containing Spring Session
 * configuration properties used to configure either Apache Geode to manage (HTTP) Session state.
 *
 * @author John Blum
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.boot.SpringBootConfiguration
 * @see org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * @see org.springframework.boot.context.properties.ConfigurationProperties
 * @see org.springframework.boot.context.properties.EnableConfigurationProperties
 * @see org.springframework.core.env.Environment
 * @see org.springframework.geode.boot.autoconfigure.configuration.SpringSessionProperties
 * @see org.springframework.session.SessionRepository
 * @since 1.0.0
 */
@SpringBootConfiguration
@ConditionalOnBean({ ClientCache.class, SessionRepository.class })
@ConditionalOnClass({ ClientCache.class, ClientCacheFactoryBean.class, GemFireHttpSessionConfiguration.class })
@EnableConfigurationProperties({ SpringSessionProperties.class })
@SuppressWarnings("unused")
public class SpringSessionPropertiesAutoConfiguration {

}
