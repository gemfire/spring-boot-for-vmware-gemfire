/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.core.util;

import java.util.Optional;

import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.logging.LoggingSystemFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;

/**
 * Abstract base class used to perform actions on Spring Boot configuration and components.
 *
 * @author John Blum
 * @see SpringExtensions
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public abstract class SpringBootExtensions extends SpringExtensions {

	/**
	 * Cleans up all resources allocated by the {@link LoggingSystem} loaded, configured and initialized by Spring Boot.
	 *
	 * @see #cleanUpLoggingSystem(ClassLoader)
	 * @see ClassLoader
	 */
	public static void cleanUpLoggingSystem() {
		cleanUpLoggingSystem(ClassUtils.getDefaultClassLoader());
	}

	/**
	 * Cleans up all resources allocated by the {@link LoggingSystem} loaded, configured and initialized by Spring Boot.
	 *
	 * @param classLoader Java {@link ClassLoader} used to resolve the Spring Boot {@link LoggingSystem}
	 * representing the logging provider (e.g. Logback).
	 * @see ClassLoader
	 */
	public static void cleanUpLoggingSystem(@NonNull ClassLoader classLoader) {

		Optional.ofNullable(LoggingSystemFactory.fromSpringFactories())
			.map(loggingSystemFactory -> loggingSystemFactory.getLoggingSystem(classLoader))
			.ifPresent(LoggingSystem::cleanUp);
	}
}
