/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.cache.peer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.LocalRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.PeerCacheApplication;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.util.RegionUtils;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the auto-configuration of an Apache Geode peer {@link Cache} instance, overriding
 * the default {@link ClientCache} instance.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Cache
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.data.gemfire.config.annotation.PeerCacheApplication
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@SuppressWarnings("unused")
public class SpringBootApacheGeodePeerCacheApplicationIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private GemFireCache peerCache;

	@Test
	public void peerCacheWithPeerLocalRegionAreAvailable() {

		Optional.ofNullable(this.peerCache)
			.map(it -> assertThat(GemfireUtils.isPeer(it)).isTrue())
			.orElseThrow(() -> newIllegalStateException("Peer cache was null"));

		Region<Object, Object> example = this.peerCache.getRegion("/Example");

		assertThat(example).isNotNull();
		assertThat(example.getName()).isEqualTo("Example");
		assertThat(example.getFullPath()).isEqualTo(RegionUtils.toRegionPath("Example"));

		example.put(1, "test");

		assertThat(example.get(1)).isEqualTo("test");
	}

	@SpringBootApplication
	@PeerCacheApplication
	static class TestConfiguration {

		@Bean("Example")
		public LocalRegionFactoryBean<Object, Object> exampleRegion(GemFireCache gemfireCache) {

			LocalRegionFactoryBean<Object, Object> exampleRegion = new LocalRegionFactoryBean<>();

			exampleRegion.setCache(gemfireCache);
			exampleRegion.setClose(false);
			exampleRegion.setPersistent(false);

			return exampleRegion;
		}
	}
}
