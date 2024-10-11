/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import java.io.FileInputStream
import java.util.*

pluginManagement {
  includeBuild("build-tools/publishing")
  includeBuild("build-tools/convention-plugins")
}

rootProject.name = "spring-boot-for-data-gemfire"

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

project(":spring-gemfire").name = "spring-gemfire"
project(":spring-gemfire-autoconfigure").name = "spring-gemfire-autoconfigure"
project(":spring-gemfire-extensions").name = "spring-gemfire-extensions"

project(":spring-gemfire-starter").name = "spring-gemfire-starter"
project(":spring-gemfire-actuator").name = "spring-gemfire-actuator"
project(":spring-gemfire-actuator-autoconfigure").name = "spring-gemfire-actuator-autoconfigure"
project(":spring-gemfire-starter-logging").name = "spring-gemfire-starter-logging"
project(":spring-gemfire-starter-session").name = "spring-gemfire-starter-session"
project(":spring-gemfire-starter-actuator").name = "spring-gemfire-starter-actuator"
project(":spring-gemfire-starter-test").name = "spring-gemfire-starter-test"

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      val properties = Properties()
      properties.load(FileInputStream("gradle.properties"))
      versionOverrideFromProperties(this, properties)
    }
    create("bom") {
      from(files("gradle/bom.versions.toml"))
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
  versionOverrideFromProperty(versionCatalogBuilder, "springSessionDataGemFireVersion", properties)
}
