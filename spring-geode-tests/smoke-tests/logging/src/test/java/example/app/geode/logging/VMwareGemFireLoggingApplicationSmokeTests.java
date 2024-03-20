/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.geode.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Smoke Tests for {@link VMwareGemFireLoggingApplication}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.3.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@SuppressWarnings("unused")
public class VMwareGemFireLoggingApplicationSmokeTests extends IntegrationTestsSupport {

	@Autowired
	private VMwareGemFireLoggingApplication.Log log;

	@Test
	public void springApplicationLogsContent() {
		assertThat(this.log.getContent()).containsSequence("RUNNER RAN!");
	}

	@Test
	public void debugLogStatementNotLogged() {
		assertThat(this.log.getContent()).doesNotContain("DEBUG TEST");
	}

	@Test
	public void gemfireLogsContent() {
		assertThat(this.log.getContent()).containsSequence("Product-Name: VMware GemFire");
	}
}
