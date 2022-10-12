/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.spring.gradle.convention;

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.jvm.tasks.Jar

/**
 * Adds ability to depend on the test JAR within other Gradle {@link Project Projects} using:
 *
 * <code>
 * testImplementation project(path: ':foo', configuration: 'tests')
 * </code>
 *
 * @author Rob Winch
 * @author John Blum
 * @see org.gradle.api.Plugin
 * @see org.gradle.api.Project
 */
class TestsConfigurationPlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {

		project.plugins.withType(JavaPlugin) {

			project.configurations {
				tests.extendsFrom testRuntimeClasspath
			}

			project.tasks.create('testJar', Jar) {
				archiveClassifier = 'test'
				from project.sourceSets.test.output
			}

			project.artifacts {
				tests project.testJar
			}
		}
	}
}
