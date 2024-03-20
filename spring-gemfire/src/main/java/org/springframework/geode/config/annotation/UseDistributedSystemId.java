/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.client.ClientCache;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

/**
 * The {@link UseDistributedSystemId} annotation configures the {@literal distributed-system-id} property
 * of a {@link Cache peer Cache member} in an Apache Geode P2P topology.
 *
 * This configuration annotation is only applicable on {@link Cache peer Cache members}
 * and has no effect on {@link ClientCache} instances.
 *
 * @author John Blum
 * @see Documented
 * @see Inherited
 * @see Retention
 * @see Target
 * @see Cache
 * @see Import
 * @see AliasFor
 * @see DistributedSystemIdConfiguration
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(DistributedSystemIdConfiguration.class)
@SuppressWarnings("unused")
public @interface UseDistributedSystemId {

	/**
	 * Configures the identifier used to distinguish messages from different distributed systems.
	 *
	 * This is required for Portable Data eXchange (PDX) data serialization.
	 *
	 * Set distributed-system-id to different values for different systems in a multi-site (WAN) configuration,
	 * and to different values for production vs. development environments. This setting must be the same
	 * for every member of a given distributed system and unique to each distributed system within a WAN installation.
	 *
	 * Valid values are integers in the range -1…255. -1 means no setting.
	 *
	 * Defaults to {@literal -1}.
	 */
	@AliasFor("id")
	int value() default -1;

	/**
	 * Configures the identifier used to distinguish messages from different distributed systems.
	 *
	 * Alias for {@literal #value()}.
	 */
	@AliasFor("value")
	int id() default -1;

}
