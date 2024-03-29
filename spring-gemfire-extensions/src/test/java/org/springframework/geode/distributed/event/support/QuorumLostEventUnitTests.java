/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.distributed.event.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.Collections;

import org.junit.Test;

import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.internal.DistributionManager;

import org.springframework.geode.distributed.event.MembershipEvent;

/**
 * Unit Tests for {@link QuorumLostEvent}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.distributed.DistributedMember
 * @see org.springframework.geode.distributed.event.MembershipEvent
 * @see org.springframework.geode.distributed.event.support.QuorumLostEvent
 * @since 1.3.0
 */
public class QuorumLostEventUnitTests {

	@SuppressWarnings("unchecked")
	private <T> Iterable<T> normalize(Iterable<? extends T> iterable) {
		return (Iterable<T>) iterable;
	}

	@Test
	public void constructsQuorumLostEvent() {

		DistributionManager mockDistributionManager = mock(DistributionManager.class);

		QuorumLostEvent event = new QuorumLostEvent(mockDistributionManager);

		assertThat(event).isNotNull();
		assertThat(event.getDistributionManager()).isEqualTo(mockDistributionManager);
		assertThat(event.getFailedMembers()).isEmpty();
		assertThat(event.getRemainingMembers()).isEmpty();
		assertThat(event.getType()).isEqualTo(MembershipEvent.Type.QUORUM_LOST);

		verifyNoInteractions(mockDistributionManager);
	}

	@Test
	public void setAndGetFailedMembers() {

		DistributedMember mockMemberOne = mock(DistributedMember.class);
		DistributedMember mockMemberTwo = mock(DistributedMember.class);

		DistributionManager mockDistributionManager = mock(DistributionManager.class);

		QuorumLostEvent event = new QuorumLostEvent(mockDistributionManager);

		assertThat(event).isNotNull();
		assertThat(event.getFailedMembers()).isNotNull();
		assertThat(event.getFailedMembers()).isEmpty();

		assertThat(event.withFailedMembers(mockMemberOne, mockMemberTwo)).isSameAs(event);
		assertThat(this.<DistributedMember>normalize(event.getFailedMembers()))
			.containsExactlyInAnyOrder(mockMemberOne, mockMemberTwo);

		assertThat(event.withFailedMembers((DistributedMember[]) null)).isSameAs(event);
		assertThat(event.getFailedMembers()).isNotNull();
		assertThat(event.getFailedMembers()).isEmpty();

		assertThat(event.withFailedMembers(mockMemberOne)).isSameAs(event);
		assertThat(this.<DistributedMember>normalize(event.getFailedMembers())).containsExactly(mockMemberOne);

		assertThat(event.withFailedMembers((Iterable<? extends DistributedMember>) null)).isSameAs(event);
		assertThat(event.getFailedMembers()).isNotNull();
		assertThat(event.getFailedMembers()).isEmpty();

		assertThat(event.withFailedMembers(Collections.singleton(mockMemberTwo))).isSameAs(event);
		assertThat(this.<DistributedMember>normalize(event.getFailedMembers())).containsExactly(mockMemberTwo);

		assertThat(event.withFailedMembers()).isSameAs(event);
		assertThat(event.getFailedMembers()).isNotNull();
		assertThat(event.getFailedMembers()).isEmpty();
	}

	@Test
	public void setAndGetRemainingMembers() {

		DistributedMember mockMemberOne = mock(DistributedMember.class);
		DistributedMember mockMemberTwo = mock(DistributedMember.class);

		DistributionManager mockDistributionManager = mock(DistributionManager.class);

		QuorumLostEvent event = new QuorumLostEvent(mockDistributionManager);

		assertThat(event).isNotNull();
		assertThat(event.getRemainingMembers()).isNotNull();
		assertThat(event.getRemainingMembers()).isEmpty();

		assertThat(event.withRemainingMembers(mockMemberOne, mockMemberTwo)).isSameAs(event);
		assertThat(this.<DistributedMember>normalize(event.getRemainingMembers()))
			.containsExactlyInAnyOrder(mockMemberOne, mockMemberTwo);

		assertThat(event.withRemainingMembers((DistributedMember[]) null)).isSameAs(event);
		assertThat(event.getRemainingMembers()).isNotNull();
		assertThat(event.getRemainingMembers()).isEmpty();

		assertThat(event.withRemainingMembers(mockMemberOne)).isSameAs(event);
		assertThat(this.<DistributedMember>normalize(event.getRemainingMembers()))
			.containsExactlyInAnyOrder(mockMemberOne);

		assertThat(event.withRemainingMembers((Iterable<? extends DistributedMember>) null)).isSameAs(event);
		assertThat(event.getRemainingMembers()).isNotNull();
		assertThat(event.getRemainingMembers()).isEmpty();

		assertThat(event.withRemainingMembers(Collections.singletonList(mockMemberTwo))).isSameAs(event);
		assertThat(this.<DistributedMember>normalize(event.getRemainingMembers()))
			.containsExactlyInAnyOrder(mockMemberTwo);

		assertThat(event.withRemainingMembers()).isSameAs(event);
		assertThat(event.getRemainingMembers()).isNotNull();
		assertThat(event.getRemainingMembers()).isEmpty();
	}
}
