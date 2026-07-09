/*
 * Copyright 2024-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
	id("project-base")
}

description = "Smoke Tests to assert (Spring) Session state caching using Apache Geode with Mock Objects auto-configured by Spring Boot."

dependencies {
	implementation(platform(libs.testcontainers.dependencies.bom))

	compileOnly(libs.findbugs.jsr305)

	testCompileOnly(libs.lombok)
	testAnnotationProcessor(libs.lombok)

	implementation("org.assertj:assertj-core")
	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation(project(":spring-gemfire-starter-session"))

	implementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group="org.junit.vintage", module="junit-vintage-engine")
	}

	testImplementation(libs.gemfire.core)
	testImplementation("junit:junit")
	testImplementation(project(":spring-gemfire-starter-test"))
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")

}
