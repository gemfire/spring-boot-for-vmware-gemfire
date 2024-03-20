/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.configuration.support;

import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Spring Boot {@link ConfigurationProperties} used to configure an embedded Apache Geode {@literal Manager}.
 *
 * A {@literal Manager} allows users to manage a cluster with tools like {@literal Gfsh} or {@literal Pulse}.
 *
 * The configuration {@link Properties} are based on well-known, documented Spring Data for Apache Geode (SDG)
 * {@link Properties}.
 *
 * @author John Blum
 * @see java.util.Properties
 * @see org.apache.geode.distributed.Locator
 * @see org.springframework.boot.context.properties.ConfigurationProperties
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class ManagerProperties {

	private static final boolean DEFAULT_START = false;

	private static final int DEFAULT_PORT = 1099;
	private static final int DEFAULT_UPDATE_RATE_IN_MILLISECONDS = 2000;

	private boolean start = DEFAULT_START;

	private int port = DEFAULT_PORT;
	private int updateRate = DEFAULT_UPDATE_RATE_IN_MILLISECONDS;

	private String accessFile;
	private String bindAddress;
	private String hostnameForClients;
	private String passwordFile;

	public String getAccessFile() {
		return this.accessFile;
	}

	public void setAccessFile(String accessFile) {
		this.accessFile = accessFile;
	}

	public String getBindAddress() {
		return this.bindAddress;
	}

	public void setBindAddress(String bindAddress) {
		this.bindAddress = bindAddress;
	}

	public String getHostnameForClients() {
		return this.hostnameForClients;
	}

	public void setHostnameForClients(String hostnameForClients) {
		this.hostnameForClients = hostnameForClients;
	}

	public String getPasswordFile() {
		return this.passwordFile;
	}

	public void setPasswordFile(String passwordFile) {
		this.passwordFile = passwordFile;
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isStart() {
		return this.start;
	}

	public void setStart(boolean start) {
		this.start = start;
	}

	public int getUpdateRate() {
		return this.updateRate;
	}

	public void setUpdateRate(int updateRate) {
		this.updateRate = updateRate;
	}
}
