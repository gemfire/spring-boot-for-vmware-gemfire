/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.cache;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;

/**
 * Integration Tests for {@link SimpleCacheResolver} using an Apache Geode {@link ClientCache}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.client.ClientCacheFactory
 * @see org.springframework.geode.cache.SimpleCacheResolver
 * @since 1.3.0
 */
public class SimpleClientCacheResolverIntegrationTests {

	private static ClientCache clientCache;

	@BeforeClass
	public static void createClientCache() {
		clientCache = new ClientCacheFactory().create();
		assertThat(clientCache).isNotNull();
	}

	@AfterClass
	public static void destroyClientCache() {
		Optional.ofNullable(clientCache).ifPresent(ClientCache::close);
		clientCache = null;
	}

	@Test
	public void resolveReturnsClientCache() {
		assertThat(SimpleCacheResolver.getInstance().resolve().orElse(null)).isSameAs(clientCache);
	}

	@Test
	public void resolveClientCacheReturnsClientCache() {
		assertThat(SimpleCacheResolver.getInstance().resolveClientCache().orElse(null)).isSameAs(clientCache);
	}

	@Test
	public void resolvePeerCacheReturnsEmptyOptional() {
		assertThat(SimpleCacheResolver.getInstance().resolvePeerCache().orElse(null)).isNull();
	}

	@Test
	public void requireReturnsClientCache() {
		assertThat(SimpleCacheResolver.getInstance().<ClientCache>require()).isSameAs(clientCache);
	}
}
