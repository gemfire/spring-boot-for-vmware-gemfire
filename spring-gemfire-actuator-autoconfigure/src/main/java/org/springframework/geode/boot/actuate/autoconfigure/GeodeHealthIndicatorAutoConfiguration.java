/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.actuate.autoconfigure;

import org.apache.geode.cache.GemFireCache;

import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.geode.boot.actuate.autoconfigure.config.BaseGeodeHealthIndicatorConfiguration;
import org.springframework.geode.boot.actuate.autoconfigure.config.ClientCacheHealthIndicatorConfiguration;
import org.springframework.geode.boot.actuate.autoconfigure.config.PeerCacheHealthIndicatorConfiguration;
import org.springframework.geode.boot.autoconfigure.ClientCacheAutoConfiguration;

/**
 * Spring Boot {@link EnableAutoConfiguration auto-configuration} for Apache Geode
 * {@link HealthIndicator HealthIndicators}.
 *
 * @author John Blum
 * @see GemFireCache
 * @see ConditionalOnEnabledHealthIndicator
 * @see org.springframework.boot.actuate.autoconfigure.health.HealthContributorAutoConfiguration
 * @see EnableAutoConfiguration
 * @see Configuration
 * @see Import
 * @see CacheFactoryBean
 * @see BaseGeodeHealthIndicatorConfiguration
 * @see ClientCacheHealthIndicatorConfiguration
 * @see PeerCacheHealthIndicatorConfiguration
 * @see ClientCacheAutoConfiguration
 * @since 1.0.0
 */
@Configuration
@AutoConfigureAfter(ClientCacheAutoConfiguration.class)
@ConditionalOnBean(GemFireCache.class)
@ConditionalOnClass(CacheFactoryBean.class)
@ConditionalOnEnabledHealthIndicator("geode")
@Import({
	BaseGeodeHealthIndicatorConfiguration.class,
	ClientCacheHealthIndicatorConfiguration.class,
	PeerCacheHealthIndicatorConfiguration.class,
})
@SuppressWarnings("unused")
public class GeodeHealthIndicatorAutoConfiguration {

}
