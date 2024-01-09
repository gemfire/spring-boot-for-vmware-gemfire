plugins {
    id("project-base")
    id("gemfire-repo-artifact-publishing")
}

description = "Spring Boot Actuator Starter for VMware GemFire"

publishingDetails {
    artifactName.set("spring-boot-actuator-3.0-gemfire-${ProjectUtils.getGemFireBaseVersion(property("gemfireVersion").toString())}")
    longName.set(project.description)
    description.set(project.description)
}

dependencies {
    api(project(":spring-gemfire-starter"))
    api(project(":spring-gemfire-actuator-autoconfigure"))
}
