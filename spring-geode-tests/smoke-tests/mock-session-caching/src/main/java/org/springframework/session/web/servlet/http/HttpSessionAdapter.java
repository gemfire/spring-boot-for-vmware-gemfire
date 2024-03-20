/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.session.web.servlet.http;

import java.time.Duration;
import java.util.Collections;
import java.util.Enumeration;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

import org.springframework.lang.NonNull;
import org.springframework.session.Session;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.http.AbstractHttpSession;

/**
 * {@link HttpSession} implementation adapting the Spring Session {@link Session} interface.
 *
 * @author John Blum
 * @see java.time.Duration
 * @see jakarta.servlet.ServletContext
 * @see jakarta.servlet.http.HttpSession
 * @see org.springframework.session.Session
 * @see org.springframework.web.servlet.http.AbstractHttpSession
 * @since 1.4.0
 */
@SuppressWarnings("unused")
public class HttpSessionAdapter extends AbstractHttpSession {

	private final ServletContext servletContext;

	private final Session session;

	public HttpSessionAdapter(@NonNull ServletContext servletContext, @NonNull Session session) {

		Assert.notNull(servletContext, "ServletContext must not be null");
		Assert.notNull(session, "Session must not be null");

		this.session = session;
		this.servletContext = servletContext;
	}

	protected @NonNull Session getSession() {
		return this.session;
	}

	@Override
	public String getId() {
		return getSession().getId();
	}

	@Override
	public long getCreationTime() {
		return getSession().getCreationTime().toEpochMilli();
	}

	@Override
	public long getLastAccessedTime() {
		return getSession().getLastAccessedTime().toEpochMilli();
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		getSession().setMaxInactiveInterval(Duration.ofSeconds(interval));
	}

	@Override
	public int getMaxInactiveInterval() {
		return Long.valueOf(getSession().getMaxInactiveInterval().getSeconds()).intValue();
	}

	@Override
	public @NonNull ServletContext getServletContext() {
		return this.servletContext;
	}

	@Override
	public void setAttribute(String name, Object value) {
		getSession().setAttribute(name, value);
	}

	@Override
	public void removeAttribute(String name) {
		getSession().removeAttribute(name);
	}

	@Override
	public Object getAttribute(String name) {
		return getSession().getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return Collections.enumeration(getSession().getAttributeNames());
	}

	@Override
	public void invalidate() {
		throw new UnsupportedOperationException("Not Implemented");
	}

	@Override
	public boolean isNew() {
		return !StringUtils.hasText(getId());
	}
}
