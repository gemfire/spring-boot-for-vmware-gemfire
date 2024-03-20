/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.configuration.support;

import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Spring Boot {@link ConfigurationProperties} used to configure Apache Geode management services, such as HTTP.
 *
 * The configuration {@link Properties} are based on well-known, documented Spring Data for Apache Geode (SDG)
 * {@link Properties}.
 *
 * @author John Blum
 * @see java.util.Properties
 * @see org.springframework.boot.context.properties.ConfigurationProperties
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class ManagementProperties {

	private static final boolean DEFAULT_USE_HTTP = false;

	private boolean useHttp = DEFAULT_USE_HTTP;

	private final HttpServiceProperties httpServiceProperties = new HttpServiceProperties();

	public HttpServiceProperties getHttp() {
		return this.httpServiceProperties;
	}

	public boolean isUseHttp() {
		return this.useHttp;
	}

	public void setUseHttp(boolean useHttp) {
		this.useHttp = useHttp;
	}

	public static class HttpServiceProperties {

		private static final int DEFAULT_PORT = 7070;

		private static final String DEFAULT_HOST = "localhost";

		private int port = DEFAULT_PORT;

		private String host = DEFAULT_HOST;

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}
	}
}
