/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.distributed.event.support;

import java.util.Optional;

import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.DistributedSystem;
import org.apache.geode.distributed.internal.DistributionManager;

import org.springframework.geode.distributed.event.MembershipEvent;

/**
 * {@link MembershipEvent} fired when a {@link DistributedMember} of the {@link DistributedSystem} is suspected
 * of being unresponsive to other {@link DistributedMember peer members} in the cluster.
 *
 * @author John Blum
 * @see org.apache.geode.distributed.DistributedMember
 * @see org.apache.geode.distributed.DistributedSystem
 * @see MembershipEvent
 * @since 1.3.0
 */
public class MemberSuspectEvent extends MembershipEvent<MemberSuspectEvent> {

	private DistributedMember suspectMember;

	private String reason;

	/**
	 * Constructs a new instance of {@link MemberSuspectEvent} initialized with the given {@link DistributionManager}.
	 *
	 * @param distributionManager {@link DistributionManager} used as the {@link #getSource() source} of this event;
	 * must not be {@literal null}.
	 * @throws IllegalArgumentException if {@link DistributionManager} is {@literal null}.
	 * @see org.apache.geode.distributed.internal.DistributionManager
	 */
	public MemberSuspectEvent(DistributionManager distributionManager) {
		super(distributionManager);
	}

	/**
	 * Returns an {@link Optional} {@link String reason} describing the suspicion of the peer member.
	 *
	 * @return an {@link Optional} {@link String reason} describing the suspicion of the peer member.
	 * @see Optional
	 */
	public Optional<String> getReason() {
		return Optional.ofNullable(this.reason);
	}

	/**
	 * Returns an {@link Optional} {@link DistributedMember} identified as the suspect in the {@link MembershipEvent}.
	 *
	 * @return an {@link Optional} {@link DistributedMember} identified as the suspect in the {@link MembershipEvent}.
	 * @see org.apache.geode.distributed.DistributedMember
	 * @see Optional
	 */
	public Optional<DistributedMember> getSuspectMember() {
		return Optional.ofNullable(this.suspectMember);
	}

	/**
	 * Builder method used to configure the {@link String reason} describing the suspicion of
	 * the {@link DistributedMember suspect member}.
	 *
	 * @param reason {@link String} describing the suspicion of the {@link DistributedMember peer member};
	 * may be {@literal null}.
	 * @return this {@link MemberSuspectEvent}.
	 * @see #getReason()
	 */
	public MemberSuspectEvent withReason(String reason) {

		this.reason = reason;

		return this;
	}

	/**
	 * Builder method used to configure the {@link DistributedMember peer member} that is the subject of the suspicion
	 * {@link MembershipEvent}.
	 *
	 * @param suspectMember {@link DistributedMember peer member} that is being suspected; may be {@literal null}.
	 * @return this {@link MemberSuspectEvent}.
	 * @see #getSuspectMember()
	 */
	public MemberSuspectEvent withSuspect(DistributedMember suspectMember) {

		this.suspectMember = suspectMember;

		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Type getType() {
		return Type.MEMBER_SUSPECT;
	}
}
