/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("project-base")
  alias(libs.plugins.lombok)
}

description = "Spring Geode Sample for Multi-Site Caching."

dependencies {
  implementation(platform(bom.testcontainers.dependencies.bom))
  compileOnly(libs.gemfire.core)

  implementation(project(":spring-gemfire-starter"))

  implementation("org.assertj:assertj-core")
  implementation("org.projectlombok:lombok")
  implementation("org.springframework.boot:spring-boot-starter-web")

  testImplementation(libs.gemfire.core)
  testImplementation(libs.gemfire.wan)
  testImplementation(project(":spring-gemfire-starter-test"))
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("junit:junit")
  testImplementation(variantOf(libs.spring.data.gemfire) { classifier("test-framework") })
}
