/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.data.json.converter;

import org.apache.geode.pdx.PdxInstance;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

/**
 * A Spring {@link Converter} interface extension defining a contract to convert
 * from {@link String JSON} to a {@link PdxInstance}.
 *
 * @author John Blum
 * @see FunctionalInterface
 * @see String
 * @see PdxInstance
 * @see Converter
 * @since 1.3.0
 */
@FunctionalInterface
public interface JsonToPdxConverter extends Converter<String, PdxInstance> {

	/**
	 * Converts the array of {@link Byte#TYPE bytes} containing JSON into a {@link PdxInstance}.
	 *
	 * @param json array of {@link Byte#TYPE bytes} containing JSON to convert into a {@link PdxInstance};
	 * must not be {@literal null}.
	 * @return a {@link PdxInstance} converted from the array of {@link Byte#TYPE bytes} containing JSON.
	 * @see #convert(Object)
	 */
	default @NonNull PdxInstance convert(@NonNull byte[] json) {
		return convert(new String(json));
	}
}
