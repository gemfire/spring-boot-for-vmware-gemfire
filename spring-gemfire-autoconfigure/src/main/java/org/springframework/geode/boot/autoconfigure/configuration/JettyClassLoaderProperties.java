/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.gemfire.jetty.classloader")
public class JettyClassLoaderProperties {
    private String[] excludedResources;

    public void setExcludedResources(String[] excludedResources) {
        this.excludedResources = excludedResources;
    }

    public String[] getExcludedResources() {
        return this.excludedResources;
    }
}
