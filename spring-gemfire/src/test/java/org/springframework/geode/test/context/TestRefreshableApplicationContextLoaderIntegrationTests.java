/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.test.context;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.geode.context.annotation.RefreshableAnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link TestRefreshableApplicationContextLoader}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.geode.context.annotation.RefreshableAnnotationConfigApplicationContext
 * @see org.springframework.geode.test.context.TestRefreshableApplicationContextLoader
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.3.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(loader = TestRefreshableApplicationContextLoader.class)
@SuppressWarnings("unused")
public class TestRefreshableApplicationContextLoaderIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void applicationContextIsAnRefreshableAnnotationConfigApplicationContext() {
		assertThat(this.applicationContext).isInstanceOf(RefreshableAnnotationConfigApplicationContext.class);
	}
}
