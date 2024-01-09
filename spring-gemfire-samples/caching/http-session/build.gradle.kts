plugins {
	id("project-base")
}

description = "Spring Geode Sample demonstrating Spring Session with Apache Geode for HTTP Session Caching."

dependencies {

	compileOnly(libs.gemfire.core)

	implementation(project(":spring-gemfire-starter"))

	implementation("org.springframework.boot:spring-boot-starter-web")

	runtimeOnly("org.springframework.boot:spring-boot-starter-thymeleaf")

	testImplementation(project(":spring-gemfire-starter-session"))
	testImplementation(project(":spring-gemfire-starter-test"))
	testImplementation(libs.gemfire.core)
	testImplementation("org.springframework.boot:spring-boot-starter-test")

}
