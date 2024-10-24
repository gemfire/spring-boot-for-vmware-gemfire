/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure;

import java.util.Optional;
import org.apache.geode.cache.client.ClientCache;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.geode.boot.autoconfigure.condition.ConditionalOnMissingProperty;
import org.springframework.util.StringUtils;

/**
 * Spring Boot {@link EnableAutoConfiguration auto-configuration} class used to configure the Apache Geode
 * {@link ClientCache} application or peer Cache member node name (i.e. {@literal gemfire.name})
 * with the Spring Boot {@literal spring.application.name} property.
 *
 * @author John Blum
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.boot.SpringBootConfiguration
 * @see org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.core.env.Environment
 * @see org.springframework.data.gemfire.client.ClientCacheFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer
 * @since 1.0.0
 */
@SpringBootConfiguration
@ConditionalOnClass({ ClientCacheFactoryBean.class, ClientCache.class })
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

	private void configureCacheName(Environment environment, ClientCacheFactoryBean cacheFactoryBean) {

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

	private void setGemFireName(ClientCacheFactoryBean cacheFactoryBean, String gemfireName) {
		cacheFactoryBean.getProperties().setProperty(GEMFIRE_NAME_PROPERTY, gemfireName);
	}
}
