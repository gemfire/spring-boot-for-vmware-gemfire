/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.jackson.databind.serializer;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializers;

/**
 * The {@link BigDecimalSerializer} class is a {@link NumberSerializers NumberSerializers.Base} serializer
 * for serializing {@link BigDecimal} values.
 *
 * @author John Blum
 * @see BigDecimal
 * @see NumberSerializers
 * @since 1.3.0
 */
@SuppressWarnings("unused")
public class BigDecimalSerializer extends NumberSerializers.Base<BigDecimal> {

	public static final BigDecimalSerializer INSTANCE = new BigDecimalSerializer();

	public BigDecimalSerializer() {
		super(BigDecimal.class, JsonParser.NumberType.BIG_DECIMAL, "bigdecimal");
	}

	@Override
	public void serialize(BigDecimal value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {

		jsonGenerator.writeNumber(value);
	}

	@Override
	public void serializeWithType(BigDecimal value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider,
			TypeSerializer typeSerializer) throws IOException {

		serialize(value, jsonGenerator, serializerProvider);
	}
}
