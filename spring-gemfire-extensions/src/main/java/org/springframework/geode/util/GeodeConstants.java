/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.util;

import org.apache.geode.distributed.ConfigurationProperties;
import org.apache.geode.management.internal.security.ResourceConstants;

/**
 * Interface encapsulating common Apache Geode constants used by SBDG.
 *
 * @author John Blum
 * @see org.apache.geode.distributed.ConfigurationProperties
 * @since 1.3.0
 */
public interface GeodeConstants {

	String GEMFIRE_PROPERTY_PREFIX = "gemfire.";

	// Logging Constants (referring to Properties)
	String LOG_DISK_SPACE_LIMIT = ConfigurationProperties.LOG_DISK_SPACE_LIMIT;
	String LOG_FILE = ConfigurationProperties.LOG_FILE;
	String LOG_FILE_SIZE_LIMIT = ConfigurationProperties.LOG_FILE_SIZE_LIMIT;
	String LOG_LEVEL = ConfigurationProperties.LOG_LEVEL;

	// Security Constants (referring to Properties)
	String PASSWORD = ResourceConstants.PASSWORD;
	String USERNAME = ResourceConstants.USER_NAME;

}
