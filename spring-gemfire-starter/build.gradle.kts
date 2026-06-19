/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.StorageOptions
import java.io.FileInputStream
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage

buildscript {
  dependencies {
    classpath("com.google.cloud:google-cloud-storage:2.30.2")
  }
}

plugins {
  id("project-base")
  id("gemfire-repo-artifact-publishing")
  id("gemfire-artifactory")
}

description = "Spring Boot Starter for VMware GemFire"

val baseGemFireVersion: String by project
val baseSpringVersion: String by project

publishingDetails {
  artifactName.set("spring-boot-$baseSpringVersion-gemfire-$baseGemFireVersion")
  longName.set(project.description)
  description.set(project.description)
}

configurations{
  create("combinedJavaDocsConfig") {
    isCanBeConsumed = false
    isCanBeResolved = true
    extendsFrom(configurations.getByName("compileClasspath"))
    attributes {
      attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage.JAVA_API))
      attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named(Category.LIBRARY))
      attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.objects.named(LibraryElements.JAR))
    }
  }
}

val exportedProjects = arrayOf(
  ":spring-gemfire",
  ":spring-gemfire-actuator",
  ":spring-gemfire-actuator-autoconfigure",
  ":spring-gemfire-autoconfigure",
  ":spring-gemfire-extensions",
  ":spring-gemfire-starter",
  ":spring-gemfire-starter-logging",
  ":spring-gemfire-starter-session",
  ":spring-gemfire-starter-test"
)

dependencies {
  implementation(platform(bom.testcontainers.dependencies.bom))
  api("org.springframework.boot:spring-boot-starter")

  api(project(":spring-gemfire"))
  api(project(":spring-gemfire-autoconfigure"))

  runtimeOnly(libs.spring.shell)

  exportedProjects
    .filter { it != project.path }
    .forEach { add("combinedJavaDocsConfig", project(it)) }
}

tasks {
  register<Javadoc>("combinedJavadoc") {
    source(exportedProjects.map { project(it).sourceSets["main"].allJava })
    title = "Spring Boot $baseSpringVersion for VMware GemFire $baseGemFireVersion Java API Reference"
    classpath = configurations["combinedJavaDocsConfig"]
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
      val storage =
        StorageOptions.newBuilder().setProjectId(project.properties["docsGCSProject"].toString()).setCredentials(
          GoogleCredentials.fromStream(FileInputStream(project.properties["docsGCSProjectCredentials"].toString()))).build().getService()
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
