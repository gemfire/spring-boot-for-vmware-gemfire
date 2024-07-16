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
	implementation(platform(bom.spring.framework.bom))
	implementation(platform(bom.spring.boot.dependencies.bom))
	implementation(platform(bom.spring.security.bom))
	implementation(platform(bom.testcontainers.dependencies.bom))
	implementation(project(":spring-gemfire-starter"))

	implementation("org.assertj:assertj-core")
	implementation("org.projectlombok:lombok")

	testImplementation(project(":spring-gemfire-starter-test"))
	testImplementation(libs.gemfire.core)
	testImplementation(libs.gemfire.testcontainers)

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group="org.junit.vintage", module="junit-vintage-engine")
	}

}

tasks.register<Jar>("testJar") {
	from(sourceSets.test.get().output)
	from(sourceSets.main.get().output)

	archiveFileName = "testJar.jar"
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.getByName<Test>("test") {
	dependsOn(tasks.named<Jar>("testJar"))
	forkEvery = 1
	maxParallelForks = 4
	val springTestGemfireDockerImage: String by project
	systemProperty("spring.test.gemfire.docker.image", springTestGemfireDockerImage)
	systemProperty("TEST_JAR_PATH", tasks.getByName<Jar>("testJar").outputs.files.singleFile.canonicalPath)
}
