/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.data.json.converter;

import org.springframework.core.convert.converter.Converter;

/**
 * A Spring {@link Converter} interface extension defining a contract to convert
 * from an {@link Object} into a {@link String JSON}.
 *
 * @author John Blum
 * @see FunctionalInterface
 * @see Object
 * @see String
 * @see Converter
 * @since 1.3.0
 */
@FunctionalInterface
public interface ObjectToJsonConverter extends Converter<Object, String> {

}
