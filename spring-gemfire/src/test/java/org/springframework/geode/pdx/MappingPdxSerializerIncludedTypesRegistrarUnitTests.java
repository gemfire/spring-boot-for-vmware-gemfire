/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.pdx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import java.util.Optional;
import java.util.function.Predicate;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.pdx.PdxSerializer;
import org.junit.Test;
import org.springframework.data.gemfire.mapping.MappingPdxSerializer;

/**
 * Unit Tests for {@link MappingPdxSerializerIncludedTypesRegistrar}.
 *
 * @author John Blum
 * @see Test
 * @see org.mockito.Mockito
 * @see ClientCache
 * @see PdxSerializer
 * @see MappingPdxSerializerIncludedTypesRegistrar
 * @since 1.5.0
 */
public class MappingPdxSerializerIncludedTypesRegistrarUnitTests {

	@Test
	public void withClassTypesIsCorrect() {

		Class<?>[] types = { TypeOne.class, TypeTwo.class };

		MappingPdxSerializerIncludedTypesRegistrar registrar = MappingPdxSerializerIncludedTypesRegistrar.with(types);

		assertThat(registrar).isNotNull();
		assertThat(registrar.getTypes()).containsExactly(types);

	}

	@Test
	public void withClassTypesContainingNullTypesIsCorrect() {

		Class<?>[] types = { null, TypeOne.class, null, null, TypeTwo.class, null, null };

		MappingPdxSerializerIncludedTypesRegistrar registrar = MappingPdxSerializerIncludedTypesRegistrar.with(types);

		assertThat(registrar).isNotNull();
		assertThat(registrar.getTypes()).containsExactly(TypeOne.class, TypeTwo.class);
	}

	@Test
	public void withNullClassTypesIsNullSafe() {

		MappingPdxSerializerIncludedTypesRegistrar registrar =
			MappingPdxSerializerIncludedTypesRegistrar.with((Class<?>[]) null);

		assertThat(registrar).isNotNull();
		assertThat(registrar.getTypes()).isNotNull();
		assertThat(registrar.getTypes()).isEmpty();
	}

	@Test
	public void getCompositeIncludeTypeFilterIsCorrect() {

		MappingPdxSerializerIncludedTypesRegistrar registrar =
			MappingPdxSerializerIncludedTypesRegistrar.with(TypeOne.class, TypeTwo.class);

		assertThat(registrar).isNotNull();
		assertThat(registrar.getTypes()).containsExactly(TypeOne.class, TypeTwo.class);

		Optional<Predicate<Class<?>>> composite = registrar.getCompositeIncludeTypeFilter();

		assertThat(composite).isNotNull();
		assertThat(composite.isPresent()).isTrue();

		assertThat(composite.get().test(TypeOne.class)).isTrue();
		assertThat(composite.get().test(TypeTwo.class)).isTrue();
		assertThat(composite.get().test(SubtypeOne.class)).isTrue();
		assertThat(composite.get().test(TypeThree.class)).isFalse();
		assertThat(composite.get().test(null)).isFalse();
	}

	@Test
	public void getCompositeIncludeTypeFilterIsNullSafe() {

		MappingPdxSerializerIncludedTypesRegistrar registrar = MappingPdxSerializerIncludedTypesRegistrar.with();

		assertThat(registrar).isNotNull();
		assertThat(registrar.getTypes()).isEmpty();

		Optional<Predicate<Class<?>>> composite = registrar.getCompositeIncludeTypeFilter();

		assertThat(composite).isNotNull();
		assertThat(composite.isPresent()).isFalse();
	}

	@Test
	public void newIncludeTypeFilterWithNonNullType() {

		Predicate<Class<?>> predicate =
			MappingPdxSerializerIncludedTypesRegistrar.with().newIncludeTypeFilter(SubtypeOne.class);

		assertThat(predicate).isNotNull();
		assertThat(predicate.test(SubtypeOne.class)).isTrue();
		assertThat(predicate.test(TypeOne.class)).isFalse();
		assertThat(predicate.test(TypeTwo.class)).isFalse();
		assertThat(predicate.test(null)).isFalse();
	}

	@Test
	public void newIncludeTypeFilterWithNullType() {
		assertThat(MappingPdxSerializerIncludedTypesRegistrar.with().newIncludeTypeFilter(null)).isNull();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void postProcessGemFireCacheAfterInitializationIsCorrect() {

		ClientCache mockCache = mock(ClientCache.class);

		MappingPdxSerializer mockPdxSerializer = mock(MappingPdxSerializer.class);

		doReturn(mockPdxSerializer).when(mockCache).getPdxSerializer();

		MappingPdxSerializerIncludedTypesRegistrar registrar =
			MappingPdxSerializerIncludedTypesRegistrar.with(TypeOne.class);

		assertThat(registrar).isNotNull();

		assertThat(registrar.postProcessAfterInitialization(mockCache, "MockCache")).isEqualTo(mockCache);

		verify(mockCache, times(1)).getPdxSerializer();
		verify(mockPdxSerializer, times(1)).setIncludeTypeFilters(isA(Predicate.class));

		verifyNoMoreInteractions(mockCache, mockPdxSerializer);
	}

	@Test
	public void postProcessGemFireCacheAfterInitializationWithNoRegisteredTypes() {

		ClientCache mockCache = mock(ClientCache.class);

		MappingPdxSerializer mockPdxSerializer = mock(MappingPdxSerializer.class);

		doReturn(mockPdxSerializer).when(mockCache).getPdxSerializer();

		MappingPdxSerializerIncludedTypesRegistrar registrar = MappingPdxSerializerIncludedTypesRegistrar.with();

		assertThat(registrar).isNotNull();
		assertThat(registrar.postProcessAfterInitialization(mockCache, "MockCache")).isEqualTo(mockCache);

		verify(mockCache, times(1)).getPdxSerializer();
		verifyNoMoreInteractions(mockCache);
		verifyNoInteractions(mockPdxSerializer);
	}

	@Test
	public void postProcessGemFireCacheAfterInitializationWithNoPdxSerializer() {

		ClientCache mockCache = mock(ClientCache.class);

		doReturn(null).when(mockCache).getPdxSerializer();

		MappingPdxSerializerIncludedTypesRegistrar registrar = spy(MappingPdxSerializerIncludedTypesRegistrar.with());

		assertThat(registrar).isNotNull();
		assertThat(registrar.postProcessAfterInitialization(mockCache, "MockCache")).isEqualTo(mockCache);

		verify(mockCache, times(1)).getPdxSerializer();
		verify(registrar, never()).getCompositeIncludeTypeFilter();
		verifyNoMoreInteractions(mockCache);
	}

	@Test
	public void postProcessGemFireCacheAfterInitializationWithNonMappingPdxSerializer() {

		ClientCache mockCache = mock(ClientCache.class);

		PdxSerializer mockPdxSerializer = mock(PdxSerializer.class);

		doReturn(mockPdxSerializer).when(mockCache).getPdxSerializer();

		MappingPdxSerializerIncludedTypesRegistrar registrar = spy(MappingPdxSerializerIncludedTypesRegistrar.with());

		assertThat(registrar).isNotNull();
		assertThat(registrar.postProcessAfterInitialization(mockCache, "MockCache")).isEqualTo(mockCache);

		verify(mockCache, times(1)).getPdxSerializer();
		verify(registrar, never()).getCompositeIncludeTypeFilter();
		verifyNoMoreInteractions(mockCache);
		verifyNoInteractions(mockPdxSerializer);
	}

	@Test
	public void postProcessNonGemFireCacheBeanAfterInitialization() {

		Object bean = new Object();

		MappingPdxSerializerIncludedTypesRegistrar registrar =
			spy(MappingPdxSerializerIncludedTypesRegistrar.with(TypeOne.class));

		assertThat(registrar).isNotNull();
		assertThat(registrar.postProcessAfterInitialization(bean, "TestBean")).isEqualTo(bean);

		verify(registrar, never()).getCompositeIncludeTypeFilter();
	}

	@Test
	public void postProcessAfterInitializationIsNullSafe() {

		assertThat(MappingPdxSerializerIncludedTypesRegistrar.with()
			.postProcessAfterInitialization(null, "NullBean")).isNull();
	}

	static class TypeOne { }

	static class TypeTwo { }

	static class TypeThree { }

	static class SubtypeOne extends TypeOne { }

}
