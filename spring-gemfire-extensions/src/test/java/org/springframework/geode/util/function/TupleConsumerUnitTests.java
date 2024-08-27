/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
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

import java.util.function.Consumer;

import org.junit.Test;
import org.mockito.InOrder;

/**
 * Unit Tests for {@link TupleConsumer}.
 *
 * @author John Blum
 * @see java.util.function.Consumer
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.springframework.geode.util.function.TupleConsumer
 * @since 1.3.0
 */
public class TupleConsumerUnitTests {

	@Test
	public void andThenComposesTupleConsumers() {

		TupleConsumer consumerOne = mock(TupleConsumer.class);
		TupleConsumer consumerTwo = mock(TupleConsumer.class);

		doCallRealMethod().when(consumerOne).andThen(any(TupleConsumer.class));

		Consumer<InvocationArguments> composedConsumer = consumerOne.andThen(consumerTwo);

		assertThat(composedConsumer).isNotNull();
		assertThat(composedConsumer).isNotSameAs(consumerOne);
		assertThat(composedConsumer).isNotSameAs(consumerTwo);

		InvocationArguments arguments = InvocationArguments.from("test", 1, true);

		composedConsumer.accept(arguments);

		InOrder order = inOrder(consumerOne, consumerTwo);

		order.verify(consumerOne, times(1)).accept(eq(arguments));
		order.verify(consumerTwo, times(1)).accept(eq(arguments));
	}

	@Test(expected = NullPointerException.class)
	public void andThenWithNull() {

		TupleConsumer consumer = mock(TupleConsumer.class);

		doCallRealMethod().when(consumer).andThen(any());

		consumer.andThen(null);
	}
}
