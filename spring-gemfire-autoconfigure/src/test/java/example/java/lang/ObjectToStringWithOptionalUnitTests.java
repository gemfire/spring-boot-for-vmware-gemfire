/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package example.java.lang;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

/**
 * Unit test for {@link Object#toString()} with {@link Optional} of {@literal null}.
 *
 * @author John Blum
 * @see java.lang.Object
 * @see java.util.Optional
 * @since 1.0.0
 */
public class ObjectToStringWithOptionalUnitTests {

	@Test
	@SuppressWarnings("all")
	public void objectToStringWithOptionalOfNull() {

		assertThat(Optional.ofNullable(null).map(Object::toString).orElse("test"))
			.isEqualTo("test");
	}

	@Test(expected = NullPointerException.class)
	public void objectToStringWithStreamOfElementsContainingNullThrowsNullPointerException() {

		assertThat(Stream.of(1, null, 3).map(Object::toString).collect(Collectors.toList()))
			.containsExactly("1", "null", "3");
	}

	@Test
	public void stringValueOfWithStreamOfElementsContainingNullIsNullSafe() {

		assertThat(Stream.of(1, null, 3).map(String::valueOf).collect(Collectors.toList()))
			.containsExactly("1", "null", "3");
	}
}
