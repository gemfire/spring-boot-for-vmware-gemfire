/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.support;

import static org.assertj.core.api.Assertions.assertThat;
import org.apache.geode.cache.client.PoolManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.gemfire.config.annotation.EnablePool;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public abstract class AbstractDisableSubscriptionWithPropertyIntegrationTests {

	@Test
	public void subscriptionNotEnabledForDefaultPool() {
		assertThat(PoolManager.find("DEFAULT").getSubscriptionEnabled()).isFalse();
	}

	@EnablePool(name = "MyCustomPool", servers = @EnablePool.Server, subscriptionEnabled = true)
	@SpringBootApplication
	static class TestConfiguration {
	}
}
