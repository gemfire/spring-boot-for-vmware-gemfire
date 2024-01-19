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
  implementation(platform(bom.spring.framework.bom))
  implementation(platform(bom.spring.boot.dependencies.bom))
  implementation(platform(bom.spring.security.bom))
  implementation(platform(bom.testcontainers.dependencies.bom))
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
  compileOnly(libs.gemfire.deployment.legacy.classloader)

  testImplementation(libs.gemfire.core)
  testImplementation(libs.gemfire.logging)
  testImplementation(libs.gemfire.cq)
  testImplementation(libs.gemfire.wan)
  testImplementation(libs.gemfire.gfsh)
  testImplementation(libs.gemfire.tcp.server)
  testImplementation(libs.gemfire.deployment.legacy.classloader)
  testImplementation("ch.qos.logback:logback-classic")
  testImplementation("org.apache.logging.log4j:log4j-to-slf4j")
  testImplementation("junit:junit")
  testImplementation("org.assertj:assertj-core")
  testImplementation(libs.mockito.core)
  testImplementation("org.projectlombok:lombok")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation(libs.spring.test.gemfire)
  testImplementation(libs.multithreadedtc)
}
