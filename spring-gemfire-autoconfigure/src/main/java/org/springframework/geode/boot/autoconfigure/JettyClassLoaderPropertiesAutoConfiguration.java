/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.geode.boot.autoconfigure.configuration.JettyClassLoaderProperties;

@SpringBootConfiguration
@EnableConfigurationProperties({ JettyClassLoaderProperties.class })
public class JettyClassLoaderPropertiesAutoConfiguration {
}
