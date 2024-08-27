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
 * with the default log level {@link Level#INFO}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.tests.logging.slf4j.logback.TestAppender
 * @see org.springframework.geode.logging.slf4j.logback.AbstractLoggingIntegrationTests
 * @see ch.qos.logback.classic.Level#INFO
 * @since 1.3.0
 */
public class InfoLoggingIntegrationTests extends AbstractLoggingIntegrationTests {

	@Test
	public void logLevelIsSetToInfo() {
		assertApacheGeodeLoggerLogLevel(Level.INFO);
	}

	@Test
	public void logsMessagesAtInfo() {

		TestAppender testAppender = getTestAppender();

		assertThat(testAppender.lastLogMessage()).isEqualTo("ERROR TEST");
		assertThat(testAppender.lastLogMessage()).isEqualTo("INFO TEST");
		assertThat(testAppender.lastLogMessage()).isNull();
	}
}
