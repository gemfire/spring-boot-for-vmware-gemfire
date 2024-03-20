/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.distributed.Locator;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;

/**
 * Spring Boot {@link EnableAutoConfiguration auto-configuration} for bootstrapping an Apache Geode {@link ClientCache}
 * instance constructed, configured and initialized with Spring Data for Apache Geode.
 *
 * @author John Blum
 * @see GemFireCache
 * @see ClientCache
 * @see Locator
 * @see SpringBootConfiguration
 * @see EnableAutoConfiguration
 * @see ClientCacheFactoryBean
 * @see ClientCacheApplication
 * @since 1.0.0
 */
@SpringBootConfiguration
@ConditionalOnClass({ ClientCacheFactoryBean.class, ClientCache.class })
@ConditionalOnMissingBean({ GemFireCache.class, Locator.class })
@ClientCacheApplication
public class ClientCacheAutoConfiguration {

}
