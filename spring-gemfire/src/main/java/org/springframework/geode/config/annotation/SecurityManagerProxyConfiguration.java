/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.config.annotation;

import java.util.Properties;

import org.apache.geode.cache.Cache;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.gemfire.config.annotation.EnableBeanFactoryLocator;
import org.springframework.data.gemfire.config.annotation.EnableSecurity;
import org.springframework.geode.security.support.SecurityManagerProxy;

/**
 * Spring {@link Configuration} class used to configure a {@link org.apache.geode.security.SecurityManager},
 * thereby enabling Security (Auth) on this Apache Geode node.
 *
 * @author John Blum
 * @see org.springframework.context.ApplicationListener
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.context.event.ContextRefreshedEvent
 * @see org.springframework.data.gemfire.config.annotation.EnableBeanFactoryLocator
 * @see org.springframework.data.gemfire.config.annotation.EnableSecurity
 * @see org.springframework.geode.security.support.SecurityManagerProxy
 * @since 1.1.0
 */
@Configuration
@EnableBeanFactoryLocator
@EnableSecurity(securityManagerClassName = "org.springframework.geode.security.support.SecurityManagerProxy")
@SuppressWarnings("unused")
public class SecurityManagerProxyConfiguration implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		ApplicationContext applicationContext = event.getApplicationContext();

		SecurityManagerProxy securityManagerProxy = SecurityManagerProxy.getInstance();

		securityManagerProxy.setBeanFactory(applicationContext.getAutowireCapableBeanFactory());
		securityManagerProxy.initialize(applicationContext.getBean(Cache.class), new Properties());
	}
}
