/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("project-base")
  alias(libs.plugins.lombok)
}

description = "Quick Start for Spring Boot for Apache Geode"

dependencies {
  implementation(platform(libs.testcontainers.dependencies.bom))
  implementation(project(":spring-gemfire-starter"))

  implementation("org.assertj:assertj-core")
  implementation("org.projectlombok:lombok")

  //runtime project(":spring-gemfire-starter-logging")

  testImplementation(libs.gemfire.core)
  testImplementation(project(":spring-gemfire-starter-test"))

  testImplementation("org.springframework.boot:spring-boot-starter-test")

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}
