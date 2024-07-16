/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import org.apache.geode.cache.client.ClientCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration tests for {@link UseMemberName} and {@link MemberNameConfiguration}.
 *
 * @author John Blum
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.data.gemfire.config.annotation.ClientCacheApplication
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class MemberNameConfigurationIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ClientCache gemfireCache;

	@Test
	public void memberNameWasConfiguredCorrectly() {

		assertThat(this.gemfireCache).isNotNull();
		assertThat(this.gemfireCache.getDistributedSystem()).isNotNull();
		assertThat(this.gemfireCache.getDistributedSystem().getProperties()).isNotNull();
		assertThat(this.gemfireCache.getDistributedSystem().getProperties().getProperty("name"))
			.isEqualTo("TestClient");
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@UseMemberName("TestClient")
	static class TestConfiguration { }

}
