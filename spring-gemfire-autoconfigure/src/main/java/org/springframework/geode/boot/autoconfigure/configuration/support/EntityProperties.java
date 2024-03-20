/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.configuration.support;

import java.util.Properties;

import org.apache.geode.cache.Region;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Spring Boot {@link ConfigurationProperties} used to configure the application base {@link Package}
 * containing the application entity classes.  The entity classes are then used to create and configure
 * Apache Geode {@link Region Regions}.
 *
 * The configuration {@link Properties} are based on well-known, documented Spring Data for Apache Geode (SDG)
 * {@link Properties}.
 *
 * @author John Blum
 * @see Properties
 * @see Region
 * @see ConfigurationProperties
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class EntityProperties {

	private String basePackages;

	public String getBasePackages() {
		return this.basePackages;
	}

	public void setBasePackages(String basePackages) {
		this.basePackages = basePackages;
	}
}
