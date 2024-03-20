/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheWriter;
import org.apache.geode.cache.Region;

import org.springframework.data.gemfire.PeerRegionFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.RegionConfigurer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * A {@link RegionConfigurer} implementation used to enable Inline Caching on a designated {@link Region}.
 *
 * @author John Blum
 * @see Predicate
 * @see CacheLoader
 * @see CacheWriter
 * @see Region
 * @see RegionConfigurer
 * @see RepositoryCacheLoaderRegionConfigurer
 * @see RepositoryCacheWriterRegionConfigurer
 * @since 1.1.0
 */
public class InlineCachingRegionConfigurer<T, ID> implements RegionConfigurer {

	private final List<RegionConfigurer> regionConfigurers = new ArrayList<>();

	private final RegionConfigurer compositeRegionConfigurer = new RegionConfigurer() {

		@Override
		public void configure(String beanName, ClientRegionFactoryBean<?, ?> bean) {
			regionConfigurers.forEach(regionConfigurer -> regionConfigurer.configure(beanName, bean));
		}

		@Override
		public void configure(String beanName, PeerRegionFactoryBean<?, ?> bean) {
			regionConfigurers.forEach(regionConfigurer -> regionConfigurer.configure(beanName, bean));
		}
	};

	/**
	 * Constructs a new instance of {@link InlineCachingRegionConfigurer} initialized with
	 * the given {@link CrudRepository} used for Inline Caching and {@link Predicate} used to identify
	 * the target {@link Region} on which the {@link CacheLoader} and {@link CacheWriter} will be registered.
	 *
	 * @param repository Spring Data {@link CrudRepository} used for Inline Caching between a {@link Region}
	 * and external data source.
	 * @param regionBeanName {@link Predicate} identifying the target {@link Region} on which to enable Inline Caching.
	 * @throws IllegalArgumentException if {@link CrudRepository} is {@literal null}.
	 * @see CrudRepository
	 * @see Predicate
	 */
	public InlineCachingRegionConfigurer(@NonNull CrudRepository<T, ID> repository,
			@Nullable Predicate<String> regionBeanName) {

		Assert.notNull(repository, "CrudRepository is required");

		regionBeanName = regionBeanName != null ? regionBeanName : beanName -> false;

		this.regionConfigurers.add(newRepositoryCacheLoaderRegionConfigurer(repository, regionBeanName));
		this.regionConfigurers.add(newRepositoryCacheWriterRegionConfigurer(repository, regionBeanName));
	}

	/**
	 * Constructs a new instance of {@link RepositoryCacheLoaderRegionConfigurer} initialized with
	 * the given {@link CrudRepository} to load (read-through) {@link Region} values on cache misses
	 * and {@link Predicate} to identify the target {@link Region} on which to register the {@link CacheLoader}.
	 *
	 * @param repository {@link CrudRepository} used to load {@link Region} values on cache misses.
	 * @param regionBeanName {@link Predicate} used to identify the target {@link Region} on which
	 * to register the {@link CacheLoader}.
	 * @return a new {@link RepositoryCacheLoaderRegionConfigurer}.
	 * @see RepositoryCacheLoaderRegionConfigurer
	 * @see CrudRepository
	 * @see Predicate
	 */
	protected RepositoryCacheLoaderRegionConfigurer<T, ID> newRepositoryCacheLoaderRegionConfigurer(
			@NonNull CrudRepository<T, ID> repository, @Nullable Predicate<String> regionBeanName) {

		return new RepositoryCacheLoaderRegionConfigurer<>(repository, regionBeanName);
	}

	/**
	 * Constructs a new instance of {@link RepositoryCacheWriterRegionConfigurer} initialized with
	 * the given {@link CrudRepository} to write-through to an external data source and {@link Predicate}
	 * to identify the target {@link Region} on which to register the {@link CacheWriter}.
	 *
	 * @param repository {@link CrudRepository} used to write-through to the external data source.
	 * @param regionBeanName {@link Predicate} used to identify the target {@link Region} on which
	 * to register the {@link CacheWriter}.
	 * @return a new {@link RepositoryCacheWriterRegionConfigurer}.
	 * @see RepositoryCacheWriterRegionConfigurer
	 * @see CrudRepository
	 * @see Predicate
	 */
	protected RepositoryCacheWriterRegionConfigurer<T, ID> newRepositoryCacheWriterRegionConfigurer(
			@NonNull CrudRepository<T, ID> repository, @Nullable Predicate<String> regionBeanName) {

		return new RepositoryCacheWriterRegionConfigurer<>(repository, regionBeanName);
	}

	@Override
	public void configure(String beanName, ClientRegionFactoryBean<?, ?> bean) {
		this.compositeRegionConfigurer.configure(beanName, bean);
	}

	@Override
	public void configure(String beanName, PeerRegionFactoryBean<?, ?> bean) {
		this.compositeRegionConfigurer.configure(beanName, bean);
	}
}
