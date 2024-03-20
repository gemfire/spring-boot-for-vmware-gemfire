/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.geode.cache.peer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.geode.distributed.event.ApplicationContextMembershipListener;
import org.springframework.geode.distributed.event.MembershipListenerAdapter;
import org.springframework.geode.distributed.event.support.MemberDepartedEvent;
import org.springframework.geode.distributed.event.support.MemberJoinedEvent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit Tests for {@link SpringBootApacheGeodePeerCacheApplication}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Cache
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.context.support.AbstractRefreshableApplicationContext
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.geode.distributed.event.ApplicationContextMembershipListener
 * @see org.springframework.geode.distributed.event.MembershipListenerAdapter
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.3.0
 */
@RunWith(SpringRunner.class)
@EnableGemFireMockObjects
@ContextConfiguration(
	classes = SpringBootApacheGeodePeerCacheApplication.class,
	loader = TestRefreshableApplicationContextLoader.class
)
@SuppressWarnings({ "unused" })
public class SpringBootApacheGeodePeerCacheApplicationUnitTests extends IntegrationTestsSupport {

	@Autowired
	private ConfigurableApplicationContext applicationContext;

	@Autowired
	@Qualifier("applicationContextMembershipListener")
	private MembershipListenerAdapter<ApplicationContextMembershipListener> membershipListener;

	@Before
	public void setup() {

		//System.err.printf("ApplicationContext Type [%s]%n", ObjectUtils.nullSafeClassName(this.applicationContext));

		assertThat(this.applicationContext).isInstanceOf(AbstractRefreshableApplicationContext.class);
	}

	@Test
	public void memberFartedAndDepartedThenJoinedAndWasPurloined() {

		MemberDepartedEvent mockMemberDepartedEvent = mock(MemberDepartedEvent.class);
		MemberJoinedEvent mockMemberJoinedEvent = mock(MemberJoinedEvent.class);

		Cache peerCache = this.applicationContext.getBean(Cache.class);

		assertThat(peerCache).isNotNull();

		this.membershipListener.handleMemberDeparted(mockMemberDepartedEvent);

		assertThat(this.applicationContext.isActive()).isFalse();
		assertThat(this.applicationContext.isRunning()).isFalse();

		this.membershipListener.handleMemberJoined(mockMemberJoinedEvent);

		assertThat(this.applicationContext.isActive()).isTrue();
		assertThat(this.applicationContext.isRunning()).isTrue();

		Cache reconnectedPeerCache = this.applicationContext.getBean(Cache.class);

		assertThat(reconnectedPeerCache).isNotNull();
		assertThat(reconnectedPeerCache).isNotSameAs(peerCache);
	}
}
