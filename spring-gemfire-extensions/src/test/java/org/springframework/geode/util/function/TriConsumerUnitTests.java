/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.util.function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import org.junit.Test;
import org.mockito.InOrder;

/**
 * Unit Tests for {@link TriConsumer}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.springframework.geode.util.function.TriConsumer
 * @since 1.3.0
 */
public class TriConsumerUnitTests {

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void andThenComposesTriConsumers() {

		TriConsumer consumerOne = mock(TriConsumer.class);
		TriConsumer consumerTwo = mock(TriConsumer.class);

		doCallRealMethod().when(consumerOne).andThen(any(TriConsumer.class));

		TriConsumer composedConsumer = consumerOne.andThen(consumerTwo);

		assertThat(composedConsumer).isNotNull();
		assertThat(composedConsumer).isNotSameAs(consumerOne);
		assertThat(composedConsumer).isNotSameAs(consumerTwo);

		composedConsumer.accept("one", "two", "three");

		InOrder order = inOrder(consumerOne, consumerTwo);

		order.verify(consumerOne, times(1))
			.accept(eq("one"), eq("two"), eq("three"));
		order.verify(consumerTwo, times(1))
			.accept(eq("one"), eq("two"), eq("three"));
	}

	@Test(expected = NullPointerException.class)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void andThenWithNull() {

		TriConsumer consumer = mock(TriConsumer.class);

		doCallRealMethod().when(consumer).andThen(any());

		consumer.andThen(null);
	}
}
