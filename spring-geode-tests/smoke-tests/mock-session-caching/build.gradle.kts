/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
	alias(libs.plugins.lombok)
	id("project-base")
}

description = "Smoke Tests to assert (Spring) Session state caching using Apache Geode with Mock Objects auto-configured by Spring Boot."

dependencies {
	implementation(platform(bom.testcontainers.dependencies.bom))

	compileOnly(libs.findbugs.jsr305)

	implementation("org.assertj:assertj-core")
	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation(project(":spring-gemfire-starter-session"))

	implementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group="org.junit.vintage", module="junit-vintage-engine")
	}

	testImplementation(libs.gemfire.core)
	testImplementation("junit:junit")
	testImplementation(project(":spring-gemfire-starter-test"))

}
