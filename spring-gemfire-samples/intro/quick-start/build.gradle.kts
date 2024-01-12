plugins {
  id("project-base")
  alias(libs.plugins.lombok)
}

description = "Quick Start for Spring Boot for Apache Geode"

dependencies {

  implementation(project(":spring-gemfire-starter"))

  implementation("org.assertj:assertj-core")
  implementation("org.projectlombok:lombok")

  //runtime project(":spring-gemfire-starter-logging")

  testImplementation(project(":spring-gemfire-starter-test"))

  testImplementation("org.springframework.boot:spring-boot-starter-test")

}
