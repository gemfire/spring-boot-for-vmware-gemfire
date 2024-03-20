/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.security.support;

import java.util.Properties;

import org.apache.geode.security.AuthenticationFailedException;
import org.apache.geode.security.ResourcePermission;

/**
 * {@link SecurityManagerSupport} is an abstract base class implementing Apache Geode's
 * {@link org.apache.geode.security.SecurityManager} interface, providing default implementations of the
 * {@link org.apache.geode.security.SecurityManager} auth methods.
 *
 * @author John Blum
 * @see org.apache.geode.security.SecurityManager
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class SecurityManagerSupport implements org.apache.geode.security.SecurityManager {

	protected static final boolean DEFAULT_AUTHORIZATION = false;

	@Override
	public void init(Properties securityProperties) { }

	@Override
	public Object authenticate(Properties credentials) throws AuthenticationFailedException {
		return new AuthenticationFailedException("Authentication Provider Not Present");
	}

	@Override
	public boolean authorize(Object principal, ResourcePermission permission) {
		return DEFAULT_AUTHORIZATION;
	}

	@Override
	public void close() { }

}
