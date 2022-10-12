/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.spring.gradle.convention

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.component.ModuleComponentSelector
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Gradle API Task to output all the configured project &amp; subproject (runtime) dependencies.
 *
 * @author Rob Winch
 * @author John Blum
 */
class DependencyManagementExportTask extends DefaultTask {

	@Internal
	def projects;

	@Input
	String getProjectNames() {
		return this.projects*.name
	}

	@TaskAction
	void dependencyManagementExport() throws IOException {

		def projects = this.projects ?: project.subprojects + project

		def configurations = projects*.configurations*.findAll {
			[ 'testRuntimeOnly', 'integrationTestRuntime', 'grettyRunnerTomcat10', 'ajtools' ].contains(it.name)
		}

		def dependencyResults = configurations*.incoming*.resolutionResult*.allDependencies.flatten()

		def moduleVersionVersions = dependencyResults
			.findAll { r -> r.requested instanceof ModuleComponentSelector }
			.collect { r -> r.selected.moduleVersion }

		def projectDependencies = projects.collect { p ->
			"${p.group}:${p.name}:${p.version}".toString()
		} as Set

		def dependencies = moduleVersionVersions
			.collect { d -> "${d.group}:${d.name}:${d.version}".toString() }
			.sort() as Set

		println ''
		println ''
		println 'dependencyManagement {'
		println '\tdependencies {'

		dependencies
			.findAll { d -> !projectDependencies.contains(d) }
			.each { println "\t\tdependency '$it'" }

		println '\t}'
		println '}'
		println ''
		println ''
		println 'TIP Use this to find duplicates:\n$ sort gradle/dependency-management.gradle| uniq -c | grep -v \'^\\s*1\''
		println ''
		println ''
	}
}
