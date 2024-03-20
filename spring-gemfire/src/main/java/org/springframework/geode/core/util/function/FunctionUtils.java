/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.core.util.function;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility methods for using Java {@link Function Functions}.
 *
 * @author John Blum
 * @see Function
 * @since 1.1.0
 */
@SuppressWarnings("unused")
public abstract class FunctionUtils {

  public static <T, R> Function<T, R> toNullReturningFunction(Consumer<T> consumer) {

    return object -> {
      consumer.accept(object);
      return null;
    };
  }
}
