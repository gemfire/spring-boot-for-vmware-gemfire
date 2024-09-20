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
  implementation(platform(bom.testcontainers.dependencies.bom))
  implementation(project(":spring-gemfire-starter-actuator"))
  implementation(project(":spring-gemfire-starter-test"))

  implementation("org.springframework.boot:spring-boot-starter-web")

  implementation(libs.gemfire.core)
  implementation(libs.gemfire.cq)

  testImplementation(libs.gemfire.testcontainers)
}

tasks.getByName<Test>("test") {
  val springTestGemfireDockerImage: String by project
  systemProperty("spring.test.gemfire.docker.image", springTestGemfireDockerImage)
}

// Before running the application, start a GemFire server with a TemperatureReadings region.
tasks.register("runSample", JavaExec::class.java) {
  classpath = sourceSets["main"].runtimeClasspath
  this.mainClass = "example.app.temp.geode.client.BootGeodeClientApplication"
}
