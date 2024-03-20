/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Predicate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.CacheLoader;

import org.springframework.data.gemfire.PeerRegionFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.repository.CrudRepository;

/**
 * Unit Tests for {@link RepositoryCacheLoaderRegionConfigurer}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.mockito.junit.MockitoJUnitRunner
 * @see org.apache.geode.cache.CacheLoader
 * @see org.springframework.data.gemfire.PeerRegionFactoryBean
 * @see org.springframework.data.gemfire.client.ClientRegionFactoryBean
 * @see org.springframework.data.repository.CrudRepository
 * @see org.springframework.geode.cache.RepositoryCacheLoader
 * @see org.springframework.geode.cache.RepositoryCacheLoaderRegionConfigurer
 * @since 1.1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class RepositoryCacheLoaderRegionConfigurerUnitTests {

	@Mock
	private CrudRepository<?, ?> mockRepository;

	@Mock
	private Predicate<String> mockPredicate;

	@Test
	public void constructRepositoryCacheLoaderRegionConfigurerIsSuccessful() {

		RepositoryCacheLoaderRegionConfigurer<?, ?> regionConfigurer =
			new RepositoryCacheLoaderRegionConfigurer<>(this.mockRepository, this.mockPredicate);

		assertThat(regionConfigurer).isNotNull();
		assertThat(regionConfigurer.getRegionBeanName()).isEqualTo(this.mockPredicate);
		assertThat(regionConfigurer.getRepository()).isEqualTo(this.mockRepository);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructRepositoryCacheLoaderRegionConfigurerWithNoRepositoryThrowsException() {

		try {
			new RepositoryCacheLoaderRegionConfigurer<>(null, this.mockPredicate);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("CrudRepository is required");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void configuresClientRegionFactoryBeanWithRepositoryCacheLoaderWhenPredicateReturnsTrue() {

		ClientRegionFactoryBean<?, ?> clientRegionFactoryBean = spy(new ClientRegionFactoryBean());

		doAnswer(answer -> {

			CacheLoader cacheLoader = answer.getArgument(0);

			assertThat(cacheLoader).isInstanceOf(RepositoryCacheLoader.class);
			assertThat(((RepositoryCacheLoader) cacheLoader).getRepository()).isEqualTo(this.mockRepository);

			return null;

		}).when(clientRegionFactoryBean).setCacheLoader(isA(CacheLoader.class));

		when(this.mockPredicate.test(anyString())).thenReturn(true);

		RepositoryCacheLoaderRegionConfigurer regionConfigurer =
			new RepositoryCacheLoaderRegionConfigurer(this.mockRepository, this.mockPredicate);

		regionConfigurer.configure("Example", clientRegionFactoryBean);

		verify(clientRegionFactoryBean, times(1))
			.setCacheLoader(isA(RepositoryCacheLoader.class));

		verify(this.mockPredicate, times(1)).test(eq("Example"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void doesNotConfigureClientRegionFactoryBeanWithRepositoryCacheLoaderWhenPredicateReturnsFalse() {

		when(this.mockPredicate.test(anyString())).thenReturn(false);

		ClientRegionFactoryBean<?, ?> clientRegionFactoryBean = spy(new ClientRegionFactoryBean());

		RepositoryCacheLoaderRegionConfigurer regionConfigurer =
			new RepositoryCacheLoaderRegionConfigurer(this.mockRepository, this.mockPredicate);

		regionConfigurer.configure("Example", clientRegionFactoryBean);

		verify(clientRegionFactoryBean, never()).setCacheLoader(any(CacheLoader.class));
		verify(this.mockPredicate, times(1)).test(eq("Example"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void configuresPeerRegionFactoryBeanWithRepositoryCacheLoaderWhenPredicateReturnsTrue() {

		PeerRegionFactoryBean<?, ?> peerRegionFactoryBean = mock(PeerRegionFactoryBean.class);

		doAnswer(answer -> {

			CacheLoader cacheLoader = answer.getArgument(0);

			assertThat(cacheLoader).isInstanceOf(RepositoryCacheLoader.class);
			assertThat(((RepositoryCacheLoader) cacheLoader).getRepository()).isEqualTo(this.mockRepository);

			return null;

		}).when(peerRegionFactoryBean).setCacheLoader(isA(CacheLoader.class));

		when(this.mockPredicate.test(anyString())).thenReturn(true);

		RepositoryCacheLoaderRegionConfigurer regionConfigurer =
			new RepositoryCacheLoaderRegionConfigurer(this.mockRepository, this.mockPredicate);

		regionConfigurer.configure("Example", peerRegionFactoryBean);

		verify(peerRegionFactoryBean, times(1))
			.setCacheLoader(isA(RepositoryCacheLoader.class));

		verify(this.mockPredicate, times(1)).test(eq("Example"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void doesNotConfigurePeerRegionFactoryBeanWithRepositoryCacheLoaderWhenPredicateReturnsFalse() {

		when(this.mockPredicate.test(anyString())).thenReturn(false);

		PeerRegionFactoryBean<?, ?> peerRegionFactoryBean = mock(PeerRegionFactoryBean.class);

		RepositoryCacheLoaderRegionConfigurer regionConfigurer =
			new RepositoryCacheLoaderRegionConfigurer(this.mockRepository, this.mockPredicate);

		regionConfigurer.configure("Example", peerRegionFactoryBean);

		verify(peerRegionFactoryBean, never()).setCacheLoader(any(CacheLoader.class));
		verify(this.mockPredicate, times(1)).test(eq("Example"));
	}
}
