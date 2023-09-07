/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.spring.gradle.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

/**
 * Defines sets of dependencies to make it easy to add a related group of dependencies to a Gradle {@link Project}.
 *
 * The dependencies set defined include:
 *
 * <ul>
 * <li>jstlDependencies</li>
 * <li>seleniumDependencies</li>
 * <li>slf4jDependencies</li>
 * <li>testDependencies</li>
 * </ul>
 *
 * {@literal testDependencies} are automatically added to Java projects
 * ({@lin Project Projects} with the {@link JavaPlugin} applied).
 *
 * @author Rob Winch
 * @author John Blum
 * @see org.gradle.api.Plugin* @see org.gradle.api.Project
 */
class DependencySetPlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {

    project.ext.jstlDependencies = [
            "jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api",
            "org.glassfish.web:jakarta.servlet.jsp.jstl"
    ]

    project.ext.seleniumDependencies = [
            "org.seleniumhq.selenium:htmlunit-driver",
            "org.seleniumhq.selenium:selenium-support"
    ]

    project.ext.slf4jDependencies = [
            "org.slf4j:slf4j-api",
            "org.slf4j:jcl-over-slf4j",
            "org.slf4j:jul-over-slf4j",
            "org.slf4j:log4j-over-slf4j",
            "ch.qos.logback:logback-classic"
    ]

    project.ext.testDependencies = [
            "junit:junit",
            "org.junit.jupiter:junit-jupiter-api",
            "org.junit.vintage:junit-vintage-engine",
            "org.assertj:assertj-core",
            "org.mockito:mockito-core",
            "org.projectlombok:lombok",
            "org.springframework:spring-test",
            "com.vmware.gemfire:spring-test-gemfire-10.1-3.0",
            "edu.umd.cs.mtc:multithreadedtc"
    ]

    project.plugins.withType(JavaPlugin) {
      project.dependencies {
        testImplementation project.testDependencies
      }
    }
  }
}
