/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure;

import org.apache.geode.cache.GemFireCache;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.function.config.EnableGemfireFunctions;
import org.springframework.data.gemfire.function.execution.GemfireFunctionOperations;
import org.springframework.geode.function.config.GemFireFunctionExecutionAutoConfigurationRegistrar;

/**
 * Spring Boot {@link EnableAutoConfiguration auto-configuration} enabling Apache Geode's Function Execution
 * functionality in a {@link GemFireCache} application.
 *
 * @author John Blum
 * @see GemFireCache
 * @see SpringBootConfiguration
 * @see EnableAutoConfiguration
 * @see Import
 * @see EnableGemfireFunctions
 * @see org.springframework.data.gemfire.function.config.EnableGemfireFunctionExecutions
 * @see GemFireFunctionExecutionAutoConfigurationRegistrar
 * @see GemfireFunctionOperations
 * @see ClientCacheAutoConfiguration
 * @since 1.0.0
 */
@SpringBootConfiguration
@AutoConfigureAfter(ClientCacheAutoConfiguration.class)
@ConditionalOnBean(GemFireCache.class)
@ConditionalOnClass({ GemfireFunctionOperations.class, GemFireCache.class })
@EnableGemfireFunctions
@Import(GemFireFunctionExecutionAutoConfigurationRegistrar.class)
@SuppressWarnings("unused")
public class FunctionExecutionAutoConfiguration {

}
