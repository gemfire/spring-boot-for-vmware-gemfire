/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
	id("project-base")
	alias(libs.plugins.lombok)
}

description = "Spring Boot Test Starter for VMware GemFire"


dependencies {
	implementation(platform(bom.testcontainers.dependencies.bom))
	api(project(":spring-gemfire-starter"))
	api("org.springframework.boot:spring-boot-starter-test")
	api(variantOf(libs.spring.data.gemfire) { classifier("test-framework") })
}
