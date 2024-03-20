/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.actuate.autoconfigure.config;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientCache;

import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.geode.boot.actuate.GeodeCacheHealthIndicator;
import org.springframework.geode.boot.actuate.GeodeDiskStoresHealthIndicator;
import org.springframework.geode.boot.actuate.GeodeIndexesHealthIndicator;
import org.springframework.geode.boot.actuate.GeodeRegionsHealthIndicator;

/**
 * Spring {@link Configuration} class declaring Spring beans for general Apache Geode peer {@link Cache}
 * and {@link ClientCache} {@link HealthIndicator HealthIndicators}.
 *
 * @author John Blum
 * @see org.apache.geode.cache.Cache
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.boot.actuate.health.HealthIndicator
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.geode.boot.actuate.GeodeCacheHealthIndicator
 * @see org.springframework.geode.boot.actuate.GeodeDiskStoresHealthIndicator
 * @see org.springframework.geode.boot.actuate.GeodeIndexesHealthIndicator
 * @see org.springframework.geode.boot.actuate.GeodeRegionsHealthIndicator
 * @since 1.0.0
 */
@Configuration
@SuppressWarnings("unused")
public class BaseGeodeHealthIndicatorConfiguration {

	@Bean("GeodeCacheHealthIndicator")
	GeodeCacheHealthIndicator cacheHealthIndicator(GemFireCache gemfireCache) {
		return new GeodeCacheHealthIndicator(gemfireCache);
	}

	@Bean("GeodeDiskStoresHealthIndicator")
	GeodeDiskStoresHealthIndicator diskStoresHealthIndicator(ApplicationContext applicationContext) {
		return new GeodeDiskStoresHealthIndicator(applicationContext);
	}

	@Bean("GeodeIndexesHealthIndicator")
	GeodeIndexesHealthIndicator indexesHealthIndicator(ApplicationContext applicationContext) {
		return new GeodeIndexesHealthIndicator(applicationContext);
	}

	@Bean("GeodeRegionsHealthIndicator")
	GeodeRegionsHealthIndicator regionsHealthIndicator(GemFireCache gemfireCache) {
		return new GeodeRegionsHealthIndicator(gemfireCache);
	}
}
