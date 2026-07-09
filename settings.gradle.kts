/*
 * Copyright 2024-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import java.io.FileInputStream
import java.util.*

pluginManagement {
  repositories {
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

include("spring-gemfire-samples")
include("spring-gemfire-samples:boot:actuator")
include("spring-gemfire-samples:boot:configuration")
include("spring-gemfire-samples:caching:http-session")
include("spring-gemfire-samples:caching:inline")
include("spring-gemfire-samples:caching:look-aside")
include("spring-gemfire-samples:caching:near")
include("spring-gemfire-samples:intro:getting-started")
include("spring-gemfire-samples:intro:quick-start")
//
include("spring-geode-tests:smoke-tests:function-execution-on-region")
include("spring-geode-tests:smoke-tests:logging")
include("spring-geode-tests:smoke-tests:mock-session-caching")
include("spring-geode-tests:smoke-tests:multi-store")


dependencyResolutionManagement {
  versionCatalogs {
    val projectRootPath = layout.rootDirectory.asFile.toPath()
    create("libs") {
      val properties = Properties()
      properties.load(FileInputStream("gradle.properties"))
      versionOverrideFromProperties(this, properties)
    }
  }
}

fun versionOverrideFromProperty(
  versionCatalogBuilder: VersionCatalogBuilder,
  propertyName: String,
  propertiesFile: Properties
): String {
  val propertyValue = providers.systemProperty(propertyName).getOrElse(propertiesFile.getProperty(propertyName))

  return versionCatalogBuilder.version(propertyName, propertyValue)
}

fun versionOverrideFromPropertyIfPresent(
  versionCatalogBuilder: VersionCatalogBuilder,
  externalPropertyName: String,
  catalogVersionKey: String,
  propertiesFile: Properties
) {
  val propertyValue = providers.systemProperty(externalPropertyName).orNull
    ?: propertiesFile.getProperty(externalPropertyName)
  if (propertyValue != null) {
    versionCatalogBuilder.version(catalogVersionKey, propertyValue)
  }
}

fun versionOverrideFromProperties(versionCatalogBuilder: VersionCatalogBuilder, properties: Properties) {
  versionOverrideFromProperty(versionCatalogBuilder, "gemfireVersion", properties)
  versionOverrideFromProperty(versionCatalogBuilder, "springDataGemFireVersion", properties)
  versionOverrideFromProperty(versionCatalogBuilder, "springSessionDataGemFireVersion", properties)
  versionOverrideFromPropertyIfPresent(versionCatalogBuilder, "spring-boot.version", "springBootVersion", properties)
  versionOverrideFromPropertyIfPresent(versionCatalogBuilder, "spring-data-bom.version", "springDataBomVersion", properties)
  versionOverrideFromPropertyIfPresent(versionCatalogBuilder, "spring-framework.version", "springFrameworkVersion", properties)
  versionOverrideFromPropertyIfPresent(versionCatalogBuilder, "spring-security.version", "springSecurityVersion", properties)
  versionOverrideFromPropertyIfPresent(versionCatalogBuilder, "spring-session.version", "springSessionBomVersion", properties)
}
