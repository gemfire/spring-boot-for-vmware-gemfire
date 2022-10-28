---
Title: Externalized Configuration
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

Like Spring Boot itself (see
[Spring Boot’s documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)), Spring Boot for VMware GemFire (SBDG)
supports externalized configuration.

By externalized configuration, we mean configuration metadata stored in
Spring Boot
[`application.properties`](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-application-property-files).
You can even separate concerns by addressing each concern in an
individual properties file. Optionally, you could also enable any given
property file for only a specific
[profile](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-profile-specific-properties).

You can do many other powerful things, such as (but not limited to)
using
[placeholders](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-placeholders-in-properties)
in properties,
[encrypting](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-encrypting-properties)
properties, and so on. In this section, we focus particularly on
[type safety](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-typesafe-configuration-properties).

Like Spring Boot, Spring Boot for VMware GemFire provides a
hierarchy of classes that captures configuration for several
VMware GemFire features in an associated
`@ConfigurationProperties` annotated class. Again, the configuration
metadata is specified as well-known, documented properties in one or
more Spring Boot `application.properties` files.

For instance, a Spring Boot, VMware GemFire `ClientCache`
application might be configured as follows:

Example 1. Spring Boot `application.properties` containing Spring Data
properties for VMware GemFire

``` highlight
# Spring Boot application.properties used to configure VMware GemFire

spring.data.gemfire.name=MySpringBootApacheGeodeApplication

# Configure general cache properties
spring.data.gemfire.cache.copy-on-read=true
spring.data.gemfire.cache.log-level=debug

# Configure ClientCache specific properties
spring.data.gemfire.cache.client.durable-client-id=123
spring.data.gemfire.cache.client.keep-alive=true

# Configure a log file
spring.data.gemfire.logging.log-file=/path/to/geode.log

# Configure the client's connection Pool to the servers in the cluster
spring.data.gemfire.pool.locators=10.105.120.16[11235],boombox[10334]
```

You can use many other properties to externalize the configuration of
your Spring Boot, VMware GemFire applications. See the
[Javadoc](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/package-frame.html)
for specific configuration properties. Specifically, review the
`enabling` annotation attributes.

You may sometimes require access to the configuration metadata
(specified in properties) in your Spring Boot applications themselves,
perhaps to further inspect or act on a particular configuration setting.
You can access any property by using Spring’s
[`Environment`](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/core/env/Environment.html)
abstraction:

Example 2. Using the Spring `Environment`

``` highlight
@Configuration
class GeodeConfiguration {

    void readConfigurationFromEnvironment(Environment environment) {
        boolean copyOnRead = environment.getProperty("spring.data.gemfire.cache.copy-on-read",
            Boolean.TYPE, false);
    }
}
```

While using `Environment` is a nice approach, you might need access to
additional properties or want to access the property values in a
type-safe manner. Therefore, you can now, thanks to SBDG’s
auto-configured configuration processor, access the configuration
metadata by using `@ConfigurationProperties` classes.

To add to the preceding example, you can now do the following:

Example 3. Using `GemFireProperties`

``` highlight
@Component
class MyApplicationComponent {

    @Autowired
    private GemFireProperties gemfireProperties;

    public void someMethodUsingGemFireProperties() {

        boolean copyOnRead = this.gemfireProperties.getCache().isCopyOnRead();

        // do something with `copyOnRead`
    }
}
```

Given a handle to
[`GemFireProperties`](https://docs.spring.io/spring-boot-data-geode-build/current/api/org/springframework/geode/boot/autoconfigure/configuration/GemFireProperties.html),
you can access any of the configuration properties that are used to
configure VMware GemFire in a Spring context. You need only
autowire an instance of `GemFireProperties` into your application
component.

See the complete reference for the
[SBDG `@ConfigurationProperties` classes and supporting classes](https://docs.spring.io/spring-boot-data-geode-build/current/api/org/springframework/geode/boot/autoconfigure/configuration/package-frame.html).

### Externalized Configuration of Spring Session

You can access the externalized configuration of Spring Session when you
use VMware GemFire as your (HTTP) session state caching
provider.

In this case, you need only acquire a reference to an instance of the
[`SpringSessionProperties`](https://docs.spring.io/spring-boot-data-geode-build/current/api/org/springframework/geode/boot/autoconfigure/configuration/SpringSessionProperties.html) class.

As shown earlier in this chapter, you can specify Spring Session for
VMware GemFire (SSDG) properties as follows:

Example 4. Spring Boot `application.properties` for Spring Session using
VMware GemFire as the (HTTP) session state caching provider

``` highlight
# Spring Boot application.properties used to configure VMware GemFire as a (HTTP) session state caching provider
# in Spring Session

spring.session.data.gemfire.session.expiration.max-inactive-interval-seconds=300
spring.session.data.gemfire.session.region.name=UserSessions
```

Then, in your application, you can do something similar to the following
example:

Example 5. Using `SpringSessionProperties`

``` highlight
@Component
class MyApplicationComponent {

    @Autowired
    private SpringSessionProperties springSessionProperties;

    public void someMethodUsingSpringSessionProperties() {

        String sessionRegionName = this.springSessionProperties
            .getSession().getRegion().getName();

        // do something with `sessionRegionName`
    }
}
```

