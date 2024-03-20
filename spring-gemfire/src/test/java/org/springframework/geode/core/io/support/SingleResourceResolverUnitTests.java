/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.core.io.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.Test;

import org.springframework.core.io.Resource;

/**
 * Unit Tests for {@link SingleResourceResolver}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.springframework.core.io.Resource
 * @see org.springframework.geode.core.io.ResourceResolver
 * @see org.springframework.geode.core.io.support.SingleResourceResolver
 * @since 1.3.1
 */
public class SingleResourceResolverUnitTests {

	@Test
	public void constructsSingleResourceResolverWithNonNullResource() {

		Resource mockResource = mock(Resource.class);

		SingleResourceResolver resourceResolver = new SingleResourceResolver(mockResource);

		assertThat(resourceResolver.resolve("/location/of/resource").orElse(null)).isEqualTo(mockResource);
		assertThat(resourceResolver.resolve("/path/to/resource").orElse(null)).isEqualTo(mockResource);
		assertThat(resourceResolver.resolve("/another/path/to/resource").orElse(null)).isEqualTo(mockResource);
		assertThat(resourceResolver.resolve("/yet/another/path/to/resource").orElse(null)).isEqualTo(mockResource);

		verifyNoInteractions(mockResource);
	}

	@Test
	public void constructSingleResourceResolverWithNullResource() {

		SingleResourceResolver resourceResolver = new SingleResourceResolver(null);

		assertThat(resourceResolver.resolve("/location/of/resource").orElse(null)).isNull();
		assertThat(resourceResolver.resolve("/path/to/resource").orElse(null)).isNull();
	}
}
