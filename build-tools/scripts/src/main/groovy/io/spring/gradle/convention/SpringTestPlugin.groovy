/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.spring.gradle.convention

import org.gradle.api.Project

/**
 * Gradle Plugin used to disable Sonar Qube inspection(s) during Spring project tests.
 * @author Rob Winch
 * @author John Blum
 */
class SpringTestPlugin extends AbstractSpringJavaPlugin {

	@Override
	void applyAdditionalPlugins(Project project) {
		Utils.skipProjectWithSonarQubePlugin(project)
	}
}
