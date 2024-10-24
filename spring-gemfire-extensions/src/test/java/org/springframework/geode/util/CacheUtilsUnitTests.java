/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.RegionService;

/**
 * Unit Tests for {@link CacheUtils}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.RegionAttributes
 * @see org.apache.geode.cache.RegionService
 * @see org.apache.geode.cache.client.ClientCache
 * @since 1.3.0
 */
@SuppressWarnings("unchecked")
public class CacheUtilsUnitTests {

	@Test
	public void collectValuesFromClientRegion() {

		Region<Object, Object> mockRegion = mock(Region.class);

		RegionAttributes<Object, Object> mockRegionAttributes = mock(RegionAttributes.class);

		RegionService mockRegionService = mock(ClientCache.class);

		Set<?> keySetOnServer = new TreeSet<>(Arrays.asList(1, 2, 3));

		Map<Object, Object> keysValues = new HashMap<>();

		keysValues.put(1, "one");
		keysValues.put(2, "two");
		keysValues.put(3, "three");

		doReturn(mockRegionAttributes).when(mockRegion).getAttributes();
		doReturn(mockRegionService).when(mockRegion).getRegionService();
		doReturn(keySetOnServer).when(mockRegion).keySetOnServer();
		doReturn(keysValues).when(mockRegion).getAll(eq(keySetOnServer));
		doReturn(DataPolicy.EMPTY).when(mockRegionAttributes).getDataPolicy();

		assertThat(CacheUtils.collectValues(mockRegion)).containsExactlyInAnyOrder(keysValues.values().toArray());

		verify(mockRegion, times(2)).getAttributes();
		verify(mockRegion, times(1)).keySetOnServer();
		verify(mockRegion, times(1)).getAll(eq(keySetOnServer));
		verify(mockRegion, never()).values();
		verify(mockRegionAttributes, times(1)).getDataPolicy();
		verify(mockRegionAttributes, never()).getPoolName();
		verifyNoInteractions(mockRegionService);
		verifyNoMoreInteractions(mockRegion);
	}

	@Test
	public void collectValuesFromClientRegionWhenClientRegionGetAllKeysReturnsNullMapIsNullSafe() {

		Region<Object, Object> mockRegion = mock(Region.class);

		RegionAttributes<Object, Object> mockRegionAttributes = mock(RegionAttributes.class);

		RegionService mockRegionService = mock(ClientCache.class);

		Set<Object> keySetOnServer = new TreeSet<>(Arrays.asList(1, 2));

		doReturn(mockRegionAttributes).when(mockRegion).getAttributes();
		doReturn(mockRegionService).when(mockRegion).getRegionService();
		doReturn(keySetOnServer).when(mockRegion).keySetOnServer();
		doReturn(null).when(mockRegion).getAll(eq(keySetOnServer));
		doReturn(DataPolicy.NORMAL).when(mockRegionAttributes).getDataPolicy();
		doReturn("Car").when(mockRegionAttributes).getPoolName();

		Collection<Object> values = CacheUtils.collectValues(mockRegion);

		assertThat(values).isNotNull();
		assertThat(values).isEmpty();

		verify(mockRegion, times(3)).getAttributes();
		verify(mockRegion, times(1)).keySetOnServer();
		verify(mockRegion, times(1)).getAll(eq(keySetOnServer));
		verify(mockRegion, never()).values();
		verify(mockRegionAttributes, times(1)).getDataPolicy();
		verify(mockRegionAttributes, times(1)).getPoolName();
		verifyNoInteractions(mockRegionService);
		verifyNoMoreInteractions(mockRegion);
	}

	@Test
	public void collectValuesFromClientRegionWhenClientRegionKeySetOnServerReturnsNullSetIsNullSafe() {

		Region<Object, Object> mockRegion = mock(Region.class);

		RegionAttributes<Object, Object> mockRegionAttributes = mock(RegionAttributes.class);

		RegionService mockRegionService = mock(ClientCache.class);

		doReturn(mockRegionAttributes).when(mockRegion).getAttributes();
		doReturn(mockRegionService).when(mockRegion).getRegionService();
		doReturn(null).when(mockRegion).keySetOnServer();
		doReturn(DataPolicy.PARTITION).when(mockRegionAttributes).getDataPolicy();
		doReturn("Dead").when(mockRegionAttributes).getPoolName();

		Collection<Object> values = CacheUtils.collectValues(mockRegion);

		assertThat(values).isNotNull();
		assertThat(values).isEmpty();

		verify(mockRegion, times(3)).getAttributes();
		verify(mockRegion, times(1)).keySetOnServer();
		verify(mockRegion, never()).getAll(any());
		verify(mockRegion, never()).values();
		verify(mockRegionAttributes, times(1)).getDataPolicy();
		verify(mockRegionAttributes, times(1)).getPoolName();
		verifyNoInteractions(mockRegionService);
		verifyNoMoreInteractions(mockRegion);
	}

	@Test
	public void collectValuesFromClientRegionWhenClientRegionGetRegionServiceReturnsNullIsNullSafe() {

		Collection<Object> values = Arrays.asList("one", "two", "three");

		Region<Object, Object> mockRegion = mock(Region.class);

		doReturn(null).when(mockRegion).getRegionService();
		doReturn(values).when(mockRegion).values();

		assertThat(CacheUtils.collectValues(mockRegion)).containsExactlyInAnyOrder(values.toArray());

		verify(mockRegion, times(1)).values();
	}

	@Test
	public void collectValuesFromLocalClientRegion() {

		Region<?, String> mockRegion = mock(Region.class);

		RegionAttributes<?, String> mockRegionAttributes = mock(RegionAttributes.class);

		RegionService mockRegionService = mock(ClientCache.class);

		doReturn(mockRegionAttributes).when(mockRegion).getAttributes();
		doReturn(mockRegionService).when(mockRegion).getRegionService();
		doReturn(DataPolicy.NORMAL).when(mockRegionAttributes).getDataPolicy();
		doReturn("  ").when(mockRegionAttributes).getPoolName();
		doReturn(Arrays.asList("one", "two")).when(mockRegion).values();

		Collection<String> values = CacheUtils.collectValues(mockRegion);

		assertThat(values).isNotNull();
		assertThat(values).containsExactly("one", "two");

		verify(mockRegion, times(3)).getAttributes();
		verify(mockRegion, times(1)).values();
		verify(mockRegion, never()).keySetOnServer();
		verify(mockRegion, never()).getAll(any());
		verify(mockRegionAttributes, times(1)).getDataPolicy();
		verify(mockRegionAttributes, times(1)).getPoolName();
		verifyNoInteractions(mockRegionService);
	}

	@Test(expected = IllegalArgumentException.class)
	public void collectValuesWithNullRegionThrowsIllegalArgumentException() {

		try {
			CacheUtils.collectValues(null);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessageStartingWith("Argument must not be null");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void isProxyRegionWithProxyRegionBasedOnDataPolicy() {

		Region<?, ?> mockRegion = mock(Region.class);

		RegionAttributes<?, ?> mockRegionAttributes = mock(RegionAttributes.class);

		doReturn(mockRegionAttributes).when(mockRegion).getAttributes();
		doReturn(DataPolicy.EMPTY).when(mockRegionAttributes).getDataPolicy();

		assertThat(CacheUtils.isProxyRegion(mockRegion)).isTrue();

		verify(mockRegion, times(2)).getAttributes();
		verify(mockRegionAttributes, times(1)).getDataPolicy();
		verify(mockRegionAttributes, never()).getPoolName();
	}

	@Test
	public void isProxyRegionWithProxyRegionBasedOnPoolConfiguration() {

		Region<?, ?> mockRegion = mock(Region.class);

		RegionAttributes<?, ?> mockRegionAttributes = mock(RegionAttributes.class);

		doReturn(mockRegionAttributes).when(mockRegion).getAttributes();
		doReturn(DataPolicy.NORMAL).when(mockRegionAttributes).getDataPolicy();
		doReturn("TestPool").when(mockRegionAttributes).getPoolName();

		assertThat(CacheUtils.isProxyRegion(mockRegion)).isTrue();

		verify(mockRegion, times(3)).getAttributes();
		verify(mockRegionAttributes, times(1)).getDataPolicy();
		verify(mockRegionAttributes, times(1)).getPoolName();
	}

	@Test
	public void isProxyRegionWithNonProxyRegion() {

		Region<?, ?> mockRegion = mock(Region.class);

		RegionAttributes<?, ?> mockRegionAttributes = mock(RegionAttributes.class);

		doReturn(mockRegionAttributes).when(mockRegion).getAttributes();
		doReturn(DataPolicy.NORMAL).when(mockRegionAttributes).getDataPolicy();
		doReturn("  ").when(mockRegionAttributes).getPoolName();

		assertThat(CacheUtils.isProxyRegion(mockRegion)).isFalse();

		verify(mockRegion, times(3)).getAttributes();
		verify(mockRegionAttributes, times(1)).getDataPolicy();
		verify(mockRegionAttributes, times(1)).getPoolName();
	}

	@Test
	public void isProxyRegionWithRegionHavingNoAttributesIsNullSafe() {

		Region<?, ?> mockRegion = mock(Region.class);

		assertThat(CacheUtils.isProxyRegion(mockRegion)).isFalse();

		verify(mockRegion, times(1)).getAttributes();
		verifyNoMoreInteractions(mockRegion);
	}

	@Test
	public void isProxyRegionWithNullRegionIsNullSafe() {
		assertThat(CacheUtils.isProxyRegion(null)).isFalse();
	}

	@Test
	public void isRegionWithPoolWithPooledRegion() {

		Region<?, ?> mockRegion = mock(Region.class);

		RegionAttributes<?, ?> mockRegionAttributes = mock(RegionAttributes.class);

		doReturn(mockRegionAttributes).when(mockRegion).getAttributes();
		doReturn("Swimming").when(mockRegionAttributes).getPoolName();

		assertThat(CacheUtils.isRegionWithPool(mockRegion)).isTrue();

		verify(mockRegion, times(1)).getAttributes();
		verify(mockRegionAttributes, times(1)).getPoolName();
	}

	@Test
	public void isRegionWithPoolUsingRegionHavingNoAttributesIsNullSafe() {

		Region<?, ?> mockRegion = mock(Region.class);

		assertThat(CacheUtils.isRegionWithPool(mockRegion)).isFalse();

		verify(mockRegion, times(1)).getAttributes();
		verifyNoMoreInteractions(mockRegion);
	}

	@Test
	public void isRegionWithPoolUsingNullRegionIsNullSafe() {
		assertThat(CacheUtils.isRegionWithPool(null)).isFalse();
	}

	private void testIsRegionWithPoolUsingRegionWithInvalidPoolNameReturnsFalse(String poolName) {

		Region<?, ?> mockRegion = mock(Region.class);

		RegionAttributes<?, ?> mockRegionAttributes = mock(RegionAttributes.class);

		doReturn(mockRegionAttributes).when(mockRegion).getAttributes();
		doReturn(poolName).when(mockRegionAttributes).getPoolName();

		assertThat(CacheUtils.isRegionWithPool(mockRegion)).isFalse();

		verify(mockRegion, times(1)).getAttributes();
		verify(mockRegionAttributes, times(1)).getPoolName();
	}

	@Test
	public void isRegionWithPoolUsingRegionConfiguredWithBlankPoolName() {
		testIsRegionWithPoolUsingRegionWithInvalidPoolNameReturnsFalse("  ");
	}

	@Test
	public void isRegionWithPoolUsingRegionConfiguredWithEmptyPoolName() {
		testIsRegionWithPoolUsingRegionWithInvalidPoolNameReturnsFalse("");
	}

	@Test
	public void isRegionWithPoolUsingRegionConfiguredWithNullPoolName() {
		testIsRegionWithPoolUsingRegionWithInvalidPoolNameReturnsFalse(null);
	}
}
