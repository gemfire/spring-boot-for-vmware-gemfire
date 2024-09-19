/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
	id("project-base")
}

description = "Smoke Tests to assert that Spring for Apache Geode logging functions as expected."

dependencies {
	implementation(platform(bom.testcontainers.dependencies.bom))
	implementation("org.assertj:assertj-core")

	// NOTE: 'spring-geode-starter-logging' must be the first entry on the application/test classpath
	implementation(project(":spring-gemfire-starter-logging"))
	implementation(project(":spring-gemfire-starter"))

	testImplementation(libs.gemfire.core)
	testImplementation(project(":spring-gemfire-starter-test"))
}
