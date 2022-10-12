/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.spring.gradle.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.testing.Test
import org.gradle.plugins.ide.eclipse.EclipsePlugin
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.springframework.gradle.propdeps.PropDepsPlugin

/**
 * Adds Integration Test support to Java projects.
 *
 * <ul>
 * <li>Adds integrationTestCompile and integrationTestRuntimeOnly configurations</li>
 * <li>Adds new source test folder of src/integration-test/java</li>
 * <li>Adds a task to run integration tests named integrationTest</li>
 * <li>Adds a new source test folder src/integration-test/groovy if the Groovy Plugin was added</li>
 * </ul>
 *
 * @author Rob Winch
 * @author John Blum
 * @see org.gradle.api.Plugin
 * @see org.gradle.api.Project
 * @see org.gradle.api.Task
 * @see org.gradle.api.tasks.testing.Test
 */
class IntegrationTestPlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {
		project.plugins.withType(JavaPlugin.class) {
			applyJava(project)
		}
	}

	private void applyJava(Project project) {

		// Do not add any configuration if there are no (integration) tests to avoid adding Gretty.
		if (isIntegrationTestSourceAvailable(project)) {

			project.configurations {
				integrationTestCompile {
					extendsFrom testCompileClasspath
				}
				integrationTestRuntime {
					extendsFrom integrationTestCompile, testRuntimeOnly
				}
			}

			project.sourceSets {
				integrationTest {
					java.srcDir project.file('src/integration-test/java')
					resources.srcDir project.file('src/integration-test/resources')
					compileClasspath = project.sourceSets.main.output + project.sourceSets.test.output + project.configurations.integrationTestCompile
					runtimeClasspath = output + compileClasspath + project.configurations.integrationTestRuntime
				}
			}

			Task integrationTestTask = project.tasks.create("integrationTest", Test) {
				group = 'Verification'
				description = 'Runs Integration Tests'
				dependsOn 'jar'
				testClassesDirs = project.sourceSets.integrationTest.output.classesDirs
				classpath = project.sourceSets.integrationTest.runtimeClasspath
				shouldRunAfter project.tasks.test
				useJUnitPlatform()
			}

			project.tasks.check.dependsOn integrationTestTask

			project.plugins.withType(EclipsePlugin) {
				project.eclipse.classpath {
					plusConfigurations += [ project.configurations.integrationTestCompile ]
				}
			}

			project.plugins.withType(IdeaPlugin) {
				project.idea {
					module {
						testSourceDirs += project.file('src/integration-test/java')
						scopes.TEST.plus += [ project.configurations.integrationTestCompile ]
					}
				}
			}

			project.plugins.withType(GroovyPlugin) {
				project.sourceSets {
					integrationTest {
						groovy.srcDirs project.file('src/integration-test/groovy')
					}
				}
				project.plugins.withType(IdeaPlugin) {
					project.idea {
						module {
							testSourceDirs += project.file('src/integration-test/groovy')
						}
					}
				}
			}

			project.plugins.withType(PropDepsPlugin) {
				project.configurations {
					integrationTestCompile {
						extendsFrom optional, provided
					}
				}
			}
		}
	}

	@SuppressWarnings("all")
	private boolean isIntegrationTestSourceAvailable(Project project) {
		return project.file('src/integration-test/').exists()
	}
}
