/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.geode.cache;

import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.LoaderHelper;

import org.springframework.geode.cache.support.CacheLoaderSupport;

/**
 * The {@link EchoCacheLoader} class is an implementation of {@link CacheLoader} that echos the key as the value.
 *
 * @author John Blum
 * @see org.apache.geode.cache.CacheLoader
 * @see org.springframework.geode.cache.support.CacheLoaderSupport
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class EchoCacheLoader implements CacheLoaderSupport<String, String> {

	public static final EchoCacheLoader INSTANCE = new EchoCacheLoader();

	@Override
	public String load(LoaderHelper<String, String> helper) throws CacheLoaderException {
		return helper.getKey();
	}
}
