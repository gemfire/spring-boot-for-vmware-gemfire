/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
	id("project-base")
	alias(libs.plugins.lombok)
}

description = "Spring Geode Sample demonstrating Apache Geode security configured with Spring."

dependencies {
	implementation(platform(bom.testcontainers.dependencies.bom))

	compileOnly(libs.gemfire.core)

	implementation(project(":spring-gemfire-starter"))
	implementation(project(":spring-gemfire-starter-test"))

	implementation("org.assertj:assertj-core")
	implementation("org.projectlombok:lombok")
	implementation("org.springframework.boot:spring-boot-starter-web")

	testImplementation(libs.gemfire.core)
}
