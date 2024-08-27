/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.configuration.support;

import java.util.Properties;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionShortcut;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Spring Boot {@link ConfigurationProperties} used to configure the {@link DataPolicy} of all {@link Region Regions}
 * in an Apache Geode cluster.
 *
 * The configuration {@link Properties} are based on well-known, documented Spring Data for Apache Geode (SDG)
 * {@link Properties}.
 *
 * @author John Blum
 * @see java.util.Properties
 * @see org.apache.geode.cache.Region
 * @see org.springframework.boot.context.properties.ConfigurationProperties
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class ClusterProperties {

	private final RegionProperties regionProperties = new RegionProperties();

	public RegionProperties getRegion() {
		return this.regionProperties;
	}

	public static class RegionProperties {

		private RegionShortcut peerRegionType;

		public RegionShortcut getType() {
			return this.peerRegionType;
		}

		public void setType(RegionShortcut peerRegionType) {
			this.peerRegionType = peerRegionType;
		}
	}
}
