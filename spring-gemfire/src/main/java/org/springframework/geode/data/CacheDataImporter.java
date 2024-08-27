/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.data;

import org.apache.geode.cache.Region;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * The {@link CacheDataImporter} interface is a {@link FunctionalInterface} defininig a contract for importing data
 * into a cache {@link Region}.
 *
 * @author John Blum
 * @see java.lang.FunctionalInterface
 * @see org.apache.geode.cache.Region
 * @see org.springframework.beans.factory.config.BeanPostProcessor
 * @since 1.3.0
 */
@FunctionalInterface
@SuppressWarnings("rawtypes")
public interface CacheDataImporter extends BeanPostProcessor {

	/**
	 * Imports data from an external data source into a given {@link Region} after initialization.
	 *
	 * @param bean {@link Object} bean to evaluate.
	 * @param beanName {@link String} containing the name of the bean.
	 * @throws BeansException if importing data into a {@link Region} fails!
	 * @see org.apache.geode.cache.Region
	 * @see #importInto(Region)
	 */
	@Nullable @Override
	default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

		if (bean instanceof Region) {
			bean = importInto((Region) bean);
		}

		return bean;
	}

	/**
	 * Imports data into the given {@link Region}.
	 *
	 * @param region {@link Region} to import data into.
	 * @return the given {@link Region}.
	 * @see org.apache.geode.cache.Region
	 */
	@NonNull Region importInto(@NonNull Region region);

}
