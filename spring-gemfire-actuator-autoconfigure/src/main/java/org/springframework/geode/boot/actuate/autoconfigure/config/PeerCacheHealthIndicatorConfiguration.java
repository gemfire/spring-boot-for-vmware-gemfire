/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.actuate.autoconfigure.config;

import java.util.Optional;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.server.CacheServer;
import org.apache.geode.cache.server.ServerLoadProbe;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.gemfire.server.CacheServerFactoryBean;
import org.springframework.data.gemfire.util.CacheUtils;
import org.springframework.geode.boot.actuate.GeodeAsyncEventQueuesHealthIndicator;
import org.springframework.geode.boot.actuate.GeodeCacheServersHealthIndicator;
import org.springframework.geode.boot.actuate.GeodeGatewayReceiversHealthIndicator;
import org.springframework.geode.boot.actuate.GeodeGatewaySendersHealthIndicator;
import org.springframework.geode.boot.actuate.health.support.ActuatorServerLoadProbeWrapper;
import org.springframework.geode.core.util.ObjectUtils;
import org.springframework.lang.Nullable;

/**
 * Spring {@link Configuration} class declaring Spring beans for Apache Geode peer {@link Cache}
 * {@link HealthIndicator HealthIndicators}.
 *
 * @author John Blum
 * @see org.apache.geode.cache.Cache
 * @see org.apache.geode.cache.GemFireCache
 * @see org.springframework.beans.factory.config.BeanPostProcessor
 * @see org.springframework.boot.actuate.health.HealthIndicator
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.geode.boot.actuate.GeodeAsyncEventQueuesHealthIndicator
 * @see org.springframework.geode.boot.actuate.GeodeCacheServersHealthIndicator
 * @see org.springframework.geode.boot.actuate.GeodeGatewayReceiversHealthIndicator
 * @see org.springframework.geode.boot.actuate.GeodeGatewaySendersHealthIndicator
 * @since 1.0.0
 */
@Configuration
@Conditional(PeerCacheHealthIndicatorConfiguration.PeerCacheCondition.class)
@SuppressWarnings("unused")
public class PeerCacheHealthIndicatorConfiguration {

	@Bean("GeodeAsyncEventQueuesHealthIndicator")
	GeodeAsyncEventQueuesHealthIndicator asyncEventQueuesHealthIndicator(GemFireCache gemfireCache) {
		return new GeodeAsyncEventQueuesHealthIndicator(gemfireCache);
	}

	@Bean("GeodeCacheServersHealthIndicator")
	GeodeCacheServersHealthIndicator cacheServersHealthIndicator(GemFireCache gemfireCache) {
		return new GeodeCacheServersHealthIndicator(gemfireCache);
	}

	@Bean("GeodeGatewayReceiversHealthIndicator")
	GeodeGatewayReceiversHealthIndicator gatewayReceiversHealthIndicator(GemFireCache gemfireCache) {
		return new GeodeGatewayReceiversHealthIndicator(gemfireCache);
	}

	@Bean("GeodeGatewaySendersHealthIndicator")
	GeodeGatewaySendersHealthIndicator gatewaySendersHealthIndicator(GemFireCache gemfireCache) {
		return new GeodeGatewaySendersHealthIndicator(gemfireCache);
	}

	@Bean
	BeanPostProcessor cacheServerLoadProbeWrappingBeanPostProcessor() {

		return new BeanPostProcessor() {

			@Nullable @Override @SuppressWarnings("all")
			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

				if (bean instanceof CacheServerFactoryBean) {

					CacheServerFactoryBean cacheServerFactoryBean = (CacheServerFactoryBean) bean;

					ServerLoadProbe serverLoadProbe =
						ObjectUtils.<ServerLoadProbe>get(bean, "serverLoadProbe");

					if (serverLoadProbe != null) {
						cacheServerFactoryBean.setServerLoadProbe(wrap(serverLoadProbe));
					}
				}

				return bean;
			}

			@Nullable @Override
			public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

				if (bean instanceof CacheServer) {

					CacheServer cacheServer = (CacheServer) bean;

					Optional.ofNullable(cacheServer.getLoadProbe())
						.filter(it -> !(it instanceof ActuatorServerLoadProbeWrapper))
						.filter(it -> cacheServer.getLoadPollInterval() > 0)
						.filter(it -> !cacheServer.isRunning())
						.ifPresent(serverLoadProbe ->
							cacheServer.setLoadProbe(new ActuatorServerLoadProbeWrapper(serverLoadProbe)));
				}

				return bean;
			}

			private ServerLoadProbe wrap(ServerLoadProbe serverLoadProbe) {
				return new ActuatorServerLoadProbeWrapper(serverLoadProbe);
			}
		};
	}

	public static final class PeerCacheCondition implements Condition {

		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

			Cache peerCache = CacheUtils.getCache();

			ClientCache clientCache = CacheUtils.getClientCache();

			return peerCache != null || clientCache == null;
		}
	}
}
