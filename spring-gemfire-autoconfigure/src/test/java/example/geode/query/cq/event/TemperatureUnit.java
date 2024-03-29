/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package example.geode.query.cq.event;

import java.util.Locale;
import java.util.Optional;

/**
 * The {@link TemperatureUnit} enum is an enumeration of different temperature units
 * as defined by International System of Units (SI).
 *
 * @author John Blum
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public enum TemperatureUnit {

	CELSIUS("°C"),
	FAHRENHEIT("°F"),
	KELVIN("K");

	public static TemperatureUnit defaultTemperatureUnit() {
		//Always use Fahrenheit
		return TemperatureUnit.FAHRENHEIT;
	}

	private final String symbol;

	TemperatureUnit(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbol() {
		return this.symbol;
	}

	@Override
	public String toString() {
		return getSymbol();
	}
}
