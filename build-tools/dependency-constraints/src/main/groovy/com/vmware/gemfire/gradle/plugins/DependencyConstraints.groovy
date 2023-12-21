/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.vmware.gemfire.gradle.plugins

import org.gradle.api.Project

class DependencyConstraints {
/** By necessity, the version of those plugins used in the build-scripts are defined in the
 * buildscript {} configuration in the root project's build.gradle. */
  static Map<String, String> disparateDependencies = initExternalDependencies()

  static String get(String name) {
    return disparateDependencies.get(name)
  }


  static private Map<String, String> initExternalDependencies() {
    Map<String, String> depVersionMapping = new HashMap<>()

    depVersionMapping.put("antlrVersion", "2.7.7")
    depVersionMapping.put("findbugsVersion", "3.0.2")
    depVersionMapping.put("multithreadedtcVersion", "1.01")
    depVersionMapping.put("springBootVersion", "3.0.13")
    depVersionMapping.put("springShellVersion", "1.2.0.RELEASE")
    depVersionMapping.put("springFrameworkVersion", "6.0.15")
    depVersionMapping.put("springSecurityVersion", "6.0.8")
    depVersionMapping.put("testcontainersVersion", "1.17.6")

    return depVersionMapping
  }

  void apply(Project project) {
    project.dependencies {

      api(platform(group: 'org.springframework.boot', name: 'spring-boot-dependencies', version: get('springBootVersion')))
      api(platform(group: 'org.testcontainers', name: 'testcontainers-bom', version: get('testcontainersVersion')))

      constraints {
        api(group: 'org.springframework', name: 'spring-context-support', version: get("springFrameworkVersion"))
        api(group: 'org.springframework', name: 'spring-jcl', version: get("springFrameworkVersion"))
        api(group: 'org.springframework', name: 'spring-web', version: get("springFrameworkVersion"))
        api(group: 'org.springframework', name: 'spring-test', version: get("springFrameworkVersion"))

        api(group: 'antlr', name: 'antlr', version: get('antlrVersion'))
        api(group: 'com.google.code.findbugs', name: 'jsr305', version: get('findbugsVersion'))
        api(group: 'edu.umd.cs.mtc', name: 'multithreadedtc', version: get('multithreadedtcVersion'))
        api(group: 'org.springframework.shell', name: 'spring-shell', version: get('springShellVersion'))
        api(group: 'org.springframework.boot', name: 'spring-boot-configuration-processor', version: get('springBootVersion'))
        api(group: 'org.springframework.boot', name: 'spring-boot-autoconfigure-processor', version: get('springBootVersion'))
        api(group: 'org.springframework.security', name: 'spring-security-core', version: get('springSecurityVersion'))
        api(group: 'org.springframework.security', name: 'spring-security-web', version: get('springSecurityVersion'))
        api(group: 'org.testcontainers', name: 'testcontainers', version: get('testcontainersVersion'))
        api(group: 'com.vmware.gemfire', name: 'gemfire-web', version: get("gemfireVersion"))
      }

    }
  }
}
