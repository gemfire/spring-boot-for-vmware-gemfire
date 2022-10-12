/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.spring.gradle.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.PluginManager
import org.gradle.api.tasks.bundling.Zip

/**
 * Aggregates Asciidoc, Javadoc, and deploying of the docs into a single Gradle Plugin.
 *
 * @author Rob Winch
 * @author John Blum
 */
class DocsPlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {

		PluginManager pluginManager = project.getPluginManager()

		pluginManager.apply("org.asciidoctor.jvm.convert")
		pluginManager.apply("org.asciidoctor.jvm.pdf")
		pluginManager.apply(AsciidoctorConventionPlugin)
		pluginManager.apply(DeployDocsPlugin)
		pluginManager.apply(JavadocApiPlugin)

		def projectName = Utils.getProjectName(project);
		def pdfFilename = projectName + '-reference.pdf';

		Task docsZip = project.tasks.create('docsZip', Zip) {

			archiveBaseName = project.rootProject.name
			archiveClassifier = 'docs'
			group = 'Distribution'
			description = "Builds -${archiveClassifier} archive containing all documenation for deployment to docs-ip.spring.io."
			dependsOn 'api', 'asciidoctor'

			from(project.tasks.api.outputs) {
				into 'api'
			}
			from(project.tasks.asciidoctor.outputs) {
				into 'reference/html5'
				include '**'
			}
			from(project.tasks.asciidoctorPdf.outputs) {
				into 'reference/pdf'
				include '**'
				rename "index.pdf", pdfFilename
			}

			into 'docs'
			duplicatesStrategy DuplicatesStrategy.EXCLUDE
		}

		Task docs = project.tasks.create("docs") {
			group = 'Documentation'
			description 'Aggregator Task to generate all documentation.'
			dependsOn docsZip
		}

		project.tasks.assemble.dependsOn docs

	}
}
