/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.geode.core.env.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Unit tests for {@link Service}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.geode.core.env.support.Service
 * @since 1.0.0
 */
public class ServiceUnitTests {

	@Test
	public void withNameReturnsNewService() {

		Service service = Service.with("test");

		assertThat(service).isNotNull();
		assertThat(service.getName()).isEqualTo("test");
	}

	private void testWithInvalidNameThrowsIllegalArgumentException(String name) {

		try {
			Service.with(name);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("Service name [%s] is required", name);
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void withBlankServiceNameThrowsIllegalArgumentException() {
		testWithInvalidNameThrowsIllegalArgumentException("  ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void withEmptyServiceNameThrowsIllegalArgumentException() {
		testWithInvalidNameThrowsIllegalArgumentException("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void withNullServiceNameThrowsIllegalArgumentException() {
		testWithInvalidNameThrowsIllegalArgumentException("");
	}

	@Test
	public void toStringReturnsServiceName() {
		assertThat(Service.with("test").toString()).isEqualTo("test");
	}
}
