/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
	id("project-base")
}

description = "Spring Geode Sample demonstrating Spring's Cache Abstraction using Apache Geode as the caching provider for Look-Aside Caching."

dependencies {
	implementation(platform(bom.testcontainers.dependencies.bom))

	compileOnly(libs.gemfire.core)

	implementation(project(":spring-gemfire-starter"))

	implementation("org.springframework.boot:spring-boot-starter-web")

	testImplementation(libs.gemfire.core)

	testImplementation(project(":spring-gemfire-starter-test"))
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}
