---
title: Spring Boot for VMware GemFire
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


Spring Boot for VMware GemFire provides the convenience of Spring Boot’s
*convention over configuration* approach by using *auto-configuration*
with Spring Framework’s powerful abstractions and highly consistent
programming model to simplify the development of VMware GemFire
applications in a Spring context.


This project is a continuation and a logical extension to Spring Data
for VMware GemFire’s Annotation-based configuration
model, and the goals set forth in that model: 

*To enable application developers to **get up and running** as **quickly**, **reliably**, and as
**easily** as possible*. In fact, 


## Introduction

Spring Boot for GemFire automatically applies
*auto-configuration* to several key *use cases* including, but not limited to:


- *Cache-Aside, \[Async\] Inline, Near* and *Multi-Site Caching*, by
  using GemFire as a caching provider in Spring’s Cache
  Abstraction.  

- [*System of Record* (SOR)](https://en.wikipedia.org/wiki/System_of_record), persisting application state in GemFire by using [Spring Data
  Repositories](https://docs.spring.io/spring-data/commons/docs/current/reference/html/#repositories).
  
- *Transactions*, managing application state consistently with [Spring Transaction Management](https://docs.spring.io/spring/docs/current/spring-framework-reference/data-access.html#transaction) with support for both Local Cache and Global JTA.

- *Distributed Computations*, run with VMware GemFire’s [Function Execution](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-function_exec-chapter_overview.html)
  framework and conveniently implemented and executed with POJO-based, annotation support for functions.

- *Continuous Queries*, expressing interests in a stream of events and letting applications react to and process changes to data in near
  real-time with VMware GemFire’s [Continuous Query (CQ)](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-continuous_querying-chapter_overview.html?). Listeners/Handlers are defined as simple Message-Driven POJOs (MDP) with Spring’s [Message Listener Container](https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#jms-mdp).
 
- *Data Serialization* using GemFire [PDX](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-data_serialization-gemfire_pdx_serialization.html) with first-class
  configuration.

- *Data Initialization* to quickly load (import) data to hydrate the
  cache during application startup or write (export) data on application
  shutdown to move data between environments (for example, TEST to DEV).
  
- *Actuator*, to gain insight into the runtime behavior and operation of
  your cache, whether a client or a peer. 

- *Logging*, to quickly and conveniently enable or adjust GemFire
  log levels in your Spring Boot application to gain insight into the
  runtime operations of the application as they occur. 

- *Security*, first-class support for configuring **Auth** and **SSL**

- *HTTP Session state management*, by including Spring Session on your application’s classpath. 





