plugins {
  id("project-base")
  alias(libs.plugins.lombok)
}

description =
  "Spring Geode Sample demonstrating Spring's Cache Abstraction using Apache Geode as the caching provider for Asynchronous Inline Caching."

dependencies {
  compileOnly(libs.findbugs.jsr305)
  compileOnly(libs.gemfire.core)

  implementation(project(":spring-gemfire-starter")) {
    exclude(group = "com.sun.xml.bind", module = "jaxb-impl")
  }

  implementation("org.projectlombok:lombok")
  implementation("jakarta.persistence:jakarta.persistence-api")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-web")

  runtimeOnly("org.hsqldb:hsqldb")

  testImplementation(project(":spring-gemfire-starter-test"))
  testImplementation(libs.gemfire.core)
  testImplementation(libs.awaitility)
  testImplementation("org.springframework.boot:spring-boot-starter-test")

}
