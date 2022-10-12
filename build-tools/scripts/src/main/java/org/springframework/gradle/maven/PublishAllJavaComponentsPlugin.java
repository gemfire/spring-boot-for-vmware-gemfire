/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.gradle.maven;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlatformPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;

/**
 * Adds Java and JavaPlatform based Gradle {@link Project Pojects} to be published by Maven.
 *
 * @author Rob Winch
 * @author John Blum
 * @see org.gradle.api.Plugin
 * @see org.gradle.api.Project
 * @see org.gradle.api.plugins.JavaPlatformPlugin
 * @see org.gradle.api.plugins.JavaPlugin
 * @see org.gradle.api.publish.PublishingExtension
 * @see org.gradle.api.publish.maven.MavenPublication
 * @see org.gradle.api.publish.maven.plugins.MavenPublishPlugin
 */
public class PublishAllJavaComponentsPlugin implements Plugin<Project> {

	private static final String JAVA_COMPONENT_NAME = "java";
	private static final String JAVA_PLATFORM_COMPONENT_NAME = "javaPlatform";

	@Override
	public void apply(Project project) {

		project.getPlugins().withType(MavenPublishPlugin.class).all(mavenPublish -> {

			PublishingExtension publishingExtension = project.getExtensions().getByType(PublishingExtension.class);

			publishingExtension.getPublications().create("mavenJava", MavenPublication.class, mavenPublication -> {

				project.getPlugins().withType(JavaPlugin.class, javaPlugin ->
					mavenPublication.from(project.getComponents().getByName(JAVA_COMPONENT_NAME)));

				project.getPlugins().withType(JavaPlatformPlugin.class, javaPlatformPlugin ->
					mavenPublication.from(project.getComponents().getByName(JAVA_PLATFORM_COMPONENT_NAME)));
			});
		});
	}
}
