/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.spring.gradle.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.javadoc.Javadoc

/**
 * Configures Javadoc (Gradle Task) to disable the DocLint tool by setting the {@literal -Xdoclint} JVM extension option
 * to {@literal none} as well as setting the {@literal -quiet} Javadoc option thereby suppressing the output from
 * the Javadoc tool.
 *
 * @author Rob Winch
 * @author John Blum
 * @see org.gradle.api.Plugin
 * @see org.gradle.api.Project
 * @see org.gradle.api.tasks.javadoc.Javadoc
 */
class JavadocOptionsPlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {

		project.getTasks().withType(Javadoc).all { task ->
			task.options.addStringOption('Xdoclint:none', '-quiet')
		}
	}
}
