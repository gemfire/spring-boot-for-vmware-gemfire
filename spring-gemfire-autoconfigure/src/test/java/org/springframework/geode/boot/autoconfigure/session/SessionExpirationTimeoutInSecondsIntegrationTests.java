/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.session;

import java.time.Duration;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.session.Session;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link Session} {@literal expiration timeout} in {@literal seconds}.
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
	properties = "spring.session.timeout=600s",
	webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
public class SessionExpirationTimeoutInSecondsIntegrationTests
		extends AbstractSessionExpirationTimeoutInTimeUnitIntegrationTests {

	@Override
	protected int getExpectedMaxInactiveIntervalInSeconds() {
		return Long.valueOf(Duration.ofSeconds(600).getSeconds()).intValue();
	}
}
