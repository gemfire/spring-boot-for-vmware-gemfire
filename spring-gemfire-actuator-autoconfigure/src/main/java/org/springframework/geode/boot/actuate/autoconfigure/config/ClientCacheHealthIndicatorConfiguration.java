/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.actuate.autoconfigure.config;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.listener.ContinuousQueryListenerContainer;
import org.springframework.geode.boot.actuate.GeodeContinuousQueriesHealthIndicator;
import org.springframework.geode.boot.actuate.GeodePoolsHealthIndicator;

/**
 * Spring {@link Configuration} class declaring Spring beans for Apache Geode {@link ClientCache}
 * {@link HealthIndicator HealthIndicators}.
 *
 * @author John Blum
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.boot.actuate.health.HealthIndicator
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.geode.boot.actuate.GeodeContinuousQueriesHealthIndicator
 * @see org.springframework.geode.boot.actuate.GeodePoolsHealthIndicator
 * @since 1.0.0
 */
@Configuration
@SuppressWarnings("unused")
public class ClientCacheHealthIndicatorConfiguration {

	@Bean("GeodeContinuousQueryHealthIndicator")
	GeodeContinuousQueriesHealthIndicator continuousQueriesHealthIndicator(
			@Autowired(required = false) ContinuousQueryListenerContainer continuousQueryListenerContainer) {

		return new GeodeContinuousQueriesHealthIndicator(continuousQueryListenerContainer);
	}

	@Bean("GeodePoolsHealthIndicator")
	GeodePoolsHealthIndicator poolsHealthIndicator(ClientCache gemfireCache) {
		return new GeodePoolsHealthIndicator(gemfireCache);
	}
}
