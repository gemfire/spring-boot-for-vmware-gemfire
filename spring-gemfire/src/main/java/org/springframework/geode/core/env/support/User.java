/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.geode.core.env.support;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * The {@link User} class is an Abstract Data Type (ADT) modeling a user in Pivotal CloudFoundry (PCF).
 *
 * @author John Blum
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class User implements Comparable<User> {

	private Role role;

	private final String name;

	private String password;

	/**
	 * Factory method used to construct a new {@link User} initialized with the given {@link String name}.
	 *
	 * @param name {@link String} containing the name of the {@link User}.
	 * @return a new {@link User} initialized witht he given {@link String name}.
	 * @throws IllegalArgumentException if {@link String name} is {@literal null} or empty.
	 * @see #User(String)
	 */
	public static User with(String name) {
		return new User(name);
	}

	/**
	 * Constructs a new {@link User} initialized with the given {@link String name}.
	 *
	 * @param name {@link String} containing the name of the {@link User}.
	 * @throws IllegalArgumentException if {@link String name} is {@literal null} or empty.
	 */
	private User(String name) {

		Assert.hasText(name, String.format("User name [%s] is required", name));

		this.name = name;
	}

	/**
	 * Returns the {@link String name} of this {@link User}.
	 *
	 * @return a {@link String} containing the {@link User User's} name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns an {@link Optional} {@link String} containing {@link User User's} password.
	 *
	 * @return an {@link Optional} {@link String} containing {@link User User's} password.
	 * @see java.util.Optional
	 */
	public Optional<String> getPassword() {
		return Optional.ofNullable(this.password).filter(StringUtils::hasText);
	}

	/**
	 * Returns an {@link Optional} {@link Role} for this {@link User}.
	 *
	 * @return an {@link Optional} {@link Role} for this {@link User}.
	 * @see org.springframework.geode.core.env.support.User.Role
	 * @see java.util.Optional
	 */
	public Optional<Role> getRole() {
		return Optional.ofNullable(this.role);
	}

	/**
	 * Builder method used to set this {@link User User's} {@link String password}.
	 *
	 * @param password {@link String} containing this {@link User User's} password.
	 * @return this {@link User}.
	 */
	public User withPassword(String password) {
		this.password = password;
		return this;
	}

	/**
	 * Builder method used to set this {@link User User's} {@link Role}.
	 *
	 * @param role assigned {@link Role} of this {@link User}.
	 * @return this {@link User}.
	 * @see org.springframework.geode.core.env.support.User
	 */
	public User withRole(Role role) {
		this.role = role;
		return this;
	}

	@Override
	@SuppressWarnings("all")
	public int compareTo(User other) {
		return this.getName().compareTo(other.getName());
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

		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getName());

		return hashValue;
	}

	@Override
	public String toString() {
		return getName();
	}

	public enum Role {

		CLUSTER_OPERATOR,
		DEVELOPER;

		public static Role of(String name) {

			return Arrays.stream(values())
				.filter(role -> role.name().equalsIgnoreCase(String.valueOf(name).trim()))
				.findFirst()
				.orElse(null);
		}

		public boolean isClusterOperator() {
			return CLUSTER_OPERATOR.equals(this);
		}

		public boolean isDeveloper() {
			return DEVELOPER.equals(this);
		}

		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}
}
