/*
 * Copyright 2023-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import nl.littlerobots.vcu.plugin.versionSelector
import java.net.HttpURLConnection
import java.net.URI

plugins {
  id("java")
  id("idea")
  id("eclipse")
  id("maven-publish")
  alias(libs.plugins.ben.manes.versions)
  alias(libs.plugins.littlerobots.version.catalog.update)
  id("gemfire-artifactory")
}

repositories {
  addGemFireRepositories(
    providers,
    addMavenCentral = providers.gradleProperty("useMavenCentral").getOrElse("false").toBoolean()
  )
}

// Suppress warning from gemfire-artifactory plugin. We need the module to be on this project in order to get buildInfo
// uploaded, but there is no artifact on the root project, so we skip that part.
tasks.artifactoryPublish {
  skip = true
}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(17)
  }
}

group = "com.vmware.gemfire"

allprojects {
  configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "minutes")
  }
}

versionCatalogUpdate {
  // These options will be set as default for all version catalogs
  sortByKey = true
  // Referenced that are pinned are not automatically updated.
  // They are also not automatically kept however (use keep for that).
  pin {
  }
  keep {
    keepUnusedVersions = true
  }
  versionSelector {
    val candidate = it.candidate
    if (!isPatch(candidate.version, it.currentVersion)) {
      false
    } else if (candidate.group == "com.vmware.gemfire") {
      // com.vmware.gemfire artifacts are expected to come from our commercial repos;
      // no public-availability check applies to them.
      true
    } else {
      // Our internal repos mirror/aggregate several other Broadcom-internal repos
      // (e.g. commercially patched Spring builds) alongside com.vmware.gemfire
      // artifacts, so version listings for non-gemfire groups can include
      // commercial-only versions (e.g. org.springframework:spring-tx:5.3.50) that
      // don't exist publicly. Since every non-gemfire library we track here is meant
      // to stay on publicly available versions, reject any candidate that doesn't
      // actually resolve from Maven Central.
      isPubliclyAvailable(candidate.group, candidate.module, candidate.version)
    }
  }
}

fun isPubliclyAvailable(group: String, module: String, version: String): Boolean {
  val path = group.replace(".", "/")
  val url = "https://repo.maven.apache.org/maven2/$path/$module/$version/$module-$version.pom"
  return try {
    val connection = URI(url).toURL().openConnection() as HttpURLConnection
    connection.requestMethod = "HEAD"
    connection.connectTimeout = 5000
    connection.readTimeout = 5000
    val code = connection.responseCode
    connection.disconnect()
    code == 200
  } catch (e: java.io.IOException) {
    false
  }
}

tasks.withType<DependencyUpdatesTask> {
  rejectVersionIf {
    !isPatch(candidate.version, currentVersion)
  }
}

fun isPatch(candidateVersion: String, currentVersion: String): Boolean {
  val candidateSplit = candidateVersion.split(".")
  val currentSplit = currentVersion.split(".")

  val strings = listOf("rc", "alpha", "beta")

  if (strings.filter { candidateVersion.lowercase().contains(it) }.toList().isNotEmpty()) {
    return false
  }

  if (currentSplit.size == 3) {
    if (candidateSplit.size == currentSplit.size) {
      return if (candidateSplit[0] != currentSplit[0]) {
        false
      } else if (candidateSplit[1] != currentSplit[1]) {
        false
      } else {
        true
      }
    }
  } else {
    return false
  }
  return false
}
