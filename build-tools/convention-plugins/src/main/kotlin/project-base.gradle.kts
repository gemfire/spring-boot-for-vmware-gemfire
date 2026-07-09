/*
 * Copyright 2024-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import java.util.LinkedList
import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
  id("java")
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

tasks.withType(Javadoc::class).configureEach {
  isFailOnError = false
}

dependencies {
  val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
  api(platform(libs.findLibrary("spring-boot-bom").get()))
  api(platform(libs.findLibrary("spring-data-bom").get()))
  api(platform(libs.findLibrary("spring-framework-bom").get()))
  api(platform(libs.findLibrary("spring-security-bom").get()))
  api(platform(libs.findLibrary("spring-session-bom").get()))
}

repositories {
  if (providers.gradleProperty("useMavenCentral").getOrElse("false").toBoolean()) {
    mavenCentral()
  }
  val additionalMavenRepoURLs = project.findProperty("additionalMavenRepoURLs").toString()
  if (!additionalMavenRepoURLs.isNullOrBlank() && additionalMavenRepoURLs.isNotEmpty()) {
    additionalMavenRepoURLs.split(",").forEach {
      project.repositories.maven {
        this.url = uri(it)
      }
    }
  }

  val repoList = LinkedList(this.toList())
  val gcsRepos = repoList.filter { it.name.startsWith("GCS") }
  repoList.removeAll(gcsRepos.toSet())
  gcsRepos.forEach { repoList.addFirst(it) }
  this.clear()
  repoList.forEach { this.add(it) }
}

configurations.all {
  resolutionStrategy.cacheChangingModulesFor(0, "minutes")
}

tasks.withType<JavaCompile>().configureEach {
  options.compilerArgs.add("-parameters")
}
