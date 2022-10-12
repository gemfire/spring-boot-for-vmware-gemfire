/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.spring.gradle.convention

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gradle {@link Plugin} to ZIP and deploy Spring XML schemas (XSDK) files.
 *
 * @author Rob Winch
 * @author John Blum
 * @see org.gradle.api.Plugin
 * @see org.gradle.api.Project
 */
class SchemaPlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {
		project.getPluginManager().apply(SchemaZipPlugin)
		project.getPluginManager().apply(SchemaDeployPlugin)
	}
}
