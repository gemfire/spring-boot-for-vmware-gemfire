/*
 * Copyright 2024-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  alias(libs.plugins.lombok)
  id("project-base")
  id("gemfire-repo-artifact-publishing")
  id("gemfire-artifactory")
}

description = "Spring Boot Auto-Configuration for VMware GemFire"

val baseGemFireVersion: String by project
val baseSpringVersion: String by project

publishingDetails {
  artifactName.set("spring-boot-$baseSpringVersion-gemfire-autoconfigure-$baseGemFireVersion")
  longName.set(project.description)
  description.set(project.description)
}

dependencies {
  api(project(":spring-gemfire"))
  implementation(project(":spring-gemfire-extensions"))
  compileOnly(libs.gemfire.core)
  compileOnly(libs.findbugs.jsr305)
  compileOnly(libs.spring.session.gemfire)
  compileOnly("org.springframework.boot:spring-boot-configuration-processor")
  compileOnly("org.springframework.boot:spring-boot-autoconfigure-processor")
  implementation("jakarta.annotation:jakarta.annotation-api")

  implementation(libs.aspectj.tools)

  testImplementation(libs.gemfire.core)
  testImplementation(libs.gemfire.cq)
  testImplementation(libs.gemfire.wan)
  testImplementation(libs.spring.session.gemfire)
  testImplementation("jakarta.servlet:jakarta.servlet-api")
  testImplementation("org.apache.httpcomponents.client5:httpclient5")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.boot:spring-boot-starter-web")
  testCompileOnly(libs.findbugs.jsr305)

  testRuntimeOnly("javax.cache:cache-api")
  testRuntimeOnly(libs.gemfire.web)
  testRuntimeOnly("org.springframework.boot:spring-boot-starter-jetty")
  testRuntimeOnly("org.springframework.boot:spring-boot-starter-json")
  testRuntimeOnly(libs.spring.shell)
  testImplementation(libs.spring.data.gemfire.test.framework)
  testImplementation(libs.gemfire.testcontainers)
  testImplementation(libs.awaitility)
  testImplementation("junit:junit")
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

repositories {
  if (providers.gradleProperty("useMavenCentral").getOrElse("false").toBoolean()) {
    mavenCentral()
  }
}
