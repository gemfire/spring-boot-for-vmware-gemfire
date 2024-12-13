/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import ProjectUtils.getBaseVersion

plugins {
  id("project-base")
  id("gemfire-repo-artifact-publishing")
  id("gemfire-artifactory")
}

description = "Spring Boot Actuator Starter for VMware GemFire"

publishingDetails {
  artifactName.set("spring-boot-actuator-${getBaseVersion(property("spring-boot.version").toString())}-gemfire-${getBaseVersion(property("gemfireVersion").toString())}")
  longName.set(project.description)
  description.set(project.description)
}

dependencies {
    implementation(platform(bom.testcontainers.dependencies.bom))
    api(project(":spring-gemfire-starter"))
    api(project(":spring-gemfire-actuator-autoconfigure"))
}
