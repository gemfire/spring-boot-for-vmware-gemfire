/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.cache;

import org.apache.geode.cache.CacheListener;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.RegionEvent;
import org.apache.geode.cache.util.CacheListenerAdapter;

/**
 * An {@link Class abstract base class} implementing the Apache Geode {@link CacheListener} interface
 * by extending the {@link CacheListenerAdapter} base class, which processes all {@link EntryEvent EntryEvents}
 * and {@link RegionEvent RegionEvents} using the same logic.
 *
 * @author John Blum
 * @see org.apache.geode.cache.CacheListener
 * @see org.apache.geode.cache.EntryEvent
 * @see org.apache.geode.cache.RegionEvent
 * @see org.apache.geode.cache.util.CacheListenerAdapter
 * @since 1.1.0
 */
public abstract class AbstractCommonEventProcessingCacheListener<K, V> extends CacheListenerAdapter<K, V> {

	@Override
	public void afterCreate(EntryEvent<K, V> event) {
		processEntryEvent(event, EntryEventType.CREATE);
	}

	@Override
	public void afterDestroy(EntryEvent<K, V> event) {
		processEntryEvent(event, EntryEventType.DESTROY);
	}

	@Override
	public void afterInvalidate(EntryEvent<K, V> event) {
		processEntryEvent(event, EntryEventType.INVALIDATE);
	}

	@Override
	public void afterUpdate(EntryEvent<K, V> event) {
		processEntryEvent(event, EntryEventType.UPDATE);
	}

	protected void processEntryEvent(EntryEvent<K, V> event, EntryEventType eventType) { }

	@Override
	public void afterRegionClear(RegionEvent<K, V> event) {
		processRegionEvent(event, RegionEventType.CLEAR);
	}

	@Override
	public void afterRegionCreate(RegionEvent<K, V> event) {
		processRegionEvent(event, RegionEventType.CREATE);
	}

	@Override
	public void afterRegionDestroy(RegionEvent<K, V> event) {
		processRegionEvent(event, RegionEventType.DESTROY);
	}

	@Override
	public void afterRegionInvalidate(RegionEvent<K, V> event) {
		processRegionEvent(event, RegionEventType.INVALIDATE);
	}

	@Override
	public void afterRegionLive(RegionEvent<K, V> event) {
		processRegionEvent(event, RegionEventType.LIVE);
	}

	protected void processRegionEvent(RegionEvent<K, V> event, RegionEventType eventType) { }

	public enum EntryEventType {

		CREATE,
		DESTROY,
		INVALIDATE,
		UPDATE;

	}

	public enum RegionEventType {

		CLEAR,
		CREATE,
		DESTROY,
		INVALIDATE,
		LIVE;

	}
}
