/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.util.function;

import java.util.function.Consumer;

/**
 * {@link Consumer} implementation accepting a tuple of {@link InvocationArguments arguments}.
 *
 * @author John Blum
 * @see java.util.function.Consumer
 * @see org.springframework.geode.util.function.InvocationArguments
 * @since 1.3.0
 */
@SuppressWarnings("unused")
public interface TupleConsumer extends Consumer<InvocationArguments> {

}
