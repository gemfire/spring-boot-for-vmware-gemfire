plugins {
    id("project-base")
}

description = "Smoke Tests asserting the proper function of a Spring Boot configured and bootstrapped Apache Geode Locator application."

dependencies {

    implementation(platform(bom.spring.framework.bom))
    implementation(platform(bom.spring.boot.dependencies.bom))
    implementation(platform(bom.spring.security.bom))
    implementation(platform(bom.testcontainers.dependencies.bom))
    implementation("org.assertj:assertj-core")

    implementation(project(":spring-gemfire-starter"))
    implementation(project(":spring-gemfire-starter-logging"))

    compileOnly(libs.gemfire.core)

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    testImplementation(project(":spring-gemfire-starter-test"))
    testImplementation(libs.gemfire.core)
    testImplementation(libs.gemfire.testcontainers)

}

tasks.getByName<Test>("test") {
//    dependsOn(tasks.named<Jar>("testJar"))
    forkEvery = 1
    maxParallelForks = 4
    val springTestGemfireDockerImage: String by project
    systemProperty("spring.test.gemfire.docker.image", springTestGemfireDockerImage)
//    systemProperty("TEST_JAR_PATH", tasks.getByName<Jar>("testJar").outputs.files.singleFile.absolutePath)
}
