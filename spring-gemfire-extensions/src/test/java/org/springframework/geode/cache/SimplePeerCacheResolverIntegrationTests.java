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

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;

/**
 * Integration Tests for {@link SimpleCacheResolver} using an Apache Geode {@literal peer} {@link Cache}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Cache
 * @see org.apache.geode.cache.CacheFactory
 * @see org.springframework.geode.cache.SimpleCacheResolver
 * @since 1.3.0
 */
public class SimplePeerCacheResolverIntegrationTests {

	private static Cache peerCache;

	@BeforeClass
	public static void createPeerCache() {
		peerCache = new CacheFactory().create();
		assertThat(peerCache).isNotNull();
	}

	@AfterClass
	public static void destroyPeerCache() {
		Optional.ofNullable(peerCache).ifPresent(Cache::close);
	}

	@Test
	public void resolveReturnsPeerCache() {
		assertThat(SimpleCacheResolver.getInstance().resolve().orElse(null)).isSameAs(peerCache);
	}

	@Test
	public void resolveClientCacheReturnsEmptyOptional() {
		assertThat(SimpleCacheResolver.getInstance().resolveClientCache().orElse(null)).isNull();
	}

	@Test
	public void resolvePeerCacheReturnsPeerCache() {
		assertThat(SimpleCacheResolver.getInstance().resolvePeerCache().orElse(null)).isSameAs(peerCache);
	}

	@Test
	public void requireReturnsPeerCache() {
		assertThat(SimpleCacheResolver.getInstance().<Cache>require()).isSameAs(peerCache);
	}
}
