/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    id("project-base")
}

description = "Smoke Tests asserting the proper function of a Spring Boot configured and bootstrapped Apache Geode Locator application."

dependencies {

    implementation(platform(bom.testcontainers.dependencies.bom))
    implementation("org.assertj:assertj-core")

    implementation(project(":spring-gemfire-starter"))
    implementation(project(":spring-gemfire-starter-logging"))

    compileOnly(libs.gemfire.core)

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    testImplementation(project(":spring-gemfire-starter-test"))
    testImplementation(libs.gemfire.core)
    testImplementation(libs.gemfire.testcontainers)

}

tasks.getByName<Test>("test") {
    forkEvery = 1
    maxParallelForks = 4
    val springTestGemfireDockerImage: String by project
    systemProperty("spring.test.gemfire.docker.image", springTestGemfireDockerImage)
}
