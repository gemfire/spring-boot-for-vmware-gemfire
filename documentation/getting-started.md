---
title: Getting Started
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


The Spring Boot for VMware GemFire dependencies are available from the [Pivotal Commercial Maven Repository](https://commercial-repo.pivotal.io/login/auth). Access to the Pivotal Commercial Maven Repository requires a one-time registration step to create an account.

Spring Boot for VMware GemFire requires users to add the GemFire repository to their projects.

### Add The GemFire Maven Repository 
To add the GemFire Maven Repository to your project:

1. In a browser, navigate to the [Pivotal Commercial Maven Repository](https://commercial-repo.pivotal.io/login/auth).

1. Click the **Create Account** link.

1. Complete the information in the registration page.

1. Click **Register**.

1. After registering, you will receive a confirmation email. Follow the instruction in this email to activate your account.

1. After account activation, log in to the [Pivotal Commercial Maven Repository](https://commercial-repo.pivotal.io/login/auth) to access the configuration information found in [gemfire-release-repo](https://commercial-repo.pivotal.io/repository/gemfire-release-repo).

1. Add the GemFire repository to your project:

  * **Maven**: Add the following block to the `pom.xml` file:

      ```xml
      <repository>
          <id>gemfire-release-repo</id>
          <name>Pivotal GemFire Release Repository</name>
          <url>https://commercial-repo.pivotal.io/data3/gemfire-release-repo/gemfire</url>
      </repository>
      ```

  * **Gradle**: Add the following block to the `repositories` section of the `build.gradle` file:

      ```groovy
      repositories {
          mavenCentral()
          maven {
              credentials {
                  username "$gemfireRepoUsername"
                  password "$gemfireRepoPassword"
              }
              url = uri("https://commercial-repo.pivotal.io/data3/gemfire-release-repo/gemfire")
          }
      }
      ```

1. Add your Pivotal Commercial Maven Repository credentials.

  * **Maven**: Add the following to the `.m2/settings.xml` file. Replace `MY-USERNAME@example` and `MY-DECRYPTED-PASSWORD` with your Pivotal Commercial Maven Repository credentials.

      ```
      <settings>
          <servers>
              <server>
                  <id>gemfire-release-repo</id>
                  <username>MY-USERNAME@example.com</username>
                  <password>MY-DECRYPTED-PASSWORD</password>
              </server>
          </servers>
      </settings>
      ```

  * **Gradle**: Add the following to the local (`.gradle/gradle.properties`) or project `gradle.properties` file. Replace `MY-USERNAME@example` and `MY-DECRYPTED-PASSWORD` with your Pivotal Commercial Maven Repository credentials.

      ```
      gemfireRepoUsername=MY-USERNAME@example.com 
      gemfireRepoPassword=MY-DECRYPTED-PASSWORD
      ```

### Add The Dependencies To The Project

After you have set up the repository and credentials, add the Spring Boot for VMware GemFire dependency to your application. To allow for more flexibility with multiple GemFire version, the Spring Boot for VMware GemFire dependency also requires users to add an explicit dependency on the desired version of GemFire. The required dependencies differ depending on whether users a building a client side application or a server side application.

For client side applications:

* **Maven**: Add the following to your `pom.xml` file. 
  
  - Replace `<version>1.1.1</version>` with the current version of Spring Boot for VMware GemFire available.  
  - Replace `<version>9.15</version>` with the version of VMware GemFire being used for the project.
  
    For example, if using GemFire 9.15 with the current version of Spring Boot for VMware GemFire (1.1.1):
  
    ```xml
          <dependencies>
              <dependency>
                  <groupId>com.vmware.gemfire</groupId>
                  <artifactId>spring-boot-2.7-gemfire-9.15</artifactId>
                  <version>1.1.1</version>
              </dependency>
              <dependency>
                  <groupId>com.vmware.gemfire</groupId>
                  <artifactId>geode-core</artifactId>
                  <version>9.15</version>
              </dependency>
              <!--if using continuous queries-->
              <dependency>
                  <groupId>com.vmware.gemfire</groupId>
                  <artifactId>geode-cq</artifactId>
                  <version>9.15</version>
              </dependency>
          </dependencies>
     ```

* **Gradle**: Add the following to your `build.gradle` file. 

  - Replace `:1.1.1` with the current version of Spring Boot for VMware GemFire available.
  - Replace `:9.15` with the version of VMware GemFire being used for the project.

    For example, if using GemFire 9.15 with the current version of Spring Boot for VMware GemFire (1.1.1):
            
    ```groovy
       dependencies {
                implementation "com.vmware.gemfire:spring-boot-2.7-gemfire-9.15:1.1.1"
                implementation "com.vmware.gemfire:geode-core:9.15"
                // if using continuous queries
                implementation "com.vmware.gemfire:geode-cq:9.15"
            }
    ```

For server applications:

> NOTE: The server dependencies are only required if the user is starting an embedded GemFire server using Spring.

* **Maven**: Add the following to your `pom.xml` file. 
  - Replace `<version>1.1.1</version>` with the current version of Spring Boot for VMware GemFire available.
  - Replace `<version>9.15</version>` with the version of VMware GemFire being used for the project.

  For example, if using GemFire 9.15 with the current version of Spring Boot for VMware GemFire (1.1.1): 
  
    ```xml
          <dependency>
              <groupId>com.vmware.gemfire</groupId>
              <artifactId>spring-boot-2.7-gemfire-9.15</artifactId>
              <version>1.1.1</version>
          </dependency>
          <dependency>
              <groupId>com.vmware.gemfire</groupId>
              <artifactId>geode-server-all</artifactId>
              <version>9.15</version>
              <exclusions>
                  <exclusion>
                      <groupId>com.vmware.gemfire</groupId>
                      <artifactId>geode-log4j</artifactId>
                  </exclusion>
              </exclusions>
          </dependency>
     ```

* **Gradle**: Add the following to your `build.gradle` file. 
  - Replace `:1.1.1` with the current version of Spring Boot for VMware GemFire available.
  - Replace `:9.15` with the version of VMware GemFire being used for the project.

      ```groovy
            dependencies {
                implementation "com.vmware.gemfire:spring-boot-2.7-gemfire-9.15:1.1.1"
                implementation ("com.vmware.gemfire:geode-server-all:9.15"){
                    exclude group: 'com.vmware.gemfire', module: 'geode-log4j'
                }
            }
      ```

1. Your application is now ready to connect with your GemFire instance.


## Modules

Your application may require more than one module if, for example, you need (HTTP) Session state management, or you need to enable Spring Boot Actuator endpoints for GemFire.


You can declare and use any one of the Spring Boot for GemFire modules (in addition to the Spring Boot for GemFire dependency).  You can find a full list of modules and compatible Spring and GemFire versions [here](compatibility.html). 






