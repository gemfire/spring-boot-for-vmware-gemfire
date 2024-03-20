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
import org.springframework.geode.util.GeodeAssertions;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the configuration of TLS (e.g. SSL) in a Cloud Platform Environment/Context (e.g. PCF)
 * using live Apache Geode objects.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.geode.boot.autoconfigure.security.tls.AbstractTlsEnabledAutoConfigurationIntegrationTests
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.3.0
 */
@ActiveProfiles("tls")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TlsEnabledAutoConfigurationIntegrationTests.TestConfiguration.class,
	properties = {
		"VCAP_APPLICATION={ \"name\": \"TlsEnabledAutoConfigurationIntegrationTests\", \"uris\": [] }",
		"VCAP_SERVICES={ \"p-cloudcache\": [{ \"credentials\": { \"tls-enabled\": \"true\" }, \"name\": \"jblum-pcc\", \"tags\": [ \"gemfire\", \"cloudcache\", \"database\", \"pivotal\" ]}]}"
	}
)
@SuppressWarnings("unused")
public class TlsEnabledAutoConfigurationIntegrationTests extends AbstractTlsEnabledAutoConfigurationIntegrationTests {

	@Before
	public void testWithLiveGeodeObjects() {
		GeodeAssertions.assertThat(this.clientCache).isInstanceOfGemFireCacheImpl();
		GeodeAssertions.assertThat(this.clientCache.getDistributedSystem()).isInstanceOfInternalDistributedSystem();
	}

	@SpringBootApplication
	@Profile("tls")
	static class TestConfiguration { }

}
