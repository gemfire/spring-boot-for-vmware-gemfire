/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure;

import java.util.Optional;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientCache;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.config.annotation.PeerCacheConfigurer;
import org.springframework.geode.boot.autoconfigure.condition.ConditionalOnMissingProperty;
import org.springframework.util.StringUtils;

/**
 * Spring Boot {@link EnableAutoConfiguration auto-configuration} class used to configure the Apache Geode
 * {@link ClientCache} application or peer {@link Cache} member node name (i.e. {@literal gemfire.name})
 * with the Spring Boot {@literal spring.application.name} property.
 *
 * @author John Blum
 * @see Cache
 * @see GemFireCache
 * @see ClientCache
 * @see SpringBootConfiguration
 * @see EnableAutoConfiguration
 * @see Bean
 * @see Environment
 * @see CacheFactoryBean
 * @see ClientCacheConfigurer
 * @see PeerCacheConfigurer
 * @since 1.0.0
 */
@SpringBootConfiguration
@ConditionalOnClass({ CacheFactoryBean.class, GemFireCache.class })
@SuppressWarnings("unused")
public class CacheNameAutoConfiguration {

	private static final String GEMFIRE_NAME_PROPERTY = "name";
	private static final String SPRING_APPLICATION_NAME_PROPERTY = "spring.application.name";
	private static final String SPRING_DATA_GEMFIRE_CACHE_NAME_PROPERTY = "spring.data.gemfire.cache.name";
	private static final String SPRING_DATA_GEMFIRE_NAME_PROPERTY = "spring.data.gemfire.name";
	private static final String SPRING_DATA_GEODE_CACHE_NAME_PROPERTY = "spring.data.geode.cache.name";
	private static final String SPRING_DATA_GEODE_NAME_PROPERTY = "spring.data.geode.name";

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE + 1) // apply next (e.g. after @UseMemberName)
	@ConditionalOnMissingProperty({
		SPRING_DATA_GEMFIRE_CACHE_NAME_PROPERTY,
		SPRING_DATA_GEMFIRE_NAME_PROPERTY,
		SPRING_DATA_GEODE_CACHE_NAME_PROPERTY,
		SPRING_DATA_GEODE_NAME_PROPERTY,
	})
	ClientCacheConfigurer clientCacheNameConfigurer(Environment environment) {
		return (beanName, clientCacheFactoryBean) -> configureCacheName(environment, clientCacheFactoryBean);
	}

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE + 1) // apply next (e.g. after @UseMemberName)
	@ConditionalOnMissingProperty({
		SPRING_DATA_GEMFIRE_CACHE_NAME_PROPERTY,
		SPRING_DATA_GEMFIRE_NAME_PROPERTY,
		SPRING_DATA_GEODE_CACHE_NAME_PROPERTY,
		SPRING_DATA_GEODE_NAME_PROPERTY,
	})
	PeerCacheConfigurer peerCacheNameConfigurer(Environment environment) {
		return (beanName, peerCacheFactoryBean) -> configureCacheName(environment, peerCacheFactoryBean);
	}

	private void configureCacheName(Environment environment, CacheFactoryBean cacheFactoryBean) {

		String springApplicationName = resolveSpringApplicationName(environment);

		if (StringUtils.hasText(springApplicationName)) {
			setGemFireName(cacheFactoryBean, springApplicationName);
		}
	}

	private String resolveSpringApplicationName(Environment environment) {

		return Optional.ofNullable(environment)
			.filter(it -> it.containsProperty(SPRING_APPLICATION_NAME_PROPERTY))
			.map(it -> it.getProperty(SPRING_APPLICATION_NAME_PROPERTY))
			.filter(StringUtils::hasText)
			.orElse(null);
	}

	private void setGemFireName(CacheFactoryBean cacheFactoryBean, String gemfireName) {
		cacheFactoryBean.getProperties().setProperty(GEMFIRE_NAME_PROPERTY, gemfireName);
	}
}
