/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.core.io;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.geode.core.io.support.ResourceUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Abstract base class providing functionality common to all {@link ResourceWriter} implementations.
 *
 * @author John Blum
 * @see OutputStream
 * @see Resource
 * @see WritableResource
 * @see ResourceWriter
 * @since 1.3.1
 */
public abstract class AbstractResourceWriter implements ResourceWriter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(@NonNull Resource resource, byte[] data) {

		ResourceUtils.asWritableResource(resource)
			.filter(this::isAbleToHandle)
			.map(this::preProcess)
			.map(it -> {
				try (OutputStream out = it.getOutputStream()) {
					doWrite(out, data);
					return true;
				}
				catch (IOException cause) {
					throw new ResourceWriteException(String.format("Failed to write to Resource [%s]",
						it.getDescription()), cause);
				}
			})
			.orElseThrow(() -> new UnhandledResourceException(String.format("Unable to handle Resource [%s]",
				ResourceUtils.nullSafeGetDescription(resource))));
	}

	/**
	 * Determines whether this writer is able to handle and write to the target {@link Resource}.
	 *
	 * The default implementation determines that the {@link Resource} can be handled if the {@link Resource} handle
	 * is not {@literal null}.
	 *
	 * @param resource {@link Resource} to evaluate.
	 * @return a boolean value indicating whether this writer is able to handle and write to the target {@link Resource}.
	 * @see Resource
	 */
	@SuppressWarnings("unused")
	protected boolean isAbleToHandle(@Nullable Resource resource) {
		return resource != null;
	}

	/**
	 * Writes the given data to the target {@link Resource} (intentionally) by using the {@link OutputStream}
	 * returned by {@link WritableResource#getOutputStream()}.
	 *
	 * However, other algorithm/strategy implementations are free to write to the {@link Resource} as is appropriate
	 * for the given context (e.g. cloud environment).  In those cases, implementors should override
	 * the {@link #write(Resource, byte[])} method.
	 *
	 * @param resourceOutputStream {@link OutputStream} returned from {@link WritableResource#getOutputStream()}
	 * used to write the given data to the locations identified by the target {@link Resource}.
	 * @param data array of bytes containing the data to write.
	 * @throws IOException if an I/O error occurs while writing to the target {@link Resource}.
	 * @see OutputStream
	 */
	protected abstract void doWrite(OutputStream resourceOutputStream, byte[] data) throws IOException;

	/**
	 * Pre-processes the target {@link WritableResource} before writing to the {@link WritableResource}.
	 *
	 * @param resource {@link WritableResource} to pre-process; never {@literal null}.
	 * @return the given, target {@link WritableResource}.
	 * @see WritableResource
	 */
	protected @NonNull WritableResource preProcess(@NonNull WritableResource resource) {
		return resource;
	}
}
