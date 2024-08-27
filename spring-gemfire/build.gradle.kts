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
  artifactName.set("spring-boot-2.6-gemfire-core-${ProjectUtils.getGemFireBaseVersion(property("gemfireVersion").toString())}")
  longName.set(project.description)
  description.set(project.description)
}

dependencies {
  implementation(platform(bom.spring.framework.bom))
  implementation(platform(bom.spring.boot.dependencies.bom))
  implementation(platform(bom.spring.security.bom))
  implementation(platform(bom.testcontainers.dependencies.bom))
  api(project(":spring-gemfire-extensions"))

  api("org.springframework:spring-context-support")
  api("org.springframework:spring-jcl")
  api("org.springframework.boot:spring-boot-starter")
  api(libs.spring.data.gemfire)

  compileOnly(libs.gemfire.core)
  compileOnly(libs.gemfire.lucene)

  compileOnly(libs.findbugs.jsr305)

  runtimeOnly("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
  implementation(libs.faster.xml.jackson.annotations)
  implementation(libs.faster.xml.jackson.databind)

  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "org.skyscreamer", module = "jsonassert")
  }

  testImplementation("org.springframework:spring-test")
  testImplementation(libs.logback.classic)
  testImplementation("org.springframework.boot:spring-boot-starter-data-jpa")
  testImplementation("org.springframework.boot:spring-boot-starter-data-cassandra")
  testImplementation(libs.spring.test.gemfire)
  testImplementation(libs.gemfire.core)
  testImplementation(libs.gemfire.lucene)
  testImplementation("org.testcontainers:testcontainers")
  testImplementation("org.testcontainers:cassandra")
  testImplementation(libs.mockito.core)
  testRuntimeOnly("org.hsqldb:hsqldb")
}
