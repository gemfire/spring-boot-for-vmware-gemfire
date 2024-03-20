/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.geode.core.io.support.ResourceUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Abstract base class providing functionality common to all {@link ResourceReader} implementations.
 *
 * @author John Blum
 * @see InputStream
 * @see Resource
 * @see ResourceReader
 * @since 1.3.1
 */
public abstract class AbstractResourceReader implements ResourceReader {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public @NonNull byte[] read(@NonNull Resource resource) {

		return Optional.ofNullable(resource)
			.filter(this::isAbleToHandle)
			.map(this::preProcess)
			.map(it -> {
				try (InputStream in = it.getInputStream()) {
					return doRead(in);
				}
				catch (IOException cause) {
					throw new ResourceReadException(String.format("Failed to read from Resource [%s]",
						it.getDescription()), cause);
				}
			})
			.orElseThrow(() -> new UnhandledResourceException(String.format("Unable to handle Resource [%s]",
				ResourceUtils.nullSafeGetDescription(resource))));
	}

	/**
	 * Determines whether this reader is able to handle and read from the target {@link Resource}.
	 *
	 * The default implementation determines that the {@link Resource} can be handled if the {@link Resource} handle
	 * is not {@literal null}.
	 *
	 * @param resource {@link Resource} to evaluate.
	 * @return a boolean value indicating whether this reader is able to handle and read from
	 * the target {@link Resource}.
	 * @see Resource
	 */
	@SuppressWarnings("unused")
	protected boolean isAbleToHandle(@Nullable Resource resource) {
		return resource != null;
	}

	/**
	 * Reads data from the target {@link Resource} (intentionally) by using the {@link InputStream} returned by
	 * {@link Resource#getInputStream()}.
	 *
	 * However, other algorithm/strategy implementations are free to read from the {@link Resource} as is appropriate
	 * for the given context (e.g. cloud environment).  In those cases, implementors should override
	 * the {@link #read(Resource)} method.
	 *
	 * @param resourceInputStream {@link InputStream} used to read data from the target {@link Resource}.
	 * @return a {@literal non-null} byte array containing the data from the target {@link Resource}.
	 * @throws IOException if an I/O error occurs while reading from the {@link Resource}.
	 * @see InputStream
	 * @see #read(Resource)
	 */
	protected abstract @NonNull byte[] doRead(@NonNull InputStream resourceInputStream) throws IOException;

	/**
	 * Pre-processes the target {@link Resource} before reading from the {@link Resource}.
	 *
	 * @param resource {@link Resource} to pre-process; never {@literal null}.
	 * @return the given, target {@link Resource}.
	 * @see Resource
	 */
	protected @NonNull Resource preProcess(@NonNull Resource resource) {
		return resource;
	}
}
