/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import java.util.LinkedList

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
  api(platform("org.springframework.boot:spring-boot-dependencies:${project.ext.get("spring-boot.version")}"))
  api(platform("org.springframework.data:spring-data-bom:${project.ext.get("spring-data-bom.version")}"))
  api(platform("org.springframework:spring-framework-bom:${project.ext.get("spring-framework.version")}"))
  api(platform("org.springframework.security:spring-security-bom:${project.ext.get("spring-security.version")}"))
  api(platform("org.springframework.session:spring-session-bom:${project.ext.get("spring-session.version")}"))
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
