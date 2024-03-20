/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.geode.query.cq.event;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.geode.cache.query.CqEvent;

import org.springframework.data.gemfire.listener.annotation.ContinuousQuery;

/**
 * The {@link TemperatureReadingsContinuousQueriesHandler} class is a POJO containing Apache Geode Continuous Query (CQ)
 * definitions.
 *
 * @author John Blum
 * @see org.apache.geode.cache.query.CqEvent
 * @see org.springframework.data.gemfire.listener.annotation.ContinuousQuery
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public final class TemperatureReadingsContinuousQueriesHandler {

	private final AtomicInteger temperatureReadingsCounter = new AtomicInteger(0);

	private final List<TemperatureReading> boilingTemperatureReadings = new CopyOnWriteArrayList<>();
	private final List<TemperatureReading> freezingTemperatureReadings = new CopyOnWriteArrayList<>();

	public List<TemperatureReading> getBoilingTemperatureReadings() {
		return Collections.unmodifiableList(this.boilingTemperatureReadings);
	}

	public List<Integer> getBoilingTemperatures() {

		return getBoilingTemperatureReadings().stream()
			.map(TemperatureReading::getTemperature)
			.collect(Collectors.toList());
	}

	public List<TemperatureReading> getFreezingTemperatureReadings() {
		return Collections.unmodifiableList(this.freezingTemperatureReadings);
	}

	public List<Integer> getFreezingTemperatures() {

		return getFreezingTemperatureReadings().stream()
			.map(TemperatureReading::getTemperature)
			.collect(Collectors.toList());
	}

	public int getTemperatureReadingCount() {
		return this.temperatureReadingsCounter.get();
	}

	@ContinuousQuery(name = "BoilingTemperatures",
		query = "SELECT * FROM /TemperatureReadings r WHERE r.temperature >= 212")
	public void boilingTemperatures(CqEvent event) {

		TemperatureReading temperatureReading = (TemperatureReading) event.getNewValue();

		this.boilingTemperatureReadings.add(temperatureReading);
		this.temperatureReadingsCounter.incrementAndGet();
	}

	@ContinuousQuery(name = "FreezingTemperatures",
		query = "SELECT * FROM /TemperatureReadings r WHERE r.temperature <= 32")
	public void freezingTemperatures(CqEvent event) {

		TemperatureReading temperatureReading = (TemperatureReading) event.getNewValue();

		this.freezingTemperatureReadings.add(temperatureReading);
		this.temperatureReadingsCounter.incrementAndGet();
	}
}
