/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.configuration.support;

import java.util.Properties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Spring Boot {@link ConfigurationProperties} used to configure Apache Geode embedded services.
 *
 * Currently, the supported embedded services include HTTP service (Jetty), and Memcached (protocol) server..
 *
 * The configuration {@link Properties} are based on well-known, documented Spring Data for Apache Geode (SDG)
 * {@link Properties}.
 *
 * @author John Blum
 * @see java.util.Properties
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.boot.context.properties.ConfigurationProperties
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class ServiceProperties {

	private final HttpServiceProperties httpServiceProperties = new HttpServiceProperties();

	private final MemcachedServerProperties memcachedServerProperties = new MemcachedServerProperties();

	public HttpServiceProperties getHttp() {
		return this.httpServiceProperties;
	}

	public MemcachedServerProperties getMemcached() {
		return this.memcachedServerProperties;
	}

	public static class DeveloperRestApiProperties {

		private static final boolean DEFAULT_START = false;

		private boolean start = DEFAULT_START;

		public boolean isStart() {
			return this.start;
		}

		public void setStart(boolean start) {
			this.start = start;
		}
	}

	public static class HttpServiceProperties {

		private static final boolean DEFAULT_SSL_REQUIRE_AUTHENTICATION = false;

		private static final int DEFAULT_PORT = 7070;

		private boolean sslRequireAuthentication = DEFAULT_SSL_REQUIRE_AUTHENTICATION;

		private int port = DEFAULT_PORT;

		private final DeveloperRestApiProperties developerRestApiProperties = new DeveloperRestApiProperties();

		private String bindAddress;

		public String getBindAddress() {
			return this.bindAddress;
		}

		public void setBindAddress(String bindAddress) {
			this.bindAddress = bindAddress;
		}

		public DeveloperRestApiProperties getDevRestApi() {
			return developerRestApiProperties;
		}

		public int getPort() {
			return this.port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public boolean isSslRequireAuthentication() {
			return this.sslRequireAuthentication;
		}

		public void setSslRequireAuthentication(boolean sslRequireAuthentication) {
			this.sslRequireAuthentication = sslRequireAuthentication;
		}
	}

	public static class MemcachedServerProperties {

		private static final int DEFAULT_PORT = 11211;

		private int port = DEFAULT_PORT;

		public int getPort() {
			return this.port;
		}

		public void setPort(int port) {
			this.port = port;
		}
	}
}
