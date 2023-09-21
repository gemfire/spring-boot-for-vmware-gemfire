/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.distributed.event.support;

import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.DistributedSystem;
import org.apache.geode.distributed.internal.DistributionManager;

import org.springframework.geode.distributed.event.MembershipEvent;

/**
 * {@link MembershipEvent} fired when a {@link DistributedMember} joins the {@link DistributedSystem}.
 *
 * @author John Blum
 * @see org.apache.geode.distributed.DistributedMember
 * @see org.apache.geode.distributed.DistributedSystem
 * @see MembershipEvent
 * @since 1.3.0
 */
public class MemberJoinedEvent extends MembershipEvent<MemberJoinedEvent> {

	/**
	 * Constructs a new instance of {@link MemberJoinedEvent} initialized with the given {@link DistributionManager}.
	 *
	 * @param distributionManager {@link DistributionManager} used as the {@link #getSource() source} of this event;
	 * must not be {@literal null}.
	 * @throws IllegalArgumentException if {@link DistributionManager} is {@literal null}.
	 * @see org.apache.geode.distributed.internal.DistributionManager
	 */
	public MemberJoinedEvent(DistributionManager distributionManager) {
		super(distributionManager);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Type getType() {
		return Type.MEMBER_JOINED;
	}
}
