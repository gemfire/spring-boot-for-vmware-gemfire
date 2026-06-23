/*
 * Copyright 2024-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("project-base")
  id("gemfire-repo-artifact-publishing")
  id("gemfire-artifactory")
}

description = "Spring Boot Actuator for VMware GemFire"

val baseGemFireVersion: String by project
val baseSpringVersion: String by project

publishingDetails {
  artifactName.set("spring-boot-$baseSpringVersion-gemfire-actuator-$baseGemFireVersion")
  longName.set(project.description)
  description.set(project.description)
}

dependencies {
  implementation(platform(bom.testcontainers.dependencies.bom))
  api(project(":spring-gemfire"))

  api("org.springframework.boot:spring-boot-starter-actuator")

  compileOnly(libs.gemfire.logging)

  compileOnly(libs.gemfire.core)
  compileOnly(libs.gemfire.cq)
  compileOnly(libs.gemfire.gfsh)
  compileOnly(libs.gemfire.tcp.server)
  compileOnly(libs.gemfire.deployment.chained.classloader)

  testImplementation(libs.gemfire.core)
  testImplementation(libs.gemfire.logging)
  testImplementation(libs.gemfire.cq)
  testImplementation(libs.gemfire.gfsh)
  testImplementation(libs.gemfire.tcp.server)
  testImplementation(libs.gemfire.deployment.chained.classloader)
  testImplementation("junit:junit")
  testImplementation("org.assertj:assertj-core")
  testImplementation(libs.mockito.core)
  testImplementation("org.projectlombok:lombok")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation(libs.spring.data.gemfire.test.framework)
  testImplementation(libs.multithreadedtc)
}
