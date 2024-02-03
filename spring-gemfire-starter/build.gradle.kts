import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.StorageOptions

buildscript {
  dependencies {
    classpath("com.google.cloud:google-cloud-storage:2.30.1")
  }
}

plugins {
  id("project-base")
  id("gemfire-repo-artifact-publishing")
}

description = "Spring Boot Starter for VMware GemFire"

val gemfireVersion = ProjectUtils.getGemFireBaseVersion(property("gemfireVersion").toString())

publishingDetails {
  artifactName.set("spring-boot-3.0-gemfire-$gemfireVersion")
  longName.set(project.description)
  description.set(project.description)
}

dependencies {
  api(platform(bom.spring.framework.bom))
  api(platform(bom.spring.boot.dependencies.bom))
  api(platform(bom.spring.security.bom))
  api(platform(bom.testcontainers.dependencies.bom))
  api("org.springframework.boot:spring-boot-starter")

  api(project(":spring-gemfire"))
  api(project(":spring-gemfire-autoconfigure"))

  runtimeOnly(libs.spring.shell)
}

val exportedProjects = arrayOf(
  ":spring-gemfire",
  ":spring-gemfire-actuator",
  ":spring-gemfire-actuator-autoconfigure",
  ":spring-gemfire-autoconfigure",
  ":spring-gemfire-extensions",
  ":spring-gemfire-jetty11",
  ":spring-gemfire-starter",
  ":spring-gemfire-starter-logging",
  ":spring-gemfire-starter-session",
  ":spring-gemfire-starter-test"
)


tasks {
  register("combinedJavadoc", Javadoc::class.java) {
    source(exportedProjects.map { project(it).sourceSets["main"].allJava })
    title = "Spring Boot 3.0 for VMware GemFire $gemfireVersion Java API Reference"
    classpath = files(exportedProjects.map { project(it).sourceSets["main"].compileClasspath })
    setDestinationDir(file("${layout.buildDirectory}/docs/javadoc"))
  }
  register("combinedJavadocJar", Jar::class.java) {
    dependsOn(named("combinedJavadoc"))
    archiveClassifier = "javadoc"
    from(named<Javadoc>("combinedJavadoc").get().destinationDir)
  }
  register("copyJavadocsToBucket") {
    val javadocJarTask = named("combinedJavadocJar")
    dependsOn(javadocJarTask)
    doLast {
      val storage = StorageOptions.newBuilder().setProjectId(project.properties["docsGCSProject"].toString())
        .build().getService()
      val javadocJarFiles = javadocJarTask.get().outputs.files
      val blobId = BlobId.of(
        project.properties["docsGCSBucket"].toString(),
        "${publishingDetails.artifactName.get()}/${project.version}/${javadocJarFiles.singleFile.name}"
      )
      val blobInfo = BlobInfo.newBuilder(blobId).build()
      storage.createFrom(blobInfo, javadocJarFiles.singleFile.toPath())
    }
  }
}
