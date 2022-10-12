/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.spring.gradle.convention

import org.gradle.api.Project

/**
 * Gradle Spring Java Plugin used to identify a Gradle {@link Project} as a {@literal Sample} and add configuration
 * to skip Sonar Qube inspections.
 *
 * @author Rob Winch
 * @author John Blum
 * @see org.gradle.api.Project
 */
class SpringSamplePlugin extends AbstractSpringJavaPlugin {

    @Override
    void applyAdditionalPlugins(Project project) {
        Utils.skipProjectWithSonarQubePlugin(project)
    }
}
