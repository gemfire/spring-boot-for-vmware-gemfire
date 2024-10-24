/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.configuration.support;

import java.util.Properties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Spring Boot {@link ConfigurationProperties} used to configure an Apache Geode peer Cache Security
 * (authentication &amp; authorization).
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
public class PeerSecurityProperties {

	private static final long DEFAULT_VERIFY_MEMBER_TIMEOUT_IN_MILLISECONDS = 1000;

	private long verifyMemberTimeout = DEFAULT_VERIFY_MEMBER_TIMEOUT_IN_MILLISECONDS;

	private String authenticationInitializer;
	private String authenticator;

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

	public long getVerifyMemberTimeout() {
		return this.verifyMemberTimeout;
	}

	public void setVerifyMemberTimeout(long verifyMemberTimeout) {
		this.verifyMemberTimeout = verifyMemberTimeout;
	}
}
