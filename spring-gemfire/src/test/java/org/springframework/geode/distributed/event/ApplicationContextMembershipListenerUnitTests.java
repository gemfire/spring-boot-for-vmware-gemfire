/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.distributed.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.geode.distributed.event.support.MemberDepartedEvent;
import org.springframework.geode.distributed.event.support.MemberJoinedEvent;

/**
 * Unit Tests for {@link ApplicationContextMembershipListener}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.geode.distributed.event.ApplicationContextMembershipListener
 * @see org.springframework.geode.distributed.event.support.MemberDepartedEvent
 * @see org.springframework.geode.distributed.event.support.MemberJoinedEvent
 * @since 1.3.0
 */
public class ApplicationContextMembershipListenerUnitTests {

	@Test
	public void constructsApplicationContextMembershipListener() {

		ConfigurableApplicationContext mockApplicationContext = mock(ConfigurableApplicationContext.class);

		ApplicationContextMembershipListener listener =
			new ApplicationContextMembershipListener(mockApplicationContext);

		assertThat(listener).isNotNull();
		assertThat(listener.getApplicationContext()).isEqualTo(mockApplicationContext);

		verifyNoInteractions(mockApplicationContext);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructsApplicationContextMembershipListenerWithNullThrowsIllegalArgumentException() {

		try {
			new ApplicationContextMembershipListener(null);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("ConfigurableApplicationContext must not be null");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void handleMemberDepartedCallsApplicationContextClose() {

		ConfigurableApplicationContext mockApplicationContext = mock(ConfigurableApplicationContext.class);

		MemberDepartedEvent mockEvent = mock(MemberDepartedEvent.class);

		ApplicationContextMembershipListener listener =
			spy(new ApplicationContextMembershipListener(mockApplicationContext));

		listener.handleMemberDeparted(mockEvent);

		verify(mockApplicationContext, times(1)).close();
		verifyNoMoreInteractions(mockApplicationContext);
	}

	@Test
	public void handleMemberJoinedCallApplicationContextRefresh() {

		ConfigurableApplicationContext mockApplicationContext = mock(ConfigurableApplicationContext.class);

		MemberJoinedEvent mockEvent = mock(MemberJoinedEvent.class);

		ApplicationContextMembershipListener listener =
			spy(new ApplicationContextMembershipListener(mockApplicationContext));

		listener.handleMemberJoined(mockEvent);

		verify(mockApplicationContext, times(1)).refresh();
		verifyNoMoreInteractions(mockApplicationContext);
	}
}
