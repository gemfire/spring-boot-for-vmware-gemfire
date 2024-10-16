/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright 2017-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package example.app.crm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.apache.geode.cache.client.ClientRegionShortcut;

import example.app.crm.model.Customer;

/**
 * Spring {@link Configuration} class used to configure and initialize Apache Geode for the CRM application
 * beyond Spring Boot {@literal auto-configuration}.
 *
 * @author John Blum
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions
 * @see org.springframework.geode.config.annotation.EnableClusterAware
 * @since 1.2.0
 */
// tag::class[]
@Configuration
@EnableEntityDefinedRegions(basePackageClasses = Customer.class, clientRegionShortcut = ClientRegionShortcut.LOCAL)
public class CustomerConfiguration {

}
// end::class[]
