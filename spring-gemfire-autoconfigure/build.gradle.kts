/*
 * Copyright 2024-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import ProjectUtils.getBaseVersion

plugins {
  id("project-base")
  id("gemfire-repo-artifact-publishing")
  id("gemfire-artifactory")
}

description = "Spring Boot Auto-Configuration for VMware GemFire"

publishingDetails {
  artifactName.set("spring-boot-${getBaseVersion(property("spring-boot.version").toString())}-gemfire-autoconfigure-${getBaseVersion(property("gemfireVersion").toString())}")
  longName.set(project.description)
  description.set(project.description)
}

dependencies {
  implementation(platform(bom.testcontainers.dependencies.bom))
  api(project(":spring-gemfire"))
  implementation(project(":spring-gemfire-extensions"))
  compileOnly(libs.gemfire.core)
  compileOnly(libs.findbugs.jsr305)
  compileOnly(libs.spring.session.gemfire)
  compileOnly("org.springframework.boot:spring-boot-configuration-processor")
  compileOnly("org.springframework.boot:spring-boot-autoconfigure-processor")
  implementation("jakarta.annotation:jakarta.annotation-api")
  implementation("org.springframework.boot:spring-boot-cache")
  implementation("org.springframework.boot:spring-boot-websocket")

  implementation(libs.aspectj.tools)

  testCompileOnly(libs.lombok)
  testAnnotationProcessor(libs.lombok)

  testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

  testImplementation(libs.gemfire.core)
  testImplementation(libs.gemfire.cq)
  testImplementation(libs.gemfire.gfsh)
  testImplementation(libs.spring.session.gemfire)
  testImplementation("jakarta.servlet:jakarta.servlet-api")
  testImplementation("org.apache.httpcomponents.client5:httpclient5")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.boot:spring-boot-starter-web")
  testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
  testCompileOnly(libs.findbugs.jsr305)

  testRuntimeOnly("javax.cache:cache-api")
  testRuntimeOnly(libs.gemfire.web)
  testRuntimeOnly("org.springframework.boot:spring-boot-starter-jetty")
  testRuntimeOnly("org.springframework.boot:spring-boot-starter-json")
  testRuntimeOnly(libs.spring.shell)
  testImplementation(libs.spring.data.gemfire.test.framework)
  testImplementation(libs.gemfire.testcontainers)
  testImplementation("org.testcontainers:testcontainers") {
    version {
      strictly(bom.versions.testcontainersVersion.get())
    }
  }
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
