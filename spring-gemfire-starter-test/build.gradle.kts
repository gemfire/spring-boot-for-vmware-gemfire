plugins {
	id("project-base")
	alias(libs.plugins.lombok)
}

description = "Spring Boot Test Starter for VMware GemFire"


dependencies {
	api(project(":spring-gemfire-starter"))
	api("org.springframework.boot:spring-boot-starter-test")
	api(libs.spring.test.gemfire)
}
