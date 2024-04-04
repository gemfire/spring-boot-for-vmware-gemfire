/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.config.annotation;

import static org.springframework.geode.config.annotation.LocatorsConfiguration.DEFAULT_LOCATORS;
import static org.springframework.geode.config.annotation.LocatorsConfiguration.DEFAULT_REMOTE_LOCATORS;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.geode.cache.Cache;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

/**
 * The {@link UseLocators} annotation configures the {@literal locators} and/or {@literal remote-locators} Apache Geode
 * properties used by a {@link Cache peer Cache member} to join a cluster of servers when using the P2P topology
 * as well as when configuring the multi-site, WAN topology.
 *
 * @author John Blum
 * @see Documented
 * @see Inherited
 * @see Retention
 * @see Target
 * @see Cache
 * @see Import
 * @see AliasFor
 * @see MemberNameConfiguration
 * @since 1.0.0
 * @deprecated to be removed in 2.0 release
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(LocatorsConfiguration.class)
@Deprecated(forRemoval = true)
@SuppressWarnings("unused")
public @interface UseLocators {

	/**
	 * @see #locators()
	 */
	@AliasFor("locators")
	String value() default DEFAULT_LOCATORS;

	/**
	 * The list of Locators used by system members.  The list must be configured consistently for every member of
	 * the cluster (a.k.a. distributed system).  If the list is empty, Locators will not be used.
	 *
	 * For each Locator, provide a hostname and/or address (separated by ‘@’, if you use both), followed by
	 * a port number in brackets.
	 *
	 * For example:
	 *
	 * <code>
	 * locators=address1[port1],address2[port2],...,addressN[portN]
	 * locators=hostname1@address1[port1],hostname2@address2[port2],...,hostnameN@addressN[portN]
	 * locators=hostname1[port1],hostname2[port2],...,hostnameN[portN]
	 * </code>
	 *
	 *  Defaults to {@literal localhost[10334]}.
	 */
	@AliasFor("value")
	String locators() default DEFAULT_LOCATORS;

	/**
	 * Used to configure the Locators that a cluster will use in order to connect to a remote site in a multi-site
	 * (WAN) topology configuration.
	 *
	 * To use Locators in a WAN configuration, you must specify a unique distributed system ID ({@literal distributed-system-id})
	 * for the local cluster and remote Locator(s) for the remote clusters to which you will connect.
	 *
	 * For each remote Locator, provide a host name and/or address (separated by ‘@’, if you use both), followed by
	 * a port number in brackets.
	 *
	 * For example:
	 *
	 * <code>
	 * remote-locators=address1[port1],address2[port2],...,addressN[portN]
	 * remote-locators=hostname1@address1[port1],hostname2@address2[port2],...,hostnameN@addressN[portN]
	 * remote-locators=hostname1[port1],hostname2[port2],...,hostnameN[portN]
	 * </code>
	 *
	 * Defaults to unset.
	 */
	String remoteLocators() default DEFAULT_REMOTE_LOCATORS;

}
