/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.data.json.converter.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.geode.pdx.PdxInstance;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.geode.data.json.converter.JsonToPdxArrayConverter;
import org.springframework.geode.data.json.converter.JsonToPdxConverter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * The {@link JacksonJsonToPdxConverter} class is an implementation of the {@link JsonToPdxArrayConverter} that is
 * capable of converting an array of {@literal JSON} objects into an array of {@link PdxInstance PdxInstances}.
 *
 * @author John Blum
 * @see JsonNode
 * @see ObjectMapper
 * @see ArrayNode
 * @see ObjectNode
 * @see PdxInstance
 * @see JsonToPdxArrayConverter
 * @see JsonToPdxConverter
 * @since 1.3.0
 */
public class JacksonJsonToPdxConverter implements JsonToPdxArrayConverter {

	private JsonToPdxConverter converter = newJsonToPdxConverter();

	private ObjectMapper objectMapper = newObjectMapper();

	private @NonNull <T> Iterable<T> asIterable(@NonNull Iterator<T> iterator) {
		return () -> iterator;
	}

	// TODO configure via an SPI
	private JsonToPdxConverter newJsonToPdxConverter() {
		return new JSONFormatterJsonToPdxConverter();
	}

	// TODO configure via an SPI
	private ObjectMapper newObjectMapper() {
		return new ObjectMapper();
	}

	/**
	 * Returns a reference to the configured {@link JsonToPdxConverter} used to convert from a single object,
	 * {@literal JSON} {@link String} to PDX (i.e. as a {@link PdxInstance}.
	 *
	 * @return a reference to the configured {@link JsonToPdxConverter}; never {@literal null}.
	 * @see JsonToPdxConverter
	 */
	protected @NonNull JsonToPdxConverter getJsonToPdxConverter() {
		return this.converter;
	}

	/**
	 * Returns a reference to the configured Jackson {@link ObjectMapper}.
	 *
	 * @return a reference to the configured Jackson {@link ObjectMapper}; never {@literal null}.
	 * @see ObjectMapper
	 */
	protected @NonNull ObjectMapper getObjectMapper() {
		return this.objectMapper;
	}

	/**
	 * Converts the given {@link String JSON} containing multiple objects into an array of {@link PdxInstance} objects.
	 *
	 * @param json {@link String JSON} data to convert.
	 * @return an array of {@link PdxInstance} objects from the given {@link String JSON}.
	 * @throws IllegalStateException if the {@link String JSON} does not start with
	 * either a JSON array or a JSON object.
	 * @see PdxInstance
	 */
	@Nullable @Override
	public PdxInstance[] convert(String json) {

		try {
			JsonNode jsonNode = getObjectMapper().readTree(json);

			List<PdxInstance> pdxList = new ArrayList<>();

			if (isArray(jsonNode)) {

				ArrayNode arrayNode = (ArrayNode) jsonNode;

				JsonToPdxConverter converter = getJsonToPdxConverter();

				for (JsonNode object : asIterable(CollectionUtils.nullSafeIterator(arrayNode.elements()))) {
					pdxList.add(converter.convert(object.toString()));
				}
			}
			else if (isObject(jsonNode)) {

				ObjectNode objectNode = (ObjectNode) jsonNode;

				pdxList.add(getJsonToPdxConverter().convert(objectNode.toString()));
			}
			else {

				String message = String.format("Unable to process JSON node of type [%s];"
					+ " expected either an [%s] or an [%s]", jsonNode.getNodeType(),
						JsonNodeType.OBJECT, JsonNodeType.ARRAY);

				throw new IllegalStateException(message);
			}

			return pdxList.toArray(new PdxInstance[0]);
		}
		catch (JsonProcessingException cause) {
			throw new DataRetrievalFailureException("Failed to read JSON content", cause);
		}
	}

	private boolean isArray(@Nullable JsonNode node) {
		return node != null && (node.isArray() || JsonNodeType.ARRAY.equals(node.getNodeType()));
	}

	private boolean isObject(@Nullable JsonNode node) {
		return node != null && (node.isObject() || JsonNodeType.OBJECT.equals(node.getNodeType()));
	}
}
