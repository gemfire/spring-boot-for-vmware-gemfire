/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure;

import org.apache.geode.cache.client.ClientCache;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.EnableContinuousQueries;
import org.springframework.geode.boot.autoconfigure.support.EnableSubscriptionConfiguration;
import org.springframework.geode.config.annotation.ClusterAvailableConfiguration;

/**
 * Spring Boot {@link EnableAutoConfiguration auto-configuration} enabling Apache Geode's Continuous Query (CQ)
 * functionality in a {@link ClientCache} application.
 *
 * @author John Blum
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.boot.SpringBootConfiguration
 * @see org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Conditional
 * @see org.springframework.context.annotation.Import
 * @see org.springframework.data.gemfire.client.ClientCacheFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer
 * @see org.springframework.data.gemfire.config.annotation.EnableContinuousQueries
 * @see org.springframework.geode.boot.autoconfigure.support.EnableSubscriptionConfiguration
 * @see org.springframework.geode.config.annotation.ClusterAvailableConfiguration.AnyClusterAvailableCondition
 * @since 1.0.0
 */
@SpringBootConfiguration
@Conditional(ClusterAvailableConfiguration.AnyClusterAvailableCondition.class)
@ConditionalOnBean(ClientCacheFactoryBean.class)
@ConditionalOnMissingBean(name = "continuousQueryBeanPostProcessor",
	type = "org.springframework.data.gemfire.listener.ContinuousQueryListenerContainer")
@EnableContinuousQueries
@Import(EnableSubscriptionConfiguration.class)
@SuppressWarnings("unused")
public class ContinuousQueryAutoConfiguration {

}
