/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.config.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * Spring {@link Annotation} to enable Apache Geode Security (Auth).
 *
 * @author John Blum
 * @see Annotation
 * @see Documented
 * @see Inherited
 * @see Retention
 * @see Target
 * @see Import
 * @see SecurityManagerConfiguration
 * @since 1.1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(SecurityManagerConfiguration.class)
@SuppressWarnings("unused")
public @interface EnableSecurityManager {

}
