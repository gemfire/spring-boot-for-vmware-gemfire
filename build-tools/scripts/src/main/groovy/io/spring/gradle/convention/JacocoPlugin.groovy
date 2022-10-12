/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.spring.gradle.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

/**
 * Applies the Jacoco Gradle {@link Plugin} to the target Gradle {@link Project}
 * and configures {@literal check} Gradle Task to depend on the {@literal jacocoTestReport} Gradle Task.
 *
 * @author Rob Winch
 * @author John Blum
 * @see org.gradle.api.Plugin
 * @see org.gradle.api.Project
 */
class JacocoPlugin implements Plugin<Project> {

	private static final String JACOCO_VERSION = '0.8.7';

	@Override
	void apply(Project project) {

		project.plugins.withType(JavaPlugin) {

			project.getPluginManager().apply("jacoco")
			project.tasks.check.dependsOn project.tasks.jacocoTestReport

			project.jacoco {
				toolVersion = JACOCO_VERSION
			}
		}
	}
}
