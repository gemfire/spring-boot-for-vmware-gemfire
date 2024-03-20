/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.core.io.support;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;

import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Abstract utility class containing functionality to work with {@link Resource Resources}.
 *
 * @author John Blum
 * @see Resource
 * @see WritableResource
 * @since 1.3.1
 */
public abstract class ResourceUtils {

	/**
	 * Returns the {@link Resource} as a {@link WritableResource} if possible.
	 *
	 * This method makes a best effort to determine whether the target {@link Resource} is actually {@literal writable}.
	 * Even still, it may be possible that a write to the target {@link Resource} will fail.
	 *
	 * The {@link Resource} is {@literal writable} if the {@link Resource} is an instance of {@link WritableResource}
	 * and {@link WritableResource#isWritable()} returns {@literal true}.
	 *
	 * @param resource {@link Resource} to cast to a {@link WritableResource}.
	 * @return a {@link WritableResource} from the target {@link Resource} if possible; never {@literal null}.
	 * @throws IllegalStateException if the target {@link Resource} is not {@literal writable}.
	 * @see WritableResource
	 * @see Resource
	 */
	public static @NonNull WritableResource asStrictlyWritableResource(@Nullable Resource resource) {

		return Optional.ofNullable(resource)
			.filter(WritableResource.class::isInstance)
			.map(WritableResource.class::cast)
			.filter(WritableResource::isWritable)
			.orElseThrow(() -> newIllegalStateException("Resource [%s] is not writable",
				ResourceUtils.nullSafeGetDescription(resource)));
	}

	/**
	 * {@link Optional Optionally} return the {@link Resource} as a {@link WritableResource}.
	 *
	 * The {@link Resource} must be an instance of {@link WritableResource}.
	 *
	 * @param resource {@link Resource} to cast to a {@link WritableResource}.
	 * @return the {@link Resource} as a {@link WritableResource} if the {@link Resource}
	 * is an instance of {@link WritableResource}, otherwise returns {@link Optional#empty()}.
	 * @see WritableResource
	 * @see Resource
	 * @see Optional
	 */
	public static Optional<WritableResource> asWritableResource(@Nullable Resource resource) {

		return Optional.ofNullable(resource)
			.filter(WritableResource.class::isInstance)
			.map(WritableResource.class::cast);
	}

	/**
	 * Determines whether the given byte array is {@literal null} or {@literal empty}.
	 *
	 * @param array byte array to evaluate.
	 * @return a boolean value indicating whether the given byte array is {@literal null} or {@literal empty}.
	 */
	public static boolean isNotEmpty(@Nullable byte[] array) {
		return array != null && array.length > 0;
	}

	/**
	 * Null-safe operation to determine whether the given {@link Resource} is readable.
	 *
	 * @param resource {@link Resource} to evaluate.
	 * @return a boolean value indicating whether the given {@link Resource} is readable.
	 * @see Resource#isReadable()
	 * @see Resource
	 */
	public static boolean isReadable(@Nullable Resource resource) {
		return resource != null && resource.isReadable();
	}

	/**
	 * Null-safe operation to determine whether the given {@link Resource} is writable.
	 *
	 * @param resource {@link Resource} to evaluate.
	 * @return a boolean value indicating whether the given {@link Resource} is writable.
	 * @see WritableResource#isWritable()
	 * @see WritableResource
	 * @see Resource
	 */
	public static boolean isWritable(@Nullable Resource resource) {
		return resource instanceof WritableResource && ((WritableResource) resource).isWritable();
	}

	/**
	 * Null-safe method to get the {@link Resource#getDescription() description} of the given {@link Resource}.
	 *
	 * @param resource {@link Resource} to describe.
	 * @return a {@link Resource#getDescription() description} of the {@link Resource}, or {@literal null}
	 * if the {@link Resource} handle is {@literal null}.
	 * @see Resource
	 */
	public static @Nullable String nullSafeGetDescription(@Nullable Resource resource) {
		return resource != null ? resource.getDescription() : null;
	}
}
