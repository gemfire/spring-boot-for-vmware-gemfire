/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.data.json.converter.support;

import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.JSONFormatterException;
import org.apache.geode.pdx.PdxInstance;

import org.springframework.geode.data.json.converter.JsonToObjectConverter;
import org.springframework.geode.data.json.converter.JsonToPdxConverter;
import org.springframework.geode.pdx.ObjectPdxInstanceAdapter;
import org.springframework.geode.pdx.PdxInstanceWrapper;
import org.springframework.lang.NonNull;

/**
 * A {@link JsonToPdxConverter} implementation using the Apache Geode {@link JSONFormatter} to convert
 * from a {@literal JSON} {@link String} to a {@link PdxInstance}.
 *
 * @author John Blum
 * @see JSONFormatter
 * @see PdxInstance
 * @see JsonToPdxConverter
 * @since 1.3.0
 */
public class JSONFormatterJsonToPdxConverter implements JsonToPdxConverter {

	private JsonToObjectConverter converter = newJsonToObjectConverter();

	// TODO configure via an SPIs
	private JsonToObjectConverter newJsonToObjectConverter() {
		return new JacksonJsonToObjectConverter();
	}

	/**
	 * Returns a reference to the configured {@link JsonToObjectConverter} used to convert from {@link String JSON}
	 * to an {@link Object}.
	 *
	 * @return a reference to the configured {@link JsonToObjectConverter}; never {@literal null}.
	 * @see JsonToObjectConverter
	 */
	protected @NonNull JsonToObjectConverter getJsonToObjectConverter() {
		return this.converter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final @NonNull PdxInstance convert(@NonNull String json) {

		try {
			return convertJsonToPdx(json);
		}
		catch (JSONFormatterException cause) {
			return convertJsonToObjectToPdx(json);
		}
	}

	/**
	 * Adapts the given {@link Object} as a {@link PdxInstance}.
	 *
	 * @param target {@link Object} to adapt as PDX; must not be {@literal null}.
	 * @return a {@link PdxInstance} representing the given {@link Object}.
	 * @see ObjectPdxInstanceAdapter#from(Object)
	 * @see PdxInstance
	 */
	protected @NonNull PdxInstance adapt(@NonNull Object target) {
		return ObjectPdxInstanceAdapter.from(target);
	}

	/**
	 * Converts the given {@link String JSON} into a {@link Object} and then adapts the {@link Object}
	 * as a {@link PdxInstance}.
	 *
	 * @param json {@link String JSON} to convert into an {@link Object} into PDX.
	 * @return a {@link PdxInstance} converted from the given {@link String JSON}.
	 * @see PdxInstance
	 * @see #getJsonToObjectConverter()
	 * @see #adapt(Object)
	 */
	protected @NonNull PdxInstance convertJsonToObjectToPdx(@NonNull String json) {
		return adapt(getJsonToObjectConverter().convert(json));
	}

	/**
	 * Converts the given {@link String JSON} to {@link PdxInstance PDX}.
	 *
	 * @param json {@link String} containing JSON to convert to PDX; must not be {@literal null}.
	 * @return JSON for the given {@link PdxInstance PDX}.
	 * @see PdxInstance
	 * @see #jsonFormatterFromJson(String)
	 * @see #wrap(PdxInstance)
	 */
	protected @NonNull PdxInstance convertJsonToPdx(@NonNull String json) {
		return wrap(jsonFormatterFromJson(json));
	}

	/**
	 * Converts {@link String JSON} into {@link PdxInstance PDX} using {@link JSONFormatter#fromJSON(String)}.
	 *
	 * @param json {@link String JSON} to convert to {@link PdxInstance PDX}; must not be {@literal null}.
	 * @return {@link PdxInstance PDX} generated from the given, required {@link String JSON}; never {@literal null}.
	 * @see JSONFormatter#fromJSON(String)
	 * @see PdxInstance
	 */
	protected @NonNull PdxInstance jsonFormatterFromJson(@NonNull String json) {
		return JSONFormatter.fromJSON(json);
	}

	/**
	 * Wraps the given {@link PdxInstance} in a new instance of {@link PdxInstanceWrapper}.
	 *
	 * @param pdxInstance {@link PdxInstance} to wrap.
	 * @return a new instance of {@link PdxInstanceWrapper} wrapping the given {@link PdxInstance}.
	 * @see org.springframework.geode.pdx.PdxInstanceWrapper#from(PdxInstance)
	 * @see org.springframework.geode.pdx.PdxInstanceWrapper
	 * @see PdxInstance
	 */
	protected @NonNull PdxInstanceWrapper wrap(@NonNull PdxInstance pdxInstance) {
		return PdxInstanceWrapper.from(pdxInstance);
	}
}
