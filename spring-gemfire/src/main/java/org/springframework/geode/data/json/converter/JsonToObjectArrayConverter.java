/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.data.json.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

/**
 * Spring {@link Converter} interface extension defining a contract to convert {@link String JSON}
 * to an array of {@link Object Objects}.
 *
 * @author John Blum
 * @see Object
 * @see String
 * @see Converter
 * @since 1.3.0
 */
public interface JsonToObjectArrayConverter extends Converter<String, Object[]> {

	/**
	 * Converts the array of {@link Byte#TYPE bytes} containing JSON into an array of {@link Object Objects}.
	 *
	 * @param json array of {@link Byte#TYPE bytes} containing the JSON to convert; must not be {@literal null}.
	 * @return an array of {@link Object Objects} converted from the array of {@link Byte#TYPE bytes} containing JSON.
	 * @see #convert(Object)
	 */
	default @NonNull Object[] convert(@NonNull byte[] json) {
		return convert(new String(json));
	}
}
