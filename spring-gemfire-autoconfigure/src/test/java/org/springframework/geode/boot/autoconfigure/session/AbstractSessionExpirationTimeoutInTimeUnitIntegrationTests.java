/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.data.gemfire.GemFireOperationsSessionRepository;
import org.springframework.session.data.gemfire.config.annotation.web.http.GemFireHttpSessionConfiguration;

/**
 * Abstract base test class for {@link Session} {@literal} in the configured {@literal time unit},
 * for example: seconds, minutes, etc.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.session.Session
 * @see org.springframework.session.SessionRepository
 * @see org.springframework.session.data.gemfire.GemFireOperationsSessionRepository
 * @see org.springframework.session.data.gemfire.config.annotation.web.http.GemFireHttpSessionConfiguration
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public abstract class AbstractSessionExpirationTimeoutInTimeUnitIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private GemFireHttpSessionConfiguration configuration;

	@Autowired
	private SessionRepository<Session> repository;

	protected int getExpectedMaxInactiveIntervalInSeconds() {
		return GemFireHttpSessionConfiguration.DEFAULT_MAX_INACTIVE_INTERVAL_IN_SECONDS;
	}

	@Test
	public void sessionTimeoutConfigurationIsCorrect() {

		int expectedMaxInactiveIntervalInSeconds = getExpectedMaxInactiveIntervalInSeconds();

		assertThat(this.configuration).isNotNull();
		assertThat(this.configuration.getMaxInactiveIntervalInSeconds()).isEqualTo(expectedMaxInactiveIntervalInSeconds);
		assertThat(this.repository).isInstanceOf(GemFireOperationsSessionRepository.class);

		Session session = this.repository.createSession();

		assertThat(session).isNotNull();
		assertThat(session.getMaxInactiveInterval().getSeconds()).isEqualTo(expectedMaxInactiveIntervalInSeconds);
	}

	@SpringBootApplication
	@EnableGemFireMockObjects
	static class TestConfiguration { }

}
