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
 * with the log level {@link Level#ERROR}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.tests.logging.slf4j.logback.TestAppender
 * @see org.springframework.geode.logging.slf4j.logback.ErrorLoggingIntegrationTests
 * @see ch.qos.logback.classic.Level#ERROR
 * @since 1.7.5
 */
public class ErrorLoggingIntegrationTests extends AbstractLoggingIntegrationTests {

  private static final Level TEST_LOG_LEVEL = Level.ERROR;

  @BeforeClass
  public static void setupLogback() {
    setupLogback(TEST_LOG_LEVEL);
  }

  @Test
  public void logLevelIsSetToDebug() {
    assertApacheGeodeLoggerLogLevel(TEST_LOG_LEVEL);
  }

  @Test
  public void logMessagesAtDebug() {
    assertLogMessages("ERROR TEST");
  }
}
