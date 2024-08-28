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
  artifactName.set("spring-boot-3.1-gemfire-extensions-${ProjectUtils.getGemFireBaseVersion(property("gemfireVersion").toString())}")
  longName.set(project.description)
  description.set(project.description)
}

dependencies {
  implementation(platform(bom.spring.framework.bom))
  implementation(platform(bom.spring.boot.dependencies.bom))
  implementation(platform(bom.spring.security.bom))
  implementation(platform(bom.testcontainers.dependencies.bom))
  api("org.springframework:spring-web")

  compileOnly(libs.gemfire.core)
  compileOnly(libs.gemfire.cq)
  compileOnly(libs.gemfire.wan)

  implementation("com.fasterxml.jackson.core:jackson-databind")

  testImplementation(libs.gemfire.core)
  testImplementation(libs.gemfire.cq)
  testImplementation(libs.gemfire.wan)
  testImplementation(libs.gemfire.membership)
  testImplementation(libs.gemfire.serialization)
  testImplementation("junit:junit")
  testImplementation("org.assertj:assertj-core")
  testImplementation(libs.mockito.core)
  testImplementation("org.projectlombok:lombok")
  testImplementation(libs.multithreadedtc)
}
