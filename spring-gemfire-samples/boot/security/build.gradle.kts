plugins {
	id("project-base")
	alias(libs.plugins.lombok)
}

description = "Spring Geode Sample demonstrating Apache Geode security configured with Spring."

dependencies {

	compileOnly(libs.gemfire.core)
	compileOnly(libs.gemfire.lucene)

	implementation(project(":spring-gemfire-starter"))
	implementation(project(":spring-gemfire-starter-test"))

	implementation("org.assertj:assertj-core")
	implementation("org.projectlombok:lombok")
	implementation("org.springframework.boot:spring-boot-starter-web")

	testImplementation(libs.gemfire.core)
	testImplementation(libs.gemfire.lucene)
}
