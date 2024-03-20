/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.geode.core.env.support;

import org.springframework.util.Assert;

/**
 * The {@link Service} class is an Abstract Data Type (ADT) modeling a Pivotal CloudFoundry Service.
 *
 * @author John Blum
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class Service {

	/**
	 * Factory method to construct a new {@link Service} initialized with a {@link String name}.
	 *
	 * @param name {@link String} containing the name of the {@link Service}.
	 * @return a new {@link Service} configured with the given {@link String name}.
	 * @throws IllegalArgumentException if the {@link String name} is {@literal null} or empty.
	 * @see #Service(String)
	 */
	public static Service with(String name) {
		return new Service(name);
	}

	private final String name;

	/**
	 * Constructs a new {@link Service} initialized with a {@link String name}.
	 *
	 * @param name {@link String} containing the name of the {@link Service}.
	 * @throws IllegalArgumentException if the {@link String name} is {@literal null} or empty.
	 */
	Service(String name) {

		Assert.hasText(name, String.format("Service name [%s] is required", name));

		this.name = name;
	}

	/**
	 * Returns the {@link String name} of this {@link Service}.
	 *
	 * @return this {@link Service Service's} {@link String name}.
	 */
	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return getName();
	}
}
