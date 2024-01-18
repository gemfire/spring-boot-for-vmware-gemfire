pluginManagement {
    repositories.gradlePluginPortal()
    repositories.google()
    repositories.maven {
        url = uri("https://repo.spring.io/plugins-release")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

dependencyResolutionManagement {
    repositories.mavenCentral()
    versionCatalogs {
        create("bom"){
            from(files("../../gradle/bom.versions.toml"))
        }
    }
}
