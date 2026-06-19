/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import nl.littlerobots.vcu.plugin.versionSelector

plugins {
  id("java")
  id("idea")
  id("eclipse")
  id("maven-publish")
  alias(libs.plugins.ben.manes.versions)
  alias(libs.plugins.littlerobots.version.catalog.update)
  id("gemfire-artifactory")
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
  // vCU v1.x resolves catalog entries directly via its own detached configurations,
  // independently of DependencyUpdatesTask. Without this selector the rejectVersionIf
  // filter is bypassed for that second resolution path. Mirror the same logic here so
  // both paths apply isAllowedUpdate consistently.
  versionSelector {
    val allowMajor = project.hasProperty("updateMajor")
    val allowMinor = project.hasProperty("updateMinor")
    isAllowedUpdate(it.candidate.version, it.currentVersion, allowMajor, allowMinor)
  }
  versionCatalogs{
    create("bom"){
      catalogFile.set(file("gradle/bom.versions.toml"))
    }
    create("publish"){
      catalogFile.set(file("gradle/publish.versions.toml"))
    }
  }

}

tasks.withType<DependencyUpdatesTask> {
  rejectVersionIf {
    val allowMajor = project.hasProperty("updateMajor")
    val allowMinor = project.hasProperty("updateMinor")
    !isAllowedUpdate(candidate.version, currentVersion, allowMajor, allowMinor)
  }
}

fun isAllowedUpdate(
  candidateVersion: String,
  currentVersion: String,
  allowMajor: Boolean,
  allowMinor: Boolean
): Boolean {
  val nonStableMarkers = listOf("alpha", "beta", "rc", "snapshot", "dev", "preview", "build", "milestone")
  if (nonStableMarkers.any { candidateVersion.contains(it, ignoreCase = true) }) {
    return false
  }
  if (candidateVersion.contains(Regex("""[.\-][Mm]\d"""))) {
    return false
  }

  // Normalize Gradle version ranges (e.g., "[4.0,4.1)" -> "4.0").
  val cleanCurrentVersion = if (currentVersion.startsWith("[") || currentVersion.startsWith("(")) {
    currentVersion
      .replace("[", "").replace("]", "").replace("(", "").replace(")", "")
      .split(",").first().trim()
  } else {
    currentVersion
  }

  if (allowMajor) return true

  fun parseMajorMinor(v: String): Pair<Int, Int>? {
    val parts = v.split(".")
    val major = parts.getOrNull(0)?.takeWhile { it.isDigit() }?.toIntOrNull() ?: return null
    val minor = parts.getOrNull(1)?.takeWhile { it.isDigit() }?.toIntOrNull() ?: return null
    return major to minor
  }

  val (currentMajor, currentMinor) = parseMajorMinor(cleanCurrentVersion) ?: return false
  val (candidateMajor, candidateMinor) = parseMajorMinor(candidateVersion) ?: return false

  if (currentMajor != candidateMajor) return false
  if (allowMinor) return true
  return currentMinor == candidateMinor
}
