/*
 * Copyright 2024-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("project-base")
}

description = "Spring Geode Sample for Getting Started with Spring Boot for Apache Geode quickly, easily and reliably."

dependencies {
  implementation(platform(libs.testcontainers.dependencies.bom))
  implementation(project(":spring-gemfire-starter"))
  implementation(libs.gemfire.core)
  testCompileOnly(libs.lombok)
  testAnnotationProcessor(libs.lombok)

  implementation("org.assertj:assertj-core")
  implementation("org.projectlombok:lombok")
  implementation("org.springframework.boot:spring-boot-starter-web")

  testImplementation("junit:junit")
  testImplementation(project(":spring-gemfire-starter-test"))
  testImplementation("org.springframework.boot:spring-boot-starter-test")

}
