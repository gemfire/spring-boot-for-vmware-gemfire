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

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
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
