plugins {
  id("project-base")
  id("gemfire-repo-artifact-publishing")
}

description = "Spring Boot Starter for Spring Session using VMware GemFire"

publishingDetails {
  artifactName.set("spring-boot-session-3.0-gemfire-${ProjectUtils.getGemFireBaseVersion(property("gemfireVersion").toString())}")
  longName.set(project.description)
  description.set(project.description)
}

dependencies {
  api(project(":spring-gemfire-starter"))

  api(libs.spring.session.gemfire)

}