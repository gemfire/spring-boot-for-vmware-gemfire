/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.core.io;

import org.springframework.core.io.Resource;

/**
 * A Java {@link RuntimeException} indicating a problem accessing (e.g. reading/writing) the data
 * of the target {@link Resource}.
 *
 * @author John Blum
 * @see RuntimeException
 * @see Resource
 * @since 1.3.1
 */
@SuppressWarnings("unused")
public class ResourceDataAccessException extends RuntimeException {

	/**
	 * Constructs a new instance of {@link ResourceDataAccessException} with no {@link String message}
	 * or known {@link Throwable cause}.
	 */
	public ResourceDataAccessException() { }

	/**
	 * Constructs a new instance of {@link ResourceDataAccessException} initialized with the given {@link String message}
	 * to describe the error.
	 *
	 * @param message {@link String} describing the {@link RuntimeException}.
	 */
	public ResourceDataAccessException(String message) {
		super(message);
	}

	/**
	 * Constructs a new instance of {@link ResourceDataAccessException} initialized with the given {@link Throwable}
	 * signifying the underlying cause of this {@link RuntimeException}.
	 *
	 * @param cause {@link Throwable} signifying the underlying cause of this {@link RuntimeException}.
	 * @see Throwable
	 */
	public ResourceDataAccessException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new instance of {@link ResourceDataAccessException} initialized with the given {@link String message}
	 * describing the error along with a {@link Throwable} signifying the underlying cause of this
	 * {@link RuntimeException}.
	 *
	 * @param message {@link String} describing the {@link RuntimeException}.
	 * @param cause {@link Throwable} signifying the underlying cause of this {@link RuntimeException}.
	 * @see Throwable
	 */
	public ResourceDataAccessException(String message, Throwable cause) {
		super(message, cause);
	}
}
