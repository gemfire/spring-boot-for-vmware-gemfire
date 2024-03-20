/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package example.geode.query.cq.event;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * The {@link TemperatureReading} class is an Abstract Data Type (ADT) modeling a temperature event,
 * tracking the recorded temperature, unit and timestamp of the event.
 *
 * @author John Blum
 * @see java.time.LocalDateTime
 * @see lombok
 * @see example.geode.query.cq.event.TemperatureUnit
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(of = { "temperature", "temperatureUnit" })
@RequiredArgsConstructor(staticName = "of")
public class TemperatureReading {

	@NonNull
	private Integer temperature;

	private LocalDateTime timestamp = LocalDateTime.now();

	private TemperatureUnit temperatureUnit = TemperatureUnit.defaultTemperatureUnit();

	public TemperatureReading at(LocalDateTime timestamp) {
		setTimestamp(timestamp);
		return this;
	}

	public TemperatureReading in(TemperatureUnit temperatureUnit) {
		setTemperatureUnit(temperatureUnit);
		return this;
	}

	@Override
	public String toString() {
		return String.format("%d %s", getTemperature(), getTemperatureUnit());
	}
}
