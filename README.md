<!--
  ~ Copyright (c) VMware, Inc. 2022. All rights reserved.
  ~ SPDX-License-Identifier: Apache-2.0
  -->
# Spring Boot For VMware GemFire
This project aims to provide an integration of [VMware GemFire](https://tanzu.vmware.com/gemfire) with [Spring Boot](https://spring.io/projects/spring-boot) project.

This project builds on the great work already provided by [Spring Boot for Apache Geode](https://github.com/spring-projects/spring-boot-data-geode).

## Project Structure
The current project structure is different to normal projects, as it does not have a `main` or `develop` branch with meaningful code in it. This is because there is no "common" code branch, as this project is a combination of VMware GemFire and Spring Boot. Thus all code contributions will be found under different branches.
Current branches are:
* [9.15 - Spring Boot 2.6](https://github.com/gemfire/spring-boot-data-for-vmware-gemfire/tree/9.15-SB26)
* [9.15 - Spring Boot 2.7](https://github.com/gemfire/spring-boot-data-for-vmware-gemfire/tree/9.15-SB27)

## Versioning
As this project provides an integration between two great products, a versioning schema that adequately represents both products was chosen. The major.minor version component of the GemFire product will be added to the artifact id. The major.minor component from the Spring Data project will be use as the major.minor component of the Spring Data For VMware GemFire version. The patch version of the Spring Data For VMware GemFire project, will be independent of the two projects and will be incremented each time there is a patch version update in either project or there are bug fixes in the Spring Data For VMware GemFire project. 

## Code of Conduct
This project adheres to the Contributor Covenant [code of conduct](https://github.com/gemfire/spring-boot-data-for-vmware-gemfire/CODE-OF-CONDUCT.md). By participating, you are expected to uphold this code. 

## Reporting Issues
In the event that issue were to be found, please raise a [GitHub issue](https://github.com/gemfire/spring-boot-data-for-vmware-gemfire/issues).
Please provide:
* Project version
* Issue description
* Ways to reproduce issue AND/OR links to a repo which demonstrates the issue raised.
