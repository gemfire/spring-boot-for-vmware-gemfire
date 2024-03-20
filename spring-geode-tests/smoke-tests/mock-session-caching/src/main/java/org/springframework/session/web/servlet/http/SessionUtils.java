/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.session.web.servlet.http;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.geode.core.util.ObjectUtils;
import org.springframework.lang.Nullable;
import org.springframework.session.SessionRepository;

/**
 * Abstract utility class used to work with {@link HttpSession} objects.
 *
 * @author John Blum
 * @see javax.servlet.http.HttpServletRequest
 * @see javax.servlet.http.HttpSession
 * @since 1.4.0
 */
public abstract class SessionUtils {

	protected static final String CURRENT_SESSION_REQUEST_ATTRIBUTE =
		SessionRepository.class.getName().concat(".CURRENT_SESSION");

	protected static final String GET_CURRENT_SESSION_METHOD_NAME = "getCurrentSession";

	public static @Nullable HttpSession resolveSession(HttpServletRequest servletRequest) {

		try {
			return ObjectUtils.invoke(servletRequest, GET_CURRENT_SESSION_METHOD_NAME);
		}
		catch (IllegalArgumentException ignore) {
			return (HttpSession) servletRequest.getAttribute(CURRENT_SESSION_REQUEST_ATTRIBUTE);
		}
	}
}
