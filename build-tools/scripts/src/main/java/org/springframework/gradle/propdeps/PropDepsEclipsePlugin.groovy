/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.gradle.propdeps

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.plugins.ide.eclipse.EclipsePlugin

/**
 * Gradle {@link Plugin} to allow {@literal optional} and {@literal provided} dependency configurations
 * to work with the standard Gradle {@link EclipsePlugin}.
 *
 * @author Phillip Webb
 * @author John Blum
 * @see org.gradle.api.Plugin
 * @see org.gradle.api.Project
 * @see org.gradle.plugins.ide.eclipse.EclipsePlugin
 */
class PropDepsEclipsePlugin implements Plugin<Project> {

	void apply(Project project) {

		project.plugins.apply(PropDepsPlugin)
		project.plugins.apply(EclipsePlugin)

		project.eclipse {
			classpath {
				plusConfigurations += [ project.configurations.provided, project.configurations.optional ]
			}
		}
	}
}
