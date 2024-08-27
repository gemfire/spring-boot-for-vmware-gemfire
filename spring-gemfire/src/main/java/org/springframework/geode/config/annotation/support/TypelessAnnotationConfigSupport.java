/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.config.annotation.support;

import java.lang.annotation.Annotation;

import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport;

/**
 * The {@link TypelessAnnotationConfigSupport} class is an extension of SDG's {@link AbstractAnnotationConfigSupport}
 * based class for resolving {@link AnnotatedTypeMetadata}, however, is not based on any specific {@link Annotation}.
 *
 * @author John Blum
 * @see java.lang.annotation.Annotation
 * @see org.springframework.core.type.AnnotatedTypeMetadata
 * @see org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport
 * @since 1.2.0
 */
public class TypelessAnnotationConfigSupport extends AbstractAnnotationConfigSupport {

	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return null;
	}
}
