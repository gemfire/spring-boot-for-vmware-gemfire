plugins {
    id("project-base")
    id("gemfire-repo-artifact-publishing")
}

description = "VMware GemFire Integration with Eclipse Jetty declared and managed by Spring Boot"

publishingDetails {
    artifactName.set("spring-boot-3.2-gemfire-jetty12-${ProjectUtils.getGemFireBaseVersion(property("gemfireVersion").toString())}")
    longName.set(project.description)
    description.set(project.description)
}

dependencies {
    compileOnly(libs.gemfire.core)

    api(project(":spring-gemfire-extensions"))

    api("org.springframework.boot:spring-boot-starter-jetty") {
        exclude(group = "org.eclipse.jetty.websocket", module = "websocket-jakarta-server")
        exclude(group = "org.eclipse.jetty.websocket", module = "websocket-jetty-server")
    }

    implementation("org.apache.commons:commons-lang3")
    implementation("org.apache.tomcat:jakartaee-migration:1.0.7")
    implementation("org.eclipse.jetty:jetty-server")
    implementation("org.slf4j:slf4j-api")

    runtimeOnly("org.eclipse.jetty.ee10:jetty-ee10-apache-jsp")
}
