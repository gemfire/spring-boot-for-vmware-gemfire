/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.context.logging;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for Apache Geode Logging configured with the {@literal spring.boot.data.gemfire.log.level} property
 * in JVM {@link System} {@link System#getProperties() Properties}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see AbstractSpringConfiguredLogLevelPropertyIntegrationTests
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.3.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootDataGemFireLogLevelSystemPropertyIntegrationTests
		extends AbstractSpringConfiguredLogLevelPropertyIntegrationTests {

	@BeforeClass
	public static void setup() {
		System.setProperty("spring.boot.data.gemfire.log.level", "DEBUG");
		System.setProperty("spring.data.gemfire.logging.level", "TRACE");
		System.setProperty("spring.data.gemfire.cache.log-level", "ERROR");
	}
}
