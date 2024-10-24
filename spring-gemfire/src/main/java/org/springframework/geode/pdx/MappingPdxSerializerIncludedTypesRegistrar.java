/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.pdx;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.pdx.PdxSerializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.gemfire.mapping.MappingPdxSerializer;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Spring {@link BeanPostProcessor} used to register additional {@link Class types} handled by
 * the SDG {@link MappingPdxSerializer}.
 *
 * @author John Blum
 * @see Class
 * @see Predicate
 * @see ClientCache
 * @see PdxSerializer
 * @see BeanPostProcessor
 * @see MappingPdxSerializer
 * @since 1.5.0
 */
@SuppressWarnings("unused")
public class MappingPdxSerializerIncludedTypesRegistrar implements BeanPostProcessor {

	/**
	 * Factory methods used to construct a new instance of {@link MappingPdxSerializerIncludedTypesRegistrar}
	 * initialized with given, required array of {@link Class} types that will be registered with
	 * SDG's {@link MappingPdxSerializer} in order to de/serialize the specified {@link Class} types as PDX.
	 *
	 * @param types array of {@link Class} types to be de/serialized as PDX using SDG's {@link MappingPdxSerializer};
	 * must not be {@literal null}.
	 * @return a new instance of {@link MappingPdxSerializerIncludedTypesRegistrar}
	 * @see #MappingPdxSerializerIncludedTypesRegistrar(Class[])
	 */
	public static @NonNull MappingPdxSerializerIncludedTypesRegistrar with(Class<?>... types) {
		return new MappingPdxSerializerIncludedTypesRegistrar(types);
	}

	private final Class<?>[] types;

	/**
	 * Constructs a new instance of {@link MappingPdxSerializerIncludedTypesRegistrar} initialized with given,
	 * required array of {@link Class} types that will be registered with SDG's {@link MappingPdxSerializer}
	 * in order to de/serialize the specified {@link Class} types as PDX.
	 *
	 * @param types array of {@link Class} types to be de/serialized as PDX using SDG's {@link MappingPdxSerializer};
	 * must not be {@literal null}.
	 * @see Class
	 */
	public MappingPdxSerializerIncludedTypesRegistrar(@NonNull Class<?>[] types) {

		this.types = Arrays.stream(ArrayUtils.nullSafeArray(types, Class.class))
			.filter(Objects::nonNull)
			.toArray(Class[]::new);
	}

	/**
	 * Gets the array of {@link Class} types to register with SDG's {@link MappingPdxSerializer} in order to
	 * de/serialize the {@link Class} types as PDX.
	 *
	 * @return the configured array of {@link Class} types to be registered with SDG's {@link MappingPdxSerializer}
	 * in order to de/serialize the {@link Class} types as PDX; never {@literal null}.
	 * @see Class
	 */
	protected @NonNull Class<?>[] getTypes() {
		return this.types;
	}

	/**
	 * Composes an {@link Optional} {@link Predicate} consisting of the configured array {@link Class} types
	 * used to match possible types de/serialized as PDX using SDG's {@link MappingPdxSerializer}.
	 *
	 * @return an {@link Optional} composite {@link Predicate} consisting of the configured
	 * array of {@link Class} types.
	 * @see Predicate
	 * @see Optional
	 * @see #getTypes()
	 */
	protected Optional<Predicate<Class<?>>> getCompositeIncludeTypeFilter() {

		Predicate<Class<?>> compositeIncludeTypeFilter = null;

		for (Class<?> type : getTypes()) {
			if (type != null) {
				compositeIncludeTypeFilter = compositeIncludeTypeFilter != null
					? compositeIncludeTypeFilter.or(newIncludeTypeFilter(type))
					: newIncludeTypeFilter(type);
			}
		}

		return Optional.ofNullable(compositeIncludeTypeFilter);
	}

	/**
	 * Null-safe method used to construct a new {@link Class type} include {@link Predicate filter}
	 * that can be registered with SDG's {@link MappingPdxSerializer}.
	 *
	 * The {@link Predicate} matches tested {@link Class types} that are
	 * {@link Class#isAssignableFrom(Class) assignable from} the given {@link Class type}.
	 *
	 * @param type {@link Class} used as the basis for matching in the {@link Predicate}.
	 * @return an (optional} {@link Predicate} from the given {@link Class type};
	 * returns {@literal null} if the given {@link Class type} is {@literal null}.
	 * @see Predicate
	 * @see Class
	 */
	protected @Nullable Predicate<Class<?>> newIncludeTypeFilter(@Nullable Class<?> type) {

		return type != null
			? testType -> Objects.nonNull(testType) && type.isAssignableFrom(testType)
			: null;
	}

	/**
	 * Registers the configured {@link Class} types with SDG's {@link MappingPdxSerializer} providing the bean
	 * to post process after initialization is a {@link ClientCache} instance and SDG's {@link MappingPdxSerializer}
	 * was configured as the cache's {@link PdxSerializer} used to de/serialize objects of the specified {@link Class}
	 * types.
	 *
	 * @param bean {@link Object bean} to evaluate.
	 * @param beanName {@link String} specifying the {@literal name} of the bean in the Spring container.
	 * @return the given {@link Object} bean.
	 * @throws BeansException if post processing of the bean fails.
	 */
	@Override
	public @Nullable Object postProcessAfterInitialization(@Nullable Object bean, @Nullable String beanName)
			throws BeansException {

		if (bean instanceof ClientCache) {

			ClientCache cache = (ClientCache) bean;

			PdxSerializer pdxSerializer = cache.getPdxSerializer();

			if (pdxSerializer instanceof MappingPdxSerializer) {

				MappingPdxSerializer mappingPdxSerializer = (MappingPdxSerializer) pdxSerializer;

				getCompositeIncludeTypeFilter().ifPresent(mappingPdxSerializer::setIncludeTypeFilters);
			}
		}

		return bean;
	}
}
