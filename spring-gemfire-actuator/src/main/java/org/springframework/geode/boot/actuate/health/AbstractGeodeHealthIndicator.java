/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.geode.boot.actuate.health;

import java.util.Optional;

import org.apache.geode.cache.client.ClientCache;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.util.Assert;

/**
 * The {@link AbstractGeodeHealthIndicator} class is an abstract base class encapsulating functionality common to all
 * Apache Geode {@link HealthIndicator} objects.
 *
 * @author John Blum
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.boot.actuate.health.AbstractHealthIndicator
 * @see org.springframework.boot.actuate.health.HealthIndicator
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class AbstractGeodeHealthIndicator extends AbstractHealthIndicator {

	protected static final String UNKNOWN = "unknown";

	private final ClientCache gemfireCache;

	/**
	 * Default constructor to construct an uninitialized instance of {@link AbstractGeodeHealthIndicator},
	 * which will not provide any health information.
	 */
	public AbstractGeodeHealthIndicator(String healthCheckedFailedMessage) {
		super(healthCheckedFailedMessage);
		this.gemfireCache = null;
	}

	/**
	 * Constructs an instance of the {@link AbstractGeodeHealthIndicator} initialized with a reference to
	 * the {@link ClientCache} instance.
	 *
	 * @param gemfireCache reference to the {@link ClientCache} instance used to collect health information.
	 * @throws IllegalArgumentException if {@link ClientCache} is {@literal null}.
	 * @see org.apache.geode.cache.client.ClientCache
	 */
	public AbstractGeodeHealthIndicator(ClientCache gemfireCache) {

		Assert.notNull(gemfireCache, "ClientCache must not be null");

		this.gemfireCache = gemfireCache;
	}

	/**
	 * Returns a reference to the {@link ClientCache} instance.
	 *
	 * @return a reference to the {@link ClientCache} instance.
	 * @see org.apache.geode.cache.client.ClientCache
	 */
	protected Optional<ClientCache> getGemFireCache() {
		return Optional.ofNullable(this.gemfireCache);
	}

	/**
	 * Determines the {@link String name} of the {@link Class} type safely by handling {@literal null}.
	 *
	 * @param type {@link Class} type to evaluate.
	 * @return the {@link String name} of the {@link Class} type.
	 * @see java.lang.Class#getName()
	 */
	protected String nullSafeClassName(Class<?> type) {
		return type != null ? type.getName() : "";
	}

	/**
	 * Converts a {@link Boolean} value into a {@literal yes} / {@literal no} {@link String}.
	 *
	 * @param value {@link Boolean} value to convert.
	 * @return a {@literal yes} / {@literal no} response for the given {@link Boolean} value.
	 */
	protected String toYesNoString(Boolean value) {
		return Boolean.TRUE.equals(value) ? "Yes" : "No";
	}
}
