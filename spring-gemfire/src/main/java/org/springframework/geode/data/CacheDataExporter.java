/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.data;

import org.apache.geode.cache.Region;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.data.gemfire.ResolvableRegionFactoryBean;
import org.springframework.lang.NonNull;

/**
 * The {@link CacheDataExporter} interface is a {@link FunctionalInterface} defining a contract for exporting data
 * from a cache {@link Region}.
 *
 * @author John Blum
 * @see FunctionalInterface
 * @see Region
 * @see DestructionAwareBeanPostProcessor
 * @see ResolvableRegionFactoryBean
 * @since 1.3.0
 */
@FunctionalInterface
@SuppressWarnings("rawtypes")
public interface CacheDataExporter extends DestructionAwareBeanPostProcessor {

	/**
	 * Exports any data contained in a {@link Region} on destruction.
	 *
	 * @param bean {@link Object} bean to evaluate.
	 * @param beanName {@link String} containing the name of the bean.
	 * @throws BeansException if exporting data from a {@link Region} fails!
	 * @see Region
	 * @see #exportFrom(Region)
	 */
	@Override
	default void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {

		if (bean instanceof Region) {
			exportFrom((Region) bean);
		}
		else if (bean instanceof ResolvableRegionFactoryBean) {
			exportFrom(((ResolvableRegionFactoryBean) bean).getRegion());
		}
	}

	/**
	 * Exports data contained in the given {@link Region}.
	 *
	 * @param region {@link Region} to export data from.
	 * @return the given {@link Region}.
	 * @see Region
	 */
	@NonNull Region exportFrom(@NonNull Region region);

}
