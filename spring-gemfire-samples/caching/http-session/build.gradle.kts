/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
	id("project-base")
	id("commercial-repositories")
}

description = "Spring Geode Sample demonstrating Spring Session with Apache Geode for HTTP Session Caching."

dependencies {
	implementation(platform(bom.testcontainers.dependencies.bom))
	compileOnly(libs.gemfire.core)

	implementation(project(":spring-gemfire-starter"))

	implementation("org.springframework.boot:spring-boot-starter-web")

	runtimeOnly("org.springframework.boot:spring-boot-starter-thymeleaf")

	testImplementation("junit:junit")
	testImplementation(project(":spring-gemfire-starter-session"))
	testImplementation(project(":spring-gemfire-starter-test"))
	testImplementation(libs.gemfire.core)
	testImplementation("org.springframework.boot:spring-boot-starter-test")

}
