plugins {
  id("project-base")
  id("gemfire-repo-artifact-publishing")
}

description = "Spring Boot Actuator for VMware GemFire"

publishingDetails {
  artifactName.set("spring-boot-2.7-gemfire-actuator-${ProjectUtils.getGemFireBaseVersion(property("gemfireVersion").toString())}")
  longName.set(project.description)
  description.set(project.description)
}

dependencies {
  api(project(":spring-gemfire"))

  api("org.springframework.boot:spring-boot-starter-actuator")

  compileOnly(libs.gemfire.logging)
  runtimeOnly(libs.gemfire.logging)
  runtimeOnly(libs.gemfire.serialization)

  compileOnly(libs.gemfire.core)
  compileOnly(libs.gemfire.cq)
  compileOnly(libs.gemfire.wan)
  compileOnly(libs.gemfire.gfsh)
  compileOnly(libs.gemfire.tcp.server)
  compileOnly(libs.gemfire.deployment.chained.classloader)

  testImplementation(libs.gemfire.core)
  testImplementation(libs.gemfire.logging)
  testImplementation(libs.gemfire.cq)
  testImplementation(libs.gemfire.wan)
  testImplementation(libs.gemfire.gfsh)
  testImplementation(libs.gemfire.tcp.server)
  testImplementation(libs.gemfire.deployment.chained.classloader)
  testImplementation("junit:junit")
  testImplementation("org.assertj:assertj-core")
  testImplementation(libs.mockito.core)
  testImplementation("org.projectlombok:lombok")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation(libs.spring.test.gemfire)
  testImplementation(libs.multithreadedtc)
}
