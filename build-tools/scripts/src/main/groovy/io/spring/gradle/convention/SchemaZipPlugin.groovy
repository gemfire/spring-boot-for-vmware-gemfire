/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.spring.gradle.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Zip

/**
 * Zips all Spring XML schemas (XSD) files.
 *
 * @author Rob Winch
 * @author John Blum
 * @see org.gradle.api.Plugin
 * @see org.gradle.api.Project
 */
class SchemaZipPlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {

		Zip schemaZip = project.tasks.create('schemaZip', Zip)

		schemaZip.archiveBaseName = project.rootProject.name
		schemaZip.archiveClassifier = 'schema'
		schemaZip.description = "Builds -${schemaZip.archiveClassifier} archive containing all XSDs" +
			" for deployment to static.springframework.org/schema."
		schemaZip.group = 'Distribution'

		project.rootProject.subprojects.each { module ->

			module.getPlugins().withType(JavaPlugin.class).all {

				Properties schemas = new Properties();

				module.sourceSets.main.resources
					.find { it.path.endsWith('META-INF/spring.schemas') }
					?.withInputStream { schemas.load(it) }

				for (def key : schemas.keySet()) {

					def zipEntryName = key.replaceAll(/http.*schema.(.*).spring-.*/, '$1')

					assert zipEntryName != key

					File xsdFile = module.sourceSets.main.resources.find {
						it.path.endsWith(schemas.get(key))
					}

					assert xsdFile != null

					schemaZip.into(zipEntryName) {
						duplicatesStrategy DuplicatesStrategy.EXCLUDE
						from xsdFile.path
					}
				}
			}
		}
	}
}
