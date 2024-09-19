/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
	alias(libs.plugins.lombok)
	id("project-base")
}

description = "Smoke Tests asserting the proper execution of an Apache Geode Function using Spring Data for Apache Geode Function annotation support in a Spring Boot context."

dependencies {
	implementation(platform(bom.testcontainers.dependencies.bom))
	implementation(project(":spring-gemfire-starter"))

	implementation("org.assertj:assertj-core")
	implementation("org.projectlombok:lombok")

	testImplementation(project(":spring-gemfire-starter-test"))
	testImplementation(libs.gemfire.core)

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group="org.junit.vintage", module="junit-vintage-engine")
	}

}
