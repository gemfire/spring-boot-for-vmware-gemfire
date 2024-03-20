/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.core.io;

import org.springframework.core.io.Resource;

/**
 * A {@link ResourceDataAccessException} and Java {@link RuntimeException} indicating a problem
 * while writing to the target {@link Resource}.
 *
 * @author John Blum
 * @see RuntimeException
 * @see Resource
 * @see ResourceDataAccessException
 * @since 1.3.1
 */
@SuppressWarnings("unused")
public class ResourceWriteException extends ResourceDataAccessException {

	/**
	 * Constructs a new instance of {@link ResourceWriteException} with no {@link String message}
	 * or known {@link Throwable cause}.
	 */
	public ResourceWriteException() { }

	/**
	 * Constructs a new instance of {@link ResourceWriteException} initialized with the given {@link String message}
	 * to describe the error.
	 *
	 * @param message {@link String} describing the {@link RuntimeException}.
	 */
	public ResourceWriteException(String message) {
		super(message);
	}

	/**
	 * Constructs a new instance of {@link ResourceWriteException} initialized with the given {@link Throwable}
	 * signifying the underlying cause of this {@link RuntimeException}.
	 *
	 * @param cause {@link Throwable} signifying the underlying cause of this {@link RuntimeException}.
	 * @see Throwable
	 */
	public ResourceWriteException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new instance of {@link ResourceWriteException} initialized with the given {@link String message}
	 * describing the error along with a {@link Throwable} signifying the underlying cause of this
	 * {@link RuntimeException}.
	 *
	 * @param message {@link String} describing the {@link RuntimeException}.
	 * @param cause {@link Throwable} signifying the underlying cause of this {@link RuntimeException}.
	 * @see Throwable
	 */
	public ResourceWriteException(String message, Throwable cause) {
		super(message, cause);
	}
}
