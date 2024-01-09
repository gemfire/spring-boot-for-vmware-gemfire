plugins {
	id("project-base")
}

description = "Smoke Tests asserting the proper function of a Spring Boot configured and bootstrapped Apache Geode peer Cache application in a simulated forced-disconnect/auto-reconnect scenairo."

dependencies {

	compileOnly(libs.gemfire.core)

	implementation("org.assertj:assertj-core")

	implementation(project(":spring-gemfire-extensions"))
	implementation(project(":spring-gemfire-starter"))
	implementation(project(":spring-gemfire-starter-logging"))

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group= "org.junit.vintage", module= "junit-vintage-engine")
	}

	testImplementation(libs.gemfire.core)
	testImplementation(project(":spring-gemfire-starter-test"))

}
