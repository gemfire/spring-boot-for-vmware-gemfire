plugins {
  id("project-base")
  alias(libs.plugins.lombok)
}

description =
  "Spring Geode Sample demonstrating Spring's Cache Abstraction using Apache Geode as the caching provider for Near Caching."

dependencies {

  compileOnly(libs.gemfire.core)

  implementation(project(":spring-gemfire-extensions"))
  implementation(project(":spring-gemfire-starter"))

  implementation("org.assertj:assertj-core")
  implementation("org.projectlombok:lombok")
  implementation("org.springframework.boot:spring-boot-starter-web")

  runtimeOnly("org.springframework.boot:spring-boot-starter-jetty")

  testImplementation(libs.gemfire.core)
  testImplementation(project(":spring-gemfire-starter-test"))
  testImplementation("org.springframework.boot:spring-boot-starter-test")

}