/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.security.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.gemfire.config.annotation.support.AutoConfiguredAuthenticationInitializer.SECURITY_PASSWORD_PROPERTY;
import static org.springframework.data.gemfire.config.annotation.support.AutoConfiguredAuthenticationInitializer.SECURITY_USERNAME_PROPERTY;
import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalArgumentException;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;

import java.io.Serializable;
import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

import org.junit.Test;

import org.apache.geode.security.AuthenticationFailedException;
import org.apache.geode.security.ResourcePermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import example.echo.config.EchoClientConfiguration;
import example.echo.config.EchoServerConfiguration;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The {@link AbstractAutoConfiguredSecurityContextIntegrationTests} class is an abstract security context integration test class
 * encapsulating configuration and functionality common to both cloud and local security context integration tests.
 *
 * @author John Blum
 * @see java.security.Principal
 * @see java.util.Properties
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.security.ResourcePermission
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.context.annotation.Import
 * @see org.springframework.core.env.Environment
 * @see org.springframework.core.io.ClassPathResource
 * @see org.springframework.data.gemfire.GemfireTemplate
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class AbstractAutoConfiguredSecurityContextIntegrationTests
		extends ForkingClientServerIntegrationTestsSupport {

	private static final String SECURITY_CONTEXT_USERNAME_PROPERTY = "test.security.context.username";
	private static final String SECURITY_CONTEXT_PASSWORD_PROPERTY = "test.security.context.password";

	@Autowired
	private GemfireTemplate echoTemplate;

	@Test
	public void clientServerAuthWasSuccessful() {

		assertThat(this.echoTemplate.<String, String>get("Hello")).isEqualTo("Hello");
		assertThat(this.echoTemplate.<String, String>get("Test")).isEqualTo("Test");
		assertThat(this.echoTemplate.<String, String>get("Good-Bye")).isEqualTo("Good-Bye");
	}

	@Configuration
	@Import(EchoClientConfiguration.class)
	protected static abstract class BaseGemFireClientConfiguration { }

	@Configuration
	@Import(EchoServerConfiguration.class)
	protected static abstract class BaseGemFireServerConfiguration {

		@Bean
		TestSecurityManager testSecurityManager(Environment environment) {
			return new TestSecurityManager(environment);
		}
	}

	public static class TestSecurityManager implements org.apache.geode.security.SecurityManager {

		private final String username;
		private final String password;

		public TestSecurityManager(Environment environment) {

			this.username = Optional.ofNullable(environment.getProperty(SECURITY_CONTEXT_USERNAME_PROPERTY))
				.filter(StringUtils::hasText)
				.orElseThrow(() -> newIllegalArgumentException("Username is required"));

			this.password = Optional.ofNullable(environment.getProperty(SECURITY_CONTEXT_PASSWORD_PROPERTY))
				.filter(StringUtils::hasText)
				.orElseThrow(() -> newIllegalArgumentException("Password is required"));
		}

		private ClassPathResource resolveApplicationProperties(Environment environment) {

			Assert.notNull(environment, "Environment must not be null");

			return Arrays.stream(nullSafeArray(environment.getActiveProfiles(), String.class))
				.filter(StringUtils::hasText)
				.filter(it -> !"default".equalsIgnoreCase(it))
				.map(it -> String.format("application-%s.properties", it))
				.map(ClassPathResource::new)
				.filter(ClassPathResource::exists)
				.findFirst()
				.orElseThrow(() ->
					newIllegalStateException("Unable to resolve application.properties from Environment [%s]",
						environment));

		}

		String getUsername() {
			return this.username;
		}

		String getPassword() {
			return this.password;
		}

		@Override
		public Object authenticate(Properties credentials) throws AuthenticationFailedException {

			String username = credentials.getProperty(SECURITY_USERNAME_PROPERTY);
			String password = credentials.getProperty(SECURITY_PASSWORD_PROPERTY);

			if (!(getUsername().equals(username) && getPassword().equals(password))) {
				throw new AuthenticationFailedException(String.format("Failed to authenticate user [%s]", username));
			}

			return User.with(username).having(password);
		}

		@Override
		public boolean authorize(Object principal, ResourcePermission permission) {
			return true;
		}
	}

	@Getter
	@ToString(of = "name")
	@EqualsAndHashCode(of = "name")
	@RequiredArgsConstructor(staticName = "with")
	static class User implements Principal, Serializable {

		@NonNull
		private final String name;

		@Setter(AccessLevel.PRIVATE)
		private String password;

		User having(String password) {
			setPassword(password);
			return this;
		}
	}
}