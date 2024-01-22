plugins {
	id("project-base")
	alias(libs.plugins.lombok)
}

description = "Spring Boot Test Starter for VMware GemFire"


dependencies {
	api(platform(bom.spring.framework.bom))
	api(platform(bom.spring.boot.dependencies.bom))
	api(platform(bom.spring.security.bom))
	api(platform(bom.testcontainers.dependencies.bom))
	api(project(":spring-gemfire-starter"))
	api("org.springframework.boot:spring-boot-starter-test")
	api(libs.spring.test.gemfire)
}
