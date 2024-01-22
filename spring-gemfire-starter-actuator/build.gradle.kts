plugins {
    id("project-base")
    id("gemfire-repo-artifact-publishing")
}

description = "Spring Boot Actuator Starter for VMware GemFire"

publishingDetails {
    artifactName.set("spring-boot-actuator-2.7-gemfire-${ProjectUtils.getGemFireBaseVersion(property("gemfireVersion").toString())}")
    longName.set(project.description)
    description.set(project.description)
}

dependencies {
    api(platform(bom.spring.framework.bom))
    api(platform(bom.spring.boot.dependencies.bom))
    api(platform(bom.spring.security.bom))
    api(platform(bom.testcontainers.dependencies.bom))
    api(project(":spring-gemfire-starter"))
    api(project(":spring-gemfire-actuator-autoconfigure"))
}
