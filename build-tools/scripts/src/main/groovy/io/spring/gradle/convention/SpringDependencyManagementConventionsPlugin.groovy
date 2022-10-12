/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.spring.gradle.convention

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Applies and configures the Spring Gradle {@link DependencyManagementPlugin}.
 *
 * Additionally, if a {@literal gradle/dependency-management.gradle} file is present in a Gradle {@link Project},
 * then this file will be automatically applied in order to configure {@link Project} additional dependencies.
 *
 * @author Rob Winch
 * @author John Blum
 * @see org.gradle.api.Plugin
 * @see org.gradle.api.Project
 * @see org.gradle.api.plugins.PluginManager
 */
class SpringDependencyManagementConventionsPlugin implements Plugin<Project> {

    static final String DEPENDENCY_MANAGEMENT_RESOURCE = "gradle/dependency-management.gradle"

    @Override
    void apply(Project project) {

        applyAndConfigureDependencyManagementPlugin(project)
        applyDependencyManagementResources(project)
    }

    private void applyAndConfigureDependencyManagementPlugin(Project project) {

        project.getPluginManager().apply(DependencyManagementPlugin)

        project.dependencyManagement {
            resolutionStrategy {
                cacheChangingModulesFor 0, "seconds"
            }
        }
    }

    @SuppressWarnings("all")
    private void applyDependencyManagementResources(Project project) {

        File rootDir = project.rootDir

        List<File> dependencyManagementFiles = [ project.rootProject.file(DEPENDENCY_MANAGEMENT_RESOURCE) ]

        for (File dir = project.projectDir; dir != rootDir; dir = dir.parentFile) {
            dependencyManagementFiles.add(new File(dir, DEPENDENCY_MANAGEMENT_RESOURCE))
        }

        dependencyManagementFiles.each { file ->
            if (file.exists()) {
                project.apply from: file.absolutePath
            }
        }
    }
}
