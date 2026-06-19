/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("project-base")
  id("gemfire-repo-artifact-publishing")
  id("gemfire-artifactory")
}

description = "Spring Boot for VMware GemFire Extensions"

val baseGemFireVersion: String by project
val baseSpringVersion: String by project

publishingDetails {
  artifactName.set("spring-boot-$baseSpringVersion-gemfire-extensions-$baseGemFireVersion")
  longName.set(project.description)
  description.set(project.description)
}

dependencies {
  implementation(platform(bom.testcontainers.dependencies.bom))
  api("org.springframework:spring-web")

  compileOnly(libs.gemfire.core)
  compileOnly(libs.gemfire.cq)

  implementation("com.fasterxml.jackson.core:jackson-databind")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310");

  testImplementation(libs.gemfire.core)
  testImplementation(libs.gemfire.cq)
  testImplementation(libs.gemfire.serialization)
  testImplementation("junit:junit")
  testImplementation("org.assertj:assertj-core")
  testImplementation(libs.mockito.core)
  testImplementation("org.projectlombok:lombok")
  testImplementation(libs.multithreadedtc)
}
