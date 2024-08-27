/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    id("project-base")
    id("gemfire-repo-artifact-publishing")
    id("gemfire-artifactory")
}

description = "Spring Boot Actuator Starter for VMware GemFire"

publishingDetails {
    artifactName.set("spring-boot-actuator-2.7-gemfire-${ProjectUtils.getGemFireBaseVersion(property("gemfireVersion").toString())}")
    longName.set(project.description)
    description.set(project.description)
}

dependencies {
    api(project(":spring-gemfire-starter"))
    api(project(":spring-gemfire-actuator-autoconfigure"))
}
