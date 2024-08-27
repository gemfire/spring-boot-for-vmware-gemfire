/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.geode.function.support;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.ResultCollector;

/**
 * The {@link AbstractResultCollector} class is an abstract base implementation of the {@link ResultCollector} interface
 * encapsulating common functionality for collecting results from a Function execution.
 *
 * @author John Blum
 * @see org.apache.geode.cache.execute.ResultCollector
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class AbstractResultCollector<T, S> implements ResultCollector<T, S> {

	protected static final String NOT_IMPLEMENTED = "Not Implemented";

	protected static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;

	private AtomicBoolean resultsEnded = new AtomicBoolean(false);

	private S result = null;

	@Override
	public synchronized S getResult() throws FunctionException {
		return this.result;
	}

	@Override
	public S getResult(long duration, TimeUnit unit) throws FunctionException, InterruptedException {

		unit = resolveTimeUnit(unit);

		long durationInMilliseconds = unit.toMillis(duration);
		long timeout = System.currentTimeMillis() + unit.toMillis(duration);
		long waitInMilliseconds = Math.max(50, Math.min(durationInMilliseconds / 5, durationInMilliseconds));

		synchronized (this) {
			while (getResult() == null && System.currentTimeMillis() < timeout) {
				unit.timedWait(this, waitInMilliseconds);
			}
		}

		return getResult();
	}

	protected synchronized void setResult(S result) {
		this.result = result;
	}

	protected TimeUnit resolveTimeUnit(TimeUnit unit) {
		return unit != null ? unit : DEFAULT_TIME_UNIT;
	}

	@Override
	public void clearResults() {
		setResult(null);
	}

	@Override
	public void endResults() {
		this.resultsEnded.set(true);
	}

	protected boolean hasResultsEnded() {
		return this.resultsEnded.get();
	}

	protected boolean hasResultsNotEnded() {
		return !this.resultsEnded.get();
	}
}
