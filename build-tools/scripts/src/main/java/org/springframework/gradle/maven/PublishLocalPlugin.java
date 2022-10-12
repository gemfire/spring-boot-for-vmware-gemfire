/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.gradle.maven;

import java.io.File;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;

/**
 * Gradle Plugin used to publish all {@link Project} artifacts locally
 * under {@literal rootProject/buildDir/publications/repos}.
 *
 * This is useful for inspecting the generated {@link Project} artifacts to ensure they are correct
 * before publishing the {@link Project} artifacts to Artifactory or Maven Central.
 *
 * @author Rob Winch
 * @author John Blum
 * @see org.gradle.api.Plugin
 * @see org.gradle.api.Project
 * @since 2.0.0
 */
public class PublishLocalPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {

		project.getPlugins().withType(MavenPublishPlugin.class).all(mavenPublish -> {

			PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);

			publishing.getRepositories().maven(maven -> {
				maven.setName("local");
				maven.setUrl(new File(project.getRootProject().getBuildDir(), "publications/repos"));
			});
		});
	}
}
