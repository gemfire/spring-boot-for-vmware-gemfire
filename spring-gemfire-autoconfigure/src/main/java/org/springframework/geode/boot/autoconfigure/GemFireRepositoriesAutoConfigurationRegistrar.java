/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure;

import java.lang.annotation.Annotation;

import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.data.gemfire.repository.config.GemfireRepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/**
 * Spring {@link ImportBeanDefinitionRegistrar} used to auto-configure Spring Data Geode Repositories.
 *
 * @author John Blum
 * @see AbstractRepositoryConfigurationSourceSupport
 * @see EnableGemfireRepositories
 * @see GemfireRepositoryConfigurationExtension
 * @see RepositoriesAutoConfiguration
 * @since 1.0.0
 */
public class GemFireRepositoriesAutoConfigurationRegistrar extends AbstractRepositoryConfigurationSourceSupport {

	@Override
	protected Class<? extends Annotation> getAnnotation() {
		return EnableGemfireRepositories.class;
	}

	@Override
	protected Class<?> getConfiguration() {
		return EnableGemFireRepositoriesConfiguration.class;
	}

	@Override
	protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
		return new GemfireRepositoryConfigurationExtension();
	}

	@EnableGemfireRepositories
	private static class EnableGemFireRepositoriesConfiguration { }

}
