/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.support;

import java.util.Optional;
import java.util.Set;

import org.apache.geode.cache.client.Pool;

import org.apache.shiro.util.CollectionUtils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.config.annotation.PoolConfigurer;

/**
 * A Spring {@link Configuration} class used to enable subscription on the Apache Geode {@literal DEFAULT} {@link Pool}
 * as well as the SDG {@literal gemfirePool} {@link Pool}, only.
 *
 * @author John Blum
 * @see Pool
 * @see Bean
 * @see Configuration
 * @see ClientCacheConfigurer
 * @see PoolConfigurer
 * @since 1.2.0
 */
@Configuration
@SuppressWarnings("unused")
public class EnableSubscriptionConfiguration {

	private static final String DEFAULT_POOL_NAME = "DEFAULT";
	private static final String GEMFIRE_POOL_NAME = "gemfirePool";

	private static final Set<String> POOL_NAMES = CollectionUtils.asSet(DEFAULT_POOL_NAME, GEMFIRE_POOL_NAME);

	@Bean
	public ClientCacheConfigurer enableSubscriptionClientCacheConfigurer() {
		return (beanName, clientCacheFactoryBean) -> clientCacheFactoryBean.setSubscriptionEnabled(true);
	}

	@Bean
	public PoolConfigurer enableSubscriptionPoolConfigurer() {

		return (beanName, poolFactoryBean) -> Optional.ofNullable(beanName)
			.filter(POOL_NAMES::contains)
			.ifPresent(poolName -> poolFactoryBean.setSubscriptionEnabled(true));
	}
}
