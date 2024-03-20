/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.geode.boot.actuate.health;

import java.util.Optional;

import org.apache.geode.cache.GemFireCache;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.util.Assert;

/**
 * The {@link AbstractGeodeHealthIndicator} class is an abstract base class encapsulating functionality common to all
 * Apache Geode {@link HealthIndicator} objects.
 *
 * @author John Blum
 * @see GemFireCache
 * @see AbstractHealthIndicator
 * @see HealthIndicator
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class AbstractGeodeHealthIndicator extends AbstractHealthIndicator {

	protected static final String UNKNOWN = "unknown";

	private final GemFireCache gemfireCache;

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
	 * the {@link GemFireCache} instance.
	 *
	 * @param gemfireCache reference to the {@link GemFireCache} instance used to collect health information.
	 * @throws IllegalArgumentException if {@link GemFireCache} is {@literal null}.
	 * @see GemFireCache
	 */
	public AbstractGeodeHealthIndicator(GemFireCache gemfireCache) {

		Assert.notNull(gemfireCache, "GemFireCache must not be null");

		this.gemfireCache = gemfireCache;
	}

	/**
	 * Returns a reference to the {@link GemFireCache} instance.
	 *
	 * @return a reference to the {@link GemFireCache} instance.
	 * @see GemFireCache
	 */
	protected Optional<GemFireCache> getGemFireCache() {
		return Optional.ofNullable(this.gemfireCache);
	}

	/**
	 * Determines the {@link String name} of the {@link Class} type safely by handling {@literal null}.
	 *
	 * @param type {@link Class} type to evaluate.
	 * @return the {@link String name} of the {@link Class} type.
	 * @see Class#getName()
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
