/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.configuration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("jetty-config-test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@SuppressWarnings("unused")
public class JettyClassLoaderPropertiesIntegrationTest extends IntegrationTestsSupport {

    @Autowired
    private JettyClassLoaderProperties jettyClassLoaderProperties;

    @Test
    public void jettyClassLoaderPropertiesAreCorrect() {
        assertThat(jettyClassLoaderProperties).isNotNull();
        assertThat(jettyClassLoaderProperties.getExcludedResources())
                .containsExactlyInAnyOrder("something.config", "other.factories");
    }

    @SpringBootApplication
    @EnableGemFireMockObjects
    static class TestConfiguration { }
}
