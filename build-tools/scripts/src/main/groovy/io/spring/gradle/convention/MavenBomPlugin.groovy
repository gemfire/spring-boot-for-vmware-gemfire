/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.spring.gradle.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlatformPlugin
import org.gradle.api.plugins.PluginManager
import org.springframework.gradle.CopyPropertiesPlugin
import org.springframework.gradle.maven.SpringMavenPlugin

/**
 * Gradle {@link Plugin} used to generate a Maven BOM for the Gradle {@link Project}.
 *
 * @author Rob Winch
 * @author John Blum
 * @see org.gradle.api.Plugin
 * @see org.gradle.api.Project
 */
class MavenBomPlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {

		PluginManager pluginManager = project.getPluginManager();

		pluginManager.apply(JavaPlatformPlugin)
		pluginManager.apply(SpringMavenPlugin)
		pluginManager.apply(CopyPropertiesPlugin)

	}
}
