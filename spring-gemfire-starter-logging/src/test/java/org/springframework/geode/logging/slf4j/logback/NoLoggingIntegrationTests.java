/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.logging.slf4j.logback;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.springframework.data.gemfire.tests.logging.slf4j.logback.TestAppender;

import ch.qos.logback.classic.Level;

/**
 * Integration Tests testing the {@literal org.apache.geode} {@link org.slf4j.Logger}
 * with no logging enabled, i.e. {@link Level#OFF}.
 *
 * <code>
 *   -Dspring.boot.data.gemfire.log.level=DEBUG
 * </code>
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.tests.logging.slf4j.logback.TestAppender
 * @see org.springframework.geode.logging.slf4j.logback.AbstractLoggingIntegrationTests
 * @see ch.qos.logback.classic.Level#OFF
 * @since 1.3.0
 */
public class NoLoggingIntegrationTests extends AbstractLoggingIntegrationTests {

	@Override
	protected Level getTestLogLevel() {
		return Level.OFF;
	}

	@Test
	public void logLevelIsSetToOff() {
		assertApacheGeodeLoggerLogLevel(Level.OFF);
	}

	@Test
	public void logsNoMessages() {

		TestAppender testAppender = getTestAppender();

		assertThat(testAppender.lastLogMessage()).isNull();
	}
}
