/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.core.io.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;

import org.springframework.core.io.Resource;

/**
 * Unit Tests for {@link ByteArrayResourceReader}.
 *
 * @author John Blum
 * @see java.io.ByteArrayInputStream
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.springframework.core.io.Resource
 * @see org.springframework.geode.core.io.support.ByteArrayResourceReader
 * @since 1.3.1
 */
public class ByteArrayResourceReaderUnitTests {

	@Test
	public void doReadResourceReturnsByteArray() throws IOException {

		byte[] data = { (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE };

		ByteArrayInputStream in = new ByteArrayInputStream(data);

		ByteArrayResourceReader reader = spy(new ByteArrayResourceReader());

		Resource mockResource = mock(Resource.class);

		doReturn(in).when(mockResource).getInputStream();
		doReturn(2).when(reader).getBufferSize();

		assertThat(reader.read(mockResource)).isEqualTo(data);

		verify(reader, times(1)).read(eq(mockResource));
		verify(mockResource, times(1)).getInputStream();
		verify(reader, times(1)).doRead(eq(in));
		verifyNoMoreInteractions(mockResource);
	}

	@Test
	public void getBufferSizeIsDefault() {
		assertThat(new ByteArrayResourceReader().getBufferSize())
			.isEqualTo(ByteArrayResourceReader.DEFAULT_BUFFER_SIZE);
	}
}
