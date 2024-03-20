/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.data.json.converter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

/**
 * Unit Tests for {@link JsonToObjectConverter}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.springframework.geode.data.json.converter.JsonToObjectConverter
 * @since 1.3.0
 */
public class JsonToObjectConverterUnitTests {

	@Test
	public void convertByteArrayCallsConvertJsonString() {

		JsonToObjectConverter converter = mock(JsonToObjectConverter.class);

		doCallRealMethod().when(converter).convert(any(byte[].class));

		String json = "{ \"name\": \"Jon Doe\"}";

		converter.convert(json.getBytes());

		verify(converter, times(1)).convert(eq(json));
	}
}
