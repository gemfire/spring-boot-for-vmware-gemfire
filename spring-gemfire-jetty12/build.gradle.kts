/*
 * Copyright 2024-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("project-base")
  id("gemfire-repo-artifact-publishing")
  id("gemfire-artifactory")
}

description = "VMware GemFire Integration with Eclipse Jetty declared and managed by Spring Boot"

val baseGemFireVersion: String by project
val baseSpringVersion: String by project

publishingDetails {
  artifactName.set("spring-boot-$baseSpringVersion-gemfire-jetty12-$baseGemFireVersion")
  longName.set(project.description)
  description.set(project.description)
}

dependencies {
  compileOnly(libs.gemfire.core)

  api(project(":spring-gemfire-extensions"))

  api("org.springframework.boot:spring-boot-starter-jetty") {
    exclude(group = "org.eclipse.jetty.websocket", module = "websocket-jakarta-server")
    exclude(group = "org.eclipse.jetty.websocket", module = "websocket-jetty-server")
  }

  implementation("org.apache.commons:commons-lang3")
  implementation("org.eclipse.jetty:jetty-server")
  implementation(libs.jetty.ee8.webapp)
  implementation("org.slf4j:slf4j-api")

  runtimeOnly(libs.jetty.ee8.apache.jsp)
  runtimeOnly(libs.jetty.jndi)

  testImplementation("junit:junit")
  testImplementation("org.assertj:assertj-core")
  testImplementation(libs.mockito.core)
  testImplementation(libs.gemfire.core)
  testImplementation(libs.jetty.ee10.webapp)
}
