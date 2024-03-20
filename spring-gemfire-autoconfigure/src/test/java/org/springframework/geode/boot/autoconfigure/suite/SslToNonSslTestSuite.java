/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.suite;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import org.springframework.geode.boot.autoconfigure.cluster.aware.SecureClusterAwareConfigurationIntegrationTests;
import org.springframework.geode.boot.autoconfigure.security.ssl.AutoConfiguredSslIntegrationTests;
import org.springframework.geode.boot.autoconfigure.topology.clientserver.SpringBootApacheGeodeClientServerIntegrationTests;

/**
 * Test Suite that combines Apache Geode SSL Integration Tests with Non-SSL Integration Tests.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.junit.runners.Suite
 * @since 1.5.0
 */
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
	AutoConfiguredSslIntegrationTests.class,
	SecureClusterAwareConfigurationIntegrationTests.class,
	SpringBootApacheGeodeClientServerIntegrationTests.class
})
public class SslToNonSslTestSuite {

}
