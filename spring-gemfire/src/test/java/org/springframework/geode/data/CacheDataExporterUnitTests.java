/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.data;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.Test;

import org.apache.geode.cache.Region;

import org.springframework.data.gemfire.ResolvableRegionFactoryBean;

/**
 * Unit Tests for {@link CacheDataExporter}
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.Region
 * @see org.springframework.geode.data.CacheDataExporter
 * @since 1.3.0
 */
public class CacheDataExporterUnitTests {

	@Test
	public void postProcessBeforeDestructionCallsExportFromGivenARegion() {

		Region<?, ?> mockRegion = mock(Region.class);

		CacheDataExporter exporter = mock(CacheDataExporter.class);

		doCallRealMethod().when(exporter).postProcessBeforeDestruction(any(), anyString());

		exporter.postProcessBeforeDestruction(mockRegion, "TestRegion");

		verify(exporter, times(1)).exportFrom(eq(mockRegion));
		verifyNoInteractions(mockRegion);
	}

	@Test
	public void postProcessBeforeDestructionCallsExportFromGivenAResolvableRegionFactoryBean() {

		ResolvableRegionFactoryBean<?, ?> mockRegionFactoryBean = mock(ResolvableRegionFactoryBean.class);

		Region<?, ?> mockRegion = mock(Region.class);

		CacheDataExporter exporter = mock(CacheDataExporter.class);

		doReturn(mockRegion).when(mockRegionFactoryBean).getRegion();
		doCallRealMethod().when(exporter).postProcessBeforeDestruction(any(), anyString());

		exporter.postProcessBeforeDestruction(mockRegionFactoryBean, "TestRegion");

		verify(mockRegionFactoryBean, times(1)).getRegion();
		verify(exporter, times(1)).exportFrom(eq(mockRegion));
		verifyNoInteractions(mockRegion);
	}

	@Test
	public void postProcessBeforeDestructionWillNotCallExportFromGivenAnObject() {

		CacheDataExporter exporter = mock(CacheDataExporter.class);

		doCallRealMethod().when(exporter).postProcessBeforeDestruction(any(), anyString());

		exporter.postProcessBeforeDestruction(new Object(), "TestRegion");

		verify(exporter, never()).exportFrom(any(Region.class));
	}

	@Test
	public void postProcessBeforeDestructionIsNullSafe() {

		CacheDataExporter exporter = mock(CacheDataExporter.class);

		doCallRealMethod().when(exporter).postProcessBeforeDestruction(any(), anyString());

		exporter.postProcessBeforeDestruction(null, "TestBean");

		verify(exporter, never()).exportFrom(any(Region.class));
	}
}
