/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.actuate.autoconfigure.config;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.gemfire.listener.ContinuousQueryListenerContainer;
import org.springframework.data.gemfire.util.CacheUtils;
import org.springframework.geode.boot.actuate.GeodeContinuousQueriesHealthIndicator;
import org.springframework.geode.boot.actuate.GeodePoolsHealthIndicator;

/**
 * Spring {@link Configuration} class declaring Spring beans for Apache Geode {@link ClientCache}
 * {@link HealthIndicator HealthIndicators}.
 *
 * @author John Blum
 * @see GemFireCache
 * @see ClientCache
 * @see HealthIndicator
 * @see Bean
 * @see Configuration
 * @see GeodeContinuousQueriesHealthIndicator
 * @see GeodePoolsHealthIndicator
 * @since 1.0.0
 */
@Configuration
@Conditional(ClientCacheHealthIndicatorConfiguration.ClientCacheCondition.class)
@SuppressWarnings("unused")
public class ClientCacheHealthIndicatorConfiguration {

	@Bean("GeodeContinuousQueryHealthIndicator")
	GeodeContinuousQueriesHealthIndicator continuousQueriesHealthIndicator(
			@Autowired(required = false) ContinuousQueryListenerContainer continuousQueryListenerContainer) {

		return new GeodeContinuousQueriesHealthIndicator(continuousQueryListenerContainer);
	}

	@Bean("GeodePoolsHealthIndicator")
	GeodePoolsHealthIndicator poolsHealthIndicator(GemFireCache gemfireCache) {
		return new GeodePoolsHealthIndicator(gemfireCache);
	}

	public static final class ClientCacheCondition implements Condition {

		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

			Cache peerCache = CacheUtils.getCache();

			ClientCache clientCache = CacheUtils.getClientCache();

			return clientCache != null || peerCache == null;
		}
	}
}
