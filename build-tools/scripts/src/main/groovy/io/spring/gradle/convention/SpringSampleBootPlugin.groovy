/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.spring.gradle.convention

import org.gradle.api.Project

/**
 * @author Rob Winch
 * @author John Blum
 */
class SpringSampleBootPlugin extends SpringSamplePlugin {

    @Override
    void applyAdditionalPlugins(Project project) {

        project.getPluginManager().apply("org.springframework.boot");

        super.applyAdditionalPlugins(project);

        project.repositories {
            maven { url 'https://repo.spring.io/milestone' }
            maven { url 'https://repo.spring.io/snapshot' }
        }
    }
}
