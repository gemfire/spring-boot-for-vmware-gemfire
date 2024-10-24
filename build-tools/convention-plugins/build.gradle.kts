/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    id("groovy-gradle-plugin")
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(gradleApi())
    implementation("org.jfrog.buildinfo:build-info-extractor-gradle:5.2.2")
}

gradlePlugin {
    plugins.register("gemfire-artifactory") {
        id = "gemfire-artifactory"
        implementationClass = "com.vmware.gemfire.gradle.ArtifactoryPlugin"
    }
}
