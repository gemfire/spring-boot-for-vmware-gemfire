/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.session.web.servlet.http;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

import java.util.concurrent.atomic.AtomicReference;

import jakarta.servlet.http.HttpSession;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.session.Session;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.servlet.http.HttpSessionProxy;

/**
 * Spring Test Mock Web MVC framework {@link RequestPostProcessor} that substitutes the Spring Session {@link Session}
 * for the {@link MockHttpSession}.
 *
 * @author John Blum
 * @see javax.servlet.http.HttpSession
 * @see org.springframework.mock.web.MockHttpServletRequest
 * @see org.springframework.mock.web.MockHttpSession
 * @see org.springframework.session.Session
 * @see org.springframework.test.web.servlet.request.RequestPostProcessor
 * @see org.springframework.web.servlet.http.HttpSessionProxy
 * @since 1.4.0
 */
public class SpringSessionSubstitutingSpyRequestPostProcessor implements RequestPostProcessor {

	private static final AtomicReference<SpringSessionSubstitutingSpyRequestPostProcessor> instance = new AtomicReference<>(null);

	public static SpringSessionSubstitutingSpyRequestPostProcessor create() {
		return instance.updateAndGet(instance -> instance != null ? instance
			: new SpringSessionSubstitutingSpyRequestPostProcessor());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {

		MockHttpServletRequest requestSpy = spy(request);

		doAnswer(invocation -> {

			HttpSession session = SessionUtils.resolveSession(request);

			return session != null
				? HttpSessionProxy.from(session)
				: request.getSession();

		}).when(requestSpy).getSession();

		return requestSpy;
	}
}
