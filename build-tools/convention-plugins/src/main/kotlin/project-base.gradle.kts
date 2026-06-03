/*
 * Copyright 2024-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import java.util.LinkedList

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

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
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
