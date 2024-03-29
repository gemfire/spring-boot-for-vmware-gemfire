/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.core.env.support;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalArgumentException;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.geode.core.util.ObjectUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * The {@link CloudCacheService} class is an Abstract Data Type (ADT) modeling the Pivotal Cloud Cache service
 * in Pivotal CloudFoundry (PCF).
 *
 * @author John Blum
 * @see URL
 * @see Service
 * @since 1.0.0
 */
public class CloudCacheService extends Service {

	/**
	 * Factory method used to construct a new {@link CloudCacheService} initialized with the given {@link String name}.
	 *
	 * @param name {@link String} containing the name of the {@link CloudCacheService}.
	 * @throws IllegalArgumentException if the {@link String name} is {@literal null} or empty.
	 * @return the new {@link CloudCacheService} with the given {@link String name}.
	 * @see #CloudCacheService(String)
	 */
	public static CloudCacheService with(String name) {
		return new CloudCacheService(name);
	}

	private Boolean tlsEnabled;

	private String locators;

	private URL gfshUrl;

	/**
	 * Construct a new instance of {@link CloudCacheService} initialized with the given {@link String name}.
	 *
	 * @param name {@link String} containing the name of the {@link CloudCacheService}.
	 * @throws IllegalArgumentException if the {@link String name} is {@literal null} or empty.
	 */
	private CloudCacheService(String name) {
		super(name);
	}

	/**
	 * Returns an {@link Optional} Gfsh {@link URL} if configured, used to connect to Apache Geode's Management REST API
	 * (service).
	 *
	 * @return an {@link Optional} Gfsh {@link URL} used to connect to Apache Geode's Management REST API (service).
	 * @see #withGfshUrl(URL)
	 * @see Optional
	 * @see URL
	 */
	public Optional<URL> getGfshUrl() {
		return Optional.ofNullable(this.gfshUrl);
	}

	/**
	 * Returns an {@link Optional} {@link String} containing the list of Apache Geode Locator network endpoints.
	 *
	 * The format of the {@link String}, if present, is {@literal host1[port1],host2[port2], ...,hostN[portN]}.
	 *
	 * @return an {@link Optional} {@link String} containing the list of Apache Geode Locator network endpoints.
	 * @see #withLocators(String)
	 */
	public Optional<String> getLocators() {

		return Optional.ofNullable(this.locators)
			.filter(StringUtils::hasText);
	}

	/**
	 * Returns a {@link List} of Apache Geode Locator network endpoints.
	 *
	 * Returns an {@link Collections#emptyList() empty List} if no Locators were configured.
	 *
	 * @return a {@link List} of Apache Geode Locator network endpoints.
	 * @see #getLocators()
	 */
	public List<Locator> getLocatorList() {

		return getLocators()
			.map(Locator::parseLocators)
			.orElseGet(Collections::emptyList);
	}

	/**
	 * Returns a {@link Boolean} determining whether TLS/SSL is enabled between the client and the Pivotal Cloud Cache
	 * (PCC) service instance in Pivotal CloudFoundry (PCF).
	 *
	 * @return {@literal true} if TLS is enabled, {@literal false} if not.
	 */
	public boolean isTlsEnabled() {
		return Boolean.TRUE.equals(this.tlsEnabled);
	}

	/**
	 * Builder method used to configure the Gfsh {@link URL} to connect to the Apache Geode
	 * Management REST API (service).
	 *
	 * @param gfshUrl {@link URL} used to connect to the Apache Geode Management REST API (service).
	 * @return this {@link CloudCacheService}.
	 * @see #getGfshUrl()
	 */
	public CloudCacheService withGfshUrl(URL gfshUrl) {

		this.gfshUrl = gfshUrl;

		return this;
	}

	/**
	 * Builder method used to configure the {@link String list of Locator} network endpoints.
	 *
	 * @param locators {@link String} containing a comma-delimited list of Locator network endpoints
	 * of the format: {@literal host1[port1],host2[port2], ...,hostN[portN]}.
	 * @return this {@link CloudCacheService}.
	 * @see #getLocators()
	 */
	public CloudCacheService withLocators(String locators) {

		this.locators = locators;

		return this;
	}

	/**
	 * Builder method used to configure whether TLS/SSL is enabled between a client and the Pivotal Cloud Cache (PCC)
	 * service instance in Pivotal CloudFoundry (PCF).
	 *
	 * @param enabled {@link Boolean} value indicating whether TLS/SSL is enabled.
	 * @return this {@link CloudCacheService}.
	 * @see #isTlsEnabled()
	 */
	public CloudCacheService withTls(Boolean enabled) {

		this.tlsEnabled = enabled;

		return this;
	}

	public static class Locator implements Comparable<Locator> {

		static final int DEFAULT_LOCATOR_PORT = GemfireUtils.DEFAULT_LOCATOR_PORT;

		static final String DEFAULT_LOCATOR_HOST = "localhost";

		private Integer port;

		private String host;

		/**
		 * Factory method used to construct a new {@link Locator} on the default {@link String host}
		 * and {@link Integer port}.
		 *
		 * @return a new, default {@link Locator}.
		 * @see #newLocator(String, int)
		 */
		public static Locator newLocator() {
			return newLocator(DEFAULT_LOCATOR_HOST, DEFAULT_LOCATOR_PORT);
		}

		/**
		 * Factory method used to construct a new {@link Locator} running on the default {@link String host}
		 * and configured to listen on the given {@link Integer port}.
		 *
		 * @param port {@link Integer} containing the port number on which the {@link Locator} is listening.
		 * @return a new {@link Locator} running on the default {@link String host},
		 * listening on the given {@link Integer port}.
		 * @throws IllegalArgumentException if the {@link Integer port} is less than {@literal 0}.
		 * @see #newLocator(String, int)
		 */
		public static Locator newLocator(int port) {
			return newLocator(DEFAULT_LOCATOR_HOST, port);
		}

		/**
		 * Factory method used to construct a new {@link Locator} configured to run on the given {@link String host}
		 * and listening on the default {@link Integer port}.
		 *
		 * @param host {@link String} containing the name of the host on which the {@link Locator} is running.
		 * @return a new {@link Locator} running on the configured {@link String host},
		 * listening on the default {@link Integer port}.
		 * @throws IllegalArgumentException if {@link String host} is {@literal null} or empty.
		 * @see #newLocator(String, int)
		 */
		public static Locator newLocator(String host) {
			return newLocator(host, DEFAULT_LOCATOR_PORT);
		}

		/**
		 * Factory method used to construct a new {@link Locator} running on the configured {@link String host}
		 * and listening on the configured {@link Integer port}.
		 *
		 * @param host {@link String} containing the name of the host on which the {@link Locator} is running.
		 * @param port {@link Integer} containing the port number on which the {@link Locator} is listening.
		 * @throws IllegalArgumentException if {@link String host} is {@literal null} or empty,
		 * or the {@link Integer port} is less than {@literal 0}.
		 * @return a new {@link Locator} on the configured {@link String host} and {@link Integer port}.
		 */
		public static Locator newLocator(String host, int port) {

			Assert.hasText(host, String.format("Host [%s] is required", host));
			Assert.isTrue(port > -1, String.format("Port [%d] must be greater than equal to 0", port));

			return new Locator(host, port);
		}

		/**
		 * Factory method used to parse a {@link String comma-delimited list of Locator network endpoints}
		 * into a {@link List} of {@link Locator} objects.
		 *
		 * The {@link String comma-delimited list of Locators} must be formatted as
		 * {@literal host1[port1],host2[port2], ...,hostN[portN]}.
		 *
		 * @param locators {@link String} containing a comma-delimited list of Locator network endpoints.
		 * @return a new {@link List} of {@link Locator} objects or an empty {@link List}
		 * if no Locators were specified.
		 * @throws IllegalArgumentException if an individual Locator {@link String host[port]} is not valid.
		 * @see #parse(String)
		 */
		public static List<Locator> parseLocators(String locators) {

			return Arrays.stream(String.valueOf(locators).split(","))
				.filter(StringUtils::hasText)
				.map(Locator::parse)
				.collect(Collectors.toList());
		}

		/**
		 * Factory method used to parse an individual {@link String host[port]} network endpoint for a Locator.
		 *
		 * @param hostPort {@link String} containing the Locator host and port to parse.
		 * @return a new {@link Locator} configured from the given {@link String}.
		 * @throws IllegalArgumentException if the {@link String hostPort} are not valid.
		 * @see #parseHost(String)
		 * @see #parsePort(String)
		 * @see #newLocator(String, int)
		 */
		public static Locator parse(String hostPort) {

			return Optional.ofNullable(hostPort)
				.filter(StringUtils::hasText)
				.map(it -> {

					String host = parseHost(it);
					int port = parsePort(it);

					return newLocator(host, port);
				})
				.orElseThrow(() -> newIllegalArgumentException("Locator host/port [%s] is not valid", hostPort));
		}

		private static String parseHost(String value) {

			int index = String.valueOf(value).trim().indexOf("[");

			return index > 0 ? value.trim().substring(0, index).trim()
				: index != 0 && StringUtils.hasText(value) ? value.trim()
				: DEFAULT_LOCATOR_HOST;
		}

		private static int parsePort(String value) {

			StringBuilder digits = new StringBuilder();

			for (char chr : String.valueOf(value).toCharArray()) {
				if (Character.isDigit(chr)) {
					digits.append(chr);
				}
			}

			return digits.length() > 0 ? Integer.parseInt(digits.toString()) : DEFAULT_LOCATOR_PORT;
		}

		/**
		 * Construct a new {@link Locator} initialized with the {@link String host} and {@link Integer port}
		 * on which this {@link Locator} is running and listening for connections.
		 *
		 * @param host {@link String} containing the name of the host on which this {@link Locator} is running.
		 * @param port {@link Integer} specifying the port number on which this {@link Locator} is listening.
		 */
		private Locator(String host, Integer port) {

			this.host = host;
			this.port = port;
		}

		/**
		 * Return the {@link String name} of the host on which this {@link Locator} is running.
		 *
		 * Defaults to {@literal localhost}.
		 *
		 * @return the {@link String name} of the host on which this {@link Locator} is running.
		 */
		public String getHost() {
			return StringUtils.hasText(this.host) ? this.host : DEFAULT_LOCATOR_HOST;
		}

		/**
		 * Returns the {@link Integer port} on which this {@link Locator} is listening.
		 *
		 * Defaults to {@literal 10334}.
		 *
		 * @return the {@link Integer port} on which this {@link Locator} is listening.
		 */
		public int getPort() {
			return this.port != null ? this.port : DEFAULT_LOCATOR_PORT;
		}

		@Override
		public int compareTo(Locator other) {

			int result = this.getHost().compareTo(other.getHost());

			return result != 0 ? result : (this.getPort() - other.getPort());
		}

		@Override
		public boolean equals(Object obj) {

			if (this == obj) {
				return true;
			}

			if (!(obj instanceof Locator)) {
				return false;
			}

			Locator that = (Locator) obj;

			return this.getHost().equals(that.getHost())
				&& this.getPort() == that.getPort();
		}

		@Override
		public int hashCode() {

			int hashValue = 17;

			hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getHost());
			hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getPort());

			return hashValue;
		}

		@Override
		public String toString() {
			return String.format("%s[%d]", getHost(), getPort());
		}
	}
}
