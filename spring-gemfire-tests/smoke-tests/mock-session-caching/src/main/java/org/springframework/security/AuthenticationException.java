/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.security;

/**
 * {@link RuntimeException} implementation indicating a Security Authentication error.
 *
 * @author John Blum
 * @see java.lang.Runtime
 * @since 1.4.0
 */
@SuppressWarnings("unused")
public class AuthenticationException extends RuntimeException {

	public AuthenticationException() { }

	public AuthenticationException(String message) {
		super(message);
	}

	public AuthenticationException(Throwable cause) {
		super(cause);
	}

	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}
}
