plugins {
  id("project-base")
  id("gemfire-repo-artifact-publishing")
}

description = "Spring Boot Actuator Auto-Configuration for VMware GemFire"


publishingDetails {
  artifactName.set("spring-boot-2.7-gemfire-actuator-autoconfigure-${ProjectUtils.getGemFireBaseVersion(property("gemfireVersion").toString())}")
  longName.set(project.description)
  description.set(project.description)
}

dependencies {
    api(project(":spring-gemfire-actuator"))
    api(project(":spring-gemfire-autoconfigure"))

    compileOnly(libs.gemfire.core)
    compileOnly(libs.findbugs.jsr305)

    testImplementation(libs.gemfire.core)
    testImplementation("junit:junit")
    testImplementation("org.assertj:assertj-core")
    testImplementation(libs.mockito.core)
    testImplementation("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(libs.spring.test.gemfire)
    testImplementation(libs.multithreadedtc)
}

