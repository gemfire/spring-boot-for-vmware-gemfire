/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  alias(libs.plugins.lombok)
  id("project-base")
  id("gemfire-repo-artifact-publishing")
  id("gemfire-artifactory")
}

description = "Spring Boot Auto-Configuration for VMware GemFire"

publishingDetails {
  artifactName.set("spring-boot-3.0-gemfire-autoconfigure-${ProjectUtils.getGemFireBaseVersion(property("gemfireVersion").toString())}")
  longName.set(project.description)
  description.set(project.description)
}

dependencies {
  api(platform(bom.spring.framework.bom))
  api(platform(bom.spring.boot.dependencies.bom))
  api(platform(bom.spring.security.bom))
  api(platform(bom.testcontainers.dependencies.bom))
  api(project(":spring-gemfire"))
  implementation(project(":spring-gemfire-extensions"))
  compileOnly(libs.gemfire.core)
  compileOnly(libs.findbugs.jsr305)
  implementation("jakarta.annotation:jakarta.annotation-api")

  implementation("org.springframework.boot:spring-boot-configuration-processor")
  implementation("org.springframework.boot:spring-boot-autoconfigure-processor")
  compileOnly(libs.spring.session.gemfire)
  implementation(libs.aspectj.tools)

  testImplementation(libs.gemfire.core)
  testImplementation(libs.gemfire.cq)
  testImplementation(libs.gemfire.wan)
  testImplementation(libs.gemfire.lucene)
  testImplementation(libs.spring.session.gemfire)
  testImplementation("jakarta.servlet:jakarta.servlet-api")
  testImplementation("org.apache.httpcomponents.client5:httpclient5")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.boot:spring-boot-starter-web")
  testCompileOnly(libs.findbugs.jsr305)

  testRuntimeOnly(project(":spring-gemfire-jetty11"))

  testRuntimeOnly("javax.cache:cache-api")
  testRuntimeOnly(libs.gemfire.web)
  testRuntimeOnly("org.springframework.boot:spring-boot-starter-jetty")
  testRuntimeOnly("org.springframework.boot:spring-boot-starter-json")
  testRuntimeOnly(libs.spring.shell)
  testImplementation(libs.spring.test.gemfire)
  testImplementation(libs.awaitility)
}
