/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.cache;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Unit Tests for {@link SimpleCacheResolver}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Cache
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.geode.cache.SimpleCacheResolver
 * @since 1.3.0
 */
public class SimpleCacheResolverUnitTests {

	@Test
	public void getInstanceReturnsASingleInstance() {

		SimpleCacheResolver cacheResolver = SimpleCacheResolver.getInstance();

		assertThat(cacheResolver).isNotNull();
		assertThat(cacheResolver).isSameAs(SimpleCacheResolver.getInstance());
	}

	@Test
	public void resolveWhenNoCacheIsPresentReturnsEmptyOptional() {
		assertThat(SimpleCacheResolver.getInstance().resolve().orElse(null)).isNull();
	}

	@Test
	public void resolveClientCacheWhenNoClientCacheIsPresentReturnsEmptyOptional() {
		assertThat(SimpleCacheResolver.getInstance().resolveClientCache().orElse(null)).isNull();
	}

	@Test
	public void resolvePeerCacheWhenNoPeerCacheIsPresentReturnsEmptyOptional() {
		assertThat(SimpleCacheResolver.getInstance().resolvePeerCache().orElse(null)).isNull();
	}

	@Test(expected = IllegalStateException.class)
	public void requireCacheWhenNoCacheIsPresentThrowsIllegalStateException() {

		try {
			SimpleCacheResolver.getInstance().require();
		}
		catch (IllegalStateException expected) {

			assertThat(expected).hasMessage("GemFireCache not found");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}
}
