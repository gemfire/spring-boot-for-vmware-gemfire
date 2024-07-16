/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.data;

import org.apache.geode.cache.client.ClientCache;

/**
 * Convenient {@link Class#isInterface() interface} to extend when the implementation supports both
 * data import and export from/to a {@link ClientCache}.
 *
 * @author John Blum
 * @since 1.3.0
 */
public interface CacheDataImporterExporter extends CacheDataExporter, CacheDataImporter {

}
