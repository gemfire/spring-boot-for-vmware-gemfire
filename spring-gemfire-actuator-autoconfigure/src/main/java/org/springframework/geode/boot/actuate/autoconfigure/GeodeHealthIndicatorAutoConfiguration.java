/*
 * Copyright 2023-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.actuate.autoconfigure;

import org.apache.geode.cache.client.ClientCache;
import org.springframework.boot.health.autoconfigure.contributor.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.health.autoconfigure.contributor.HealthContributorAutoConfiguration;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.geode.boot.actuate.autoconfigure.config.BaseGeodeHealthIndicatorConfiguration;
import org.springframework.geode.boot.actuate.autoconfigure.config.ClientCacheHealthIndicatorConfiguration;
import org.springframework.geode.boot.autoconfigure.ClientCacheAutoConfiguration;

/**
 * Spring Boot {@link EnableAutoConfiguration auto-configuration} for Apache Geode
 * {@link HealthIndicator HealthIndicators}.
 *
 * @author John Blum
 * @see org.apache.geode.cache.client.ClientCache
 * @see ConditionalOnEnabledHealthIndicator
 * @see HealthContributorAutoConfiguration
 * @see org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.context.annotation.Import
 * @see org.springframework.data.gemfire.client.ClientCacheFactoryBean
 * @see org.springframework.geode.boot.actuate.autoconfigure.config.BaseGeodeHealthIndicatorConfiguration
 * @see org.springframework.geode.boot.actuate.autoconfigure.config.ClientCacheHealthIndicatorConfiguration
 * @see org.springframework.geode.boot.autoconfigure.ClientCacheAutoConfiguration
 * @since 1.0.0
 */
@Configuration
@AutoConfigureAfter(ClientCacheAutoConfiguration.class)
@ConditionalOnBean(ClientCache.class)
@ConditionalOnClass(ClientCacheFactoryBean.class)
@ConditionalOnEnabledHealthIndicator("geode")
@Import({
	BaseGeodeHealthIndicatorConfiguration.class,
	ClientCacheHealthIndicatorConfiguration.class,
})
@SuppressWarnings("unused")
public class GeodeHealthIndicatorAutoConfiguration {

}
