/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.function.executions;

import org.springframework.data.gemfire.function.annotation.OnMember;

/**
 * The {@link Calculator} interface defines Apache Geode Functions.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.function.annotation.OnRegion
 * @see org.springframework.data.gemfire.function.annotation.OnMember
 * @since 1.0.0
 */
// TODO change Function returns type when SDG properly handles Function method return types/values
@OnMember(groups = "test")
public interface Calculator {

	Object add(double operandOne, double operandTwo);

	Object divide(double numerator, double divisor);

	Object factorial(long number);

	Object multiply(double operandOne, double operandTwo);

	Object squareRoot(double number);

	Object squared(double number);

	Object subtract(double operandOne, double operandTwo);

}
