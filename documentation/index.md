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


This project is a continuation and a logical extension to Spring Data for VMware GemFire’s Annotation-based configuration model, and the goals set forth in that model: 

*To enable application developers to **get up and running** as **quickly**, **reliably**, and as **easily** as possible*. 

In fact, Spring Boot for GemFire automatically applies *auto-configuration* to several key *use cases* including, but not limited to:

- *Cache-Aside, \[Async\] Inline, Near* and *Multi-Site Caching*
- [*System of Record* (SOR)](https://en.wikipedia.org/wiki/System_of_record)
- *Transactions*
- *Distributed Computations*
- *Continuous Queries*
- *Data Serialization* 
- *Data Initialization* 
- *Spring Boot Actuator*
- *HTTP Session state management*


This initial set of documentation is meant to get users up and running quickly with Spring Boot for VMware GemFire.  For more in-depth discussion of features please refer to the [Spring Boot Data Geode documentation](https://docs.spring.io/spring-boot-data-geode-build/current/reference/html5/).





