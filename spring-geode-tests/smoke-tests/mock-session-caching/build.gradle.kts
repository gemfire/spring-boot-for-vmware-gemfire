plugins {
	alias(libs.plugins.lombok)
	id("project-base")
}

description = "Smoke Tests to assert (Spring) Session state caching using Apache Geode with Mock Objects auto-configured by Spring Boot."

dependencies {

	compileOnly(libs.findbugs.jsr305)

	implementation("org.assertj:assertj-core")
	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation(project(":spring-gemfire-starter-session"))

	implementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group="org.junit.vintage", module="junit-vintage-engine")
	}

	testImplementation(libs.gemfire.core)
	testImplementation(project(":spring-gemfire-starter-test"))

}
