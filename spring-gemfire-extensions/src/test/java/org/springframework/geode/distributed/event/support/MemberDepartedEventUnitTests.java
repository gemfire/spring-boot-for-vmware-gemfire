/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.distributed.event.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.Test;

import org.apache.geode.distributed.internal.DistributionManager;

import org.springframework.geode.distributed.event.MembershipEvent;

/**
 * Unit Tests for {@link MemberDepartedEvent}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.springframework.geode.distributed.event.MembershipEvent
 * @see org.springframework.geode.distributed.event.support.MemberDepartedEvent
 * @since 1.3.0
 */
public class MemberDepartedEventUnitTests {

	@Test
	public void constructMemberDepartedEvent() {

		DistributionManager mockDistributionManager = mock(DistributionManager.class);

		MemberDepartedEvent event = new MemberDepartedEvent(mockDistributionManager);

		assertThat(event).isNotNull();
		assertThat(event.getDistributionManager()).isEqualTo(mockDistributionManager);
		assertThat(event.isCrashed()).isFalse();
		assertThat(event.getType()).isEqualTo(MembershipEvent.Type.MEMBER_DEPARTED);

		verifyNoInteractions(mockDistributionManager);
	}

	@Test
	public void setAndGetCrashed() {

		DistributionManager mockDistributionManager = mock(DistributionManager.class);

		MemberDepartedEvent event = new MemberDepartedEvent(mockDistributionManager);

		assertThat(event).isNotNull();
		assertThat(event.isCrashed()).isFalse();
		assertThat(event.crashed(true)).isEqualTo(event);
		assertThat(event.isCrashed()).isTrue();
		assertThat(event.crashed(false)).isEqualTo(event);
		assertThat(event.isCrashed()).isFalse();
	}
}
