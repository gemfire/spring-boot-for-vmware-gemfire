/*
 * Copyright 2024-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

pluginManagement {
  repositories {
    if (providers.gradleProperty("useMavenLocal").getOrElse("false").toBoolean()) {
      mavenLocal()
    }
    val repositoryConfigFilePath = providers.gradleProperty("spring.gemfire.repositories").getOrElse(
      providers.environmentVariable("HOME").get() + "/.gradle/gradleRepositories.json"
    )

    val jsonString = File(repositoryConfigFilePath).readText(Charsets.UTF_8)
    val repositories = groovy.json.JsonSlurper().parseText(jsonString) as Map<*, *>
    (repositories["repositories"] as List<*>).filterNotNull().map { entry -> entry as Map<*, *> }
      .forEach { entry ->
        entry.apply {
          maven {
            url = uri(entry["url"]!! as String)
            if (!entry["username"]?.toString().isNullOrBlank()) {
              credentials {
                username = entry["username"] as String
                password = entry["password"] as String
              }
            }
          }
        }
      }
    if (providers.gradleProperty("useMavenCentral").getOrElse("false").toBoolean()) {
      gradlePluginPortal()
    }
  }
  includeBuild("build-tools/publishing")
  includeBuild("build-tools/convention-plugins")
}


rootProject.name = "spring-boot-data-gemfire"

include("spring-gemfire")
include("spring-gemfire-autoconfigure")
include("spring-gemfire-extensions")

include("spring-gemfire-starter")
include("spring-gemfire-actuator")
include("spring-gemfire-actuator-autoconfigure")
include("spring-gemfire-starter-logging")
include("spring-gemfire-starter-session")
include("spring-gemfire-starter-actuator")
include("spring-gemfire-starter-test")
include("spring-gemfire-jetty12")

include("spring-gemfire-samples")
include("spring-gemfire-samples:boot:actuator")
include("spring-gemfire-samples:boot:configuration")
include("spring-gemfire-samples:boot:security")
include("spring-gemfire-samples:caching:http-session")
include("spring-gemfire-samples:caching:inline")
include("spring-gemfire-samples:caching:inline-async")
include("spring-gemfire-samples:caching:look-aside")
include("spring-gemfire-samples:caching:multi-site")
include("spring-gemfire-samples:caching:near")
include("spring-gemfire-samples:intro:getting-started")
include("spring-gemfire-samples:intro:quick-start")
//
include("spring-geode-tests:smoke-tests:function-execution-on-region")
include("spring-geode-tests:smoke-tests:locator-application")
include("spring-geode-tests:smoke-tests:logging")
include("spring-geode-tests:smoke-tests:mock-session-caching")
include("spring-geode-tests:smoke-tests:multi-store")
include("spring-geode-tests:smoke-tests:peer-cache-application")


dependencyResolutionManagement {
  repositories {
    if (providers.gradleProperty("useMavenLocal").getOrElse("false").toBoolean()) {
      mavenLocal()
    }
    val repositoryConfigFilePath = providers.gradleProperty("spring.gemfire.repositories").getOrElse(
      providers.environmentVariable("HOME").get() + "/.gradle/gradleRepositories.json"
    )

    val jsonString = File(repositoryConfigFilePath).readText(Charsets.UTF_8)
    val repositories = groovy.json.JsonSlurper().parseText(jsonString) as Map<*, *>
    (repositories["repositories"] as List<*>).filterNotNull().map { entry -> entry as Map<*, *> }
      .forEach { entry ->
        entry.apply {
          maven {
            url = uri(entry["url"]!! as String)
            if (!entry["username"]?.toString().isNullOrBlank()) {
              credentials {
                username = entry["username"] as String
                password = entry["password"] as String
              }
            }
          }
        }
      }
    if (providers.gradleProperty("useMavenCentral").getOrElse("false").toBoolean()) {
      gradlePluginPortal()
    }
  }
  versionCatalogs {
    create("libs") {
      overrideProperty("gemfireVersion")
      overrideProperty("springDataGemFireVersion")
      overrideProperty("springSessionDataGemFireVersion")
      overrideProperty("springBootVersion")
      overrideProperty("springDataBomVersion")
      overrideProperty("springFrameworkVersion")
      overrideProperty("springSecurityVersion")
      overrideProperty("springSessionBomVersion")
    }
//    create("bom") {
//      from(files("gradle/bom.versions.toml"))
//    }
  }
}

fun VersionCatalogBuilder.overrideProperty(property: String) {
  val value = System.getProperty(property)
    ?: (settings as? ExtensionAware)?.extensions?.extraProperties?.let {
      if (it.has(property)) it.get(property) as? String else null
    }
  if (value != null) {
    logger.debug("Overriding $property: $value")
    version(property, value)
  }
}
