/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.function.config;

import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;

import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.data.gemfire.function.config.EnableGemfireFunctionExecutions;

/**
 * The {@link GemFireFunctionExecutionAutoConfigurationRegistrar} class is a Spring {@link ImportBeanDefinitionRegistrar}
 * used to register SDG POJO interfaces defining Apache Geode {@link Function} {@link Execution Executions}.
 *
 * @author John Blum
 * @see Execution
 * @see Function
 * @see EnableGemfireFunctionExecutions
 * @see AbstractFunctionExecutionAutoConfigurationExtension
 * @since 1.0.0
 */
public class GemFireFunctionExecutionAutoConfigurationRegistrar
		extends AbstractFunctionExecutionAutoConfigurationExtension {

	@Override
	protected Class<?> getConfiguration() {
		return EnableGemfireFunctionExecutionsConfiguration.class;
	}

	@EnableGemfireFunctionExecutions
	private static class EnableGemfireFunctionExecutionsConfiguration { }

}
