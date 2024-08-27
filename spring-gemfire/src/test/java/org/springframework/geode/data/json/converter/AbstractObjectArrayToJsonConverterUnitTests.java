/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.data.json.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.Test;
import org.mockito.ArgumentMatchers;

import example.app.crm.model.Customer;

/**
 * Unit Tests for {@link AbstractObjectArrayToJsonConverter}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.springframework.geode.data.json.converter.AbstractObjectArrayToJsonConverter
 * @since 1.3.0
 */
public class AbstractObjectArrayToJsonConverterUnitTests {

	@Test
	public void convertIterable() {

		Customer jonDoe = Customer.newCustomer(1L, "Jon Doe");
		Customer janeDoe = Customer.newCustomer(2L, "Jane Doe");

		Iterable<Object> iterable = Arrays.asList(jonDoe, janeDoe);

		String jonDoeJson = "{ \"name\": \"Jon Doe\" }";
		String janeDoeJson = "{ \"name\": \"Jane Doe\" }";
		String json = String.format("[%s, %s]", jonDoeJson, janeDoeJson);

		ObjectToJsonConverter mockConverter = mock(ObjectToJsonConverter.class);

		doReturn(jonDoeJson).when(mockConverter).convert(eq(jonDoe));
		doReturn(janeDoeJson).when(mockConverter).convert(eq(janeDoe));

		AbstractObjectArrayToJsonConverter converter = mock(AbstractObjectArrayToJsonConverter.class);

		doCallRealMethod().when(converter).convert(ArgumentMatchers.<Iterable<Object>>any());
		doReturn(mockConverter).when(converter).getObjectToJsonConverter();

		assertThat(converter.convert(iterable)).isEqualTo(json);

		verify(converter, times(1)).getObjectToJsonConverter();
		verify(mockConverter, times(1)).convert(eq(jonDoe));
		verify(mockConverter, times(1)).convert(eq(janeDoe));
	}

	@Test
	public void convertEmptyIterable() {

		String json = "[]";

		AbstractObjectArrayToJsonConverter converter = mock(AbstractObjectArrayToJsonConverter.class);

		doCallRealMethod().when(converter).convert(ArgumentMatchers.<Iterable<Object>>any());

		assertThat(converter.convert(Collections.emptySet())).isEqualTo(json);

		verify(converter, times(1)).getObjectToJsonConverter();
	}

	@Test(expected = IllegalArgumentException.class)
	public void convertNullIterable() {

		AbstractObjectArrayToJsonConverter converter = mock(AbstractObjectArrayToJsonConverter.class);

		doCallRealMethod().when(converter).convert(ArgumentMatchers.<Iterable<Object>>any());

		try {
			converter.convert((Iterable<Object>) null);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("Iterable must not be null");
			assertThat(expected).hasNoCause();

			throw expected;
		}
		finally {
			verify(converter, never()).getObjectToJsonConverter();
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void convertMap() {

		String json = "[{ ... }, { ... }]";

		Map<?, ?> mockMap = mock(Map.class);

		AbstractObjectArrayToJsonConverter converter = mock(AbstractObjectArrayToJsonConverter.class);

		doCallRealMethod().when(converter).convert(any(Map.class));
		doReturn(json).when(converter).convert(any(Iterable.class));

		assertThat(converter.convert(mockMap)).isEqualTo(json);

		verify(converter, times(1)).convert(eq(Collections.emptyList()));
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void convertMapIsNullSafe() {

		String json = "[]";

		AbstractObjectArrayToJsonConverter converter = mock(AbstractObjectArrayToJsonConverter.class);

		doCallRealMethod().when(converter).convert(ArgumentMatchers.<Map>any());
		doReturn(json).when(converter).convert(any(Iterable.class));

		assertThat(converter.convert((Map) null)).isEqualTo(json);

		verify(converter, times(1)).convert(isA(Iterable.class));
	}
}
