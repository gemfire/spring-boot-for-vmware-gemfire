/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.core.io;

import org.springframework.core.io.Resource;

/**
 * A {@link RuntimeException} indicating that a {@link Resource} was not properly handled during normal processing.
 *
 * @author John Blum
 * @see RuntimeException
 * @see Resource
 * @since 1.3.1
 */
@SuppressWarnings("unused")
public class UnhandledResourceException extends RuntimeException {

	/**
	 * Constructs a new instance of {@link UnhandledResourceException} with no {@link String message}
	 * or no known {@link Throwable cause}.
	 */
	public UnhandledResourceException() { }

	/**
	 * Constructs a new instance of {@link UnhandledResourceException} initialized with the given {@link String message}
	 * to describe the error.
	 *
	 * @param message {@link String} describing the {@link RuntimeException}.
	 */
	public UnhandledResourceException(String message) {
		super(message);
	}

	/**
	 * Constructs a new instance of {@link UnhandledResourceException} initialized with the given {@link Throwable}
	 * signifying the underlying cause of this {@link RuntimeException}.
	 *
	 * @param cause {@link Throwable} signifying the underlying cause of this {@link RuntimeException}.
	 * @see Throwable
	 */
	public UnhandledResourceException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new instance of {@link UnhandledResourceException} initialized with the given {@link String message}
	 * describing the error along with a {@link Throwable} signifying the underlying cause of this
	 * {@link RuntimeException}.
	 *
	 * @param message {@link String} describing the {@link RuntimeException}.
	 * @param cause {@link Throwable} signifying the underlying cause of this {@link RuntimeException}.
	 * @see Throwable
	 */
	public UnhandledResourceException(String message, Throwable cause) {
		super(message, cause);
	}
}
