/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package io.spring.gradle.convention

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.PluginManager
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test

/**
 * Spring Sample Gradle Plugin used to build Samples as a Java Web Application (WAR archive).
 *
 * @author Rob Winch
 * @author John Blum
 * @see org.gradle.api.Project
 * @see org.gradle.api.Task
 */
class SpringSampleWarPlugin extends SpringSamplePlugin {

    @Override
    void applyAdditionalPlugins(Project project) {

        super.applyAdditionalPlugins(project)

        PluginManager pluginManager = project.getPluginManager()

        pluginManager.apply("war")
        pluginManager.apply("org.gretty")

        def resolvedLogbackConfigFile = new File(project.extensions.getByType(SourceSetContainer)
            .findByName("integrationTest")?.resources?.filter {
                "logback-test.xml".equals(it.name)
            }?.singleFile?.absolutePath)

        // println "Resolved logbackConfigFile [$resolvedLogbackConfigFile]"

        project.gretty {
            servletContainer = 'tomcat10'
            contextPath = '/'
            consoleLogEnabled = false
            fileLogEnabled = false
            loggingLevel = 'OFF'

            if (resolvedLogbackConfigFile?.isFile()) {
                logbackConfigFile = resolvedLogbackConfigFile
            }
        }

        Task prepareAppServerBeforeIntegrationTests = project.tasks.create('prepareAppServerBeforeIntegrationTests') {
            group = 'Verification'
            description = 'Prepares the Web application server for Integration Testing'
            doFirst {
                project.gretty {
                    httpPort = getRandomPort()
                    httpsPort = getRandomPort()
                }
            }
        }

        // Gretty Gradle Plugin Task.
        project.tasks.matching { it.name == "appBeforeIntegrationTest" }.all { task ->
            task.dependsOn prepareAppServerBeforeIntegrationTests
        }

        project.tasks.withType(Test).all { task ->
            if ("integrationTest".equals(task.name)) {
                applyForIntegrationTest(project, task)
            }
        }
    }

    def static applyForIntegrationTest(Project project, Task integrationTest) {

        project.gretty.integrationTestTask = integrationTest.name

        integrationTest.doFirst {

            def gretty = project.gretty

            boolean isHttps = gretty.httpsEnabled

            Integer httpPort = integrationTest.systemProperties['gretty.httpPort']
            Integer httpsPort = integrationTest.systemProperties['gretty.httpsPort']

            int port = isHttps ? httpsPort : httpPort

            String host = gretty.host ?: 'localhost'
            String contextPath = gretty.contextPath
            String httpBaseUrl = "http://${host}:${httpPort}${contextPath}"
            String httpsBaseUrl = "https://${host}:${httpsPort}${contextPath}"
            String baseUrl = isHttps ? httpsBaseUrl : httpBaseUrl

            integrationTest.systemProperty 'app.port', port
            integrationTest.systemProperty 'app.httpPort', httpPort
            integrationTest.systemProperty 'app.httpsPort', httpsPort
            integrationTest.systemProperty 'app.baseURI', baseUrl
            integrationTest.systemProperty 'app.httpBaseURI', httpBaseUrl
            integrationTest.systemProperty 'app.httpsBaseURI', httpsBaseUrl
            integrationTest.systemProperty 'geb.build.baseUrl', baseUrl
            integrationTest.systemProperty 'geb.build.reportsDir', 'build/geb-reports'
        }
    }

    def static getRandomPort() {
        new ServerSocket(0).withCloseable { it.localPort }
    }
}
