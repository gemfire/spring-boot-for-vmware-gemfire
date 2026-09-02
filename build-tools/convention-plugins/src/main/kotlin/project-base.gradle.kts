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
  // com.vmware.gemfire:gemfire-core pulls in com.github.oshi:oshi-core, which requires
  // org.slf4j:slf4j-api 2.x. Gradle's conflict resolution otherwise lets that outrank
  // Spring Boot 2.7's managed slf4j/logback versions (1.7.x/1.2.x), dragging logback-classic
  // up to a 1.3.x build too. Spring Boot 2.7's LogbackLoggingSystem is compiled against SLF4J 1.x
  // and calls org.slf4j.impl.StaticLoggerBinder, which SLF4J 2.x removed entirely, so any
  // Spring Boot context that initializes logging (e.g. every @SpringBootTest) fails with
  // NoClassDefFoundError before running. Force back to Boot's managed versions.
  resolutionStrategy.force(
    "org.slf4j:slf4j-api:1.7.36",
    "ch.qos.logback:logback-classic:1.2.12",
    "ch.qos.logback:logback-core:1.2.12"
  )
}

dependencies {
  components {
    // Belt-and-suspenders alongside the force above: strip oshi-core's own slf4j-api
    // dependency edge so the SLF4J 2.x request never enters the graph in the first place.
    withModule("com.github.oshi:oshi-core") {
      allVariants {
        withDependencies {
          removeAll { it.group == "org.slf4j" && it.name == "slf4j-api" }
        }
      }
    }
  }
}

tasks.withType<JavaCompile>().configureEach {
  options.compilerArgs.add("-parameters")
}
