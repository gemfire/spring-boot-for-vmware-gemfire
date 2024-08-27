/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.data.json.converter;

import org.springframework.core.convert.converter.Converter;

/**
 * A Spring {@link Converter} interface extension defining a contract to convert
 * from an {@link Object} into a {@link String JSON}.
 *
 * @author John Blum
 * @see java.lang.FunctionalInterface
 * @see java.lang.Object
 * @see java.lang.String
 * @see org.springframework.core.convert.converter.Converter
 * @since 1.3.0
 */
@FunctionalInterface
public interface ObjectToJsonConverter extends Converter<Object, String> {

}
