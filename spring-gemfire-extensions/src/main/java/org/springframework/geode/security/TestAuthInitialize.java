/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.security;

import java.util.Properties;

import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.security.AuthInitialize;
import org.apache.geode.security.AuthenticationFailedException;

import org.springframework.geode.util.GeodeConstants;

/**
 * Simple, test {@link AuthInitialize} implementation.
 *
 * @author John Blum
 * @see org.apache.geode.security.AuthInitialize
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class TestAuthInitialize implements AuthInitialize {

	private static final String DEFAULT_USERNAME = "test";
	private static final String DEFAULT_PASSWORD = DEFAULT_USERNAME;

	public static TestAuthInitialize create() {
		return new TestAuthInitialize();
	}

	@Override
	public Properties getCredentials(Properties securityProperties, DistributedMember server, boolean isPeer)
			throws AuthenticationFailedException {

		Properties credentials = new Properties();

		credentials.setProperty(GeodeConstants.USERNAME,
			securityProperties.getProperty(GeodeConstants.USERNAME, DEFAULT_USERNAME));

		credentials.setProperty(GeodeConstants.PASSWORD,
			securityProperties.getProperty(GeodeConstants.PASSWORD, DEFAULT_PASSWORD));

		return credentials;
	}

	@Override
	public void close() { }

}
