/*
 * Copyright 2024-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("java-library")
  id("idea")
  id("eclipse")
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

val catalog = versionCatalogs.named("libs")

dependencies {
  api(platform(catalog.findLibrary("spring-boot-bom").get()))
  api(platform(catalog.findLibrary("spring-data-bom").get()))
  api(platform(catalog.findLibrary("spring-framework-bom").get()))
  api(platform(catalog.findLibrary("spring-security-bom").get()))
  api(platform(catalog.findLibrary("spring-session-bom").get()))
}

configurations.all {
  resolutionStrategy.cacheChangingModulesFor(0, "minutes")
}

tasks.withType<JavaCompile>().configureEach {
  options.compilerArgs.add("-parameters")
}
