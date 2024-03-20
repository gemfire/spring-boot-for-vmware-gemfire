/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.cache.support;

import org.apache.geode.cache.CacheWriter;
import org.apache.geode.cache.CacheWriterException;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.RegionEvent;

/**
 * Class supporting the implementation of Apache Geode {@link CacheWriter CacheWriters}.
 *
 * @author John Blum
 * @see CacheWriter
 * @since 1.1.0
 */
@SuppressWarnings("unused")
public interface CacheWriterSupport<K, V> extends CacheWriter<K, V> {

  @Override
  default void beforeCreate(EntryEvent<K, V> event) throws CacheWriterException { }

  @Override
  default void beforeUpdate(EntryEvent<K, V> event) throws CacheWriterException { }

  @Override
  default void beforeDestroy(EntryEvent<K, V> event) throws CacheWriterException { }

  @Override
  default void beforeRegionClear(RegionEvent<K, V> event) throws CacheWriterException { }

  @Override
  default void beforeRegionDestroy(RegionEvent<K, V> event) throws CacheWriterException { }

}
