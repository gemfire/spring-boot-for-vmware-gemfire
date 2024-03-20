/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.security;

import java.io.Serializable;
import java.security.Principal;
import java.util.Properties;

import org.apache.geode.security.AuthenticationFailedException;
import org.springframework.geode.util.GeodeConstants;

/**
 * Simple, test {@link org.apache.geode.security.SecurityManager}.
 *
 * @author John Blum
 * @see Principal
 * @see Properties
 * @see org.apache.geode.security.SecurityManager
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class TestSecurityManager implements org.apache.geode.security.SecurityManager {

	@Override
	public Object authenticate(Properties credentials) throws AuthenticationFailedException {

		String username = credentials.getProperty(USER_NAME,"nousername");
		String password = credentials.getProperty(PASSWORD,"nopassword");
		if (username.equals(password)) {
			return username;
//			return User.create(username);
		}
		throw new AuthenticationFailedException("invalid username/password");
	}

	public static class User implements Comparable<User>, Principal, Serializable {

		public static User create(String name) {
			return new User(name);
		}

		private final String name;

		public User(String name) {

			if (name == null || name.trim().isEmpty()) {
				throw new IllegalArgumentException("Username is required");
			}

			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		@Override
		public int compareTo(User user) {
			return this.getName().compareTo(user.getName());
		}

		@Override
		public boolean equals(Object obj) {

			if (this == obj) {
				return true;
			}

			if (!(obj instanceof User)) {
				return false;
			}

			User that = (User) obj;

			return this.getName().equals(that.getName());
		}

		@Override
		public int hashCode() {

			int hashValue = 17;

			hashValue = 37 * hashValue + getName().hashCode();

			return hashValue;
		}

		@Override
		public String toString() {
			return getName();
		}
	}
}
