/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.config.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.config.annotation.PeerCacheConfigurer;

/**
 * Spring {@link Configuration} class used to configure a {@link org.apache.geode.security.SecurityManager},
 * thereby enabling Security (Auth) on this Apache Geode node.
 *
 * @author John Blum
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer
 * @see org.springframework.data.gemfire.config.annotation.PeerCacheConfigurer
 * @since 1.1.0
 */
@Configuration
@SuppressWarnings("unused")
public class SecurityManagerConfiguration {

	@Bean
	ClientCacheConfigurer clientSecurityManagerConfigurer(org.apache.geode.security.SecurityManager securityManager) {
		return (beanName, clientCacheFactoryBean) -> clientCacheFactoryBean.setSecurityManager(securityManager);
	}

	@Bean
	PeerCacheConfigurer peerSecurityManagerConfigurer(org.apache.geode.security.SecurityManager securityManager) {
		return (beanName, cacheFactoryBean) -> cacheFactoryBean.setSecurityManager(securityManager);
	}
}
