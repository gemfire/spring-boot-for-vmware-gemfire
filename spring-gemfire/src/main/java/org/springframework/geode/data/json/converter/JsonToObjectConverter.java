/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.data.json.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

/**
 * Spring {@link Converter} interface extension defining a contract to convert
 * from {@link String JSON} to an {@link Object} (POJO).
 *
 * @author John Blum
 * @see Object
 * @see String
 * @see Converter
 * @since 1.3.0
 */
public interface JsonToObjectConverter extends Converter<String, Object> {

	/**
	 * Converts the array of {@link Byte#TYPE bytes} containing JSON into an {@link Object} (POJO).
	 *
	 * @param json array of {@link Byte#TYPE bytes} containing JSON to convert into an {@link Object} (POJO);
	 * must not be {@literal null}.
	 * @return an {@link Object} (POJO) converted from the array of {@link Byte#TYPE bytes} containing JSON.
	 * @see #convert(Object)
	 */
	default @NonNull Object convert(@NonNull byte[] json) {
		return convert(new String(json));
	}
}
