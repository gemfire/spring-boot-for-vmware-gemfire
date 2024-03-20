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

/**
 * The {@link EnableClusterAware} annotation helps Spring Boot applications using Apache Geode decide whether it needs
 * to operate in {@literal local-only mode} or in a {@literal client/server topology}.
 *
 * @author John Blum
 * @see java.lang.annotation.Annotation
 * @see Documented
 * @see Inherited
 * @see Retention
 * @see Target
 * @see ClientCache
 * @see Import
 * @see org.springframework.data.gemfire.config.annotation.EnableClusterConfiguration
 * @since 1.2.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(ClusterAwareConfiguration.class)
@SuppressWarnings("unused")
public @interface EnableClusterAware {

	/**
	 * Determines whether the matching algorithm is strict.
	 *
	 * This means that at least 1 connection to a cluster of servers (1 or more) must be established
	 * before the cluster aware logic considers that a cluster actually exists.
	 *
	 * Previously, in cloud-managed environments (e.g. VMware Tanzu Application Service (TAS) for VMs, formerly known as
	 * Pivotal Platform or Pivotal CloudFoundry (PCF), or Kubernetes, known as VMware Tanzu Application Platform for K8S)
	 * it was assumed that a cluster would be provisioned and available, and that the Spring Boot, Apache Geode
	 * {@link ClientCache} application would connect to the cluster on deployment (push).
	 *
	 * However, is entirely possible that users may push Spring Boot, Apache Geode {@link ClientCache} applications
	 * to a cloud-managed environment where no cluster was provisioned and is available, and users simply want their
	 * apps to run in local-only mode.
	 *
	 * The strict match configuration setting absolutely requires at least 1 connection must be established. Use of this
	 * configuration setting also promotes a fail-fast protocol, or at least early detection (when log levels are
	 * adjusted accordingly) that a cluster is not available.
	 *
	 * Use {@literal spring.boot.data.gemfire.cluster.condition.match.strict}
	 * in Spring Boot {@literal application.properties}.
	 *
	 * Defaults to {@literal false}.
	 *
	 * @return a boolean value indicating whether strict matching mode is enabled.
	 */
	boolean strictMatch() default ClusterAwareConfiguration.DEFAULT_CLUSTER_AWARE_CONDITION_STRICT_MATCH;

}
