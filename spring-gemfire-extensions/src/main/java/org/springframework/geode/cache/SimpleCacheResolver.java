/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.cache;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;

/**
 * The {@link SimpleCacheResolver} abstract class contains utility functions for resolving Apache Geode
 * {@link ClientCache} instances, such as a {@link ClientCache}.
 *
 * @author John Blum
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.CacheFactory
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.RegionService
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.client.ClientCacheFactory
 * @since 1.3.0
 */
@SuppressWarnings("unused")
public abstract class SimpleCacheResolver {

	private static final AtomicReference<SimpleCacheResolver> instance = new AtomicReference<>(null);

	/**
	 * Lazily constructs and gets an instance to the {@link SimpleCacheResolver}, as needed.
	 *
	 * @return an instance of the {@link SimpleCacheResolver}.
	 * @see #newSimpleCacheResolver()
	 */
	public static SimpleCacheResolver getInstance() {

		return instance.updateAndGet(cacheResolver -> cacheResolver != null
			? cacheResolver
			: newSimpleCacheResolver());
	}

	// TODO Consider resolving the SimpleCacheResolver instance using Java's ServiceProvider API.
	private static SimpleCacheResolver newSimpleCacheResolver() {
		return new SimpleCacheResolver() { };
	}

	/**
	 * 	The 1st {@code resolve():Optional<? extends ClientCache>} method signature avoids the cast
	 * 	  and the @SuppressWarnings("unchecked") annotation, but puts the burden on the caller.
	 * 	The 2nd {@code resolve():Optional<T extends ClientCache>} method signature requires a cast
	 * 	  and the @SuppressWarnings("unchecked") annotation, but avoids putting the burden on the caller.
	 */
	private static void testCallResolve() {
		Optional<ClientCache> clientCache = getInstance().resolve();
	}

	/**
	 * The resolution algorithm first tries to resolve an {@link Optional} {@link ClientCache} instance.
	 *
	 * If a {@link ClientCache}, then {@link Optional#empty()}
	 * is returned.  No {@link Throwable Exception} is thrown.
	 *
	 * @param <T> {@link Class subclass} of {@link ClientCache}.
	 * @return a {@link ClientCache} instance if present.
	 * @see org.apache.geode.cache.client.ClientCache
	 * @see Optional
	 * @see #resolveClientCache()
	 */
	//public static Optional<? extends ClientCache> resolve() {
	@SuppressWarnings("unchecked")
	public <T extends ClientCache> Optional<T> resolve() {

		Optional<ClientCache> clientCache = resolveClientCache();

		return (Optional<T>) (clientCache.isPresent() ? clientCache: Optional.empty());
	}

	/**
	 * Attempts to resolve an {@link Optional} {@link ClientCache} instance.
	 *
	 * @return an {@link Optional} {@link ClientCache} instance.
	 * @see org.apache.geode.cache.client.ClientCacheFactory#getAnyInstance()
	 * @see org.apache.geode.cache.client.ClientCache
	 * @see Optional
	 */
	public Optional<ClientCache> resolveClientCache() {

		try {
			return Optional.ofNullable(ClientCacheFactory.getAnyInstance());
		}
		catch (Throwable ignore) {
			return Optional.empty();
		}
	}

	/**
	 * Requires an instance of either a {@link ClientCache}.
	 *
	 * @param <T> {@link Class subclass} of {@link ClientCache} to resolve.
	 * @return an instance of either a {@link ClientCache}.
	 * @throws IllegalStateException if a cache instance cannot be resolved.
	 * @see org.apache.geode.cache.client.ClientCache
	 * @see org.apache.geode.cache.client.ClientCache
	 * @see org.apache.geode.cache.client.ClientCache
	 * @see #resolve()
	 */
	public <T extends ClientCache> T require() {
		return this.<T>resolve()
			.orElseThrow(() -> new IllegalStateException("ClientCache not found"));
	}
}
