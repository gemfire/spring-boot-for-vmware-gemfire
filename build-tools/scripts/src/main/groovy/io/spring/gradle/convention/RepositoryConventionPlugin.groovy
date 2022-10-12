/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.spring.gradle.convention;

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Declares Maven Repositories (for example: mavenLocal(), mavenCentral(), jcenter(), Spring Repositories, etc)
 * based on a {@link Project Project's} release artifact(s).
 *
 * @author Rob Winch
 * @author John Blum
 * @see org.gradle.api.Plugin
 * @see org.gradle.api.Project
 */
class RepositoryConventionPlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {

		String[] forceMavenRepositories =
			((String) project.findProperty("forceMavenRepositories"))?.split(',')

		boolean isImplicitSnapshotRepository = forceMavenRepositories == null && Utils.isSnapshot(project)
		boolean isImplicitMilestoneRepository = forceMavenRepositories == null && Utils.isMilestone(project)
		boolean isSnapshot = isImplicitSnapshotRepository || forceMavenRepositories?.contains('snapshot')
		boolean isMilestone = isImplicitMilestoneRepository || forceMavenRepositories?.contains('milestone')

		project.repositories {

			if (forceMavenRepositories?.contains('local')) {
				mavenLocal()
			}

			mavenCentral()

			jcenter() {
				content {
					includeGroup "org.gretty"
				}
			}

			if (isSnapshot) {
				maven {
					name = 'artifactory-snapshot'
					if (project.hasProperty('artifactoryUsername')) {
						credentials {
							username project.artifactoryUsername
							password project.artifactoryPassword
						}
					}
					url = 'https://repo.spring.io/snapshot/'
				}
			}

			if (isSnapshot || isMilestone) {
				maven {
					name = 'artifactory-milestone'
					if (project.hasProperty('artifactoryUsername')) {
						credentials {
							username project.artifactoryUsername
							password project.artifactoryPassword
						}
					}
					url = 'https://repo.spring.io/milestone/'
				}
			}

			maven {
				name = 'artifactory-release'
				if (project.hasProperty('artifactoryUsername')) {
					credentials {
						username project.artifactoryUsername
						password project.artifactoryPassword
					}
				}
				url = 'https://repo.spring.io/release/'
			}

			maven {
				name = 'shibboleth'
				url = 'https://build.shibboleth.net/nexus/content/repositories/releases/'
			}
		}
	}
}
