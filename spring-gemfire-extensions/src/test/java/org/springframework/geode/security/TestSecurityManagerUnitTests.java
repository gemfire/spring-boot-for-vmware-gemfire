/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.security.Principal;
import java.util.Properties;

import org.junit.Test;

import org.apache.geode.security.AuthenticationFailedException;
import org.apache.geode.security.ResourcePermission;

import org.springframework.geode.util.GeodeConstants;

/**
 * Unit Tests for {@link org.springframework.geode.security.TestSecurityManager}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.springframework.geode.security.TestSecurityManager
 * @since 1.0.0
 */
public class TestSecurityManagerUnitTests {

	private TestSecurityManager securityManager = new TestSecurityManager();

	private Properties newSecurityProperties(String username, String password) {

		Properties securityProperties = new Properties();

		securityProperties.setProperty(GeodeConstants.USERNAME, username);
		securityProperties.setProperty(GeodeConstants.PASSWORD, password);

		return securityProperties;
	}

	@Test
	public void userAuthenticates() {

		Object user = this.securityManager.authenticate(newSecurityProperties("test", "test"));

		assertThat(user).isInstanceOf(TestSecurityManager.User.class);
		assertThat(((TestSecurityManager.User) user).getName()).isEqualTo("test");
	}

	@Test(expected = AuthenticationFailedException.class)
	public void userDoesNotAuthenticateBecauseUsernamePasswordAreCaseSensitive() {

		try {
			this.securityManager.authenticate(newSecurityProperties("TestUser", "testuser"));
		}
		catch (AuthenticationFailedException expected) {

			assertThat(expected).hasMessage("User [TestUser] could not be authenticated");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test(expected = AuthenticationFailedException.class)
	public void userDoesNotAuthenticateWhenUsernamePasswordDoNotMatch() {

		try {
			this.securityManager.authenticate(newSecurityProperties("testUser", "testPassword"));
		}
		catch (AuthenticationFailedException expected) {

			assertThat(expected).hasMessage("User [testUser] could not be authenticated");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void userIsAlwaysAuthorized() {

		ResourcePermission clusterManage =
			new ResourcePermission(ResourcePermission.Resource.CLUSTER, ResourcePermission.Operation.MANAGE);

		assertThat(this.securityManager.authorize(null, clusterManage)).isTrue();
		assertThat(this.securityManager.authorize(new TestSecurityManager.User("test"), clusterManage)).isTrue();
		assertThat(this.securityManager.authorize(mock(Principal.class), clusterManage)).isTrue();
	}
}
