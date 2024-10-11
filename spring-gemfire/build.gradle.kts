/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("project-base")
  alias(libs.plugins.lombok)
  id("gemfire-repo-artifact-publishing")
  id("gemfire-artifactory")
}

description = "Spring GemFire base build for VMware GemFire"

publishingDetails {
  artifactName.set("spring-boot-3.0-gemfire-core-${ProjectUtils.getGemFireBaseVersion(property("gemfireVersion").toString())}")
  longName.set(project.description)
  description.set(project.description)
}

dependencies {
  api(platform(bom.testcontainers.dependencies.bom))
  api(project(":spring-gemfire-extensions"))

  api("org.springframework:spring-context-support")
  api("org.springframework:spring-jcl")
  api("org.springframework.boot:spring-boot-starter")
  api(libs.spring.data.gemfire)

  compileOnly(libs.gemfire.core)
  compileOnly(libs.gemfire.lucene)

  compileOnly(libs.findbugs.jsr305)

  implementation("org.springframework:spring-test")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "org.skyscreamer", module = "jsonassert")
  }

  testImplementation("jakarta.persistence:jakarta.persistence-api")
  testImplementation("org.springframework.boot:spring-boot-starter-data-jpa")
  testImplementation("org.springframework.boot:spring-boot-starter-data-cassandra")
  testImplementation(variantOf(libs.spring.data.gemfire) { classifier("test-framework") })
  testImplementation(libs.gemfire.core)
  testImplementation(libs.gemfire.lucene)
  testImplementation("org.testcontainers:testcontainers")
  testImplementation("org.testcontainers:cassandra")
  testImplementation(libs.mockito.core)
  testImplementation(libs.multithreadedtc)
  testRuntimeOnly("org.hsqldb:hsqldb")
}
