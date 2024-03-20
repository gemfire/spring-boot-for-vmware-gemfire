/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.test.service;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * {@link TestCacheableService} is a Spring {@link Service} bean with caching applied.
 *
 * @author John Blum
 * @see java.util.Random
 * @see org.springframework.cache.annotation.Cacheable
 * @see org.springframework.stereotype.Service
 * @since 1.2.1
 */
@Service
public class TestCacheableService {

	private final AtomicBoolean cacheMiss = new AtomicBoolean(false);

	private final Random random = new Random(System.currentTimeMillis());

	public boolean isCacheMiss() {
		return this.cacheMiss.getAndSet(false);
	}

	@Cacheable("RandomNumbers")
	public Number getRandomNumber(@SuppressWarnings("unused") String key) {

		this.cacheMiss.set(true);

		return this.random.nextInt();
	}
}
