/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.logging.slf4j.logback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.helpers.NOPAppender;

/**
 * Unit Tests for {@link DelegatingAppender}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.springframework.geode.logging.slf4j.logback.DelegatingAppender
 * @since 1.3.0
 */
public class DelegatingAppenderUnitTests {

	@Test
	public void delegatingAppenderDefaultsNameToDelegate() {
		assertThat(new DelegatingAppender<>().getName()).isEqualTo(DelegatingAppender.DEFAULT_NAME);
	}

	@Test
	public void delegatingAppenderDefaultsToNoOpAppender() {
		assertThat(new DelegatingAppender<>().getAppender()).isInstanceOf(NOPAppender.class);
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void delegatingAppenderDelegatesToMockAppender() {

		Appender mockAppender = mock(Appender.class);

		DelegatingAppender delegatingAppender = new DelegatingAppender<>();

		delegatingAppender.setAppender(mockAppender);

		assertThat(delegatingAppender.getAppender()).isSameAs(mockAppender);

		delegatingAppender.append("TEST");

		verify(mockAppender, times(1)).doAppend(eq("TEST"));
	}
}
