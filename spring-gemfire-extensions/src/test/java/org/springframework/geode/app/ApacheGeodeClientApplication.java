/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.Properties;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

/**
 * An example Apache Geode {@link ClientCache} application.
 *
 * @author John Blum
 * @see java.util.Properties
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.client.ClientCache
 * @since 1.3.0
 */
@SuppressWarnings("unused")
public class ApacheGeodeClientApplication implements Runnable {

	private static final ClientRegionShortcut CLIENT_REGION_SHORTCUT = ClientRegionShortcut.LOCAL;

	private static final String APPLICATION_NAME = ApacheGeodeClientApplication.class.getSimpleName();
	private static final String GEMFIRE_LOG_LEVEL = "info";

	private static final String[] EMPTY_ARGUMENTS = {};

	public static void main(String[] args) {
		new ApacheGeodeClientApplication(args).run();
	}

	private final String[] arguments;

	public ApacheGeodeClientApplication(String[] arguments) {
		this.arguments = arguments != null ? arguments : EMPTY_ARGUMENTS;
	}

	protected String[] getArguments() {
		return this.arguments;
	}

	@Override
	public void run() {
		run(getArguments());
	}

	public void run(String[] arguments) {

		ClientCache clientCache = registerShutdownHook(newClientCache(gemfireProperties()));

		Region<Object, Object> example = newClientRegion(clientCache, "Example");

		doDataAccessOperationsTest(example);
	}

	protected Properties gemfireProperties() {

		Properties gemfireProperties = new Properties();

		gemfireProperties.setProperty("name", APPLICATION_NAME);
		gemfireProperties.setProperty("log-level", GEMFIRE_LOG_LEVEL);

		return gemfireProperties;
	}

	protected ClientCache newClientCache(Properties gemfireProperties) {
		return new ClientCacheFactory(gemfireProperties).create();
	}

	protected <K, V> Region<K, V> newClientRegion(ClientCache clientCache, String regionName) {
		return clientCache.<K, V>createClientRegionFactory(CLIENT_REGION_SHORTCUT).create(regionName);
	}

	@SuppressWarnings("unchecked")
	protected <K, V> Region<K, V> doDataAccessOperationsTest(Region<Object, Object> region) {

		assertThat(region.put(1, "TEST")).isNull();
		assertThat(region.get(1)).isEqualTo("TEST");

		return (Region<K, V>) region;
	}

	protected ClientCache registerShutdownHook(ClientCache clientCache) {

		Runtime.getRuntime().addShutdownHook(new Thread(() ->
			Optional.ofNullable(clientCache).ifPresent(ClientCache::close),
			"ClientCache Shutdown Hook"));

		return clientCache;
	}
}
