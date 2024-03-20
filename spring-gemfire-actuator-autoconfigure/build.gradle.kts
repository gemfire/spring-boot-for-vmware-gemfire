/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("project-base")
  id("gemfire-repo-artifact-publishing")
}

description = "Spring Boot Actuator Auto-Configuration for VMware GemFire"


publishingDetails {
  artifactName.set("spring-boot-3.0-gemfire-actuator-autoconfigure-${ProjectUtils.getGemFireBaseVersion(property("gemfireVersion").toString())}")
  longName.set(project.description)
  description.set(project.description)
}

dependencies {
  implementation(platform(bom.spring.framework.bom))
  implementation(platform(bom.spring.boot.dependencies.bom))
  implementation(platform(bom.spring.security.bom))
  implementation(platform(bom.testcontainers.dependencies.bom))
  api(project(":spring-gemfire-actuator"))
  api(project(":spring-gemfire-autoconfigure"))

    compileOnly(libs.gemfire.core)
    compileOnly(libs.findbugs.jsr305)

    testImplementation(libs.gemfire.core)
    testImplementation("junit:junit")
    testImplementation("org.assertj:assertj-core")
    testImplementation(libs.mockito.core)
    testImplementation("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(libs.spring.test.gemfire)
    testImplementation(libs.multithreadedtc)
}

