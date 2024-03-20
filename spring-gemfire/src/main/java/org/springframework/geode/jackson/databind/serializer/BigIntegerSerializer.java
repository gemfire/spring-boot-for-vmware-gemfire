/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.jackson.databind.serializer;

import java.io.IOException;
import java.math.BigInteger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializers;

/**
 * The {@link BigIntegerSerializer} class is a {@link NumberSerializers NumberSerializers.Base} serializer
 * for serializing {@link BigInteger} values.
 *
 * @author John Blum
 * @see BigInteger
 * @see NumberSerializers
 * @since 1.3.0
 */
@SuppressWarnings("unused")
public class BigIntegerSerializer extends NumberSerializers.Base<BigInteger> {

	public static final BigIntegerSerializer INSTANCE = new BigIntegerSerializer();

	public BigIntegerSerializer() {
		super(BigInteger.class, JsonParser.NumberType.BIG_INTEGER, "biginteger");
	}

	@Override
	public void serialize(BigInteger value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {

		jsonGenerator.writeNumber(value);
	}

	@Override
	public void serializeWithType(BigInteger value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider,
			TypeSerializer typeSerializer) throws IOException {

		serialize(value, jsonGenerator, serializerProvider);
	}
}
