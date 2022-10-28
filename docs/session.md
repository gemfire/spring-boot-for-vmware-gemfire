---
Title: Spring Session
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



This chapter covers auto-configuration of Spring Session for
VMware GemFire to manage (HTTP) session state in a reliable
(consistent), highly available (replicated), and clustered manner.





{spring-session-website}\[Spring Session\] provides an API and several
implementations for managing a user’s session information. It has the
ability to replace the `javax.servlet.http.HttpSession` in an
application container-neutral way and provide session IDs in HTTP
headers to work with RESTful APIs.





Furthermore, Spring Session provides the ability to keep the
`HttpSession` alive even when working with `WebSockets` and reactive
Spring WebFlux `WebSessions`.





A complete discussion of Spring Session is beyond the scope of this
document. You can learn more by reading the
{spring-session-docs}\[docs\] and reviewing the
{spring-session-docs}/#samples\[samples\].





Spring Boot for VMware GemFire provides auto-configuration
support to configure VMware GemFire as the session management
provider and store when {spring-session-data-gemfire-website}\[Spring
Session for VMware GemFire\] is on your Spring Boot
application’s classpath.





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
<td class="content">You can learn more about Spring Session for
VMware GemFire in the
https://docs.spring.io/autorepo/docs/spring-session-data-geode-build/2.7.1/reference/html5[docs].</td>
</tr>
</tbody>
</table>





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
<td class="content">See the corresponding sample <a
href="guides/caching-http-session.html">guide</a> and
 https://github.com/spring-projects/spring-boot-data-geode/tree/1.7.4/spring-geode-samples/intro/getting-started/caching/http-session[code] to see Spring Session
for VMware GemFire in action.</td>
</tr>
</tbody>
</table>





### Configuration



You need do nothing special to use VMware GemFire as a Spring
Session provider implementation, managing the (HTTP) session state of
your Spring Boot application.





To do so, include the appropriate Spring Session dependency on your
Spring Boot application’s classpath:







Example 1. Maven dependency declaration









``` highlight
  <dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-geode</artifactId>
    <version>2.7.1</version>
  </dependency>
```











Alternatively, you may declare the provided
`spring-geode-starter-session` dependency in your Spring Boot
application Maven POM (shown here) or Gradle build file:







Example 2. Maven dependency declaration









``` highlight
  <dependency>
    <groupId>org.springframework.geode</groupId>
    <artifactId>spring-geode-starter-session</artifactId>
    <version>1.27</version>
  </dependency>
```











After declaring the required Spring Session dependency, you can begin
your Spring Boot application as you normally would:







Example 3. Spring Boot Application









``` highlight
@SpringBootApplication
public class MySpringBootApplication {

  public static void main(String[] args) {
    SpringApplication.run(MySpringBootApplication.class, args);
  }

  // ...
}
```











You can then create application-specific Spring Web MVC `Controllers` to
interact with the `HttpSession` as needed by your application:







Example 4. Spring Boot Application `Controller` using `HttpSession`









``` highlight
@Controller
class MyApplicationController {

  @GetRequest("...")
  public String processGet(HttpSession session) {
    // interact with HttpSession
  }
}
```











The `HttpSession` is replaced by a Spring managed `Session` that is
stored in VMware GemFire.







### Custom Configuration



By default, Spring Boot for VMware GemFire (SBDG) applies
reasonable and sensible defaults when configuring VMware GemFire
as the provider in Spring Session.





For instance, by default, SBDG sets the session expiration timeout to 30
minutes. It also uses a `ClientRegionShortcut.PROXY` as the data
management policy for the VMware GemFire client Region that
managing the (HTTP) session state when the Spring Boot application is
using a `ClientCache`, which it does by
[default](#geode-clientcache-applications).





However, what if the defaults are not sufficient for your application
requirements?





In that case, see the next section.





#### Custom Configuration using Properties



Spring Session for VMware GemFire publishes
https://docs.spring.io/autorepo/docs/spring-session-data-geode-build/2.7.1/reference/html5/#httpsession-gemfire-configuration-properties\[well-known
configuration properties\] for each of the various Spring Session
configuration options when you use VMware GemFire as the (HTTP)
session state management provider.





You can specify any of these properties in Spring Boot
`application.properties` to adjust Spring Session’s configuration when
using VMware GemFire.





In addition to the properties provided in and by Spring Session for
VMware GemFire, Spring Boot for VMware GemFire also
recognizes and respects the `spring.session.timeout` property and the
`server.servlet.session.timeout` property, as discussed
https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-session.html\[the Spring Boot
documentation\].





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
<td
class="content"><code>spring.session.data.gemfire.session.expiration.max-inactive-interval-seconds</code>
takes precedence over <code>spring.session.timeout</code>, which takes
precedence over <code>server.servlet.session.timeout</code> when any
combination of these properties have been simultaneously configured in
the Spring <code>Environment</code> of your application.</td>
</tr>
</tbody>
</table>







#### Custom Configuration using a Configurer



Spring Session for VMware GemFire also provides the
https://docs.spring.io/autorepo/docs/spring-session-data-geode-build/2.7.1/api/org/springframework/session/data/gemfire/config/annotation/web/http/support/SpringSessionGemFireConfigurer.html\[`SpringSessionGemFireConfigurer`\]
callback interface, which you can declare in your Spring
`ApplicationContext` to programmatically control the configuration of
Spring Session when you use VMware GemFire.





The `SpringSessionGemFireConfigurer`, when declared in the Spring
`ApplicationContext`, takes precedence over any of the Spring Session
(for VMware GemFire) configuration properties and effectively
overrides them when both are present.





More information on using the `SpringSessionGemFireConfigurer` can be
found in the
https://docs.spring.io/autorepo/docs/spring-session-data-geode-build/2.7.1/reference/html5/#httpsession-gemfire-configuration-configurer\[docs\].









### Disabling Session State Caching



There may be cases where you do not want your Spring Boot application to
manage (HTTP) session state by using VMware GemFire.





In certain cases, you may be using another Spring Session provider
implementation, such as Redis, to cache and manage your Spring Boot
application’s (HTTP) session state. In other cases, you do not want to
use Spring Session to manage your (HTTP) session state at all. Rather,
you prefer to use your Web Server’s (such as Tomcat’s) built-in
`HttpSession` state management capabilities.





Either way, you can specifically call out your Spring Session provider
implementation by using the `spring.session.store-type` property in
Spring Boot `application.properties`:







Example 5. Use Redis as the Spring Session Provider Implementation









``` highlight
#application.properties

spring.session.store-type=redis
...
```











If you prefer not to use Spring Session to manage your Spring Boot
application’s (HTTP) session state at all, you can do the following:







Example 6. Use Web Server Session State Management









``` highlight
#application.properties

spring.session.store-type=none
...
```











Again, see the Spring Boot
https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-session.html\[documentation\] for
more detail.





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
<td class="content">You can include multiple provider implementations on
the classpath of your Spring Boot application. For instance, you might
use Redis to cache your application’s (HTTP) session state while using
VMware GemFire as your application’s transactional persistent
store (System of Record).</td>
</tr>
</tbody>
</table>





<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon">
Note
</td>
<td class="content">Spring Boot does not properly recognize
<code>spring.session.store-type=[gemfire|geode]</code> even though
Spring Boot for VMware GemFire is set up to handle either of
these property values (that is, either <code>gemfire</code> or
<code>geode</code>).</td>
</tr>
</tbody>
</table>







### Using Spring Session with {pivotal-cloudcache-name} (PCC)



Whether you use Spring Session in a Spring Boot, VMware GemFire
`ClientCache` application to connect to an standalone, externally
managed cluster of VMware GemFire servers or to connect to a
cluster of servers in a {pivotal-cloudcache-name} service instance
managed by a Tanzu Application Service environment, the setup is the
same.





Spring Session for VMware GemFire expects there to be a cache
Region in the cluster that can store and manage (HTTP) session state
when your Spring Boot application is a `ClientCache` application in the
client/server topology.





By default, the cache Region used to store and manage (HTTP) session
state is called `ClusteredSpringSessions`.





We recommend that you configure the cache Region name by using the
well-known and documented property in Spring Boot
`application.properties`:







Example 7. Using properties









``` highlight
spring.session.data.gemfire.session.region.name=MySessions
```











Alternatively, you can set the name of the cache Region used to store
and manage (HTTP) session state by explicitly declaring the
`@EnableGemFireHttpSession` annotation on your main
`@SpringBootApplication` class:







Example 8. Using \`@EnableGemfireHttpSession









``` highlight
@SpringBootApplication
@EnableGemFireHttpSession(regionName = "MySessions")
class MySpringBootSpringSessionApplication {
    // ...
}
```











Once you decide on the cache Region name used to store and manage (HTTP)
sessions, you must create the cache Region in the cluster somehow.





On the client, doing so is simple, since SBDG’s auto-configuration
automatically creates the client `PROXY` Region that is used to send and
receive (HTTP) session state between the client and server for you when
either Spring Session is on the application classpath (for example,
`spring-geode-starter-session`) or you explicitly declare the
`@EnableGemFireHttpSession` annotation on your main
`@SpringBootApplication` class.





However, on the server side, you currently have a couple of options.





First, you can manually create the cache Region by using Gfsh:







Example 9. Create the Sessions Region using Gfsh









``` highlight
gfsh> create region --name=MySessions --type=PARTITION --entry-idle-time-expiration=1800
        --entry-idle-time-expiration-action=INVALIDATE
```











You must create the cache Region with the appropriate name and an
expiration policy.





In this case, we created an idle expiration policy with a timeout of
`1800` seconds (30 minutes), after which the entry (session object) is
`invalidated`.





<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon">
Note
</td>
<td class="content">Session expiration is managed by the Expiration
Policy set on the cache Region that is used to store session state. The
Servlet container’s (HTTP) session expiration configuration is not used,
since Spring Session replaces the Servlet container’s session management
capabilities with its own, and Spring Session delegates this behavior to
the individual providers, such as VMware GemFire.</td>
</tr>
</tbody>
</table>





Alternatively, you could send the definition for the cache Region from
your Spring Boot `ClientCache` application to the cluster by using the
SBDG
{spring-boot-data-geode-javadoc}/org/springframework/geode/config/annotation/EnableClusterAware.html\[`@EnableClusterAware`\]
annotation, which is meta-annotated with SDG’s
`@EnableClusterConfiguration` annotation:







Example 10. Using `@EnableClusterAware`









``` highlight
@SpringBootApplication
@EnableClusterAware
class MySpringBootSpringSessionApacheGeodeApplication {
    // ...
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
<td class="content">See the
https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableClusterConfiguration.html[Javadoc]
on the <code>@EnableClusterConfiguration</code> annotation and the
https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config-cluster[documentation]
for more detail.</td>
</tr>
</tbody>
</table>





However, you cannot currently send expiration policy configuration
metadata to the cluster. Therefore, you must manually alter the cache
Region to set the expiration policy:







Example 11. Using Gfsh to Alter Region









``` highlight
gfsh> alter region --name=MySessions --entry-idle-time-expiration=1800
        --entry-idle-time-expiration-action=INVALIDATE
```











Now your Spring Boot `ClientCache` application that uses Spring Session
in a client/server topology is configured to store and manage user
(HTTP) session state in the cluster. This works for either standalone,
externally managed VMware GemFire clusters or when you use PCC
running in a Tanzu Application Service environment.











<div id="footer">

<div id="footer-text">

Last updated 2022-10-10 12:15:09 -0700




