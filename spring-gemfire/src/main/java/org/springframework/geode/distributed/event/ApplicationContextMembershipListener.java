/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.distributed.event;

import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.DistributedSystem;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.geode.distributed.event.support.MemberDepartedEvent;
import org.springframework.geode.distributed.event.support.MemberJoinedEvent;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * The {@link ApplicationContextMembershipListener} class is an extension of {@link MembershipListenerAdapter} used to
 * adapt the {@link ConfigurableApplicationContext} to handle and process {@link MembershipEvent membership events},
 * and specifically {@link MemberDepartedEvent} and {@link MemberJoinedEvent}, by
 * {@link ConfigurableApplicationContext#close() closing} and {@link ConfigurableApplicationContext#refresh() refreshing}
 * the {@link ConfigurableApplicationContext} when the {@link DistributedMember peer member} departs and joins the
 * {@link DistributedSystem cluster}.
 *
 * @author John Blum
 * @see org.apache.geode.distributed.DistributedMember
 * @see org.apache.geode.distributed.DistributedSystem
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.geode.distributed.event.support.MemberDepartedEvent
 * @see org.springframework.geode.distributed.event.support.MemberJoinedEvent
 * @since 1.3.0
 */
public class ApplicationContextMembershipListener
		extends MembershipListenerAdapter<ApplicationContextMembershipListener> {

	private final ConfigurableApplicationContext applicationContext;

	/**
	 * Constructs a new instance of {@link ConfigurableApplicationContext} initialized with
	 * the given {@link ConfigurableApplicationContext}.
	 *
	 * @param applicationContext configured {@link ConfigurableApplicationContext}; must not be {@literal null}.
	 * @throws IllegalArgumentException if {@link ConfigurableApplicationContext} is {@literal null}.
	 * @see org.springframework.context.ConfigurableApplicationContext
	 */
	public ApplicationContextMembershipListener(@NonNull ConfigurableApplicationContext applicationContext) {

		Assert.notNull(applicationContext, "ConfigurableApplicationContext must not be null");

		this.applicationContext = applicationContext;
	}

	/**
	 * Returns a reference to the configured {@link ConfigurableApplicationContext}.
	 *
	 * @return a reference to the configured {@link ConfigurableApplicationContext}.
	 * @see org.springframework.context.ConfigurableApplicationContext
	 */
	protected @NonNull ConfigurableApplicationContext getApplicationContext() {
		return this.applicationContext;
	}

	/**
	 * Handles the {@link MembershipEvent membership event} when a {@link DistributedMember peer member}
	 * departs from the {@link DistributedSystem cluster} by calling {@link ConfigurableApplicationContext#close()}.
	 *
	 * @param event {@link MemberDepartedEvent} to handle.
	 * @see org.springframework.geode.distributed.event.support.MemberDepartedEvent
	 * @see org.springframework.context.ConfigurableApplicationContext#close()
	 */
	@Override
	public void handleMemberDeparted(MemberDepartedEvent event) {
		getApplicationContext().close();
	}

	/**
	 * Handles the {@link MembershipEvent membership event} when a {@link DistributedMember peer member}
	 * joins the {@link DistributedSystem cluster} by calling {@link ConfigurableApplicationContext#refresh()}.
	 *
	 * @param event {@link MemberJoinedEvent} to handle.
	 * @see org.springframework.geode.distributed.event.support.MemberJoinedEvent
	 * @see org.springframework.context.ConfigurableApplicationContext#refresh()
	 */
	@Override
	public void handleMemberJoined(MemberJoinedEvent event) {
		getApplicationContext().refresh();
	}
}
