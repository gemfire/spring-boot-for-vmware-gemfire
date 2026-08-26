/*
 * Copyright 2023-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.web.servlet.http;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

import javax.servlet.http.HttpSession;

import org.springframework.util.StringUtils;

/**
 * Abstract base class supporting implementations of the {@link HttpSession} interface.
 *
 * @author John Blum
 * @see java.time.Duration
 * @see java.time.Instant
 * @see javax.servlet.http.HttpSession
 * @since 1.4.0
 */
public abstract class AbstractHttpSession implements HttpSession {

	private Duration maxInactiveInterval = Duration.ofMinutes(30);

	private final Instant creationTime = Instant.now();

	@Override
	public long getCreationTime() {
		return this.creationTime.toEpochMilli();
	}

	@Override
	public int getMaxInactiveInterval() {
		return Long.valueOf(this.maxInactiveInterval.getSeconds()).intValue();
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		int resolvedInterval = interval > 0 ? interval : Integer.MAX_VALUE;
		this.maxInactiveInterval = Duration.ofSeconds(resolvedInterval);
	}

	@Override
	public boolean isNew() {
		return !StringUtils.hasText(getId());
	}

	@Override
	@Deprecated
	public Object getValue(String name) {
		return getAttribute(name);
	}

	@Override
	@Deprecated
	public String[] getValueNames() {
		return Collections.list(getAttributeNames()).toArray(new String[0]);
	}

	@Override
	@Deprecated
	public void putValue(String name, Object value) {
		setAttribute(name, value);
	}

	@Override
	@Deprecated
	public void removeValue(String name) {
		removeAttribute(name);
	}

	@Override
	@Deprecated
	public javax.servlet.http.HttpSessionContext getSessionContext() {
		return null;
	}
}
