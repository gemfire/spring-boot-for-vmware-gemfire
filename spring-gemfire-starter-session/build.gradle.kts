/*
 * Copyright 2024-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("project-base")
  id("gemfire-repo-artifact-publishing")
  id("gemfire-artifactory")
}

description = "Spring Boot Starter for Spring Session using VMware GemFire"

val baseGemFireVersion: String by project
val baseSpringVersion: String by project

publishingDetails {
  artifactName.set("spring-boot-session-$baseSpringVersion-gemfire-$baseGemFireVersion")
  longName.set(project.description)
  description.set(project.description)
}

dependencies {
  implementation(platform(libs.testcontainers.dependencies.bom))
  api(project(":spring-gemfire-starter"))

  api(libs.spring.session.gemfire)

}
