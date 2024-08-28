/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("java-library")
  id("idea")
  id("eclipse")
  id("commercial-repositories")
}

group = "com.vmware.gemfire"

java {
  withJavadocJar()
  withSourcesJar()
  toolchain {
    languageVersion = JavaLanguageVersion.of(17)
  }
}

tasks.named<Javadoc>("javadoc") {
  isFailOnError = false
}

dependencies{
}

repositories {
  mavenCentral()
  val additionalMavenRepoURLs = project.findProperty("additionalMavenRepoURLs").toString()
  if (!additionalMavenRepoURLs.isNullOrBlank() && additionalMavenRepoURLs.isNotEmpty()) {
    additionalMavenRepoURLs.split(",").forEach {
      project.repositories.maven {
        this.url = uri(it)
      }
    }
  }
}

configurations.all {
  resolutionStrategy.cacheChangingModulesFor(0, "minutes")
}

tasks.withType<JavaCompile>().configureEach {
  options.compilerArgs.add("-parameters")
}

fun getGemFireBaseVersion(): String {
  return getBaseVersion(property("gemfireVersion").toString())
}

fun getBaseVersion(version: String): String {
  val split = version.split(".")
  if (split.size < 2) {
    throw RuntimeException("version is malformed")
  }
  return "${split[0]}.${split[1]}"
}

tasks.named<Test>("test") {
  forkEvery = 1
  maxParallelForks = 1
}
