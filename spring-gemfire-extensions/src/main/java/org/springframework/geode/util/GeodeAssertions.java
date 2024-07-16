/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.util;

import java.util.Objects;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.internal.cache.AbstractRegion;
import org.apache.geode.internal.cache.GemFireCacheImpl;

/**
 * Abstract utility class containing different assertions for Apache Geode objects, such as a {@link ClientCache}
 * or {@link Region}, and so on.
 *
 * @author John Blum
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.Region
 * @since 1.3.0
 */
@SuppressWarnings("unused")
public abstract class GeodeAssertions {

	/**
	 * Asserts that given {@link Object} upholds certain contractual obligations.
	 *
	 * @param <T> {@link Class type} of the given {@link Object}.
	 * @param obj {@link Object} being evaluated in the assertion.
	 * @return a new instance of {@link AssertThat} using the given {@link Object}
	 * as the {@link AssertThat#getSubject() subject} of the assertion.
	 * @see Object
	 * @see AssertThat
	 */
	public static <T> AssertThat<T> assertThat(T obj) {
		return () -> obj;
	}

	private static void assertIsInstanceOf(Object target, Class<?> type) {

		if (!type.isInstance(target)) {
			throw new AssertionError(String.format("[%1$s] is not an instance of [%2$s]",
				nullSafeTypeName(target), nullSafeTypeName(type)));
		}
	}

	private static void assertIsNotInstanceOf(Object target, Class<?> type) {

		if (type.isInstance(target)) {
			throw new AssertionError(String.format("[%1%s] is an instance of [%2$s]",
				nullSafeTypeName(target), nullSafeTypeName(type)));
		}
	}

	private static void assertIsNotNull(Object target) {

		if (Objects.isNull(target)) {
			throw new IllegalArgumentException("Argument must not be null");
		}
	}

	private static Class<?> nullSafeType(Object obj) {
		return obj != null ? obj.getClass() : null;
	}

	private static String nullSafeTypeName(Class<?> type) {
		return type != null ? type.getName() : null;
	}

	public static String nullSafeTypeName(Object obj) {
		return nullSafeTypeName(nullSafeType(obj));
	}

	/**
	 * The {@link AssertThat} {@link FunctionalInterface interface} defines a contract for making assertion about
	 * a given {@link Object} used as the {@link #getSubject() subject} of the assert statement.
	 *
	 * @param <T> {@link Class type} of the {@link Object} that is the {@link #getSubject() subject} of the assertion.
	 */
	@FunctionalInterface
	public interface AssertThat<T> {

		/**
		 * Returns the {@link Object} used as the subject of this assertion.
		 *
		 * @return the {@link Object} used as the subject of this assertion.
		 * @see Object
		 */
		T getSubject();

		/**
		 * Asserts the {@link #getSubject() subject} is not {@literal null}.
		 */
		default void isNotNull() {
			assertIsNotNull(getSubject());
		}

		/**
		 * Asserts the {@link #getSubject()} is an instance of {@link GemFireCacheImpl}.
		 */
		default void isInstanceOfGemFireCacheImpl() {
			assertIsInstanceOf(getSubject(), GemFireCacheImpl.class);
		}

		/**
		 * Asserts the {@link #getSubject()} is an instance of {@link InternalDistributedSystem}.
		 */
		default void isInstanceOfInternalDistributedSystem() {
			assertIsInstanceOf(getSubject(), InternalDistributedSystem.class);
		}

		/**
		 * Asserts the {@link #getSubject()} is not an instance of {@link AbstractRegion}.
		 */
		default void isNotInstanceOfAbstractRegion() {
			assertIsNotInstanceOf(getSubject(), AbstractRegion.class);
		}

		/**
		 * Asserts the {@link #getSubject()} is not an instance of {@link GemFireCacheImpl}.
		 */
		default void isNotInstanceOfGemFireCacheImpl() {
			assertIsNotInstanceOf(getSubject(), GemFireCacheImpl.class);
		}

		/**
		 * Asserts the {@link #getSubject()} is not an instance of {@link InternalDistributedSystem}.
		 */
		default void isNotInstanceOfInternalDistributedSystem() {
			assertIsNotInstanceOf(getSubject(), InternalDistributedSystem.class);
		}
	}
}
