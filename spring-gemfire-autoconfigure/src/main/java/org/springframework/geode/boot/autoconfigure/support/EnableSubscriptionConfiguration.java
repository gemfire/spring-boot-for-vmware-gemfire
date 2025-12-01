/*
 * Copyright 2023-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.support;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.apache.geode.cache.client.Pool;

import org.springframework.data.gemfire.util.CollectionUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.config.annotation.PoolConfigurer;

/**
 * A Spring {@link Configuration} class used to enable subscription on the Apache Geode {@literal DEFAULT} {@link Pool}
 * as well as the SDG {@literal gemfirePool} {@link Pool}, only.
 *
 * @author John Blum
 * @see org.apache.geode.cache.client.Pool
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer
 * @see org.springframework.data.gemfire.config.annotation.PoolConfigurer
 * @since 1.2.0
 */
@Configuration
@SuppressWarnings("unused")
public class EnableSubscriptionConfiguration {

	protected static final String SPRING_DATA_GEMFIRE_PROPERTY_PREFIX = "spring.data.gemfire";
	protected static final String UNNAMED_POOL_PROPERTY = "spring.data.gemfire.pool.subscription-enabled";

	@Autowired
	protected Environment environment;

	private static final String DEFAULT_POOL_NAME = "DEFAULT";
	private static final String GEMFIRE_POOL_NAME = "gemfirePool";

	private static final Set<String> POOL_NAMES = CollectionUtils.asSet(DEFAULT_POOL_NAME, GEMFIRE_POOL_NAME);

	@Bean
	public ClientCacheConfigurer enableSubscriptionClientCacheConfigurer() {
		return (beanName, clientCacheFactoryBean) -> {
			if(!subscriptionsDisabledByProperty(POOL_NAMES)) {
				clientCacheFactoryBean.setSubscriptionEnabled(true);
			}
		};
	}

	@Bean
	public PoolConfigurer enableSubscriptionPoolConfigurer() {

		return (beanName, poolFactoryBean) -> Optional.ofNullable(beanName)
			.filter(POOL_NAMES::contains)
			.ifPresent(poolName -> {
				if(!subscriptionsDisabledByProperty(beanName)) {
					poolFactoryBean.setSubscriptionEnabled(true);
				}
			});
	}

	private boolean subscriptionsDisabledByProperty(Collection<String> propertyNames) {
		if(environment == null) {
			return false;
		}
		for (String poolName : propertyNames) {
			String namedPoolProperty = getNamedPoolProperty(DEFAULT_POOL_NAME, "subscription-enabled");
			if(Boolean.FALSE.equals(environment.getProperty(namedPoolProperty, Boolean.class))) {
				return true;
			}
		}
		return Boolean.FALSE.equals(environment.getProperty(UNNAMED_POOL_PROPERTY, Boolean.class));
	}

	private boolean subscriptionsDisabledByProperty(String propertyName) {
		return subscriptionsDisabledByProperty(Collections.singleton(propertyName));
	}

	private String getNamedPoolProperty(String poolName, String suffix) {
		return String.format("%1$s.%2$s.%3$s.%4$s", SPRING_DATA_GEMFIRE_PROPERTY_PREFIX, "pool", poolName, suffix);
	}
}
