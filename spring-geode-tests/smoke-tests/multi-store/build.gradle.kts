/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
	alias(libs.plugins.lombok)
	id("project-base")
}

description = "Smoke Tests to assert that a multi-store Spring Data project using JPA for database access and Apache Geode for caching works as expected."

dependencies {

	implementation(platform(bom.testcontainers.dependencies.bom))
	compileOnly(libs.gemfire.core)

	implementation(project(":spring-gemfire-starter")) {
		exclude(group="com.sun.xml.bind", module= "jaxb-impl")
	}

	implementation("org.assertj:assertj-core")
	implementation("org.projectlombok:lombok")
	implementation("jakarta.persistence:jakarta.persistence-api")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

	runtimeOnly("org.hsqldb:hsqldb")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group="org.junit.vintage", module="junit-vintage-engine")
	}

	testImplementation(libs.gemfire.core)
	testImplementation(project(":spring-gemfire-starter-test"))
}
