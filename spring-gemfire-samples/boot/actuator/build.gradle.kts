/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("project-base")
  alias(libs.plugins.lombok)
}

description = "Spring Geode Sample demonstrating the use of Spring Boot Actuator with Apache Geode."

dependencies {
  implementation(platform(bom.spring.framework.bom))
  implementation(platform(bom.spring.boot.dependencies.bom))
  implementation(platform(bom.spring.security.bom))
  implementation(platform(bom.testcontainers.dependencies.bom))
  implementation(project(":spring-gemfire-starter-actuator"))
  implementation(project(":spring-gemfire-starter-test"))

  implementation("org.springframework.boot:spring-boot-starter-web")

  compileOnly(libs.gemfire.core)

}

tasks.register("runServer", JavaExec::class.java) {
  classpath = sourceSets["main"].runtimeClasspath
  jvmArgs = listOf("-Dspring.profiles.active=server")
  this.mainClass = "example.app.temp.geode.server.BootGeodeServerApplication"
}
