/*
 * Copyright 2024-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import java.util.LinkedList
import org.gradle.api.artifacts.VersionCatalogsExtension

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
  val listOrderedRepos = LinkedList<ArtifactRepository>()
  val values = project.repositories.asMap.values
  values.forEach { artifactRepository ->
    if (artifactRepository is MavenArtifactRepository) {
      if (artifactRepository.url.toString().startsWith("gcs:")) {
        listOrderedRepos.addFirst(artifactRepository)
      } else {
        listOrderedRepos.add(artifactRepository)
      }
    }
  }
  project.repositories.clear()
  project.repositories.addAll(listOrderedRepos)
}

configurations.all {
  resolutionStrategy.cacheChangingModulesFor(0, "minutes")
}

tasks.withType<JavaCompile>().configureEach {
  options.compilerArgs.add("-parameters")
}

tasks.register("compileTestKotlin") {}
