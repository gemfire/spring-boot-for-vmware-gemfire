---
title: Spring Boot for VMware GemFire Reference Guide
---

<!-- 
 Copyright (c) VMware, Inc. 2022. All rights reserved.
 Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 agreements. See the NOTICE file distributed with this work for additional information regarding
 copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance with the License. You may obtain a
 copy of the License at
 
 http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software distributed under the License
 is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 or implied. See the License for the specific language governing permissions and limitations under
 the License.
-->
Version 1.27

Spring Boot for VMware GemFire provides the convenience of Spring Boot’s
*convention over configuration* approach by using *auto-configuration*
with Spring Framework’s powerful abstractions and highly consistent
programming model to simplify the development of VMware GemFire
applications in a Spring context.


Secondarily, Spring Boot for VMware GemFire provides developers with a
consistent experience whether building and running Spring Boot/VMware
GemFire applications locally or in a managed environment, such as with
[VMware Tanzu Application Service](https://tanzu.vmware.com/tanzu)
(TAS).


This project is a continuation and a logical extension to Spring Data
for VMware GemFire’s [Annotation-based configuration
model](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config),
and the goals set forth in that model: *To enable application developers
to **get up and running** as **quickly**, **reliably**, and as
**easily** as possible*. In fact, Spring Boot for VMware GemFire builds
on this very
[foundation](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config)
cemented in Spring Data for VMware GemFire since the Spring Data Kay
(2.0) Release Train.


## Introduction

Spring Boot for VMware GemFire automatically applies
*auto-configuration* to several key application concerns (*use cases*)
including, but not limited to:


- *Look-Aside, \[Async\] Inline, Near* and *Multi-Site Caching*, by
  using VMware GemFire as a caching provider in [Spring’s Cache
  Abstraction](https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache).
  For more information, see
  [Caching with VMware GemFire](./caching.html).

- [*System of Record*
  (SOR)](https://en.wikipedia.org/wiki/System_of_record), persisting
  application state in VMware GemFire by using [Working with Spring Data
  Repositories](https://docs.spring.io/spring-data/commons/docs/current/reference/html/#repositories).
  For more information, see
  [Spring Data Repositories](./repositories.html).

- *Transactions*, managing application state consistently with [Spring
  Transaction
  Management](https://docs.spring.io/spring/docs/current/spring-framework-reference/data-access.html#transaction)
  with support for both [Local
  Cache](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#apis:transaction-management)
  and [Global
  JTA](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#apis:global-transaction-management)
  Transactions.

- *Distributed Computations*, run with VMware GemFire’s [Function
  Execution](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-function_exec-chapter_overview.html)
  framework and conveniently implemented and executed with [POJO-based,
  annotation support for
  Functions](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#function-annotations).
  For more information, see [Function Implementations & Executions](./functions.md).

- *Continuous Queries*, expressing interests in a stream of events and
  letting applications react to and process changes to data in near
  real-time with VMware GemFire’s [Continuous Query
  (CQ)](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-continuous_querying-chapter_overview.html).
  Listeners/Handlers are defined as simple Message-Driven POJOs (MDP)
  with Spring’s [Message Listener
  Container](https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#jms-mdp),
  which has been
  [extended](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#apis:continuous-query)
  with its
  [configurable](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config-continuous-queries)
  CQ support. For more information, see
  [Continuous Query](./continuous-query.md).

- *Data Serialization* using VMware GemFire
  [PDX](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-data_serialization-gemfire_pdx_serialization.html)
  with first-class
  [configuration](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config-pdx)
  and
  [support](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#mapping.pdx-serializer).
  For more information, see
  [Data Serialization with PDX](./data-serialization.md).

- *Data Initialization* to quickly load (import) data to hydrate the
  cache during application startup or write (export) data on application
  shutdown to move data between environments (for example, TEST to DEV).
  For more information, see [Using Data](./data.md).

- *Actuator*, to gain insight into the runtime behavior and operation of
  your cache, whether a client or a peer. For more information, see
  [Spring Boot Actuator](./actuator.md).

- *Logging*, to quickly and conveniently enable or adjust VMware GemFire
  log levels in your Spring Boot application to gain insight into the
  runtime operations of the application as they occur. For more
  information, see [Logging](./logging.md).

- *Security*, including
  [Authentication](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-managing-security-authentication_overview.html)
  &
  [Authorization](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-managing-security-authorization_overview.html),
  and Transport Layer Security (TLS) with VMware GemFire [Secure Socket
  Layer
  (SSL)](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-managing-security-ssl_overview.html).
  Once more, Spring Data for VMware GemFire includes first-class support
  for configuring
  [Auth](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config-security)
  and
  [SSL](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config-ssl).
  For more information, see [Security](./security.md).

- *HTTP Session state management*, by including Spring Session for
  VMware GemFire on your application’s classpath. For more information,
  see [Spring Session](./session.md).

- *Testing*. Whether you write Unit or Integration Tests for Apache
  Geode in a Spring context, SBDG covers all your testing needs with the
  help of
  [STDG](https://github.com/spring-projects/spring-test-data-geode#spring-test-framework-for-apache-geode—​vmware-tanzu-gemfire).


While Spring Data for VMware GemFire offers a simple, consistent,
convenient and declarative approach to configure all these powerful
VMware GemFire features, Spring Boot for VMware GemFire makes it even
easier to do, as we will explore throughout this reference
documentation.


### Goals

While the SBDG project has many goals and objectives, the primary goals
of this project center around three key principles:


1.  From ***Open Source*** (Apache Geode) to ***Commercial*** (VMware
    GemFire).

2.  From ***Non-Managed*** (self-managed/self-hosted or on-premise
    installations) to ***Managed*** (VMware Tanzu GemFire for VMs,
    VMware Tanzu GemFire for K8S) environments.

3.  With **little to no code or configuration changes** necessary.


It is also possible to go in the reverse direction, from *Managed* back
to a *Non-Managed* environment and even from *Commercial* back to the
*Open Source* offering, again, with *little to no code or configuration*
changes.

SBDG’s promise is to deliver on these principles as
much as is technically possible and as is technically allowed by VMware
GemFire.

## <a id='getting-started'></a>Getting Started

To be immediately productive and as effective as possible when you use
Spring Boot for VMware GemFire, it helps to understand the foundation on
which this project is built.


The story begins with the Spring Framework and the [core technologies
and
concepts](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#spring-core)
built into the Spring container.


Then our journey continues with the extensions built into Spring Data
for VMware GemFire to simplify the development of VMware GemFire
applications in a Spring context, using Spring’s powerful abstractions
and highly consistent programming model. This part of the story was
greatly enhanced in Spring Data Kay, with the [Annotation-based
configuration
model](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config).
Though this new configuration approach uses annotations and provides
sensible defaults, its use is also very explicit and assumes nothing. If
any part of the configuration is ambiguous, SDG will fail fast. SDG
gives you choice, so you still must tell SDG what you want.


Next, we venture into Spring Boot and all of its wonderfully expressive
and highly opinionated “convention over configuration” approach for
getting the most out of your Spring VMware GemFire applications in the
easiest, quickest, and most reliable way possible. We accomplish this by
combining Spring Data for VMware GemFire’s [annotation-based
configuration](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config)
with Spring Boot’s
[auto-configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/#using-boot-auto-configuration)
to get you up and running even faster and more reliably so that you are
productive from the start.


As a result, it would be pertinent to begin your Spring Boot education
with [Spring Boot’s
documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/#getting-started).


Finally, we arrive at Spring Boot for VMware GemFire (SBDG).

See the corresponding Sample
[Guide](guides/getting-started.html) and
[Code](https://github.com/spring-projects/spring-boot-data-geode/tree/1.7.4/spring-geode-samples/intro/getting-started) to see Spring Boot for
VMware GemFire in action.

## Using Spring Boot for VMware GemFire

To use Spring Boot for VMware GemFire, declare the
`spring-geode-starter` on your Spring Boot application classpath:



Example 1. Maven




``` prettyprint
<dependencies>
    <dependency>
        <groupId>org.springframework.geode</groupId>
        <artifactId>spring-geode-starter</artifactId>
        <version>1.27</version>
    </dependency>
</dependencies>
```



Gradle


``` prettyprint
dependencies {
    compile 'org.springframework.geode:spring-geode-starter:1.27'
}
```


### Maven BOM


If you anticipate using more than one Spring Boot for VMware GemFire
(SBDG) module in your Spring Boot application, you can also declare the
new `org.springframework.geode:spring-geode-bom` Maven BOM in your
application Maven POM.


Your application use case may require more than one module if (for
example, you need (HTTP) Session state management and replication with,
for example, `spring-geode-starter-session`), if you need to enable
Spring Boot Actuator endpoints for VMware GemFire (for example,
`spring-geode-starter-actuator`), or if you need assistance writing
complex Unit and (Distributed) Integration Tests with Spring Test for
Apache Geode (STDG) (for example, `spring-geode-starter-test`).


You can declare and use any one of the SBDG modules:


- `spring-geode-starter`

- `spring-geode-starter-actuator`

- `spring-geode-starter-logging`

- `spring-geode-starter-session`

- `spring-geode-starter-test`


When more than one SBDG module is in use, it makes sense to declare the
`spring-geode-bom` to manage all the dependencies such that the versions
and transitive dependencies necessarily align properly.


A Spring Boot application Maven POM that declares the `spring-geode-bom`
along with two or more module dependencies might appear as follows:



Example 2. Spring Boot application Maven POM




``` prettyprint
<project xmlns="http://maven.apache.org/POM/4.0.0">

    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.4</version>
    </parent>

    <artifactId>my-spring-boot-application</artifactId>

    <properties>
        <spring-geode.version>1.27</spring-geode.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.geode</groupId>
                <artifactId>spring-geode-bom</artifactId>
                <version>${spring-geode.version}</version>
                <scope>import</scope>
                <type>pom</type>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.springframework.geode</groupId>
            <artifactId>spring-geode-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.geode</groupId>
            <artifactId>spring-geode-starter-session</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.geode</groupId>
            <artifactId>spring-geode-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

</project>
```


Notice that:


- The Spring Boot application Maven POM (`pom.xml`) contains a
  `<dependencyManagement>` section that declares the
  `org.springframework.geode:spring-geode-bom`.

- None of the `spring-geode-starter[-xyz]` dependencies explicitly
  specify a `<version>`. The version is managed by the
  `spring-geode.version` property, making it easy to switch between
  versions of SBDG as needed and use it in all the SBDG modules declared
  and used in your application Maven POM.


If you change the version of SBDG, be sure to change the
`org.springframework.boot:spring-boot-starter-parent` POM version to
match. SBDG is always one `major` version behind but matches on `minor`
version and `patch` version (and `version qualifier` — `SNAPSHOT`, `M#`,
`RC#`, or `RELEASE`, if applicable).


For example, SBDG `1.4.0` is based on Spring Boot `2.4.0`. SBDG
`1.3.5.RELEASE` is based on Spring Boot `2.3.5.RELEASE`, and so on. It
is important that the versions align.

All of these concerns are handled for you by going
to <a href="https://start.spring.io">start.spring.io</a> and adding the
“_Spring for VMware GemFire_” dependency to a project. For convenience,
you can click this <a
href="https://start.spring.io/#!platformVersion=%7Bspring-boot-version%7D&amp;dependencies=geode">link</a>
to get started.

### Gradle Dependency Management


Using Gradle is similar to using Maven.


Again, if you declare and use more than one SBDG module in your Spring
Boot application (for example, the `spring-geode-starter` along with the
`spring-geode-starter-session` dependency), declaring the
`spring-geode-bom` inside your application Gradle build file helps.


Your application Gradle build file configuration (roughly) appears as
follows:



Example 3. Spring Boot application Gradle build file




``` prettyprint
plugins {
  id 'org.springframework.boot' version '2.7.4'
  id 'io.spring.dependency-management' version '1.0.10.RELEASE'
  id 'java'
}

// ...

ext {
  set('springGeodeVersion', "1.27")
}

dependencies {
  implementation 'org.springframework.geode:spring-geode-starter'
  implementation 'org.springframework.geode:spring-geode-starter-actuator'
  testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

dependencyManagement {
  imports {
    mavenBom "org.springframework.geode:spring-geode-bom:${springGeodeVersion}"
  }
}
```


A combination of the [Spring Boot Gradle
Plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/using-spring-boot.html#using-boot-gradle)
and the [Spring Dependency Management Gradle
Plugin](https://github.com/spring-gradle-plugins/dependency-management-plugin)
manages the application dependencies for you.


In a nutshell, the *Spring Dependency Management Gradle Plugin* provides
dependency management capabilities for Gradle, much like Maven. The
*Spring Boot Gradle Plugin* defines a curated and tested set of versions
for many third party Java libraries. Together, they make adding
dependencies and managing (compatible) versions easier.


Again, you do not need to explicitly declare the version when adding a
dependency, including a new SBDG module dependency (for example,
`spring-geode-starter-session`), since this has already been determined
for you. You can declare the dependency as follows:





``` prettyprint
implementation 'org.springframework.geode:spring-geode-starter-session'
```


The version of SBDG is controlled by the extension property
(`springGeodeVersion`) in the application Gradle build file.


To use a different version of SBDG, set the `springGeodeVersion`
property to the desired version (for example, `1.3.5.RELEASE`). Remember
to be sure that the version of Spring Boot matches.


SBDG is always one `major` version behind but matches on `minor` version
and `patch` version (and `version qualifier`, such as `SNAPSHOT`, `M#`,
`RC#`, or `RELEASE`, if applicable). For example, SBDG `1.4.0` is based
on Spring Boot `2.4.0`, SBDG `1.3.5.RELEASE` is based on Spring Boot
`2.3.5.RELEASE`, and so on. It is important that the versions align.


All of these concerns are handled for you by going
to <a href="https://start.spring.io">start.spring.io</a> and adding the
“_Spring for VMware GemFire_” dependency to a project. For convenience,
you can click this <a
href="https://start.spring.io/#!platformVersion=%7Bspring-boot-version%7D&amp;dependencies=geode">link</a>
to get started.

## Primary Dependency Versions

Spring Boot for VMware GemFire 1.27 builds and depends on the following
versions of the base projects listed below:

<table class="tableblock frame-all grid-all stretch">
<caption>Table 1. Dependencies &amp; Versions</caption>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Version</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>Java (JRE)</p></td>
<td class="tableblock halign-left valign-top"><p>17</p></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>VMware GemFire</p></td>
<td
class="tableblock halign-left valign-top"><p>1.14.4</p></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>Spring
Framework</p></td>
<td
class="tableblock halign-left valign-top"><p>5.3.23</p></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>Spring Boot</p></td>
<td
class="tableblock halign-left valign-top"><p>2.7.4</p></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>Spring Data for VMware
GemFire</p></td>
<td
class="tableblock halign-left valign-top"><p>2.7.3</p></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>Spring Session for
VMware GemFire</p></td>
<td
class="tableblock halign-left valign-top"><p>2.7.1</p></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>Spring Test for VMware
GemFire</p></td>
<td
class="tableblock halign-left valign-top"><p>0.3.1-RAJ</p></td>
</tr>
</tbody>
</table>

Table 1. Dependencies & Versions


It is essential that the versions of all the dependencies listed in the
table above align accordingly. If the dependency versions are
misaligned, then functionality could be missing, or certain functions
could behave unpredictably from its specified contract.


Please follow dependency versions listed in the table above and use it
as a guide when setting up your Spring Boot projects using VMware
GemFire.


Again, the best way to setup your Spring Boot projects is by first,
declaring the `spring-boot-starter-parent` Maven POM as the parent POM
in your project POM:



Example 4. Spring Boot application Maven POM parent




``` prettyprint
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.4</version>
</parent>
```


Or, when using Grade:



Example 5. Spring Boot application Gradle build file Gradle Plugins
required for dependency management




``` prettyprint
plugins {
  id 'org.springframework.boot' version '2.7.4'
  id 'io.spring.dependency-management' version '1.0.10.RELEASE'
  id 'java'
}
```


And then, use the Spring Boot for VMware GemFire, `spring-geode-bom`.
For example, with Maven:



Example 6. Spring Boot application using the Spring Boot for VMware
GemFire, `spring-geode-bom` BOM in Maven




``` prettyprint
<properties>
    <spring-geode.version>1.27</spring-geode.version>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.geode</groupId>
            <artifactId>spring-geode-bom</artifactId>
            <version>$1.27</version>
            <scope>import</scope>
            <type>pom</type>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>org.springframework.geode</groupId>
        <artifactId>spring-geode-starter</artifactId>
    </dependency>
</dependencies>
```


Or, with Gradle:



Example 7. Spring Boot application using the Spring Boot for VMware
GemFire, `spring-geode-bom` BOM in Gradle




``` prettyprint
ext {
    set('springGeodeVersion', "1.27")
}

dependencies {
    implementation 'org.springframework.geode:spring-geode-starter'
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.geode:spring-geode-bom:${springGeodeVersion}"
    }
}
```


All of this is made simple by going to
[start.spring.io](https://start.spring.io) and creating a Spring Boot
`2.7.4` project using VMware GemFire.


### Overriding Dependency Versions


While Spring Boot for VMware GemFire requires baseline versions of the
[primary dependencies](#sbdg-dependency-versions) listed above, it is
possible, using Spring Boot’s dependency management capabilities, to
override the versions of 3rd-party Java libraries and dependencies
managed by Spring Boot itself.


When your Spring Boot application Maven POM inherits from the
`org.springframework.boot:spring-boot-starter-parent`, or alternatively,
applies the Spring Dependency Management Gradle Plugin
(`io.spring.dependency-management`) along with the Spring Boot Gradle
Plugin (`org.springframework.boot`) in your Spring Boot application
Gradle build file, then you automatically enable the dependency
management capabilities provided by Spring Boot for all 3rd-party Java
libraries and dependencies curated and managed by Spring Boot.


Spring Boot’s dependency management harmonizes all 3rd-party Java
libraries and dependencies that you are likely to use in your Spring
Boot applications. All these dependencies have been tested and proven to
work with the version of Spring Boot and other Spring dependencies (e.g.
Spring Data, Spring Security) you may be using in your Spring Boot
applications.


Still, there may be times when you want, or even need to override the
version of some 3rd-party Java libraries used by your Spring Boot
applications, that are specifically managed by Spring Boot. In cases
where you know that using a different version of a managed dependency is
safe to do so, then you have a few options for how to override the
dependency version:


<p class="note">
<strong>Note:</strong>
Use caution when overriding dependencies since they
may not be compatible with other dependencies managed by Spring Boot for
which you may have declared on your application classpath, for example,
by adding a starter. It is common for multiple Java libraries to share
the same transitive dependencies but use different versions of the Java
library (e.g. logging). This will often lead to Exceptions thrown at
runtime due to API differences. Keep in mind that Java resolves classes
on the classpath from the first class definition that is found in the
order that JARs or paths have been defined on the classpath. Finally,
Spring does not support dependency versions that have been overridden
and do not match the versions declared and managed by Spring Boot. See
<a
href="https://docs.spring.io/spring-boot/docs/current/reference/html/#appendix.dependency-versions.coordinates">documentation</a>.
</p>

- [Version Property
  Override](#sbdg-dependency-version-overrides-property)

- [Override with Dependency
  Management](#sbdg-dependency-version-overrides-dependencymanagement)


Refer to Spring Boot’s documentation on
<a
href="https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.build-systems.dependency-management">Dependency
Management</a> for more details.


#### Version Property Override


Perhaps the easiest option to change the version of a Spring Boot
managed dependency is to set the version property used by Spring Boot to
control the dependency’s version to the desired Java library version.


For example, if you want to use a different version of **Log4j** than
what is currently set and determined by Spring Boot, then you would do:



Maven dependency version property override


``` prettyprint
<properties>
  <log4j2.version>2.17.2</log4j2.version>
</properties>
```



Gradle dependency version property override


    ext['log4j2.version'] = '2.17.2'


<p class="note"><strong>Note:</strong>
The Log4j version number used in the Maven and
Gradle examples shown above is arbitrary. You must set the
<code>log4j2.version</code> property to a valid Log4j version that would
be resolvable by Maven or Gradle when given the fully qualified
artifact: <code>org.apache.logging.log4j:log4j:2.17.2</code>.
</p>

The version property name must precisely match the version property
declared in the `spring-boot-dependencies` Maven POM.


See Spring Boot’s documentation on [version
properties](https://docs.spring.io/spring-boot/docs/current/reference/html/#appendix.dependency-versions.properties).


Additional details can be found in the Spring Boot Maven Plugin
[documentation](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/#using.parent-pom)
as well as the Spring Boot Gradle Plugin
[documentation](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#managing-dependencies).


#### Override with Dependency Management


This option is not specific to Spring in general, or Spring Boot in
particular, but applies to Maven and Gradle, which both have intrinsic
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
transitive dependency, for whatever reason, perhaps because you are
using the `opentest4j` API directly in your application tests, then you
could add dependency management in either Maven or Gradle to control the
`opentest4j` dependency version.


<p class="note"><strong>Note:</strong>
The <code>opentest4j</code> dependency is pulled in
by JUnit and is not a dependency that Spring Boot specifically manages.
Of course, Maven or Gradle’s dependency management capabilities can be
used to override dependencies that are managed by Spring Boot as
well.
</p>

Using the `opentest4j` dependency as an example, you can override the
dependency version by doing the following:



#### Maven dependency version override


``` prettyprint
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



#### Gradle dependency version override


``` prettyprint
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


### Excluding Dependencies


Sometimes, though rarely, it may be necessary to exclude a (transitive)
dependency included by a Spring Boot, or Spring Boot for Apache Geode,
starter.


Perhaps a transitive dependency, such as Apache Log4j or Jackson, is
pulled in by an underlying data store dependency, such as Apache Geode
or Redis, when using a starter (for example:
`spring-boot-starter-data-redis`, or `spring-geode-starter`), that could
cause a conflict with your Spring Boot application. Or, maybe the
transitive dependency currently contains a serious bug or CVE.


Either way, you have concluded that it is safe to exclude this
(transitive) dependency without adversely affecting the runtime behavior
and correctness of your Spring Boot application.


<p class="important"><strong>Important:</strong>
You should be absolutely certain that removing the
(transitive) dependency, rather than <a
href="#sbdg-dependency-version-overrides">overridding</a> the
(transitive) dependency is the correct course of action.
</p>

For example, when you include the `spring-geode-starter` (the base
starter of Spring Boot for Apache Geode), you notice that Apache Lucene
is transitively included by `org.apache.geode:geode-lucene`:



#### Analyzing Dependencies using Gradle


``` prettyprint
$ gradlew :spring-geode-starter:dependencies

...
compileClasspath - Compile classpath for source set 'main'.
+--- org.springframework.boot:spring-boot-starter -> 3.0.0-M5
|    +--- org.springframework.boot:spring-boot:3.0.0-M5
|    |    +--- org.springframework:spring-core:6.0.0-M6
...
+--- project :spring-geode
|    +--- project :apache-geode-extensions
|    |    +--- org.apache.geode:geode-core:1.15.0
|    |    |    +--- antlr:antlr:2.7.7
...
|    |    +--- org.apache.geode:geode-lucene:1.15.0
|    |    |    +--- org.apache.geode:geode-core:1.15.0 (*)
|    |    |    \--- org.apache.lucene:lucene-core:6.6.6
...
|    |    \--- org.apache.geode:geode-wan:1.15.0
...
```



#### Analyzing Dependencies using Maven


``` prettyprint
$ mvn dependency:tree

...
[INFO] --- maven-dependency-plugin:3.3.0:tree (default-cli) @ spring-geode-app ---
[INFO] org.example.app:spring-geode-app:jar:0.0.1-SNAPSHOT
[INFO] +- org.springframework.geode:spring-geode-starter:jar:1.7.4:compile
[INFO] |  +- org.springframework.boot:spring-boot-starter:jar:2.7.1:compile
[INFO] |  |  +- org.springframework.boot:spring-boot:jar:2.7.1:compile
...
[INFO] |  +- org.springframework.geode:spring-geode:jar:1.7.4:compile
[INFO] |  |  +- org.springframework.data:spring-data-geode:jar:2.7.1:compile
[INFO] |  |  |  +- org.apache.geode:geode-core:jar:1.14.4:compile
...
[INFO] |  |  |  +- org.apache.geode:geode-lucene:jar:1.14.4:compile
[INFO] |  |  |  |  +- org.apache.lucene:lucene-core:jar:6.6.6:compile
[INFO] |  |  |  |  +- org.apache.geode:geode-gfsh:jar:1.14.4:runtime
[INFO] |  |  |  |  +- org.apache.lucene:lucene-analyzers-common:jar:6.6.6:runtime
[INFO] |  |  |  |  +- org.apache.lucene:lucene-queryparser:jar:6.6.6:runtime
[INFO] |  |  |  |  |  \- org.apache.lucene:lucene-queries:jar:6.6.6:runtime
[INFO] |  |  |  |  +- mx4j:mx4j:jar:3.0.2:runtime
[INFO] |  |  |  |  \- org.apache.lucene:lucene-analyzers-phonetic:jar:6.6.6:runtime
[INFO] |  |  |  |     \- commons-codec:commons-codec:jar:1.15:runtime
...
[INFO] |  |  |  +- org.apache.geode:geode-wan:jar:1.14.4:compile
```


However, you do not have any "search" use cases in your Spring Boot
application that would require Apache Geode’s integration with Apache
Lucene.


Using your build tool, such as Gradle or Maven, you can add an exclusion
on the `org.apache.geode:geode-lucene` transitive dependency pulled in
and included by Spring Boot for Apache Geode’s `spring-geode-starter`,
like so:


#### Declaring Exclusions with Gradle


``` prettyprint
implementation("org.springframework.geode:spring-geode-starter:1.27") {
  exclude group: "org.apache.geode", module: "geode-lucene"
}
```



Declaring Exclusions with Maven


``` prettyprint
<?xml version="1.0" encoding="UTF-8"?>
<pom>
  <dependencies>
    <dependency>
      <groupId>org.springframework.geode</groupId>
      <artifactId>spring-geode-starter</artifactId>
      <version>1.27</version>
      <exclusions>
        <exclusion>
          <groupId>org.apache.geode</groupId>
          <artifactId>geode-lucene</artifactId>
        </exclusion>
      </exclusions>
    </dependency>
  </dependencies>
</pom>
```


After the appropriate exclusion is declared, the resulting dependencies
(or dependency tree) should look like the following:



#### Analyzing Dependencies using Gradle after Exclusions


``` prettyprint
$ gradlew :spring-geode-starter:dependencies

...
compileClasspath - Compile classpath for source set 'main'.
+--- org.springframework.boot:spring-boot-starter -> 3.0.0-M5
|    +--- org.springframework.boot:spring-boot:3.0.0-M5
|    |    +--- org.springframework:spring-core:6.0.0-M6
...
+--- project :spring-geode
|    +--- project :apache-geode-extensions
|    |    +--- org.apache.geode:geode-core:1.15.0
|    |    |    +--- antlr:antlr:2.7.7
...
|    |    \--- org.apache.geode:geode-wan:1.15.0
...
```



#### Analyzing Dependencies using Maven


``` prettyprint
$ mvn dependency:tree

...
[INFO] --- maven-dependency-plugin:3.3.0:tree (default-cli) @ spring-geode-app ---
[INFO] org.example.app:spring-geode-app:jar:0.0.1-SNAPSHOT
[INFO] +- org.springframework.geode:spring-geode-starter:jar:1.7.4:compile
[INFO] |  +- org.springframework.boot:spring-boot-starter:jar:2.7.1:compile
[INFO] |  |  +- org.springframework.boot:spring-boot:jar:2.7.1:compile
...
[INFO] |  +- org.springframework.geode:spring-geode:jar:1.7.4:compile
[INFO] |  |  +- org.springframework.data:spring-data-geode:jar:2.7.1:compile
[INFO] |  |  |  +- org.apache.geode:geode-core:jar:1.14.4:compile
...
[INFO] |  |  |  +- org.apache.geode:geode-wan:jar:1.14.4:compile
```


Again, we cannot overstate the importance of being careful when declaring exclusions.


Please refer to the appropriate documentation in <a
href="https://maven.apache.org/guides/introduction/introduction-to-optional-and-excludes-dependencies.html">Maven</a>
and <a
href="https://docs.gradle.org/current/userguide/dependency_downgrade_and_exclude.html">Gradle</a>
to declare exclusions.</td>


Version 1.27  
Last updated 2022-10-10 14:40:33 -0700
