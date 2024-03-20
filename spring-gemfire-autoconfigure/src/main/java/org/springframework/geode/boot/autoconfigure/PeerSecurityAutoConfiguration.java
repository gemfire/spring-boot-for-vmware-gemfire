/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.ApacheShiroSecurityConfiguration;
import org.springframework.data.gemfire.config.annotation.GeodeIntegratedSecurityConfiguration;
import org.springframework.geode.config.annotation.EnableSecurityManager;

/**
 * Spring Boot {@link EnableAutoConfiguration auto-configuration} enabling Apache Geode's Security functionality,
 * and specifically Authentication between a client and server using Spring Data Geode Security annotations.
 *
 * @author John Blum
 * @see org.apache.geode.security.SecurityManager
 * @see SpringBootConfiguration
 * @see EnableAutoConfiguration
 * @see org.springframework.context.annotation.Import
 * @see org.springframework.data.gemfire.CacheFactoryBean
 * @see ClientCacheFactoryBean
 * @see ApacheShiroSecurityConfiguration
 * @see GeodeIntegratedSecurityConfiguration
 * @see EnableSecurityManager
 * @since 1.0.0
 */
@SpringBootConfiguration
@ConditionalOnBean(org.apache.geode.security.SecurityManager.class)
@ConditionalOnMissingBean({
	ClientCacheFactoryBean.class,
	ApacheShiroSecurityConfiguration.class,
	GeodeIntegratedSecurityConfiguration.class
})
@EnableSecurityManager
//@Import(HttpBasicAuthenticationSecurityConfiguration.class)
@SuppressWarnings("unused")
public class PeerSecurityAutoConfiguration {

}
