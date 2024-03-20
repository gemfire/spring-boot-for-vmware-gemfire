/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.geode.boot.autoconfigure.SpringSessionAutoConfiguration;
import org.springframework.session.SessionRepository;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

/**
 * Integration Tests for {@link SpringSessionAutoConfiguration} asserting that Spring Session for Apache Geode (SSDG)
 * is not auto-configured when the Spring {@link ApplicationContext} is not a {@link WebApplicationContext}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.geode.boot.autoconfigure.SpringSessionAutoConfiguration
 * @see org.springframework.session.SessionRepository
 * @see org.springframework.test.context.junit4.SpringRunner
 * @see org.springframework.web.context.WebApplicationContext
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@SuppressWarnings("unused")
public class NoAutoConfigurationOfSessionCachingIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void noSessionRepositoryBeanIsPresent() {

		assertThat(this.applicationContext).isNotNull();
		assertThat(this.applicationContext.containsBean("sessionRepository")).isFalse();
		assertThat(this.applicationContext.getBeansOfType(SessionRepository.class)).isEmpty();
	}

	@SpringBootApplication
	@EnableGemFireMockObjects
	static class TestConfiguration { }

}
