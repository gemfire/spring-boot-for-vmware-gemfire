/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.spring.gradle.convention

import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.PluginManager
import org.springframework.gradle.maven.SpringMavenPlugin

/**
 * Defines a Gradle {@link Project} as a Spring module.
 *
 * @author Rob Winch
 * @author John Blum
 * @see io.spring.gradle.convention.AbstractSpringJavaPlugin
 * @see org.springframework.gradle.maven.SpringMavenPlugin
 * @see org.gradle.api.plugins.JavaLibraryPlugin
 * @see org.gradle.api.Project
 */
class SpringModulePlugin extends AbstractSpringJavaPlugin {

	@Override
	void applyAdditionalPlugins(Project project) {

		PluginManager pluginManager = project.getPluginManager();

		pluginManager.apply(JavaLibraryPlugin.class)
		pluginManager.apply(SpringMavenPlugin.class);
	}
}
