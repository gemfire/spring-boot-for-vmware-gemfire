/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.data.json.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.mockito.ArgumentMatchers;

import example.app.crm.model.Customer;

/**
 * Unit Tests for {@link ObjectArrayToJsonConverter}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.springframework.geode.data.json.converter.ObjectArrayToJsonConverter
 * @since 1.3.0
 */
public class ObjectArrayToJsonConverterUnitTests {

	@Test
	public void convertObjectArrayCallsConvertIterable() {

		String json = "[{ \"name\": \"Jon Doe\"}, { \"name\": \"Jane Doe\"}]";

		ObjectArrayToJsonConverter mockConverter = mock(ObjectArrayToJsonConverter.class);

		doCallRealMethod().when(mockConverter).convert(ArgumentMatchers.<Object[]>any());
		doReturn(json).when(mockConverter).convert(any(Iterable.class));

		Customer jonDoe = Customer.newCustomer(1L, "Jon Doe");
		Customer janeDoe = Customer.newCustomer(2L, "Jane Doe");

		assertThat(mockConverter.convert(jonDoe, janeDoe)).isEqualTo(json);

		verify(mockConverter, times(1)).convert(eq(Arrays.asList(jonDoe, janeDoe)));
	}

	@Test
	public void convertEmptyObjectArrayToEmptyJsonArray() {

		String json = "[]";

		ObjectArrayToJsonConverter mockConverter = mock(ObjectArrayToJsonConverter.class);

		doCallRealMethod().when(mockConverter).convert(ArgumentMatchers.<Object[]>any());
		doReturn(json).when(mockConverter).convert(any(Iterable.class));

		assertThat(mockConverter.convert()).isEqualTo(json);

		verify(mockConverter, times(1)).convert(eq(Collections.emptyList()));
	}

	@Test
	public void convertNullObjectArrayIsNullSafeAndReturnsEmptyJsonArray() {

		String json = "[]";

		ObjectArrayToJsonConverter mockConverter = mock(ObjectArrayToJsonConverter.class);

		doCallRealMethod().when(mockConverter).convert(ArgumentMatchers.<Object[]>any());
		doReturn(json).when(mockConverter).convert(any(Iterable.class));

		assertThat(mockConverter.convert((Object[]) null)).isEqualTo(json);

		verify(mockConverter, times(1)).convert(eq(Collections.emptyList()));
	}
}
