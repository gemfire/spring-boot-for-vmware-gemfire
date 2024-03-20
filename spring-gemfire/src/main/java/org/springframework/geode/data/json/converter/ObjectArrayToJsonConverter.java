/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.data.json.converter;

import java.util.Arrays;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * A Spring {@link Converter} interface extension defining a contract to convert
 * an {@link Iterable} or an array of {@link Object Objects} into {@link String JSON}.
 *
 * @author John Blum
 * @see FunctionalInterface
 * @see Iterable
 * @see Object
 * @see String
 * @see Converter
 * @since 1.3.0
 */
@FunctionalInterface
public interface ObjectArrayToJsonConverter extends Converter<Iterable<?>, String> {

	/**
	 * Converts the given array of {@link Object Objects} into {@link String JSON}.
	 *
	 * @param array array of {@link Object Objects} to convert into {@link String JSON}.
	 * @return {@link String JSON} generated from the given array of {@link Object Objects}.
	 * @see #convert(Object)
	 */
	default @NonNull String convert(@Nullable Object... array) {
		return convert(Arrays.asList(ArrayUtils.nullSafeArray(array, Object.class)));
	}
}
