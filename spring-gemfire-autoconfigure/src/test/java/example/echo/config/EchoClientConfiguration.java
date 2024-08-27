/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.echo.config;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.util.RegionUtils;

/**
 * The {@link EchoClientConfiguration} class is a Spring {@link Configuration} class used to configure
 * a {@link ClientCache} {@link Region} for echo messages.
 *
 * @author John Blum
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.data.gemfire.GemfireTemplate
 * @see org.springframework.data.gemfire.client.ClientRegionFactoryBean
 * @since 1.0.0
 */
@Configuration
@SuppressWarnings("unused")
public class EchoClientConfiguration {

	protected static final String REGION_NAME = "Echo";

	@Bean(REGION_NAME)
	public ClientRegionFactoryBean<String, String> echoRegion(GemFireCache gemfireCache) {

		ClientRegionFactoryBean<String, String> echoRegion = new ClientRegionFactoryBean<>();

		echoRegion.setCache(gemfireCache);
		echoRegion.setClose(false);
		echoRegion.setShortcut(ClientRegionShortcut.PROXY);

		return echoRegion;
	}

	@Bean
	public GemfireTemplate echoTemplate(GemFireCache gemfireCache) {
		return new GemfireTemplate(gemfireCache.getRegion(RegionUtils.toRegionPath(REGION_NAME)));
	}
}
