/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
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
 * @see Cache
 * @see GemFireCache
 * @see ClientCache
 * @see SpringBootConfiguration
 * @see EnableAutoConfiguration
 * @see EnablePdx
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
