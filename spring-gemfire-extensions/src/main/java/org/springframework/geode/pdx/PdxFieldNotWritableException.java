/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.pdx;

import org.apache.geode.pdx.PdxInstance;

/**
 * A {@link RuntimeException} thrown to indicate that a PDX field of a {@link PdxInstance} is not writable.
 *
 * @author John Blum
 * @see RuntimeException
 * @see org.apache.geode.pdx.PdxInstance
 * @since 1.3.0
 */
@SuppressWarnings("unused")
public class PdxFieldNotWritableException extends RuntimeException {

	public PdxFieldNotWritableException() { }

	public PdxFieldNotWritableException(String message) {
		super(message);
	}

	public PdxFieldNotWritableException(Throwable cause) {
		super(cause);
	}

	public PdxFieldNotWritableException(String message, Throwable cause) {
		super(message, cause);
	}
}
