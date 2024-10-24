/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
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
import org.apache.geode.cache.CacheWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.repository.CrudRepository;

/**
 * Unit Tests for {@link RepositoryCacheWriterRegionConfigurer}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.mockito.junit.MockitoJUnitRunner
 * @see org.apache.geode.cache.CacheWriter
 * @see org.springframework.data.gemfire.client.ClientRegionFactoryBean
 * @see org.springframework.data.gemfire.client.ClientRegionFactoryBean
 * @see org.springframework.data.repository.CrudRepository
 * @see org.springframework.geode.cache.RepositoryCacheWriter
 * @see org.springframework.geode.cache.RepositoryCacheWriterRegionConfigurer
 * @since 1.1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class RepositoryCacheWriterRegionConfigurerUnitTests {

	@Mock
	private CrudRepository<?, ?> mockRepository;

	@Mock
	private Predicate<String> mockPredicate;

	@Test
	public void constructRepositoryCacheWriterRegionConfigurerIsSuccessful() {

		RepositoryCacheWriterRegionConfigurer<?, ?> regionConfigurer =
			new RepositoryCacheWriterRegionConfigurer<>(this.mockRepository, this.mockPredicate);

		assertThat(regionConfigurer).isNotNull();
		assertThat(regionConfigurer.getRegionBeanName()).isEqualTo(this.mockPredicate);
		assertThat(regionConfigurer.getRepository()).isEqualTo(this.mockRepository);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructRepositoryCacheWriterRegionConfigurerWithNullRepositoryThrowsException() {

		try {
			new RepositoryCacheWriterRegionConfigurer<>(null, this.mockPredicate);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("CrudRepository is required");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void configuresClientRegionFactoryBeanWithRepositoryCacheWriterWhenPredicateReturnsTrue() {

		ClientRegionFactoryBean<?, ?> clientRegionFactoryBean = spy(new ClientRegionFactoryBean());

		doAnswer(answer -> {

			CacheWriter cacheWriter = answer.getArgument(0);

			assertThat(cacheWriter).isInstanceOf(RepositoryCacheWriter.class);
			assertThat(((RepositoryCacheWriter) cacheWriter).getRepository()).isEqualTo(this.mockRepository);

			return null;

		}).when(clientRegionFactoryBean).setCacheWriter(isA(CacheWriter.class));

		when(this.mockPredicate.test(anyString())).thenReturn(true);

		RepositoryCacheWriterRegionConfigurer regionConfigurer =
			new RepositoryCacheWriterRegionConfigurer(this.mockRepository, this.mockPredicate);

		regionConfigurer.configure("Example", clientRegionFactoryBean);

		verify(clientRegionFactoryBean, times(1))
			.setCacheWriter(isA(RepositoryCacheWriter.class));

		verify(this.mockPredicate, times(1)).test(eq("Example"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void doesNotConfigureClientRegionFactoryBeanWithRepositoryCacheWriterWhenPredicateReturnsFalse() {

		when(this.mockPredicate.test(anyString())).thenReturn(false);

		ClientRegionFactoryBean<?, ?> clientRegionFactoryBean = spy(new ClientRegionFactoryBean());

		RepositoryCacheWriterRegionConfigurer regionConfigurer =
			new RepositoryCacheWriterRegionConfigurer(this.mockRepository, this.mockPredicate);

		regionConfigurer.configure("Example", clientRegionFactoryBean);

		verify(clientRegionFactoryBean, never()).setCacheWriter(any(CacheWriter.class));
		verify(this.mockPredicate, times(1)).test(eq("Example"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void configuresPeerRegionFactoryBeanWithRepositoryCacheWriterWhenPredicateReturnsTrue() {

		ClientRegionFactoryBean<?, ?> peerRegionFactoryBean = mock(ClientRegionFactoryBean.class);

		doAnswer(answer -> {

			CacheWriter cacheWriter = answer.getArgument(0);

			assertThat(cacheWriter).isInstanceOf(RepositoryCacheWriter.class);
			assertThat(((RepositoryCacheWriter) cacheWriter).getRepository()).isEqualTo(this.mockRepository);

			return null;

		}).when(peerRegionFactoryBean).setCacheWriter(isA(CacheWriter.class));

		when(this.mockPredicate.test(anyString())).thenReturn(true);

		RepositoryCacheWriterRegionConfigurer regionConfigurer =
			new RepositoryCacheWriterRegionConfigurer(this.mockRepository, this.mockPredicate);

		regionConfigurer.configure("Example", peerRegionFactoryBean);

		verify(peerRegionFactoryBean, times(1))
			.setCacheWriter(isA(RepositoryCacheWriter.class));

		verify(this.mockPredicate, times(1)).test(eq("Example"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void doesNotConfigurePeerRegionFactoryBeanWithRepositoryCacheWriterWhenPredicateReturnsFalse() {

		when(this.mockPredicate.test(anyString())).thenReturn(false);

		ClientRegionFactoryBean<?, ?> peerRegionFactoryBean = mock(ClientRegionFactoryBean.class);

		RepositoryCacheWriterRegionConfigurer regionConfigurer =
			new RepositoryCacheWriterRegionConfigurer(this.mockRepository, this.mockPredicate);

		regionConfigurer.configure("Example", peerRegionFactoryBean);

		verify(peerRegionFactoryBean, never()).setCacheWriter(any(CacheWriter.class));
		verify(this.mockPredicate, times(1)).test(eq("Example"));
	}
}
