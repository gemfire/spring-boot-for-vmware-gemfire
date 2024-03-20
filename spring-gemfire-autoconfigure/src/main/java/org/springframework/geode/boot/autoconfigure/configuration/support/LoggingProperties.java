/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.configuration.support;

import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Spring Boot {@link ConfigurationProperties} used to configure Apache Geode logging.
 *
 * The configuration {@link Properties} are based on well-known, documented Spring Data for Apache Geode (SDG)
 * {@link Properties}.
 *
 * @author John Blum
 * @see java.util.Properties
 * @see org.springframework.boot.context.properties.ConfigurationProperties
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class LoggingProperties {

	private static final int DEFAULT_LOG_DISK_SPACE_LIMIT = 0;
	private static final int DEFAULT_LOG_FILE_SIZE_LIMIT = 0;

	private static final String DEFAULT_LOG_LEVEL = "config";

	private int logDiskSpaceLimit = DEFAULT_LOG_DISK_SPACE_LIMIT;
	private int logFileSizeLimit = DEFAULT_LOG_FILE_SIZE_LIMIT;

	private String level = DEFAULT_LOG_LEVEL;
	private String logFile;

	public String getLevel() {
		return this.level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public int getLogDiskSpaceLimit() {
		return this.logDiskSpaceLimit;
	}

	public void setLogDiskSpaceLimit(int logDiskSpaceLimit) {
		this.logDiskSpaceLimit = logDiskSpaceLimit;
	}

	public String getLogFile() {
		return this.logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public int getLogFileSizeLimit() {
		return this.logFileSizeLimit;
	}

	public void setLogFileSizeLimit(int logFileSizeLimit) {
		this.logFileSizeLimit = logFileSizeLimit;
	}
}
