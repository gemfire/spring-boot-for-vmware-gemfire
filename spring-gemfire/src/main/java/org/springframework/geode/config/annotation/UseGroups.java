/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.geode.cache.client.ClientCache;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

/**
 * The {@link UseGroups} annotation configures the groups in which the member belongs in an Apache Geode
 * distributed system, whether the member is a {@link ClientCache} in a client/server topology
 * or a peer Cache in a cluster using the P2P topology.
 *
 * @author John Blum
 * @see Documented
 * @see Inherited
 * @see Retention
 * @see Target
 * @see ClientCache
 * @see Import
 * @see AliasFor
 * @see GroupsConfiguration
 * @since 1.0.0
 * @deprecated to be removed in 2.0 release
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(GroupsConfiguration.class)
@Deprecated(forRemoval = true)
@SuppressWarnings("unused")
public @interface UseGroups {

	@AliasFor("groups")
	String[] value() default {};

	@AliasFor("value")
	String[] groups() default {};

}
