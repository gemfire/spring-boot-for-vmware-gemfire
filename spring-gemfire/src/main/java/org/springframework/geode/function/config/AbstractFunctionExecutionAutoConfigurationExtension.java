/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.function.config;

import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.function.config.AbstractFunctionExecutionConfigurationSource;
import org.springframework.data.gemfire.function.config.AnnotationFunctionExecutionConfigurationSource;
import org.springframework.data.gemfire.function.config.FunctionExecutionBeanDefinitionRegistrar;
import org.springframework.util.Assert;

/**
 * The {@link AbstractFunctionExecutionAutoConfigurationExtension} class extends SDG's {@link FunctionExecutionBeanDefinitionRegistrar}
 * to redefine the location of application POJO {@link Function} {@link Execution} interfaces.
 *
 * @author John Blum
 * @see Execution
 * @see Function
 * @see BeanFactory
 * @see BeanFactoryAware
 * @see AutoConfigurationPackages
 * @see AnnotationMetadata
 * @see FunctionExecutionBeanDefinitionRegistrar
 * @since 1.0.0
 */
public abstract class AbstractFunctionExecutionAutoConfigurationExtension
		extends FunctionExecutionBeanDefinitionRegistrar implements BeanFactoryAware {

	private BeanFactory beanFactory;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	protected BeanFactory getBeanFactory() {

		Assert.state(this.beanFactory != null, "BeanFactory was not properly configured");

		return this.beanFactory;
	}

	protected abstract Class<?> getConfiguration();

	@SuppressWarnings("unused")
	@Override
	protected AbstractFunctionExecutionConfigurationSource newAnnotationBasedFunctionExecutionConfigurationSource(
			AnnotationMetadata annotationMetadata) {

		AnnotationMetadata metadata = AnnotationMetadata.introspect(getConfiguration());

		return new AnnotationFunctionExecutionConfigurationSource(metadata) {

			@Override
			public Iterable<String> getBasePackages() {
				return AutoConfigurationPackages.get(getBeanFactory());
			}
		};
	}
}
