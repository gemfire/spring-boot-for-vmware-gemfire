/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
  id("java")
  id("java-library")
  id("idea")
  id("eclipse")
}

group = "com.vmware.gemfire"

java {
  withJavadocJar()
  withSourcesJar()
  toolchain {
    languageVersion = JavaLanguageVersion.of(8)
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


configurations.all {
  resolutionStrategy.cacheChangingModulesFor(0, "minutes")
}

tasks.withType<JavaCompile>().configureEach {
  options.compilerArgs.add("-parameters")
}
