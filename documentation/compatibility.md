---
title: Compatibility and Versions
---

Spring Boot for VMware GemFire provides the convenience of Spring Boot's convention over configuration approach by using auto-configuration with Spring Framework's powerful abstractions and highly consistent programming model to simplify the development of VMware GemFire applications.

## Compatibility

<table>
   <tr>
      <th style="text-align: left">Spring Boot for VMware GemFire Artifact</th>
      <th style="text-align: left">Versions</th>
      <th style="text-align: left">Compatible GemFire Versions</th>
      <th style="text-align: left">Compatible Spring Boot Version</th>
   </tr>
   <tr>
      <td>spring-boot-2.6-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>9.15.x</td>
      <td>2.6.x</td>
   </tr>
      <td>spring-boot-2.7-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>9.15.x</td>
      <td>2.7.x</td>
   <tr>
      <td>spring-boot-3.0-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>9.15.x</td>
      <td>3.0.x</td>
   </tr>
   <tr>
      <td>spring-boot-2.7-gemfire-10.0</td>
      <td>1.0.0</td>
      <td>10.0.x</td>
      <td>2.7.x</td>
   </tr>
   <tr>
      <td>spring-boot-3.0-gemfire-10.0</td>
      <td>1.0.0</td>
      <td>10.0.x</td>
      <td>3.0.x</td>
   </tr>
</table>


## Modules
Your application may require more than one module if, for example, you may need (HTTP) Session state management, or you may need to enable Spring Boot Actuator endpoints for [vmware-gemfire-name].

You can declare and use any one of the [spring-boot-gemfire-name] modules (in addition to the Spring Boot for GemFire dependency).  

### Spring Boot Actuator
<table>
   <tr>
      <th style="text-align: left">Module</th>
      <th style="text-align: left">Versions</th>
      <th style="text-align: left">Compatible Spring Boot for GemFire Artifact</th>
   </tr>
   <tr>
      <td>spring-boot-actuator-2.6-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>spring-boot-2.6-gemfire-9.15</td>
   </tr>
      <td>spring-boot-actuator-2.7-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>spring-boot-2.7-gemfire-9.15</td>
   <tr>
      <td>spring-boot-actuator-3.0-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>spring-boot-3.0-gemfire-9.15</td>
   </tr>
   <tr>
      <td>spring-boot-actuator-2.7-gemfire-10.0</td>
      <td>1.0.0</td>
      <td>spring-boot-2.7-gemfire-10.0</td>
   </tr>
   <tr>
      <td>spring-boot-actuator-3.0-gemfire-10.0</td>
      <td>1.0.0</td>
      <td>spring-boot-3.0-gemfire-10.0</td>
   </tr>
</table>

### Spring Boot Logging
<table>
   <tr>
      <th style="text-align: left">Module</th>
      <th style="text-align: left">Versions</th>
      <th style="text-align: left">Compatible Spring Boot for GemFire Artifact</th>
   </tr>
   <tr>
      <td>spring-boot-logging-2.6-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>spring-boot-2.6-gemfire-9.15</td>
   </tr>
      <td>spring-boot-logging-2.7-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>spring-boot-2.7-gemfire-9.15</td>
   <tr>
      <td>spring-boot-logging-3.0-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>spring-boot-3.0-gemfire-9.15</td>
   </tr>
   <tr>
      <td>spring-boot-logging-2.7-gemfire-10.0</td>
      <td>1.0.0</td>
      <td>spring-boot-2.7-gemfire-10.0</td>
   </tr>
   <tr>
      <td>spring-boot-logging-3.0-gemfire-10.0</td>
      <td>1.0.0</td>
      <td>spring-boot-3.0-gemfire-10.0</td>
   </tr>
</table>

### Spring Session 
<table>
   <tr>
      <th style="text-align: left">Module</th>
      <th style="text-align: left">Versions</th>
      <th style="text-align: left">Compatible Spring Boot for GemFire Artifact</th>
   </tr>
   <tr>
      <td>spring-boot-session-2.6-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>spring-boot-2.6-gemfire-9.15</td>
   </tr>
      <td>spring-boot-session-2.7-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>spring-boot-2.7-gemfire-9.15</td>
   <tr>
      <td>spring-boot-session-3.0-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>spring-boot-3.0-gemfire-9.15</td>
   </tr>
   <tr>
      <td>spring-boot-session-2.7-gemfire-10.0</td>
      <td>1.0.0</td>
      <td>spring-boot-2.7-gemfire-10.0</td>
   </tr>
   <tr>
      <td>spring-boot-session-3.0-gemfire-10.0</td>
      <td>1.0.0</td>
      <td>spring-boot-3.0-gemfire-10.0</td>
   </tr>
</table>


## Overriding Dependency Versions

While Spring Boot for GemFire requires baseline versions of primary dependencies, it is
possible, using Spring Boot’s dependency management capabilities, to
override the versions of 3rd-party Java libraries and dependencies
managed by Spring Boot itself.

All these dependencies have been tested and proven to work with the version of Spring Boot and other Spring dependencies (e.g.
Spring Data, Spring Security) you may be using in your Spring Boot applications.

There may be times when you need to override the version of some 3rd-party Java libraries used by your Spring Boot
applications, that are specifically managed by Spring Boot. In cases where you know that using a different version of a managed dependency is
safe to do so, then you have a few options for how to override the
dependency version:

- [Version Property Override](#version-property-override)
- [Override with Dependency Management](#override-with-dependency-management)

Refer to Spring Boot’s documentation on [Dependency Management](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.build-systems.dependency-management) for more details.

> Note: Use caution when overriding dependencies since they may not be compatible with other dependencies managed by Spring Boot. It is common for multiple Java libraries to share
the same transitive dependencies but use different versions of the Java library (e.g. logging). This will often lead to Exceptions thrown at runtime due to API differences. Keep in mind that Java resolves classes on the classpath from the first class definition that is found in the order that JARs or paths have been defined on the classpath. Finally, Spring does not support dependency versions that have been overridden and do not match the versions declared and managed by Spring Boot. See the [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/#appendix.dependency-versions.coordinates) for more information.


#### Version Property Override


The simplest option to change the version of a Spring Boot managed dependency is to set the version property to the desired version.

For example, if you want to use a different version of **Log4j**, in this example *2.17.2*:

**Maven** dependency version property override

``` xml
<properties>
  <log4j2.version>2.17.2</log4j2.version>
</properties>
```

**Gradle** dependency version property override

```groovy
    ext['log4j2.version'] = '2.17.2'
```

The version property name must precisely match the version property
declared in the `spring-boot-dependencies` Maven POM.

See Spring Boot’s documentation on [version properties](https://docs.spring.io/spring-boot/docs/current/reference/html/dependency-versions.html#appendix.dependency-versions.properties).

Additional details can be found in the Spring Boot Maven Plugin [documentation](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/#using.parent-pom)
as well as the Spring Boot Gradle Plugin [documentation](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#managing-dependencies).


#### Override with Dependency Management

This option is not specific to Spring Boot in particular, but applies to Maven and Gradle, which both have intrinsic
dependency management features and capabilities.

This approach is useful to not only control the versions of the
dependencies managed by Spring Boot directly, but also control the
versions of dependencies that may be transitively pulled in by the
dependencies that are managed by Spring Boot. Additionally, this
approach is more universal since it is handled by Maven or Gradle
itself.


For example, when you declare the
`org.springframework.boot:spring-boot-starter-test` dependency in your
Spring Boot application Maven POM or Gradle build file for testing
purposes, you will see a dependency tree similar to:


`$gradlew dependencies` OR `$mvn dependency:tree`

``` prettyprint
...
[INFO] +- org.springframework.boot:spring-boot-starter-test:jar:2.6.4:test
[INFO] |  +- org.springframework.boot:spring-boot-test:jar:2.6.4:test
[INFO] |  +- org.springframework.boot:spring-boot-test-autoconfigure:jar:2.6.4:test
[INFO] |  +- com.jayway.jsonpath:json-path:jar:2.6.0:test
[INFO] |  |  +- net.minidev:json-smart:jar:2.4.8:test
[INFO] |  |  |  \- net.minidev:accessors-smart:jar:2.4.8:test
[INFO] |  |  |     \- org.ow2.asm:asm:jar:9.1:test
[INFO] |  |  \- org.slf4j:slf4j-api:jar:1.7.36:compile
[INFO] |  +- jakarta.xml.bind:jakarta.xml.bind-api:jar:2.3.3:test
[INFO] |  |  \- jakarta.activation:jakarta.activation-api:jar:1.2.2:test
[INFO] |  +- org.assertj:assertj-core:jar:3.21.0:compile
[INFO] |  +- org.hamcrest:hamcrest:jar:2.2:compile
[INFO] |  +- org.junit.jupiter:junit-jupiter:jar:5.8.2:test
[INFO] |  |  +- org.junit.jupiter:junit-jupiter-api:jar:5.8.2:test
[INFO] |  |  |  +- org.opentest4j:opentest4j:jar:1.2.0:test
[INFO] |  |  |  +- org.junit.platform:junit-platform-commons:jar:1.8.2:test
[INFO] |  |  |  \- org.apiguardian:apiguardian-api:jar:1.1.2:test
[INFO] |  |  +- org.junit.jupiter:junit-jupiter-params:jar:5.8.2:test
[INFO] |  |  \- org.junit.jupiter:junit-jupiter-engine:jar:5.8.2:test
[INFO] |  |     \- org.junit.platform:junit-platform-engine:jar:1.8.2:test
...
```

If you wanted to override and control the version of the `opentest4j`
transitive dependency then you could add dependency management in either Maven or Gradle to control the
`opentest4j` dependency version. 

Using the `opentest4j` dependency as an example, you can override the dependency version by doing the following:

**Maven** dependency version override

```xml
<project>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.opentest4j</groupId>
                <artifactId>opentest4j</artifactId>
                <version>1.0.0</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

**Gradle** dependency version override

```groovy
plugins {
    id 'org.springframework.boot' version '2.7.4'
}

apply plugin:  'io.spring.dependency-management'

dependencyManagement {
  dependencies {
    dependency 'org.opentest4j:openttest4j:1.0.0'
  }
}
```


After applying Maven or Gradle dependency management configuration, you
will then see:


`$gradlew dependencies` OR `$mvn dependency:tree`


``` prettyprint
...
[INFO] +- org.springframework.boot:spring-boot-starter-test:jar:2.6.4:test
...
[INFO] |  |  |  +- org.opentest4j:opentest4j:jar:1.0.0:test
...
```


For more details on Maven dependency management, refer to the
[documentation](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html).


For more details on Gradle dependency management, please refer to the
[documentation](https://docs.gradle.org/current/userguide/core_dependency_management.html)


## Excluding Dependencies

Sometimes, though rarely, it may be necessary to exclude a transitive dependency included by Spring Boot or Spring Boot for GemFire.

> Important: You should be absolutely certain that removing the transitive dependency, rather than overridding the
transitive dependency is the correct course of action.
