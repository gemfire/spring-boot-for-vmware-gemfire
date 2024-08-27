/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.data.json.converter;

import org.apache.geode.pdx.PdxInstance;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

/**
 * A Spring {@link Converter} interface extension defining a contract to convert
 * from {@link String JSON} to an array of {@link PdxInstance} objects.
 *
 * @author John Blum
 * @see java.lang.FunctionalInterface
 * @see java.lang.String
 * @see org.apache.geode.pdx.PdxInstance
 * @see org.springframework.core.convert.converter.Converter
 * @since 1.3.0
 */
@FunctionalInterface
public interface JsonToPdxArrayConverter extends Converter<String, PdxInstance[]> {

	/**
	 * Converts the array of {@link Byte#TYPE bytes} containing JSON into an array of {@link PdxInstance} objects.
	 *
	 * @param json array of {@link Byte#TYPE bytes} containing the JSON to convert; must not be {@literal null}.
	 * @return an array of {@link PdxInstance} objects converted from the array of {@link Byte#TYPE bytes}
	 * containing JSON.
	 * @see org.apache.geode.pdx.PdxInstance
	 * @see #convert(Object)
	 */
	default @NonNull PdxInstance[] convert(@NonNull byte[] json) {
		return convert(new String(json));
	}
}
