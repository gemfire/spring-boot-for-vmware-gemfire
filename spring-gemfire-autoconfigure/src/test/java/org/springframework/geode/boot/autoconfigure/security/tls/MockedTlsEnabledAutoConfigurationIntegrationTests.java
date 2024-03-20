/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.security.tls;

import org.junit.Before;
import org.junit.runner.RunWith;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.geode.util.GeodeAssertions;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the configuration of TLS (e.g. SSL) in a Cloud Platform Environment/Context (e.g. PCF)
 * using Apache Geode Mock Objects.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.geode.boot.autoconfigure.security.tls.AbstractTlsEnabledAutoConfigurationIntegrationTests
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.3.0
 */
@ActiveProfiles("mock-tls")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MockedTlsEnabledAutoConfigurationIntegrationTests.TestConfiguration.class,
	properties = {
	    "VCAP_APPLICATION={ \"name\": \"MockedTlsEnabledAutoConfigurationIntegrationTests\", \"uris\": [] }",
		"VCAP_SERVICES={ \"p-cloudcache\": [{ \"credentials\": { \"tls-enabled\": \"true\" }, \"name\": \"jblum-pcc\", \"tags\": [ \"gemfire\", \"cloudcache\", \"database\", \"pivotal\" ]}]}"
	}
)
@SuppressWarnings("unused")
public class MockedTlsEnabledAutoConfigurationIntegrationTests
		extends AbstractTlsEnabledAutoConfigurationIntegrationTests {

	@Before
	public void testWithMockGemFireObjects() {
		GeodeAssertions.assertThat(this.clientCache).isNotInstanceOfGemFireCacheImpl();
		GeodeAssertions.assertThat(this.clientCache.getDistributedSystem()).isNotInstanceOfInternalDistributedSystem();
	}

	@SpringBootApplication
	@EnableGemFireMockObjects
	@Profile("mock-tls")
	static class TestConfiguration { }

}
