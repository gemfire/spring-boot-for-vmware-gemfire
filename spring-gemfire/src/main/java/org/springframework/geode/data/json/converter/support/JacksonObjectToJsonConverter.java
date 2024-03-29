/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.data.json.converter.support;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.geode.data.json.converter.ObjectToJsonConverter;
import org.springframework.geode.pdx.PdxInstanceWrapper;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * A {@link ObjectToJsonConverter} implementation using Jackson's {@link ObjectMapper} to convert
 * from an {@link Object} to a {@literal JSON} {@link String}.
 *
 * @author John Blum
 * @see JsonTypeInfo
 * @see JsonGenerator
 * @see ObjectMapper
 * @see MapperFeature
 * @see TypeDescriptor
 * @see ObjectToJsonConverter
 * @see PdxInstanceWrapper
 * @since 1.3.0
 */
public class JacksonObjectToJsonConverter implements ObjectToJsonConverter {

	protected static final String AT_TYPE_METADATA_PROPERTY_NAME = PdxInstanceWrapper.AT_TYPE_FIELD_NAME;

	/**
	 * Converts the given {@link Object} into {@link String JSON}.
	 *
	 * @param source {@link Object} to convert into {@link String JSON}.
	 * @return {@link String JSON} generated from the given {@link Object} using Jackson's {@link ObjectMapper}.
	 * @throws IllegalArgumentException if {@link Object source} is {@literal null}.
	 * @throws ConversionFailedException if a {@link JsonProcessingException} is thrown or another error occurs
	 * while trying to convert the given {@link Object} to {@link String JSON}.
	 * @see ObjectMapper
	 * @see #convertObjectToJson(Object)
	 */
	@Override
	public @NonNull String convert(@NonNull Object source) {

		Assert.notNull(source, "Source object to convert must not be null");

		try {
			return convertObjectToJson(source);
		}
		catch (JsonProcessingException cause) {
			throw new ConversionFailedException(TypeDescriptor.forObject(source), TypeDescriptor.valueOf(String.class),
				source, cause);
		}
	}

	/**
	 * Converts the given {@link Object} into {@link String JSON}.
	 *
	 * @param source {@link Object} to convert to {@link String JSON}; must not be {@literal null}.
	 * @return {@link String JSON} generated from the given {@link Object}.
	 * @throws IllegalArgumentException if {@link Object source} is {@literal null}.
	 * @throws JsonProcessingException if the generation of {@link String JSON} from the given {@link Object}
	 * results in an error.
	 * @see ObjectMapper#writeValueAsString(Object)
	 * @see #newObjectMapper(Object)
	 */
	protected @NonNull String convertObjectToJson(@NonNull Object source) throws JsonProcessingException {

		Assert.notNull(source, "Source object to convert must not be null");

		return newObjectMapper(source).writeValueAsString(source);
	}

	/**
	 * Constructs a new instance of the Jackson {@link ObjectMapper} class.
	 *
	 * @return a new instance of the Jackson {@link ObjectMapper} class.
	 * @see ObjectMapper
	 */
	protected @NonNull ObjectMapper newObjectMapper(@NonNull Object target) {

		Assert.notNull(target, "Target object must not be null");

		return newObjectMapper()
			.addMixIn(target.getClass(), ObjectTypeMetadataMixin.class)
			.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
			.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
			.configure(SerializationFeature.INDENT_OUTPUT, true)
			.findAndRegisterModules();
	}

	/**
	 * Constructs a new instance of Jackson's {@link ObjectMapper}.
	 *
	 * @return a new instance of Jackson's {@link ObjectMapper}; never {@literal null}.
	 * @see ObjectMapper
	 */
	@NonNull ObjectMapper newObjectMapper() {
		return new ObjectMapper();
	}

	@JsonTypeInfo(
		use = JsonTypeInfo.Id.CLASS,
		include = JsonTypeInfo.As.PROPERTY,
		property = AT_TYPE_METADATA_PROPERTY_NAME
	)
	@SuppressWarnings("all")
	interface ObjectTypeMetadataMixin { }

}
