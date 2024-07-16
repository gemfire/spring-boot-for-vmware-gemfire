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
 * @since 1.2.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(ClusterAwareConfiguration.class)
@SuppressWarnings("unused")
public @interface EnableClusterAware {

}
