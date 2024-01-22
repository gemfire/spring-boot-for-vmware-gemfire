import java.io.FileInputStream
import java.util.*

pluginManagement {
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
  versionOverrideFromProperty(versionCatalogBuilder, "springTestGemFireVersion", properties)
  versionOverrideFromProperty(versionCatalogBuilder, "springSessionDataGemFireVersion", properties)
}
