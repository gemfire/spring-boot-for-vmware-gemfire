/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.gradle.propdeps

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.plugins.ide.idea.IdeaPlugin

/**
 * Gradle {@link Plugin} to allow {@literal optional} and {@literal provided} dependency configurations
 * to work with the standard Gradle {@link IdeaPlugin}.
 *
 * @author Phillip Webb
 * @author Brian Clozel
 * @author John Blum
 * @see org.gradle.api.Plugin
 * @see org.gradle.api.Project
 * @see org.gradle.plugins.ide.idea.IdeaPlugin
 * @link https://youtrack.jetbrains.com/issue/IDEA-107046
 * @link https://youtrack.jetbrains.com/issue/IDEA-117668
 */
class PropDepsIdeaPlugin implements Plugin<Project> {

	void apply(Project project) {

		project.plugins.apply(PropDepsPlugin)
		project.plugins.apply(IdeaPlugin)
		project.idea.module {
			// IntelliJ IDEA internally deals with 4 scopes : COMPILE, TEST, PROVIDED, RUNTIME
			// but only PROVIDED seems to be picked up
			scopes.PROVIDED.plus += [ project.configurations.provided ]
			scopes.PROVIDED.plus += [ project.configurations.optional ]
		}
	}
}
