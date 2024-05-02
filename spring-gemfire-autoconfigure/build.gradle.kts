/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  alias(libs.plugins.lombok)
  id("project-base")
  id("gemfire-repo-artifact-publishing")
  id("com.github.johnrengelman.shadow") version "8.1.1"
}

description = "Spring Boot Auto-Configuration for VMware GemFire"

publishingDetails {
  artifactName.set("spring-boot-3.3-gemfire-autoconfigure-${ProjectUtils.getGemFireBaseVersion(property("gemfireVersion").toString())}")
  longName.set(project.description)
  description.set(project.description)
}

dependencies {
  implementation(platform(bom.spring.framework.bom))
  implementation(platform(bom.spring.boot.dependencies.bom))
  implementation(platform(bom.spring.security.bom))
  implementation(platform(bom.testcontainers.dependencies.bom))
  api(project(":spring-gemfire"))
  implementation(project(":spring-gemfire-extensions"))
  compileOnly(libs.gemfire.core)
  compileOnly(libs.findbugs.jsr305)
  implementation("jakarta.annotation:jakarta.annotation-api")

  implementation("org.springframework.boot:spring-boot-configuration-processor")
  implementation("org.springframework.boot:spring-boot-autoconfigure-processor")
  implementation(libs.spring.session.gemfire)
  implementation(libs.aspectj.tools)

  testImplementation(libs.gemfire.core)
  testImplementation(libs.gemfire.cq)
  testImplementation(libs.gemfire.wan)
  testImplementation("jakarta.servlet:jakarta.servlet-api")
  testImplementation("org.apache.httpcomponents.client5:httpclient5")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.boot:spring-boot-starter-web")
  testCompileOnly(libs.findbugs.jsr305)

  testRuntimeOnly(project(":spring-gemfire-jetty12"))

  testRuntimeOnly("javax.cache:cache-api")
  testRuntimeOnly(libs.gemfire.web)
  testRuntimeOnly("org.springframework.boot:spring-boot-starter-jetty")
  testRuntimeOnly("org.springframework.boot:spring-boot-starter-json")
  testRuntimeOnly(libs.spring.shell)
  testImplementation(libs.spring.test.gemfire)
  testImplementation(libs.gemfire.testcontainers)
}

tasks.register<Jar>("testJar") {
  from(sourceSets.test.get().output)
  from(sourceSets.main.get().output)

  archiveFileName = "testJar.jar"
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.getByName<Test>("test") {
  dependsOn(tasks.named<Jar>("testJar"))
  forkEvery = 1
  maxParallelForks = 4
  val springTestGemfireDockerImage: String by project
  systemProperty("spring.test.gemfire.docker.image", springTestGemfireDockerImage)
  systemProperty("TEST_JAR_PATH", tasks.getByName<Jar>("testJar").outputs.files.singleFile.canonicalPath)
}

repositories{
  mavenCentral()
  mavenLocal()
}
