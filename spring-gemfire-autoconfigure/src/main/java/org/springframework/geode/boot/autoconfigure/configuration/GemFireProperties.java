/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.configuration;

import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.geode.boot.autoconfigure.configuration.support.CacheProperties;
import org.springframework.geode.boot.autoconfigure.configuration.support.ClusterProperties;
import org.springframework.geode.boot.autoconfigure.configuration.support.DiskStoreProperties;
import org.springframework.geode.boot.autoconfigure.configuration.support.EntityProperties;
import org.springframework.geode.boot.autoconfigure.configuration.support.LocatorProperties;
import org.springframework.geode.boot.autoconfigure.configuration.support.LoggingProperties;
import org.springframework.geode.boot.autoconfigure.configuration.support.ManagementProperties;
import org.springframework.geode.boot.autoconfigure.configuration.support.ManagerProperties;
import org.springframework.geode.boot.autoconfigure.configuration.support.PdxProperties;
import org.springframework.geode.boot.autoconfigure.configuration.support.PoolProperties;
import org.springframework.geode.boot.autoconfigure.configuration.support.SecurityProperties;
import org.springframework.geode.boot.autoconfigure.configuration.support.ServiceProperties;

/**
 * Spring Boot {@link ConfigurationProperties} for well-known, documented Spring Data for Apache Geode (SDG)
 * {@link Properties}.
 *
 * This class assists the application developer in the auto-completion / content-assist of the well-known, documented
 * SDG {@link Properties}.
 *
 * @author John Blum
 * @see Properties
 * @see ConfigurationProperties
 * @see NestedConfigurationProperty
 * @see CacheProperties
 * @see ClusterProperties
 * @see DiskStoreProperties
 * @see EntityProperties
 * @see LocatorProperties
 * @see LoggingProperties
 * @see ManagementProperties
 * @see ManagerProperties
 * @see PdxProperties
 * @see PoolProperties
 * @see SecurityProperties
 * @see ServiceProperties
 * @since 1.0.0
 */
@SuppressWarnings("unused")
@ConfigurationProperties(prefix = "spring.data.gemfire")
public class GemFireProperties {

	private static final boolean DEFAULT_USE_BEAN_FACTORY_LOCATOR = false;

	private boolean useBeanFactoryLocator = DEFAULT_USE_BEAN_FACTORY_LOCATOR;

	@NestedConfigurationProperty
	private final CacheProperties cache = new CacheProperties();

	@NestedConfigurationProperty
	private final ClusterProperties cluster = new ClusterProperties();

	@NestedConfigurationProperty
	private final DiskStoreProperties disk = new DiskStoreProperties();

	@NestedConfigurationProperty
	private final EntityProperties entities = new EntityProperties();

	@NestedConfigurationProperty
	private final LocatorProperties locator = new LocatorProperties();

	@NestedConfigurationProperty
	private final LoggingProperties logging = new LoggingProperties();

	@NestedConfigurationProperty
	private final ManagementProperties management = new ManagementProperties();

	@NestedConfigurationProperty
	private final ManagerProperties manager = new ManagerProperties();

	@NestedConfigurationProperty
	private final PdxProperties pdx = new PdxProperties();

	@NestedConfigurationProperty
	private final PoolProperties pool = new PoolProperties();

	@NestedConfigurationProperty
	private final SecurityProperties security = new SecurityProperties();

	@NestedConfigurationProperty
	private final ServiceProperties service = new ServiceProperties();

	private String name;

	private String[] locators;

	public CacheProperties getCache() {
		return this.cache;
	}

	public ClusterProperties getCluster() {
		return this.cluster;
	}

	public DiskStoreProperties getDisk() {
		return this.disk;
	}

	public EntityProperties getEntities() {
		return this.entities;
	}

	public LocatorProperties getLocator() {
		return this.locator;
	}

	public String[] getLocators() {
		return this.locators;
	}

	public void setLocators(String[] locators) {
		this.locators = locators;
	}

	public LoggingProperties getLogging() {
		return this.logging;
	}

	public ManagementProperties getManagement() {
		return this.management;
	}

	public ManagerProperties getManager() {
		return this.manager;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PdxProperties getPdx() {
		return this.pdx;
	}

	public PoolProperties getPool() {
		return this.pool;
	}

	public SecurityProperties getSecurity() {
		return this.security;
	}

	public ServiceProperties getService() {
		return this.service;
	}

	public boolean isUseBeanFactoryLocator() {
		return this.useBeanFactoryLocator;
	}

	public void setUseBeanFactoryLocator(boolean useBeanFactoryLocator) {
		this.useBeanFactoryLocator = useBeanFactoryLocator;
	}
}
