/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.core.io;

import org.springframework.core.io.Resource;

/**
 * {@link RuntimeException} indication that a {@link Resource} could not be found.
 *
 * @author John Blum
 * @see RuntimeException
 * @see Resource
 * @since 1.3.1
 */
@SuppressWarnings("unused")
public class ResourceNotFoundException extends RuntimeException {

	/**
	 * Constructs a new instance of {@link ResourceNotFoundException} with no {@link String message}
	 * or known {@link Throwable cause}.
	 */
	public ResourceNotFoundException() { }

	/**
	 * Constructs a new instance of {@link ResourceNotFoundException} initialized with the given {@link String message}
	 * to describe the error.
	 *
	 * @param message {@link String} describing the {@link RuntimeException}.
	 */
	public ResourceNotFoundException(String message) {
		super(message);
	}

	/**
	 * Constructs a new instance of {@link ResourceNotFoundException} initialized with the given {@link Throwable}
	 * signifying the underlying cause of this {@link RuntimeException}.
	 *
	 * @param cause {@link Throwable} signifying the underlying cause of this {@link RuntimeException}.
	 * @see Throwable
	 */
	public ResourceNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new instance of {@link ResourceNotFoundException} initialized with the given {@link String message}
	 * describing the error along with a {@link Throwable} signifying the underlying cause of this
	 * {@link RuntimeException}.
	 *
	 * @param message {@link String} describing the {@link RuntimeException}.
	 * @param cause {@link Throwable} signifying the underlying cause of this {@link RuntimeException}.
	 * @see Throwable
	 */
	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
