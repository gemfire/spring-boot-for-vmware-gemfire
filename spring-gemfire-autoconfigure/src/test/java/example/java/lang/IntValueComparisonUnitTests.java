/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.java.lang;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Integration Tests testing different comparisons of {@link Integer#TYPE} values.
 *
 * @author John Blum
 * @see java.lang.Integer
 * @see org.junit.Test
 * @since 2.3.0
 */
public class IntValueComparisonUnitTests {

	@Test
	public void zeroIsEqualToNegativeZero() {
		// Of course, this better be always 'true'!
		assertThat(0).isEqualTo(-0);
	}
}
