/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.geode.boot.autoconfigure.cq;

import example.geode.query.cq.event.TemperatureReading;

import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.LoaderHelper;
import org.apache.geode.cache.Region;

public class TemperatureReadingsCacheLoader implements CacheLoader<Long, TemperatureReading> {
	@Override
	public TemperatureReading load(LoaderHelper<Long, TemperatureReading> helper) throws CacheLoaderException {

		long key = helper.getKey();

		Region<Long, TemperatureReading> temperatureReadings = helper.getRegion();

		recordTemperature(temperatureReadings, ++key, 72);
		recordTemperature(temperatureReadings, ++key, 16);
		recordTemperature(temperatureReadings, ++key, 101);
		recordTemperature(temperatureReadings, ++key, 300);
		recordTemperature(temperatureReadings, ++key, -51);
		recordTemperature(temperatureReadings, ++key, 242);
		recordTemperature(temperatureReadings, ++key, 112);

		return TemperatureReading.of(99);
	}

	private void recordTemperature(Region<Long, TemperatureReading> temperatureReadings, long key, int temperature) {

		sleep(50);
		temperatureReadings.put(key, TemperatureReading.of(temperature));
	}

	private void sleep(long milliseconds) {

		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException ignore) {}
	}

	@Override
	public void close() {}
}
