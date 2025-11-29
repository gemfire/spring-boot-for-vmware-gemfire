/*
 * Copyright 2024-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("project-base")
}

description =
  "Spring Geode Sample demonstrating Spring's Cache Abstraction using Apache Geode as the caching provider for Inline Caching."

dependencies {
  implementation(platform(bom.testcontainers.dependencies.bom))
  compileOnly(libs.gemfire.core)

  testCompileOnly(libs.lombok)
  testAnnotationProcessor(libs.lombok)

  implementation(project(":spring-gemfire-starter")) {
    exclude(group = "com.sun.xml.bind", module = "jaxb-impl")
  }

  implementation("org.projectlombok:lombok")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-web")

  runtimeOnly("org.hsqldb:hsqldb")

  testImplementation(libs.gemfire.core)
  testImplementation(project(":spring-gemfire-starter-test"))
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("junit:junit")

}
