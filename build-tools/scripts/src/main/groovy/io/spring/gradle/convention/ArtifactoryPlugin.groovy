/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.spring.gradle.convention

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Applies and configures the JFrag Artifactory Gradle {@link Plugin} to publish Gradle {@link Project} artifacts
 * to the Spring {@literal snapshot}, {@literal milestone} and {@literal release} repositories in Artifactory.
 *
 * @author Rob Winch
 * @author John Blum
 * @see org.gradle.api.Plugin
 * @see org.gradle.api.Project
 */
class ArtifactoryPlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {

		project.plugins.apply('com.jfrog.artifactory')

		// (Externally-defined) Methods cannot be invoked inside the Groovy/Gradle DSL.
		def artifactoryRepoKey = resolveRepositoryKey(project)
		def authRequired = isAuthRequired(project)

		project.artifactory {
			contextUrl = 'https://repo.spring.io'
			publish {
				repository {
					repoKey = artifactoryRepoKey
					if (authRequired) {
						username = artifactoryUsername
						password = artifactoryPassword
					}
				}
				defaults {
					publications('mavenJava')
				}
			}
		}
	}

	@SuppressWarnings("all")
	private boolean isAuthRequired(Project project) {
		project?.hasProperty('artifactoryUsername')
	}

	@SuppressWarnings("all")
	private String resolveRepositoryKey(Project project) {

		boolean isSnapshot = Utils.isSnapshot(project);
		boolean isMilestone = Utils.isMilestone(project);

		return isSnapshot ? 'libs-snapshot-local'
			: isMilestone ? 'libs-milestone-local'
			: 'libs-release-local'
	}
}
