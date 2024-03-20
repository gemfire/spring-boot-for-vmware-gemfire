/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.context.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Integration Tests for {@link RefreshableAnnotationConfigApplicationContext}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.geode.context.annotation.RefreshableAnnotationConfigApplicationContext
 * @since 1.3.0
 */
@SuppressWarnings("unused")
public class RefreshableAnnotationConfigApplicationContextIntegrationTests {

	@Test
	public void constructsAndRefreshesRefreshableApplicationContextSuccessfully() {

		try (RefreshableAnnotationConfigApplicationContext applicationContext =
				 new RefreshableAnnotationConfigApplicationContext()) {

			assertThat(applicationContext).isNotNull();

			applicationContext.register(TestConfiguration.class);
			applicationContext.registerShutdownHook();
			applicationContext.refresh();

			assertThat(applicationContext.isActive()).isTrue();
			assertThat(applicationContext.isRunning()).isTrue();

			NamedBean testNameBean = applicationContext.getBean("TestName", NamedBean.class);

			assertThat(testNameBean).isNotNull();
			assertThat(testNameBean.getName()).isEqualTo("TestName");

			applicationContext.close();

			assertThat(applicationContext.isActive()).isFalse();
			assertThat(applicationContext.isRunning()).isFalse();

			applicationContext.refresh();

			assertThat(applicationContext.isActive()).isTrue();
			assertThat(applicationContext.isRunning()).isTrue();

			NamedBean refreshedTestNameBean = applicationContext.getBean("TestName", NamedBean.class);

			assertThat(refreshedTestNameBean).isNotNull();
			assertThat(refreshedTestNameBean).isEqualTo(testNameBean);
			assertThat(refreshedTestNameBean).isNotSameAs(testNameBean);
		}
	}

	@Configuration
	static class TestConfiguration {

		@Bean("TestName")
		NamedBean testNamedBean() {
			return new NamedBean();
		}
	}

	static class NamedBean implements BeanNameAware {

		private String name;

		public NamedBean() { }

		public NamedBean(String name) {
			setBeanName(name);
		}

		@Override
		public final void setBeanName(String name) {
			Assert.hasText(name, "Bean name is required");
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public boolean equals(Object obj) {

			if (this == obj) {
				return true;
			}

			if (!(obj instanceof NamedBean)) {
				return false;
			}

			NamedBean that = (NamedBean) obj;

			return this.getName().equals(that.getName());
		}

		@Override
		public int hashCode() {

			int hashValue = 17;

			hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getName());

			return hashValue;
		}

		@Override
		public String toString() {
			return getName();
		}
	}
}
