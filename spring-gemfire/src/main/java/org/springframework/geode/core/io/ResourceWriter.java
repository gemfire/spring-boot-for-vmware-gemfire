/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.core.io;

import java.nio.ByteBuffer;

import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.lang.NonNull;

/**
 * Interface (contract) for writers to define the algorithm or strategy for writing data to a target {@link Resource},
 * such as by using the {@link WritableResource WritableResource's}
 * {@link WritableResource#getOutputStream()} OutputStream}.
 *
 * @author John Blum
 * @see Resource
 * @see WritableResource
 * @since 1.3.1
 */
@FunctionalInterface
@SuppressWarnings("unused")
public interface ResourceWriter {

	/**
	 * Writes data to the target {@link Resource} as defined by the algorithm/strategy of this writer.
	 *
	 * This method should throw an {@link UnhandledResourceException} if the algorithm or strategy used by this writer
	 * is not able to or capable of writing to the {@link Resource} at its location. This allows subsequent writers
	 * in a composition to possibly handle the {@link Resource}. Any other {@link Exception} thrown by this
	 * {@code write} method will break the chain of write calls in the composition.
	 *
	 * @param resource {@link Resource} to write data to.
	 * @param data array of bytes containing the data to write to the target {@link Resource}.
	 * @see Resource
	 */
	void write(@NonNull Resource resource, byte[] data);

	/**
	 * Writes data contained in the {@link ByteBuffer} to the target {@link Resource} as defined by
	 * the algorithm/strategy of this writer.
	 *
	 * This method should throw an {@link UnhandledResourceException} if the algorithm or strategy used by this writer
	 * is not able to or capable of writing to the {@link Resource} at its location. This allows subsequent writers
	 * in a composition to possibly handle the {@link Resource}. Any other {@link Exception} thrown by this
	 * {@code write} method will break the chain of write calls in the composition.
	 *
	 * @param resource {@link Resource} to write data to.
	 * @param data {@link ByteBuffer} containing the data to write to the target {@link Resource}.
	 * @see Resource
	 * @see ByteBuffer
	 * @see #write(Resource, byte[])
	 */
	default void write (@NonNull Resource resource, ByteBuffer data) {
		write(resource, data.array());
	}

	/**
	 * Composes this {@link ResourceWriter} with the given {@link ResourceWriter}
	 * using the {@literal Composite Software Design Pattern}.
	 *
	 * @param writer {@link ResourceWriter} to compose with this writer.
	 * @return a composite {@link ResourceWriter} composed of this {@link ResourceWriter}
	 * and the given {@link ResourceWriter}. If the given {@link ResourceWriter} is {@literal null},
	 * then this {@link ResourceWriter} is returned.
	 * @see <a href="https://en.wikipedia.org/wiki/Composite_pattern">Compsite Software Design Pattern</a>
	 * @see ResourceWriter
	 */
	default ResourceWriter thenWriteTo(ResourceWriter writer) {

		return writer == null ? this
			: (resource, data) -> {
				try {
					this.write(resource, data);
				}
				catch (UnhandledResourceException ignore) {
					writer.write(resource, data);
				}
			};
	}
}
