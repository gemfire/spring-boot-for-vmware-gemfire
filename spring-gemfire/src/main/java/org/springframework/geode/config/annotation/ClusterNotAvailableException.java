/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.config.annotation;

/**
 * The {@link ClusterNotAvailableException} is a {@link RuntimeException} indicating that no Apache Geode cluster
 * was provisioned and available to service Apache Geode {@link org.apache.geode.cache.client.ClientCache} applications.
 *
 * @author John Blum
 * @see RuntimeException
 * @see org.apache.geode.cache.client.ClientCache
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class ClusterNotAvailableException extends RuntimeException {

	/**
	 * Constructs a new uninitialized instance of {@link ClusterNotAvailableException}.
	 */
	public ClusterNotAvailableException() { }

	/**
	 * Constructs a new instance of {@link ClusterNotAvailableException} initialized with
	 * the given {@link String message} describing the exception.
	 *
	 * @param message {@link String} containing a description of the exception.
	 */
	public ClusterNotAvailableException(String message) {
		super(message);
	}

	/**
	 * Constructs a new instance of {@link ClusterNotAvailableException} initialized with
	 * the given {@link Throwable} as the cause of this exception.
	 *
	 * @param cause {@link Throwable} indicating the cause of this exception.
	 */
	public ClusterNotAvailableException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new instance of {@link ClusterNotAvailableException} initialized with
	 * the given {@link String message} describing the exception along with the given {@link Throwable}
	 * as the cause of this exception.
	 *
	 * @param message {@link String} containing a description of the exception.
	 * @param cause {@link Throwable} indicating the cause of this exception.
	 */
	public ClusterNotAvailableException(String message, Throwable cause) {
		super(message, cause);
	}
}
