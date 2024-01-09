plugins {
	id("project-base")
}

description = "Spring Geode Sample demonstrating Spring's Cache Abstraction using Apache Geode as the caching provider for Look-Aside Caching."

dependencies {

	compileOnly(libs.gemfire.core)

	implementation(project(":spring-gemfire-starter"))

	implementation("org.springframework.boot:spring-boot-starter-web")

	testImplementation(libs.gemfire.core)

	testImplementation(project(":spring-gemfire-starter-test"))
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}
