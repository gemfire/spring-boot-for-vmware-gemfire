plugins {
	id("project-base")
	alias(libs.plugins.lombok)
}

description = "Spring Boot Test Starter for VMware GemFire"


dependencies {
	implementation(platform(bom.spring.framework.bom))
	implementation(platform(bom.spring.boot.dependencies.bom))
	implementation(platform(bom.spring.security.bom))
	implementation(platform(bom.testcontainers.dependencies.bom))
	api(project(":spring-gemfire-starter"))
	api("org.springframework.boot:spring-boot-starter-test")
	api(libs.spring.test.gemfire)
}
