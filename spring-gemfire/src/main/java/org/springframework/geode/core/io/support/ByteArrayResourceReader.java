/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.core.io.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.Resource;
import org.springframework.geode.core.io.AbstractResourceReader;
import org.springframework.lang.NonNull;

/**
 * A concrete {@link AbstractResourceReader} implementation that reads data from a target {@link Resource Resource's}
 * {@link Resource#getInputStream() InputStream} into a byte array.
 *
 * @author John Blum
 * @see InputStream
 * @see ByteArrayOutputStream
 * @see Resource
 * @see AbstractResourceReader
 * @since 1.3.1
 */
@SuppressWarnings("unused")
public class ByteArrayResourceReader extends AbstractResourceReader {

	protected static final int DEFAULT_BUFFER_SIZE = 32768;

	/**
	 * Returns the required {@link Integer#TYPE buffer size} used to capture data from the target {@link Resource}
	 * in chunks.
	 *
	 * Subclasses are encouraged to override this method as necessary to tune the buffer size.  By default, the
	 * buffer size is {@literal 32K} or {@literal 32768} bytes.
	 *
	 * @return the required {@link Integer#TYPE buffer size} to read from the {@link Resource} in chunks.
	 */
	protected int getBufferSize() {
		return DEFAULT_BUFFER_SIZE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected @NonNull byte[] doRead(@NonNull InputStream resourceInputStream) throws IOException {

		try (ByteArrayOutputStream out = new ByteArrayOutputStream(resourceInputStream.available())) {

			byte[] buffer = new byte[getBufferSize()];

			for (int bytesRead = resourceInputStream.read(buffer); bytesRead != -1; bytesRead = resourceInputStream.read(buffer)) {
				// using a buffer will trigger a flush() automatically in the OutputStream
				out.write(buffer, 0, bytesRead);
			}

			out.flush();

			return out.toByteArray();
		}
	}
}
