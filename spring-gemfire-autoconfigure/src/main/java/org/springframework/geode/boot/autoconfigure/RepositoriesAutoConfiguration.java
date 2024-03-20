/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientCache;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.repository.GemfireRepository;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.data.gemfire.repository.config.GemfireRepositoryConfigurationExtension;
import org.springframework.data.gemfire.repository.support.GemfireRepositoryFactoryBean;

/**
 * Spring Boot {@link EnableAutoConfiguration auto-configuration} for Spring Data for Apache Geode (SDG) Repositories.
 *
 * Activates when there is a bean of type {@link Cache} or {@link ClientCache} configured in the Spring context,
 * the Spring Data Geode {@link GemfireRepository} type is on the classpath, and no other existing
 * {@link GemfireRepository GemfireRepositories} are configured.
 *
 * Once in effect, the auto-configuration is the equivalent of enabling Geode Repositories using the
 * {@link EnableGemfireRepositories} annotation.
 *
 * @author John Blum
 * @see Cache
 * @see GemFireCache
 * @see ClientCache
 * @see SpringBootConfiguration
 * @see EnableAutoConfiguration
 * @see Import
 * @see GemfireRepository
 * @see EnableGemfireRepositories
 * @see GemfireRepositoryConfigurationExtension
 * @see GemfireRepositoryFactoryBean
 * @see ClientCacheAutoConfiguration
 * @see GemFireRepositoriesAutoConfigurationRegistrar
 * @since 1.0.0
 */
@SpringBootConfiguration
@AutoConfigureAfter(ClientCacheAutoConfiguration.class)
@ConditionalOnBean(GemFireCache.class)
@ConditionalOnClass(GemfireRepository.class)
@ConditionalOnMissingBean({ GemfireRepositoryConfigurationExtension.class, GemfireRepositoryFactoryBean.class })
@ConditionalOnProperty(prefix = "spring.data.gemfire.repositories", name = "enabled", havingValue = "true",
	matchIfMissing = true)
@Import(GemFireRepositoriesAutoConfigurationRegistrar.class)
public class RepositoriesAutoConfiguration {

}
