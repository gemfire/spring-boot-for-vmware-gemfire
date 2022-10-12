/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Copies {@literal root} {@link Project} properties to the target ({@literal this}) {@link Project},
 * the {@link Project} for which {@literal this} Gradle {@link Plugin} is applied.
 *
 * @author Rob Winch
 * @author John Blum
 * @see org.gradle.api.Plugin
 * @see org.gradle.api.Project
 */
public class CopyPropertiesPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {

		copyPropertyFromRootProjectTo("group", project);
		copyPropertyFromRootProjectTo("version", project);
		copyPropertyFromRootProjectTo("description", project);
	}

	private void copyPropertyFromRootProjectTo(String propertyName, Project project) {

		Object propertyValue = project.getRootProject().findProperty(propertyName);

		if (propertyValue != null) {
			project.setProperty(propertyName, propertyValue);
		}
	}
}
