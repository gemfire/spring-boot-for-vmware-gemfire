/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.gradle.maven;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import io.spring.gradle.convention.Utils;

/**
 * Publishes Gradle {@link Project} artifacts to either Artifactory or Maven Central.
 *
 * @author Rob Winch
 * @author John Blum
 * @see org.gradle.api.Plugin
 * @see org.gradle.api.Project
 * @see <a href="https://www.jfrog.com/confluence/display/JFROG/Gradle+Artifactory+Plugin">Artifatory Gradle Plugin</a>
 * @see <a href="https://central.sonatype.org/publish/publish-gradle/">Maven Central Sonatype Gradle Support</a>
 */
public class PublishArtifactsPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {

		project.getTasks().register("publishArtifacts", publishArtifactsTask -> {

			publishArtifactsTask.setGroup("Publishing");
			publishArtifactsTask.setDescription("Publish project artifacts to either Artifactory or Maven Central"
				+ " based on the project version.");

			if (Utils.isRelease(project)) {
				publishArtifactsTask.dependsOn("publishToOssrh");
			}
			else {
				publishArtifactsTask.dependsOn("artifactoryPublish");
			}
		});
	}
}
