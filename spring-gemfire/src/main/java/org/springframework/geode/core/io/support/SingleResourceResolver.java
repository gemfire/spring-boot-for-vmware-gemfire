/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.core.io.support;

import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.geode.core.io.ResourceResolver;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * {@link ResourceResolver} that returns a single (i.e. {@literal Singleton}) {@link Resource}
 * regardless of {@link String location}.
 *
 * @author John Blum
 * @see Resource
 * @see ResourceResolver
 * @since 1.3.1
 */
public class SingleResourceResolver implements ResourceResolver {

	@Nullable
	private final Resource resource;

	/**
	 * Constructs a new instance of {@link SingleResourceResolver} initialized with the given {@link Resource}.
	 *
	 * @param resource the {@literal single} {@link Resource} consistently resolved by this resolver.
	 * @see Resource
	 */
	public SingleResourceResolver(@Nullable Resource resource) {
		this.resource = resource;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Resource> resolve(@NonNull String location) {
		return Optional.ofNullable(this.resource);
	}
}
