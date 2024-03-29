/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.context.logging;

import java.util.Arrays;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.data.gemfire.config.annotation.EnableLogging;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Spring {@link GenericApplicationListener} used to configure Apache Geode Logging from existing Spring Data
 * for Apache Geode Logging configuration support, such as when using the {@link EnableLogging} annotation
 * or alternatively using {@link Properties}.
 *
 * This listener must be ordered before the Spring Boot {@link LoggingApplicationListener}.
 *
 * @author John Blum
 * @see Properties
 * @see SpringApplication
 * @see ApplicationEnvironmentPreparedEvent
 * @see LoggingApplicationListener
 * @see ApplicationContext
 * @see ApplicationEvent
 * @see GenericApplicationListener
 * @see Ordered
 * @see Environment
 * @since 1.3.0
 */
public class GeodeLoggingApplicationListener implements GenericApplicationListener {

	private static final Class<?>[] EVENT_TYPES = { ApplicationEnvironmentPreparedEvent.class };
	private static final Class<?>[] SOURCE_TYPES = { ApplicationContext.class, SpringApplication.class };

	public static final String SPRING_BOOT_DATA_GEMFIRE_LOG_LEVEL_PROPERTY = "spring.boot.data.gemfire.log.level";
	public static final String SPRING_DATA_GEMFIRE_CACHE_LOG_LEVEL = "spring.data.gemfire.cache.log-level";
	public static final String SPRING_DATA_GEMFIRE_LOGGING_LOG_LEVEL = "spring.data.gemfire.logging.level";

	@Override
	public int getOrder() {

		return LoggingApplicationListener.DEFAULT_ORDER > Ordered.HIGHEST_PRECEDENCE
			? LoggingApplicationListener.DEFAULT_ORDER - 1
			: Ordered.HIGHEST_PRECEDENCE;
	}

	@Override
	public void onApplicationEvent(@Nullable ApplicationEvent event) {

		if (event instanceof ApplicationEnvironmentPreparedEvent) {

			ApplicationEnvironmentPreparedEvent environmentPreparedEvent = (ApplicationEnvironmentPreparedEvent) event;

			onApplicationEnvironmentPreparedEvent(environmentPreparedEvent);
		}
	}

	protected void onApplicationEnvironmentPreparedEvent(
			@NonNull ApplicationEnvironmentPreparedEvent environmentPreparedEvent) {

		Assert.notNull(environmentPreparedEvent, "ApplicationEnvironmentPreparedEvent must not be null");

		Environment environment = environmentPreparedEvent.getEnvironment();

		if (isSystemPropertyNotSet(SPRING_BOOT_DATA_GEMFIRE_LOG_LEVEL_PROPERTY)) {

			String logLevel = environment.getProperty(SPRING_BOOT_DATA_GEMFIRE_LOG_LEVEL_PROPERTY,
				environment.getProperty(SPRING_DATA_GEMFIRE_LOGGING_LOG_LEVEL,
				environment.getProperty(SPRING_DATA_GEMFIRE_CACHE_LOG_LEVEL)));

			setSystemProperty(SPRING_BOOT_DATA_GEMFIRE_LOG_LEVEL_PROPERTY, logLevel);
		}
	}

	protected boolean isSystemPropertySet(@Nullable String propertyName) {
		return StringUtils.hasText(propertyName) && StringUtils.hasText(System.getProperty(propertyName));
	}

	protected boolean isSystemPropertyNotSet(@Nullable String propertyName) {
		return !isSystemPropertySet(propertyName);
	}

	protected void setSystemProperty(@NonNull String propertyName, @Nullable String propertyValue) {

		Assert.hasText(propertyName, () -> String.format("PropertyName [%s] is required", propertyName));

		if (StringUtils.hasText(propertyValue)) {
			System.setProperty(propertyName, propertyValue);
		}
	}

	@Override
	public boolean supportsEventType(@NonNull ResolvableType eventType) {

		Class<?> rawType = eventType.getRawClass();

		return rawType != null && Arrays.stream(EVENT_TYPES).anyMatch(it -> it.isAssignableFrom(rawType));
	}

	@Override
	public boolean supportsSourceType(@Nullable Class<?> sourceType) {
		return sourceType != null && Arrays.stream(SOURCE_TYPES).anyMatch(it -> it.isAssignableFrom(sourceType));
	}
}
