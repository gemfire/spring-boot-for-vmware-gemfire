plugins {
  id("project-base")
  id("gemfire-repo-artifact-publishing")
}

description = "VMware GemFire Integration with Eclipse Jetty declared and managed by Spring Boot"

publishingDetails {
  artifactName.set("spring-boot-3.0-gemfire-jetty11-${ProjectUtils.getGemFireBaseVersion(property("gemfireVersion").toString())}")
  longName.set(project.description)
  description.set(project.description)
}

dependencies {
  implementation(platform(bom.spring.framework.bom))
  implementation(platform(bom.spring.boot.dependencies.bom))
  implementation(platform(bom.spring.security.bom))
  implementation(platform(bom.testcontainers.dependencies.bom))
  compileOnly(libs.gemfire.core)

  api(project(":spring-gemfire-extensions"))

  api("org.springframework.boot:spring-boot-starter-jetty") {
    exclude(group = "org.eclipse.jetty.websocket", module = "websocket-jakarta-server")
    exclude(group = "org.eclipse.jetty.websocket", module = "websocket-jetty-server")
  }

  implementation("org.apache.commons:commons-lang3")
  implementation(libs.tomcat.jakartaee.migration)
  implementation("org.eclipse.jetty:jetty-server")
  implementation("org.slf4j:slf4j-api")

  runtimeOnly("org.eclipse.jetty:apache-jsp")

}
