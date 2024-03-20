/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientCache;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.gemfire.config.annotation.EnablePdx;

/**
 * Spring Boot {@link EnableAutoConfiguration auto-configuration} enabling Apache Geode's PDX Serialization
 * functionality in a either a {@link Cache peer cache} or {@link ClientCache} application.
 *
 * @author John Blum
 * @see org.apache.geode.cache.Cache
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.boot.SpringBootConfiguration
 * @see org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * @see org.springframework.data.gemfire.config.annotation.EnablePdx
 * @since 1.0.0
 */
@SpringBootConfiguration
@ConditionalOnBean(GemFireCache.class)
@ConditionalOnMissingBean(
	name = { "clientCachePdxConfigurer", "peerCachePdxConfigurer" },
	type = "org.springframework.data.gemfire.config.support.PdxDiskStoreAwareBeanFactoryPostProcessor"
)
@EnablePdx
public class PdxSerializationAutoConfiguration {

}
