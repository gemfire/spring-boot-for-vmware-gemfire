/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("project-base")
  id("gemfire-repo-artifact-publishing")
  id("gemfire-artifactory")
}
description = "Spring Boot Logging Starter for VMware GemFire"

publishingDetails {
  artifactName.set("spring-boot-logging-2.7-gemfire-${ProjectUtils.getGemFireBaseVersion(property("gemfireVersion").toString())}")
  longName.set(project.description)
  description.set("Spring Boot Logging Starter for VMware GemFire with Logback as the logging provider and adaptation of Log4j to SLF4J")
}

dependencies {
  implementation(platform(bom.spring.framework.bom))
  implementation(platform(bom.spring.boot.dependencies.bom))
  implementation(platform(bom.spring.security.bom))
  implementation(platform(bom.testcontainers.dependencies.bom))
  api("ch.qos.logback:logback-classic")
  api("org.apache.logging.log4j:log4j-to-slf4j")

  implementation("org.codehaus.janino:janino")
  implementation("org.springframework:spring-core")

  testImplementation(project(":spring-gemfire-starter-test"))
}

