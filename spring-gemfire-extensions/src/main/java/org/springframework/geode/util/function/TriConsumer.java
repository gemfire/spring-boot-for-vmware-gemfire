/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.util.function;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * {@link Consumer} like interface accepting 3 arguments.
 *
 * @author John Blum
 * @see java.util.function.Consumer
 * @since 1.3.0
 */
@FunctionalInterface
public interface TriConsumer<T, U, V> {

	/**
	 * Performs a given operation on the 3 arguments.
	 *
	 * @param t first {@link Object argument}.
	 * @param u second {@link Object argument}.
	 * @param v third {@link Object argument}.
	 */
	void accept(T t, U u, V v);

	/**
	 * Composes this {@link TriConsumer} with the given {@link TriConsumer} after this {@link TriConsumer}.
	 *
	 * @param after {@link TriConsumer} to composed with this {@link TriConsumer}; must not be {@literal null}.
	 * @return a new {@link TriConsumer} with the given {@link TriConsumer} composed after this {@link TriConsumer}.
	 * @throws NullPointerException if {@link TriConsumer} is {@literal null}.
	 */
	default TriConsumer<T, U, V> andThen(TriConsumer<T, U, V> after) {

		Objects.requireNonNull(after);

		return (t, u, v) -> {
			accept(t, u, v);
			after.accept(t, u, v);
		};
	}
}
