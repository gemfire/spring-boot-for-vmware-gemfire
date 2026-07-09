/*
 * Copyright 2024-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("project-base")
}

description = "Spring Geode Sample demonstrating the use of Spring Boot Auto-Configuration for Apache Geode."

dependencies {
  implementation(platform(libs.testcontainers.dependencies.bom))
  compileOnly(libs.gemfire.core)

  implementation(project(":spring-gemfire-starter"))
  implementation(project(":spring-gemfire-starter-test"))

  implementation("org.assertj:assertj-core")
  implementation("org.projectlombok:lombok")

  testCompileOnly(libs.lombok)
  testAnnotationProcessor(libs.lombok)

  testCompileOnly(libs.findbugs.jsr305)

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation(libs.gemfire.core)
  testImplementation("junit:junit")
}
