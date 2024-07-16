/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.cache;

import java.util.function.Supplier;
import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.CacheRuntimeException;
import org.apache.geode.cache.LoaderHelper;
import org.springframework.data.repository.CrudRepository;
import org.springframework.geode.cache.support.RepositoryCacheLoaderWriterSupport;

/**
 * A {@link CacheLoader} implementation backed by a Spring Data {@link CrudRepository} used to load an entity
 * from an external data source.
 *
 * @author John BlumLoader
 * @see CrudRepository
 * @see org.springframework.geode.cache.support.CacheLoaderSupport
 * @since 1.1.0
 */
@SuppressWarnings("unused")
public class RepositoryCacheLoader<T, ID> extends RepositoryCacheLoaderWriterSupport<T, ID> {

  protected static final String CACHE_LOAD_EXCEPTION_MESSAGE = "Error while loading Entity [%s] with Repository [%s]";

  public RepositoryCacheLoader(CrudRepository<T, ID> repository) {
    super(repository);
  }

  @Override
  public T load(LoaderHelper<ID, T> helper) throws CacheLoaderException {

    try {
      return getRepository().findById(helper.getKey()).orElse(null);
    }
    catch (Exception cause) {
      throw newCacheRuntimeException(() -> String.format(CACHE_LOAD_EXCEPTION_MESSAGE,
          helper.getKey(), getRepository().getClass().getName()), cause);
    }
  }

  @Override
  protected CacheRuntimeException newCacheRuntimeException(Supplier<String> messageSupplier, Throwable cause) {
    return new CacheLoaderException(messageSupplier.get(), cause);
  }
}
