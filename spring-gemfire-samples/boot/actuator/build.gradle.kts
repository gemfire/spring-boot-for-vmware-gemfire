plugins {
  id("project-base")
  alias(libs.plugins.lombok)
}

description = "Spring Geode Sample demonstrating the use of Spring Boot Actuator with Apache Geode."

dependencies {
  compileOnly(libs.gemfire.core)

  implementation(project(":spring-gemfire-starter-actuator"))
  implementation(project(":spring-gemfire-starter-test"))

  implementation("org.springframework.boot:spring-boot-starter-web")
}

tasks.register("runServer", JavaExec::class.java) {
  classpath = sourceSets["main"].runtimeClasspath
  jvmArgs = listOf("-Dspring.profiles.active=server")
  this.mainClass = "example.app.temp.geode.server.BootGeodeServerApplication"
}
