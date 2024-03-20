/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.distributed.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;

import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.internal.DistributionManager;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.internal.cache.InternalCache;

/**
 * Unit Tests for {@link MembershipEvent}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.distributed.DistributedMember
 * @see org.apache.geode.distributed.DistributedSystem
 * @see org.apache.geode.distributed.internal.DistributionManager
 * @see org.springframework.geode.distributed.event.MembershipEvent
 * @since 1.3.0
 */
public class MembershipEventUnitTests {

	@Test
	public void assertNotNullWithNonNullValue() {
		assertThat(MembershipEvent.assertNotNull("test", "message")).isEqualTo("test");
	}

	@Test(expected = IllegalArgumentException.class)
	public void assertNotNullWithNullValue() {

		try {
			MembershipEvent.assertNotNull(null, "Message with argument [%s]", "test");
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("Message with argument [test]");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void constructsNewMembershipEventWithDistributionManager() {

		DistributionManager mockDistributionManager = mock(DistributionManager.class);

		InternalCache mockCache = mock(InternalCache.class);

		InternalDistributedSystem mockDistributedSystem = mock(InternalDistributedSystem.class);

		doReturn(mockCache).when(mockDistributionManager).getCache();
		doReturn(mockDistributedSystem).when(mockDistributionManager).getSystem();

		MembershipEvent<TestMembershipEvent> event = new TestMembershipEvent(mockDistributionManager);

		assertThat(event).isNotNull();
		assertThat(event.getCache().orElse(null)).isEqualTo(mockCache);
		assertThat(event.getDistributedMember().orElse(null)).isNull();
		assertThat(event.getDistributedSystem().orElse(null)).isEqualTo(mockDistributedSystem);
		assertThat(event.getDistributionManager()).isEqualTo(mockDistributionManager);
		assertThat(event.getType()).isEqualTo(MembershipEvent.Type.UNQUALIFIED);

		verify(mockDistributionManager, times(1)).getCache();
		verify(mockDistributionManager, times(1)).getSystem();
		verifyNoMoreInteractions(mockDistributionManager);
	}

	@Test
	public void withMemberReturnsMember() {

		DistributedMember mockDistributedMember = mock(DistributedMember.class);

		DistributionManager mockDistributionManager = mock(DistributionManager.class);

		MembershipEvent<TestMembershipEvent> event = new TestMembershipEvent(mockDistributionManager);

		assertThat(event).isNotNull();
		assertThat(event.getDistributedMember().orElse(null)).isNull();
		assertThat(event.withMember(mockDistributedMember)).isSameAs(event);
		assertThat(event.getDistributedMember().orElse(null)).isEqualTo(mockDistributedMember);
		assertThat(event.withMember(null)).isSameAs(event);
		assertThat(event.getDistributedMember().orElse(null)).isNull();
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructWithNullDistributionManagerThrowsIllegalArgumentException() {

		try {
			new TestMembershipEvent(null);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("DistributionManager must not be null");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	static class TestMembershipEvent extends MembershipEvent<TestMembershipEvent> {

		TestMembershipEvent(DistributionManager distributionManager) {
			super(distributionManager);
		}
	}
}
