/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.logging.slf4j.logback;

import org.junit.BeforeClass;
import org.junit.Test;

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

	private static final Level TEST_LOG_LEVEL = Level.OFF;

	@BeforeClass
	public static void setupLogback() {
		setupLogback(TEST_LOG_LEVEL);
	}

	@Test
	public void logLevelIsSetToOff() {
		assertApacheGeodeLoggerLogLevel(TEST_LOG_LEVEL);
	}

	@Test
	public void logsNoMessages() {
		assertLogMessages();
	}
}
