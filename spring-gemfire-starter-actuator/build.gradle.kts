/*
 * Copyright 2024-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("project-base")
  id("gemfire-repo-artifact-publishing")
  id("gemfire-artifactory")
}

description = "Spring Boot Actuator Starter for VMware GemFire"

val baseGemFireVersion: String by project
val baseSpringVersion: String by project

publishingDetails {
  artifactName.set("spring-boot-actuator-$baseSpringVersion-gemfire-$baseGemFireVersion")
  longName.set(project.description)
  description.set(project.description)
}

dependencies {
    implementation(platform(libs.testcontainers.dependencies.bom))
    api(project(":spring-gemfire-starter"))
    api(project(":spring-gemfire-actuator-autoconfigure"))
}
