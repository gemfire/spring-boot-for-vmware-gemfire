/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("project-base")
  alias(libs.plugins.lombok)
}

description = "Spring Geode Sample for Getting Started with Spring Boot for Apache Geode quickly, easily and reliably."

dependencies {
  implementation(platform(bom.testcontainers.dependencies.bom))
  implementation(project(":spring-gemfire-starter"))

  implementation("org.assertj:assertj-core")
  implementation("org.projectlombok:lombok")
  implementation("org.springframework.boot:spring-boot-starter-web")

  testImplementation(libs.gemfire.core)
  testImplementation(project(":spring-gemfire-starter-test"))
  testImplementation("org.springframework.boot:spring-boot-starter-test")

}
