/*
 * Copyright 2024-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
	id("project-base")
}

description = "Smoke Tests to assert that a multi-store Spring Data project using JPA for database access and Apache Geode for caching works as expected."

dependencies {

	implementation(platform(bom.testcontainers.dependencies.bom))
	compileOnly(libs.gemfire.core)

	testCompileOnly(libs.lombok)
	testAnnotationProcessor(libs.lombok)

	implementation(project(":spring-gemfire-starter")) {
		exclude(group="com.sun.xml.bind", module= "jaxb-impl")
	}

	implementation("org.assertj:assertj-core")
	implementation("org.projectlombok:lombok")
	implementation("jakarta.persistence:jakarta.persistence-api")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	//The mongo-driver-sync:5.4.0 that comes from the BOM is broken. Override to 5.5.0
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb"){
		exclude(group="org.mongodb", module="mongodb-driver-sync")
	}

	implementation("org.mongodb:mongodb-driver-sync:5.5.0")

	runtimeOnly("org.hsqldb:hsqldb")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group="org.junit.vintage", module="junit-vintage-engine")
	}

	testImplementation("junit:junit")
	testImplementation(libs.gemfire.core)
	testImplementation(project(":spring-gemfire-starter-test"))
}
