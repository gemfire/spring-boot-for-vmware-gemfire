/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.core.io.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.springframework.core.io.ResourceLoader;

/**
 * Unit Tests for {@link ResourcePrefix}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @since 1.3.1
 */
public class ResourcePrefixUnitTests {

	@Test
	public void fromReturnsResourcePrefix() {

		assertThat(ResourcePrefix.from(ResourceLoader.CLASSPATH_URL_PREFIX))
			.isEqualTo(ResourcePrefix.CLASSPATH_URL_PREFIX);

		for (ResourcePrefix prefix : ResourcePrefix.values()) {
			assertThat(ResourcePrefix.from(prefix.toString())).isEqualTo(prefix);
		}
	}

	@Test
	public void fromInvalidPrefixReturnsNull() {

		assertThat(ResourcePrefix.from("ftp:")).isNull();
		assertThat(ResourcePrefix.from("scp:")).isNull();
		assertThat(ResourcePrefix.from("smtp:")).isNull();
		assertThat(ResourcePrefix.from("  ")).isNull();
		assertThat(ResourcePrefix.from("")).isNull();
		assertThat(ResourcePrefix.from(null)).isNull();
	}

	@Test
	public void getProtocolIsCorrect() {

		assertThat(ResourcePrefix.CLASSPATH_URL_PREFIX.getProtocol()).isEqualTo("classpath");
		assertThat(ResourcePrefix.FILESYSTEM_URL_PREFIX.getProtocol()).isEqualTo("file");
		assertThat(ResourcePrefix.HTTP_URL_PREFIX.getProtocol()).isEqualTo("http");
	}

	@Test
	public void toUrlPrefix() {

		assertThat(ResourcePrefix.CLASSPATH_URL_PREFIX.toUrlPrefix()).isEqualTo("classpath:");
		assertThat(ResourcePrefix.FILESYSTEM_URL_PREFIX.toUrlPrefix()).isEqualTo("file://");
		assertThat(ResourcePrefix.HTTP_URL_PREFIX.toUrlPrefix()).isEqualTo("http://");
	}
}
