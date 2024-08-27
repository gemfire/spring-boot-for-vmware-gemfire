/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.session;

import java.time.Duration;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.session.Session;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link Session} {@literal expiration timeout} in {@literal minutes}.
 *
 * @author John Blum
 * @see java.time.Duration
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.geode.boot.autoconfigure.session.AbstractSessionExpirationTimeoutInTimeUnitIntegrationTests
 * @see org.springframework.session.Session
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = AbstractSessionExpirationTimeoutInTimeUnitIntegrationTests.TestConfiguration.class,
	properties = "spring.session.timeout=5m",
	webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
public class SessionExpirationTimeoutInMinutesIntegrationTests
		extends AbstractSessionExpirationTimeoutInTimeUnitIntegrationTests {

	@Override
	protected int getExpectedMaxInactiveIntervalInSeconds() {
		return Long.valueOf(Duration.ofMinutes(5).getSeconds()).intValue();
	}
}
