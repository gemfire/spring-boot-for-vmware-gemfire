/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.cache.support;

import org.apache.geode.cache.CacheLoader;

/**
 * The {@link CacheLoaderSupport} interface is an extension of {@link CacheLoader} and a {@link FunctionalInterface}
 * useful in Lambda expressions.
 *
 * @author John Blum
 * @see FunctionalInterface
 * @see CacheLoader
 * @since 1.0.0
 */
@FunctionalInterface
public interface CacheLoaderSupport<K, V> extends CacheLoader<K, V> {

	/**
	 * Closes any resources opened and used by this {@link CacheLoader}.
	 *
	 * @see CacheLoader#close()
	 */
	@Override
	default void close() {}

}
