/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import java.io.FileInputStream
import java.util.*

pluginManagement {
//  includeBuild("build-tools/scripts")
//  includeBuild("build-tools/dependency-constraints")
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
include("spring-geode-tests:smoke-tests:logging")
include("spring-geode-tests:smoke-tests:mock-session-caching")
include("spring-geode-tests:smoke-tests:multi-store")

dependencyResolutionManagement {
  versionCatalogs {
    val projectRootPath = layout.rootDirectory.asFile.toPath()
    create("libs") {
      val properties = Properties()
      properties.load(FileInputStream(projectRootPath.resolve("gradle.properties").toString()))
      versionOverrideFromProperties(this, properties)
    }
    create("bom") {
      from(files(projectRootPath.resolve("gradle").resolve("bom.versions.toml").toString()))
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

fun versionOverrideFromProperties(versionCatalogBuilder: VersionCatalogBuilder, properties: Properties) {
  versionOverrideFromProperty(versionCatalogBuilder, "gemfireVersion", properties)
  versionOverrideFromProperty(versionCatalogBuilder, "springDataGemFireVersion", properties)
  versionOverrideFromProperty(versionCatalogBuilder, "springTestGemFireVersion", properties)
  versionOverrideFromProperty(versionCatalogBuilder, "springSessionDataGemFireVersion", properties)
}
