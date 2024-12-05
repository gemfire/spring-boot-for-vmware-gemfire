/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import gradle.kotlin.dsl.accessors._47bddfe2709b17d87106a3017b4e6c2a.api
import gradle.kotlin.dsl.accessors._47bddfe2709b17d87106a3017b4e6c2a.ext
import gradle.kotlin.dsl.accessors._47bddfe2709b17d87106a3017b4e6c2a.java
import java.util.LinkedList

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

dependencies {
  api(platform("org.springframework.boot:spring-boot-dependencies:${project.ext.get("spring-boot.version")}"))
  api(platform("org.springframework.data:spring-data-bom:${project.ext.get("spring-data-bom.version")}"))
  api(platform("org.springframework:spring-framework-bom:${project.ext.get("spring-framework.version")}"))
  api(platform("org.springframework.security:spring-security-bom:${project.ext.get("spring-security.version")}"))
  api(platform("org.springframework.session:spring-session-bom:${project.ext.get("spring-session.version")}"))
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
  val listOrderedRepos= LinkedList<ArtifactRepository>()
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
}

configurations.all {
  resolutionStrategy.cacheChangingModulesFor(0, "minutes")
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
