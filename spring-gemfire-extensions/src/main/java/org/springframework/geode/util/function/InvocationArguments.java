/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.util.function;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;

/**
 * {@link Iterable} of {@link Method} invocation {@link Object arguments}.
 *
 * @author John Blum
 * @see java.lang.Iterable
 * @since 1.3.0
 */
public class InvocationArguments implements Iterable<Object> {

	public static InvocationArguments from(Object... arguments) {
		return new InvocationArguments(arguments);
	}

	private final Object[] arguments;

	/**
	 * Constructs a new instance of {@link InvocationArguments} initialized with the given array of
	 * {@link Object arguments}.
	 *
	 * @param arguments array of {@link Object arguments} indicating the values passed to the {@link Method} invocation
	 * parameters; may be {@literal null}.
	 */
	public InvocationArguments(Object[] arguments) {
		this.arguments = arguments != null ? arguments : new Object[0];
	}

	protected Object[] getArguments() {
		return this.arguments;
	}

	@SuppressWarnings("unchecked")
	protected <T> T getArgumentAt(int index) {
		return (T) getArguments()[index];
	}

	@Override
	public Iterator<Object> iterator() {

		return new Iterator<Object>() {

			int index = 0;

			@Override
			public boolean hasNext() {
				return this.index < InvocationArguments.this.getArguments().length;
			}

			@Override
			public Object next() {
				return InvocationArguments.this.getArguments()[this.index++];
			}
		};
	}

	public int size() {
		return this.arguments.length;
	}

	@Override
	public String toString() {
		return Arrays.toString(getArguments());
	}
}
