/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.geode.function.support;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;

import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.distributed.DistributedMember;

/**
 * The {@link SingleResultReturningCollector} class is an implementation of the {@link ResultCollector} interface
 * which returns a single {@link Object result}.
 *
 * @author John Blum
 * @see ResultCollector
 * @see AbstractResultCollector
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class SingleResultReturningCollector<T> extends AbstractResultCollector<T, T> {

	@Override
	public void addResult(DistributedMember memberID, T resultOfSingleExecution) {
		setResult(extractSingleResult(resultOfSingleExecution));
	}

	@SuppressWarnings("unchecked")
	private <T> T extractSingleResult(Object result) {

		return (T) Optional.ofNullable(result)
			.filter(this::isInstanceOfIterableOrIterator)
			.map(this::toIterator)
			.filter(Iterator::hasNext)
			.map(Iterator::next)
			.map(this::extractSingleResult)
			.orElseGet(() -> isInstanceOfIterableOrIterator(result) ? null : result);
	}

	private boolean isInstanceOfIterableOrIterator(Object obj) {
		return obj instanceof Iterable || obj instanceof Iterator;
	}

	@SuppressWarnings("unchecked")
	private <T> Iterator<T> toIterator(Object obj) {
		return obj instanceof Iterator ? (Iterator<T>) obj : toIterator((Iterable<T>) obj);
	}

	private <T> Iterator<T> toIterator(Iterable<T> iterable) {
		return iterable != null ? iterable.iterator() : Collections.emptyIterator();
	}
}
