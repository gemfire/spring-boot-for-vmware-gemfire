/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.gradle.maven;

import io.spring.gradle.convention.ArtifactoryPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;

/**
 * Enables publishing to Maven for a Spring module Gradle {@link Project}.
 *
 * @author Rob Winch
 * @author John Blum
 * @see org.gradle.api.Plugin
 * @see org.gradle.api.Project
 * @see org.gradle.api.publish.maven.plugins.MavenPublishPlugin
 */
public class SpringMavenPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {

		PluginManager pluginManager = project.getPluginManager();

		pluginManager.apply(MavenPublishPlugin.class);
		pluginManager.apply(MavenPublishConventionsPlugin.class);
		pluginManager.apply(PublishAllJavaComponentsPlugin.class);
		pluginManager.apply(PublishArtifactsPlugin.class);
		pluginManager.apply(PublishLocalPlugin.class);
		pluginManager.apply(SpringSigningPlugin.class);
		pluginManager.apply(ArtifactoryPlugin.class);
	}
}
