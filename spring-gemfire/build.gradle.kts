/*
 * Copyright 2024-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import ProjectUtils.getBaseVersion

plugins {
  id("project-base")
  id("gemfire-repo-artifact-publishing")
  id("gemfire-artifactory")
}

description = "Spring GemFire base build for VMware GemFire"

publishingDetails {
  artifactName.set(
    "spring-boot-${getBaseVersion(property("spring-boot.version").toString())}-gemfire-core-${
      getBaseVersion(
        property("gemfireVersion").toString()
      )
    }"
  )
  longName.set(project.description)
  description.set(project.description)
}

project.ext.set("testcontainers.version", "1.21.3")

dependencies {
  implementation(platform(bom.testcontainers.dependencies.bom))

  api(project(":spring-gemfire-extensions"))

  api("org.springframework:spring-context-support")
  api("org.springframework.boot:spring-boot-starter")
  api(libs.spring.data.gemfire)

  testCompileOnly(libs.lombok)
  testAnnotationProcessor(libs.lombok)

  compileOnly(libs.gemfire.core)
  compileOnly(libs.findbugs.jsr305)

  implementation("org.springframework:spring-test")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "org.skyscreamer", module = "jsonassert")
  }

  testImplementation("jakarta.persistence:jakarta.persistence-api")
  testImplementation("org.springframework.boot:spring-boot-starter-data-jpa")
  testImplementation("org.springframework.boot:spring-boot-starter-data-cassandra")
  testImplementation(libs.spring.data.gemfire.test.framework)
  testImplementation(libs.gemfire.core)
  testImplementation("org.testcontainers:testcontainers") {
    version {
      strictly(bom.versions.testcontainersVersion.get())
    }
  }
  testImplementation("org.testcontainers:cassandra")
  testImplementation(libs.mockito.core)
  testImplementation(libs.mockito.subclass)
  testImplementation(libs.multithreadedtc)
  testImplementation(libs.gemfire.testcontainers)
  testRuntimeOnly("org.hsqldb:hsqldb")

}

tasks.getByName<Test>("test") {
  forkEvery = 1
  maxParallelForks = 4
  val springTestGemfireDockerImage: String by project
  systemProperty("spring.test.gemfire.docker.image", springTestGemfireDockerImage)
}
