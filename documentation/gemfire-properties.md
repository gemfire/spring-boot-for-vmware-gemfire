---
Title: Using [vmware-gemfire-short-name] Properties
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



As of [spring-boot-gemfire-name] 1.3, you can declare
[vmware-gemfire-name] properties from `gemfire.properties` in Spring
Boot `application.properties`.





<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon">
Tip
</td>
<td class="content">See the
https://docs.vmware.com/en/VMware-GemFire/9.15/gf/reference-topics-gemfire_properties.html[User Guide]
for a complete list of valid [vmware-gemfire-name] properties.</td>
</tr>
</tbody>
</table>





Note that you can declare only valid [vmware-gemfire-short-name] properties in
`gemfire.properties` or, alternatively, `gfsecurity.properties`.





The following example shows how to declare properties in
`gemfire.properties`:







Example 1. Valid `gemfire.properties`









``` highlight
# [vmware-gemfire-short-name] Properties in gemfire.properties

name=ExampleCacheName
log-level=TRACE
enable-time-statistics=true
durable-client-id=123
# ...
```











All of the properties declared in the preceding example correspond to
valid [vmware-gemfire-short-name] properties. It is illegal to declare properties in
`gemfire.properties` that are not valid [vmware-gemfire-short-name] properties, even if those
properties are prefixed with a different qualifier (such as `spring.*`).
[vmware-gemfire-name] throws an `IllegalArgumentException` for invalid
properties.





Consider the following `gemfire.properties` file with an
`invalid-property`:







Example 2. Invalid `gemfire.properties`









``` highlight
# [vmware-gemfire-short-name] Properties in gemfire.properties

name=ExampleCacheName
invalid-property=TEST
```











[vmware-gemfire-name] throws an `IllegalArgumentException`:







Example 3. `IllegalArgumentException` thrown by [vmware-gemfire-name]
for Invalid Property (Full Text Omitted)









``` highlight
Exception in thread "main" java.lang.IllegalArgumentException: Unknown configuration attribute name invalid-property.
Valid attribute names are: ack-severe-alert-threshold ack-wait-threshold archive-disk-space-limit ...
    at o.a.g.internal.AbstractConfig.checkAttributeName(AbstractConfig.java:333)
    at o.a.g.distributed.internal.AbstractDistributionConfig.checkAttributeName(AbstractDistributionConfig.java:725)
    at o.a.g.distributed.internal.AbstractDistributionConfig.getAttributeType(AbstractDistributionConfig.java:887)
    at o.a.g.internal.AbstractConfig.setAttribute(AbstractConfig.java:222)
    at o.a.g.distributed.internal.DistributionConfigImpl.initialize(DistributionConfigImpl.java:1632)
    at o.a.g.distributed.internal.DistributionConfigImpl.<init>(DistributionConfigImpl.java:994)
    at o.a.g.distributed.internal.DistributionConfigImpl.<init>(DistributionConfigImpl.java:903)
    at o.a.g.distributed.internal.ConnectionConfigImpl.lambda$new$2(ConnectionConfigImpl.java:37)
    at o.a.g.distributed.internal.ConnectionConfigImpl.convert(ConnectionConfigImpl.java:73)
    at o.a.g.distributed.internal.ConnectionConfigImpl.<init>(ConnectionConfigImpl.java:36)
    at o.a.g.distributed.internal.InternalDistributedSystem$Builder.build(InternalDistributedSystem.java:3004)
    at o.a.g.distributed.internal.InternalDistributedSystem.connectInternal(InternalDistributedSystem.java:269)
    at o.a.g.cache.client.ClientCacheFactory.connectInternalDistributedSystem(ClientCacheFactory.java:280)
    at o.a.g.cache.client.ClientCacheFactory.basicCreate(ClientCacheFactory.java:250)
    at o.a.g.cache.client.ClientCacheFactory.create(ClientCacheFactory.java:216)
    at org.example.app.ApacheGeodeClientCacheApplication.main(...)
```











It is inconvenient to have to separate [vmware-gemfire-name] properties
from other application properties, or to have to declare only
[vmware-gemfire-name] properties in a `gemfire.properties` file and
application properties in a separate properties file, such as Spring
Boot `application.properties`.





Additionally, because of [vmware-gemfire-name]'s constraint on
properties, you cannot use the full power of Spring Boot when you
compose `application.properties`.





You can include certain properties based on a Spring profile while
excluding other properties. This is essential when properties are
environment- or context-specific.





[spring-data-gemfire-name] already provides a wide range of
properties mapping to [vmware-gemfire-name] properties.





For example, the [spring-data-gemfire-name] `spring.data.gemfire.locators` property maps to the
`gemfire.locators` property (`locators` in `gemfire.properties`) from
[vmware-gemfire-name]. Likewise, there are a full set of [spring-data-gemfire-name] properties
that map to the corresponding [vmware-gemfire-name] properties in the
[Appendix](#geode-configuration-metadata-springdata).





You can express the [vmware-gemfire-short-name] properties shown earlier as [spring-data-gemfire-name] properties in
Spring Boot `application.properties`, as follows:







Example 4. Configuring [vmware-gemfire-short-name] Properties using [spring-data-gemfire-name] Properties









``` highlight
# [spring-data-gemfire-name] properties in application.properties

spring.data.gemfire.name=ExampleCacheName
spring.data.gemfire.cache.log-level=TRACE
spring.data.gemfire.cache.client.durable-client-id=123
spring.data.gemfire.stats.enable-time-statistics=true
# ...
```











However, some [vmware-gemfire-name] properties have no equivalent [spring-data-gemfire-name]
property, such as `gemfire.groups` (`groups` in `gemfire.properties`).
This is partly due to the fact that many [vmware-gemfire-name]
properties are applicable only when configured on the server (such as
`groups` or `enforce-unique-host`).





<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon">
Tip
</td>
<td class="content">See the <code>@EnableGemFireProperties</code>
annotation
([spring-data-gemfire-javadoc]/org/springframework/data/gemfire/config/annotation/EnableGemFireProperties.html[attributes])
from [spring-data-gemfire-name] for a complete list of [vmware-gemfire-name] properties with
no corresponding [spring-data-gemfire-name] property.</td>
</tr>
</tbody>
</table>





Furthermore, many of the [spring-data-gemfire-name] properties also correspond to API calls.





For example,
[spring-data-gemfire-javadoc]/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#keepAlive\[`spring.data.gemfire.cache.client.keep-alive`\]
translates to the
https://gemfire.docs.pivotal.io/apidocs/tgf-915/index.html?org/apache/geode/cache/client/ClientCache.html#close-boolean\[`ClientCache.close(boolean keepAlive)`\]
API call.





Still, it would be convenient to be able to declare application and
[vmware-gemfire-name] properties together, in a single properties file,
such as Spring Boot `application.properties`. After all, it is not
uncommon to declare JDBC Connection properties in a Spring Boot
`application.properties` file.





Therefore, as of [spring-boot-gemfire-name] 1.3, you can now declare [vmware-gemfire-name]
properties in Spring Boot `application.properties` directly, as follows:







Example 5. [vmware-gemfire-short-name] Properties declared in Spring Boot
`application.properties`









``` highlight
# Spring Boot application.properties

server.port=8181
spring.application.name=ExampleApp
gemfire.durable-client-id=123
gemfire.enable-time-statistics=true
```











This is convenient and ideal for several reasons:





- If you already have a large number of [vmware-gemfire-name]
  properties declared as `gemfire.` properties (either in
  `gemfire.properties` or `gfsecurity.properties`) or declared on the
  Java command-line as JVM System properties (such as
  `-Dgemfire.name=ExampleCacheName`), you can reuse these property
  declarations.

- If you are unfamiliar with [spring-data-gemfire-name]’s corresponding properties, you can
  declare [vmware-gemfire-short-name] properties instead.

- You can take advantage of Spring features, such as Spring profiles.

- You can also use property placeholders with [vmware-gemfire-short-name] properties (such as
  `gemfire.log-level=${external.log-level.property}`).





<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon">
Tip
</td>
<td class="content">We encourage you to use the [spring-data-gemfire-name] properties, which
cover more than [vmware-gemfire-name] properties.</td>
</tr>
</tbody>
</table>





However, [spring-boot-gemfire-name] requires that the [vmware-gemfire-short-name] property must have the `gemfire.`
prefix in Spring Boot `application.properties`. This indicates that the
property belongs to [vmware-gemfire-name]. Without the `gemfire.`
prefix, the property is not appropriately applied to the
[vmware-gemfire-name] cache instance.





It would be ambiguous if your Spring Boot applications integrated with
several technologies, including [vmware-gemfire-name], and they too had
matching properties, such as `bind-address` or `log-file`.





[spring-boot-gemfire-name] makes a best attempt to log warnings when a [vmware-gemfire-short-name] property is
invalid or is not set. For example, the following [vmware-gemfire-short-name] property would
result in logging a warning:







Example 6. Invalid [vmware-gemfire-name] Property









``` highlight
# Spring Boot application.properties

spring.application.name=ExampleApp
gemfire.non-existing-property=TEST
```











The resulting warning in the log would read:







Example 7. Invalid [vmware-gemfire-short-name] Property Warning Message









``` highlight
[gemfire.non-existing-property] is not a valid [vmware-gemfire-name] property
```











If a [vmware-gemfire-short-name] Property is not properly set, the following warning is
logged:







Example 8. Invalide [vmware-gemfire-short-name] Property Value Warning Message









``` highlight
[vmware-gemfire-name] Property [gemfire.security-manager] was not set
```











With regards to the third point mentioned earlier, you can now compose
and declare [vmware-gemfire-short-name] properties based on a context (such as your
application environment) using Spring profiles.





For example, you might start with a base set of properties in Spring
Boot `application.properties`:







Example 9. Base Properties









``` highlight
server.port=8181
spring.application.name=ExampleApp
gemfire.durable-client-id=123
gemfire.enable-time-statistics=false
```











Then you can vary the properties by environment, as the next two
listings (for QA and production) show:







Example 10. QA Properties









``` highlight
# Spring Boot application-qa.properties

server.port=9191
spring.application.name=TestApp
gemfire.enable-time-statistics=true
gemfire.enable-network-partition-detection=true
gemfire.groups=QA
# ...
```













Example 11. Production Properties









``` highlight
# Spring Boot application-prod.properties

server.port=80
spring.application.name=ProductionApp
gemfire.archive-disk-space-limit=1000
gemfire.archive-file-size-limit=50
gemfire.enforce-unique-host=true
gemfire.groups=PROD
# ...
```











You can then apply the appropriate set of properties by configuring the
Spring profile with `-Dspring.profiles.active=prod`. You can also enable
more than one profile at a time with
`-Dspring.profiles.active=profile1,profile2,…​,profileN`





If both `spring.data.gemfire.*` properties and the matching
[vmware-gemfire-name] properties are declared in Spring Boot
`application.properties`, the [spring-data-gemfire-name] properties take precedence.





If a property is specified more than once, as would potentially be the
case when composing multiple Spring Boot `application.properties` files
and you enable more than one Spring profile at time, the last property
declaration wins. In the example shown earlier, the value for
`gemfire.groups` would be `PROD` when `-Dspring.profiles.active=qa,prod`
is configured.





Consider the following Spring Boot `application.properties`:







Example 12. Property Precedence









``` highlight
# Spring Boot application.properties

gemfire.durable-client-id=123
spring.data.gemfire.cache.client.durable-client-id=987
```











The `durable-client-id` is `987`. It does not matter which order the [spring-data-gemfire-name]
or [vmware-gemfire-name] properties are declared in Spring Boot
`application.properties`. The matching [spring-data-gemfire-name] property overrides the
[vmware-gemfire-name] property when duplicates are found.





Finally, you cannot refer to [vmware-gemfire-short-name] properties declared in Spring Boot
`application.properties` with the [spring-boot-gemfire-name] `GemFireProperties` class (see
the
{spring-boot-gemfire-javadoc}/org/springframework/geode/boot/autoconfigure/configuration/GemFireProperties.html\[Javadoc\]).





Consider the following example:







Example 13. [vmware-gemfire-short-name] Properties declared in Spring Boot
`application.properties`









``` highlight
# Spring Boot application.properties

gemfire.name=TestCacheName
```











Given the preceding property, the following assertion holds:











``` highlight
import org.springframework.geode.boot.autoconfigure.configuration.GemFireProperties;

@RunWith(SpringRunner.class)
@SpringBootTest
class GemFirePropertiesTestSuite {

    @Autowired
    private GemFireProperties gemfireProperties;

    @Test
    public void gemfirePropertiesTestCase() {
        assertThat(this.gemfireProperties.getCache().getName()).isNotEqualTo("TestCacheName");
    }
}
```











<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon">
Tip
</td>
<td class="content">You can declare <code>application.properties</code>
in the <code>@SpringBootTest</code> annotation. For example, you could
have declared <code>gemfire.name</code> in the annotation by setting
<code>@SpringBootTest(properties = { "gemfire.name=TestCacheName" })</code>
for testing purposes instead of declaring the property in a separate
Spring Boot <code>application.properties</code> file.</td>
</tr>
</tbody>
</table>





Only `spring.data.gemfire.*` prefixed properties are mapped to the [spring-boot-gemfire-name]
`GemFireProperties` class hierarchy.





<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon">
Tip
</td>
<td class="content">Prefer [spring-data-gemfire-name] properties over [vmware-gemfire-short-name] properties. See the
[spring-data-gemfire-name] properties reference in the <a
href="#geode-configuration-metadata-springdata">Appendix</a>.</td>
</tr>
</tbody>
</table>









<div id="footer">

<div id="footer-text">




