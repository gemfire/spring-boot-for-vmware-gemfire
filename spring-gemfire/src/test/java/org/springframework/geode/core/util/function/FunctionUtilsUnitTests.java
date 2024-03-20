/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.core.util.function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.Test;

/**
 * Unit Tests for {@link FunctionUtils}.
 *
 * @author John Blum
 * @see java.util.function.Consumer
 * @see java.util.function.Function
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @since 1.1.0
 */
public class FunctionUtilsUnitTests {

	@Test
	@SuppressWarnings("unchecked")
	public void callsConsumerReturnsNull() {

		Consumer<Object> mockConsumer = mock(Consumer.class);

		Function<Object, Object> function = FunctionUtils.toNullReturningFunction(mockConsumer);

		assertThat(function).isNotNull();
		assertThat(function.apply("test")).isNull();

		verify(mockConsumer, times(1)).accept(eq("test"));
	}
}
