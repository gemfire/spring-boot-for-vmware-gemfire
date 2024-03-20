/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.data;

import org.apache.geode.cache.GemFireCache;

/**
 * Convenient {@link Class#isInterface() interface} to extend when the implementation supports both
 * data import and export from/to a {@link GemFireCache}.
 *
 * @author John Blum
 * @see GemFireCache
 * @see CacheDataExporter
 * @see CacheDataImporter
 * @since 1.3.0
 */
public interface CacheDataImporterExporter extends CacheDataExporter, CacheDataImporter {

}
