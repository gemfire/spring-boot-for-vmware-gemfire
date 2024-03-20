/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.configuration.support;

import java.util.Properties;

import org.apache.geode.cache.client.ClientCache;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Spring Boot {@link ConfigurationProperties} used to configure an Apache Geode {@link ClientCache} Security
 * (authentication &amp; authorization).
 *
 * The configuration {@link Properties} are based on well-known, documented Spring Data for Apache Geode (SDG)
 * {@link Properties}.
 *
 * @author John Blum
 * @see Properties
 * @see ClientCache
 * @see ConfigurationProperties
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class ClientSecurityProperties {

	private String accessor;
	private String accessorPostProcessor;
	private String authenticationInitializer;
	private String authenticator;
	private String diffieHellmanAlgorithm;

	public String getAccessor() {
		return this.accessor;
	}

	public void setAccessor(String accessor) {
		this.accessor = accessor;
	}

	public String getAccessorPostProcessor() {
		return this.accessorPostProcessor;
	}

	public void setAccessorPostProcessor(String accessorPostProcessor) {
		this.accessorPostProcessor = accessorPostProcessor;
	}

	public String getAuthenticationInitializer() {
		return this.authenticationInitializer;
	}

	public void setAuthenticationInitializer(String authenticationInitializer) {
		this.authenticationInitializer = authenticationInitializer;
	}

	public String getAuthenticator() {
		return this.authenticator;
	}

	public void setAuthenticator(String authenticator) {
		this.authenticator = authenticator;
	}

	public String getDiffieHellmanAlgorithm() {
		return this.diffieHellmanAlgorithm;
	}

	public void setDiffieHellmanAlgorithm(String diffieHellmanAlgorithm) {
		this.diffieHellmanAlgorithm = diffieHellmanAlgorithm;
	}
}
