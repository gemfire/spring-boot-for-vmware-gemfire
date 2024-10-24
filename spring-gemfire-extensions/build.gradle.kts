/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("project-base")
  id("gemfire-repo-artifact-publishing")
  id("gemfire-artifactory")
}

description = "Spring Boot for VMware GemFire Extensions"

publishingDetails {
  artifactName.set("spring-boot-3.3-gemfire-extensions-${ProjectUtils.getGemFireBaseVersion(property("gemfireVersion").toString())}")
  longName.set(project.description)
  description.set(project.description)
}

dependencies {
  implementation(platform(bom.testcontainers.dependencies.bom))
  api("org.springframework:spring-web")

  compileOnly(libs.gemfire.core)
  compileOnly(libs.gemfire.cq)

  implementation("com.fasterxml.jackson.core:jackson-databind")

  testImplementation(libs.gemfire.core)
  testImplementation(libs.gemfire.cq)
  testImplementation(libs.gemfire.serialization)
  testImplementation("junit:junit")
  testImplementation("org.assertj:assertj-core")
  testImplementation(libs.mockito.core)
  testImplementation("org.projectlombok:lombok")
  testImplementation(libs.multithreadedtc)
}
