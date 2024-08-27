/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("project-base")
  id("gemfire-repo-artifact-publishing")
  id("gemfire-artifactory")
}

description = "Spring Boot Starter for Spring Session using VMware GemFire"

publishingDetails {
  artifactName.set("spring-boot-session-2.7-gemfire-${ProjectUtils.getGemFireBaseVersion(property("gemfireVersion").toString())}")
  longName.set(project.description)
  description.set(project.description)
}

dependencies {
  api(platform(bom.spring.framework.bom))
  api(platform(bom.spring.boot.dependencies.bom))
  api(platform(bom.spring.security.bom))
  api(platform(bom.testcontainers.dependencies.bom))
  api(project(":spring-gemfire-starter"))

  api(libs.spring.session.gemfire)

}
