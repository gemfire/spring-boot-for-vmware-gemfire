---
Title: Auto-configuration
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

The following Spring Framework, Spring Data for VMware GemFire
(SDG) and Spring Session for VMware GemFire (SSDG) annotations
are implicitly declared by Spring Boot for VMware GemFire's
(SBDG) auto-configuration.

- `@ClientCacheApplication`

- `@EnableGemfireCaching` (alternatively, Spring Framework’s
  `@EnableCaching`)

- `@EnableContinuousQueries`

- `@EnableGemfireFunctions`

- `@EnableGemfireFunctionExecutions`

- `@EnableGemfireRepositories`

- `@EnableLogging`

- `@EnablePdx`

- `@EnableSecurity`

- `@EnableSsl`

- `@EnableGemFireHttpSession`

This means that you need not explicitly declare any
of these annotations on your <code>@SpringBootApplication</code> class,
since they are provided by SBDG already. The only reason you would
explicitly declare any of these annotations is to override Spring
Boot’s, and in particular, SBDG’s auto-configuration. Otherwise, doing
so is unnecessary.

You should read the chapter in Spring Boot’s
reference documentation on
[auto-configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/#using-boot-auto-configuration).

You should review the chapter in Spring Data for
VMware GemFire's (SDG) reference documentation on
[annotation-based configuration](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config). For a quick reference and overview of annotation-based
configuration, see the
[annotations quickstart](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstap-annotations-quickstart).

See the corresponding sample
[guide](guides/boot-configuration.html) and
[code](https://github.com/spring-projects/spring-boot-data-geode/tree/1.7.4/spring-geode-samples/intro/getting-started/boot/configuration) to see Spring Boot
auto-configuration for VMware GemFire in action.

### Customizing Auto-configuration

You might ask, “How do I customize the auto-configuration provided by
SBDG if I do not explicitly declare the annotation?”

For example, you may want to customize the member’s name. You know that
the
[`@ClientCacheApplication`](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html)
annotation provides the
[`name`](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableGemFireProperties.html#name--)
attribute so that you can set the client member’s name. However, SBDG
has already implicitly declared the `@ClientCacheApplication` annotation
through auto-configuration on your behalf. What do you do?

In this case, SBDG supplies a few additional annotations.

For example, to set the (client or peer) member’s name, you can use the
`@UseMemberName` annotation:

Example 1. Setting the member’s name using `@UseMemberName`


``` highlight
@SpringBootApplication
@UseMemberName("MyMemberName")
class SpringBootApacheGeodeClientCacheApplication {
    //...
}
```

Alternatively, you could set the `spring.application.name` or the
`spring.data.gemfire.name` property in Spring Boot
`application.properties`:

Example 2. Setting the member’s name using the `spring.application.name`
property


``` highlight
# Spring Boot application.properties

spring.application.name = MyMemberName
```


Example 3. Setting the member’s name using the
`spring.data.gemfire.cache.name` property


``` highlight
# Spring Boot application.properties

spring.data.gemfire.cache.name = MyMemberName
```

<p class="note"><strong>Note:</strong>
The <code>spring.data.gemfire.cache.name</code>
property is an alias for the <code>spring.data.gemfire.name</code>
property. Both properties do the same thing (set the name of the client
or peer member node).
</p>

In general, there are three ways to customize configuration, even in the
context of SBDG’s auto-configuration:

- Using
  [annotations](https://docs.spring.io/spring-boot-data-geode-build/current/api/org/springframework/geode/config/annotation/package-summary.html)
  provided by SBDG for common and popular concerns (such as naming
  client or peer members with the `@UseMemberName` annotation or
  enabling durable clients with the `@EnableDurableClient` annotation).

- Using well-known and documented
  https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config-properties\[properties\]
  (such as `spring.application.name`, or `spring.data.gemfire.name`, or
  `spring.data.gemfire.cache.name`).

- Using
  [configurers](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config-configurers)
  (such as
  [`ClientCacheConfigurer`](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheConfigurer.html)).

For the complete list of documented properties, see
[Configuration Metadata Reference](https://docs.spring.io/spring-boot-data-geode-build/current/reference/html5/#geode-configuration-metadata).


### Deactivating Auto-configuration

Spring Boot’s reference documentation explains how to
[deactivate Spring Boot auto-configuration](./configuration-deactivate.html).

[Deactivating Auto-configuration](./configuration-deactivate.html)
also explains how to deactivate SBDG auto-configuration.

In a nutshell, if you want to deactivate any auto-configuration provided by
either Spring Boot or SBDG, declare your intent in the
`@SpringBootApplication` annotation:

Example 4. Deactivating Specific Auto-configuration Classes



``` highlight
@SpringBootApplication(
  exclude = { DataSourceAutoConfiguration.class, PdxAutoConfiguration.class }
)
class SpringBootApacheGeodeClientCacheApplication {
    // ...
}
```

<p class="caution"><strong>Caution:</strong>
Make sure you understand what you are doing when you
deactivate auto-configuration.
</p>

### Overriding Auto-configuration

[Overriding](./configuration-annotations.html#auto-configuration-annotations-overriding)
explains how to override SBDG auto-configuration.

In a nutshell, if you want to override the default auto-configuration
provided by SBDG, you must annotate your `@SpringBootApplication` class
with your intent.

For example, suppose you want to configure and bootstrap an
VMware GemFire `CacheServer` application (a peer, not a client):

Example 5. Overriding the default `ClientCache` *Auto-Configuration* by
configuring & bootstrapping a `CacheServer` application


``` highlight
@SpringBootApplication
@CacheServerApplication
class SpringBootApacheGeodeCacheServerApplication {
    // ...
}
```

You can also explicitly declare the `@ClientCacheApplication` annotation
on your `@SpringBootApplication` class:

Example 6. Overriding by explicitly declaring `@ClientCacheApplication`


``` highlight
@SpringBootApplication
@ClientCacheApplication
class SpringBootApacheGeodeClientCacheApplication {
    // ...
}
```

You are overriding SBDG’s auto-configuration of the `ClientCache`
instance. As a result, you have now also implicitly consented to being
responsible for other aspects of the configuration (such as security).

Why does that happen?

It happens because, in certain cases, such as security, certain aspects
of security configuration (such as SSL) must be configured before the
cache instance is created. Also, Spring Boot always applies user
configuration before auto-configuration partially to determine what
needs to be auto-configured in the first place.

<p class="caution"><strong>Caution:</strong>
Make sure you understand what you are doing when you
override auto-configuration.
</p>

### Replacing Auto-configuration

See the Spring Boot reference documentation on
[replacing auto-configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/#using-boot-replacing-auto-configuration).

### Understanding Auto-configuration

This section covers the SBDG provided auto-configuration classes that
correspond to the SDG annotations in more detail.

To review the complete list of SBDG auto-configuration classes, see
[Complete Set of Auto-configuration Classes](./configuration-deactivate.html#auto-configuration-deactivate-classes).

#### `@ClientCacheApplication`

The SBDG
[<code>ClientCacheAutoConfiguration</code>](https://docs.spring.io/spring-boot-data-geode-build/current/api/org/springframework/geode/boot/autoconfigure/ClientCacheAutoConfiguration.html)
class corresponds to the SDG
[<code>@ClientCacheApplication</code>](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html)
annotation.

As explained in [Getting Started](./index.html#getting-started) SBDG starts with
the opinion that application developers primarily build
VMware GemFire [client
applications](./clientcache-applications.html) by using Spring Boot.

Technically, this means building Spring Boot applications with an
VMware GemFire `ClientCache` instance connected to a dedicated
cluster of VMware GemFire servers that manage the data as part
of a
[client/server](https://geode.apache.org/docs/guide/115/topologies_and_comm/cs_configuration/chapter_overview.html)
topology.

By way of example, this means that you need not explicitly declare and
annotate your `@SpringBootApplication` class with SDG’s
`@ClientCacheApplication` annotation, as the following example shows:

Example 7. Do Not Do This


``` highlight
@SpringBootApplication
@ClientCacheApplication
class SpringBootApacheGeodeClientCacheApplication {
    // ...
}
```

SBDG’s provided auto-configuration class is already meta-annotated with
SDG’s `@ClientCacheApplication` annotation. Therefore, you need only do:

Example 8. Do This


``` highlight
@SpringBootApplication
class SpringBootApacheGeodeClientCacheApplication {
    // ...
}
```

See SDG’s reference documentation for more details
on VMware GemFire
[cache applications](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config-geode-applications) and
[client/server applications](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config-client-server-applications) in particular.

#### `@EnableGemfireCaching`

The SBDG
[<code>CachingProviderAutoConfiguration</code>](https://docs.spring.io/spring-boot-data-geode-build/current/api/org/springframework/geode/boot/autoconfigure/CachingProviderAutoConfiguration.html)
class corresponds to the SDG
[<code>@EnableGemfireCaching</code>](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/cache/config/EnableGemfireCaching.html)
annotation.

If you used the core Spring Framework to configure
VMware GemFire as a caching provider in
[Spring’s Cache Abstraction](https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache), you need to:

Example 9. Configuring caching using the Spring Framework


``` highlight
@SpringBootApplication
@EnableCaching
class CachingUsingApacheGeodeConfiguration {

    @Bean
    GemfireCacheManager cacheManager(GemFireCache cache) {

        GemfireCacheManager cacheManager = new GemfireCacheManager();

        cacheManager.setCache(cache);

        return cacheManager;
    }
}
```

If you use Spring Data for VMware GemFire's
`@EnableGemfireCaching` annotation, you can simplify the preceding
configuration:

Example 10. Configuring caching using Spring Data for
VMware GemFire


``` highlight
@SpringBootApplication
@EnableGemfireCaching
class CachingUsingApacheGeodeConfiguration {

}
```

Also, if you use SBDG, you need only do:

Example 11. Configuring caching using Spring Boot for
VMware GemFire


``` highlight
@SpringBootApplication
class CachingUsingApacheGeodeConfiguration {

}
```

This lets you focus on the areas in your application that would benefit
from caching without having to enable the plumbing. You can then
demarcate the service methods in your application that are good
candidates for caching:

Example 12. Using caching in your application


``` highlight
@Service
class CustomerService {

    @Caching("CustomersByName")
    Customer findBy(String name) {
        // ...
    }
}
```

See [Caching with VMware GemFire](./caching.html) for more details.

#### `@EnableContinuousQueries`

The SBDG
[<code>ContinuousQueryAutoConfiguration</code>](https://docs.spring.io/spring-boot-data-geode-build/current/api/org/springframework/geode/boot/autoconfigure/ContinuousQueryAutoConfiguration.html)
class corresponds to the SDG
[<code>@EnableContinuousQueries</code>])(https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableContinuousQueries.html)
annotation.

Without having to enable anything, you can annotate your application
(POJO) component method(s) with the SDG
[`@ContinuousQuery`](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/listener/annotation/ContinuousQuery.html)
annotation to register a CQ and start receiving events. The method acts
as a `CqEvent` handler or, in VMware GemFire's terminology, the
method is an implementation of the
[`CqListener`](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/query/CqListener.html)
interface.

Example 13. Declare application CQs



``` highlight
@Component
class MyCustomerApplicationContinuousQueries {

    @ContinuousQuery("SELECT customer.* "
        + " FROM /Customers customers"
        + " WHERE customer.getSentiment().name().equalsIgnoreCase('UNHAPPY')")
    public void handleUnhappyCustomers(CqEvent event) {
        // ...
    }
}
```

As the preceding example shows, you can define the events you are
interested in receiving by using an OQL query with a finely tuned query
predicate that describes the events of interests and implements the
handler method to process the events (such as applying a credit to the
customer’s account and following up in email).

See [Continuous Query](continuous-query.html) for more
details.

#### `@EnableGemfireFunctionExecutions` & `@EnableGemfireFunctions`

The SBDG
https://docs.spring.io/spring-boot-data-geode-build/current/api/org/springframework/geode/boot/autoconfigure/FunctionExecutionAutoConfiguration.html[<code>FunctionExecutionAutoConfiguration</code>]
class corresponds to both the SDG
https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/function/config/EnableGemfireFunctionExecutions.html[<code>@EnableGemfireFunctionExecutions</code>]
and SDG
https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/function/config/EnableGemfireFunctions.html[<code>@EnableGemfireFunctions</code>]
annotations.

Whether you need to
[execute](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#function-execution) or
[implement](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#function-implementation) a
`Function`, SBDG detects the Function definition and auto-configures it
appropriately for use in your Spring Boot application. You need only
define the Function execution or implementation in a package below the
main `@SpringBootApplication` class:

Example 14. Declare a Function Execution


``` highlight
package example.app.functions;

@OnRegion("Accounts")
interface MyCustomerApplicationFunctions {

    void applyCredit(Customer customer);

}
```

Then you can inject the Function execution into any application
component and use it:

Example 15. Use the Function


``` highlight
package example.app.service;

@Service
class CustomerService {

    @Autowired
    private MyCustomerApplicationFunctions customerFunctions;

    void analyzeCustomerSentiment(Customer customer) {

        // ...

        this.customerFunctions.applyCredit(customer);

        // ...
    }
}
```

The same pattern basically applies to Function implementations, except
in the implementation case, SBDG registers the Function implementation
for use (that is, to be called by a Function execution).

Doing so lets you focus on defining the logic required by your
application and not worry about how Functions are registered, called,
and so on. SBDG handles this concern for you.

<p class="note"><strong>Note:</strong>
Function implementations are typically defined and
registered on the server-side.
</p>

See [Function Implementations & Executions](functions.html)
for more details.

#### `@EnableGemfireRepositories`

The SBDG
[<code>GemFireRepositoriesAutoConfigurationRegistrar</code>](https://docs.spring.io/spring-boot-data-geode-build/current/api/org/springframework/geode/boot/autoconfigure/GemFireRepositoriesAutoConfigurationRegistrar.html)
class corresponds to the SDG
[<code>@EnableGemfireRepositories</code>](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/repository/config/EnableGemfireRepositories.html)
annotation.

As with Functions, you need concern yourself only with the data access
operations (such as basic CRUD and simple queries) required by your
application to carry out its operation, not with how to create and
perform them (for example, `Region.get(key)` and `Region.put(key, obj)`)
or execute them (for example, `Query.execute(arguments)`).

Start by defining your Spring Data Repository:

Example 16. Define an application-specific Repository


``` highlight
package example.app.repo;

interface CustomerRepository extends CrudRepository<Customer, Long> {

    List<Customer> findBySentimentEqualTo(Sentiment sentiment);

}
```

Then you can inject the Repository into an application component and use
it:

Example 17. Using the application-specific Repository


``` highlight
package example.app.sevice;

@Service
class CustomerService {

    @Autowired
    private CustomerRepository repository;

    public void processCustomersWithSentiment(Sentiment sentiment) {

        this.repository.findBySentimentEqualTo(sentiment)
            .forEach(customer -> { /* ... */ });

        // ...
    }
}
```

Your application-specific Repository simply needs to be declared in a
package below the main `@SpringBootApplication` class. Again, you are
focusing only on the data access operations and queries required to
carry out the operatinons of your application, nothing more.

See [Spring Data Repositories](repositories.html)
for more details.

#### `@EnableLogging`

The SBDG
[<code>LoggingAutoConfiguration</code>](https://docs.spring.io/spring-boot-data-geode-build/current/api/org/springframework/geode/boot/autoconfigure/LoggingAutoConfiguration.html)
class corresponds to the SDG
[<code>@EnableLogging</code>](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableLogging.html)
annotation.

Logging is an essential application concern to understand what is
happening in the system along with when and where the events occurred.
By default, SBDG auto-configures logging for VMware GemFire with
the default log-level, “config”.

You can change any aspect of logging, such as the log-level, in Spring
Boot `application.properties`:

Example 18. Change the log-level for VMware GemFire

``` highlight
# Spring Boot application.properites.

spring.data.gemfire.cache.log-level=debug
```

<p class="note"><strong>Note:</strong>
The 'spring.data.gemfire.logging.level' property is
an alias for <code>spring.data.gemfire.cache.log-level</code>.
</p>

You can also configure other aspects, such as the log file size and disk
space limits for the filesystem location used to store the
VMware GemFire log files at runtime.

Under the hood, VMware GemFire's logging is based on Log4j.
Therefore, you can configure VMware GemFire logging to use any
logging provider (such as Logback) and configuration metadata
appropriate for that logging provider so long as you supply the
necessary adapter between Log4j and whatever logging system you use. For
instance, if you include
`org.springframework.boot:spring-boot-starter-logging`, you are using
Logback and you will need the `org.apache.logging.log4j:log4j-to-slf4j`
adapter.

#### `@EnablePdx`

The SBDG
[<code>PdxSerializationAutoConfiguration</code>](https://docs.spring.io/spring-boot-data-geode-build/current/api/org/springframework/geode/boot/autoconfigure/PdxSerializationAutoConfiguration.html)
class corresponds to the SDG
[<code>@EnablePdx</code>](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePdx.html)
annotation.

Any time you need to send an object over the network or overflow or
persist an object to disk, your application domain model object must be
serializable. It would be painful to have to implement
`java.io.Serializable` in every one of your application domain model
objects (such as `Customer`) that would potentially need to be
serialized.

Furthermore, using Java Serialization may not be ideal (it may not be
the most portable or efficient solution) in all cases or even possible
in other cases (such as when you use a third party library over which
you have no control).

In these situations, you need to be able to send your object anywhere,
anytime without unduly requiring the class type to be serializable and
exist on the classpath in every place it is sent. Indeed, the final
destination may not even be a Java application. This is where
VMware GemFire
[PDX Serialization](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-data_serialization-gemfire_pdx_serialization.html) steps in to help.

However, you need not figure out how to configure PDX to identify the
application class types that needs to be serialized. Instead, you can
define your class type as follows:

Example 19. Customer class

``` highlight
@Region("Customers")
class Customer {

    @Id
    private Long id;

    @Indexed
    private String name;

    // ...
}
```

SBDG’s auto-configuration handles the rest.

See [Data Serialization with PDX](data-serialization.html) for more
details.

#### `@EnableSecurity`

The SBDG
[<code>ClientSecurityAutoConfiguration</code>](https://docs.spring.io/spring-boot-data-geode-build/current/api/org/springframework/geode/boot/autoconfigure/ClientSecurityAutoConfiguration.html)
class and
[<code>PeerSecurityAutoConfiguration</code>](https://docs.spring.io/spring-boot-data-geode-build/current/api/org/springframework/geode/boot/autoconfigure/PeerSecurityAutoConfiguration.html)
class correspond to the SDG
[<code>@EnableSecurity</code>](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSecurity.html)
annotation, but they apply security (specifically, authentication and
authorization (auth) configuration) for both clients and servers.

Configuring your Spring Boot, VMware GemFire `ClientCache`
application to properly authenticate with a cluster of secure
VMware GemFire servers is as simple as setting a username and a
password in Spring Boot `application.properties`:

Example 20. Supplying Authentication Credentials

``` highlight
# Spring Boot application.properties

spring.data.gemfire.security.username=Batman
spring.data.gemfire.security.password=r0b!n5ucks
```

<p class="note"><strong>Note:</strong>
Authentication is even easier to configure in a
managed environment, such as PCF when using PCC. You need not do
anything.
</p>

Authorization is configured on the server-side and is made simple with
SBDG and the help of [Apache Shiro](https://shiro.apache.org/). Of
course, this assumes you use SBDG to configure and bootstrap your
VMware GemFire cluster in the first place, which is even easier
with SBDG. See [Running a VMware GemFire cluster with Spring Boot from your IDE](./configuration-bootstrapping.html).

See [Security](security.html) for more details.

#### `@EnableSsl`

The SBDG
[<code>SslAutoConfiguration</code>](https://docs.spring.io/spring-boot-data-geode-build/current/api/org/springframework/geode/boot/autoconfigure/SslAutoConfiguration.html)
class corresponds to the SDG
[<code>@EnableSsl</code>](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html)
annotation.

Configuring SSL for secure transport (TLS) between your Spring Boot,
VMware GemFire `ClientCache` application and an
VMware GemFire cluster can be a real problem, especially to get
right from the start. So, it is something that SBDG makes as simple as
possible.

You can supply a `trusted.keystore` file containing the certificates in
a well-known location (such as the root of your application classpath),
and SBDG’s auto-configuration steps in to handle the rest.

This is useful during development, but we highly recommend using a more
secure procedure (such as integrating with a secure credential store
like LDAP, CredHub or Vault) when deploying your Spring Boot application
to production.

See [Transport Layer Security using SSL](security.html#geode-security-ssl) for more
details.

#### `@EnableGemFireHttpSession`

The SBDG
[<code>SpringSessionAutoConfiguration</code>](https://docs.spring.io/spring-boot-data-geode-build/current/api/org/springframework/geode/boot/autoconfigure/SpringSessionAutoConfiguration.html)
class corresponds to the SSDG
[<code>@EnableGemFireHttpSession</code>](https://docs.spring.io/autorepo/docs/spring-session-data-geode-build/2.7.1/api/org/springframework/session/data/gemfire/config/annotation/EnableGemFireHttpSession.html)
annotation.

Configuring VMware GemFire to serve as the (HTTP) session state
caching provider by using Spring Session requires that you only include
the correct starter, that is `spring-geode-starter-session`:

Example 21. Using Spring Session

    <dependency>
        <groupId>org.springframework.geode</groupId>
        <artifactId>spring-geode-starter-session</artifactId>
        <version>{revnumber}</version>
    </dependency>

With Spring Session — and specifically Spring Session for
VMware GemFire (SSDG) — on the classpath of your Spring Boot,
VMware GemFire `ClientCache` Web application, you can manage
your (HTTP) session state with VMware GemFire. No further
configuration is needed. SBDG auto-configuration detects Spring Session
on the application classpath and does the rest.

See [Spring Session](session.html) for more details.

#### <a id=regiontemplateautoconfiguration></a>RegionTemplateAutoConfiguration

The SBDG
[`RegionTemplateAutoConfiguration`](https://docs.spring.io/spring-boot-data-geode-build/current/api/org/springframework/geode/boot/autoconfigure/RegionTemplateAutoConfiguration.html)
class has no corresponding SDG annotation. However, the
auto-configuration of a `GemfireTemplate` for every
VMware GemFire `Region` defined and declared in your Spring Boot
application is still supplied by SBDG.

For example, you can define a Region by using:

Example 22. Region definition using JavaConfig

``` highlight
@Configuration
class GeodeConfiguration {

    @Bean("Customers")
    ClientRegionFactoryBean<Long, Customer> customersRegion(GemFireCache cache) {

        ClientRegionFactoryBean<Long, Customer> customersRegion =
            new ClientRegionFactoryBean<>();

        customersRegion.setCache(cache);
        customersRegion.setShortcut(ClientRegionShortcut.PROXY);

        return customersRegion;
    }
}
```

Alternatively, you can define the `Customers` Region by using
`@EnableEntityDefinedRegions`:

Example 23. Region definition using `@EnableEntityDefinedRegions`

``` highlight
@Configuration
@EnableEntityDefinedRegion(basePackageClasses = Customer.class)
class GeodeConfiguration {

}
```

Then SBDG supplies a `GemfireTemplate` instance that you can use to
perform low-level data-access operations (indirectly) on the `Customers`
Region:

Example 24. Use the `GemfireTemplate` to access the "Customers" Region

``` highlight
@Repository
class CustomersDao {

    @Autowired
    @Qualifier("customersTemplate")
    private GemfireTemplate customersTemplate;

    Customer findById(Long id) {
        return this.customerTemplate.get(id);
    }
}
```

You need not explicitly configure `GemfireTemplates` for each Region to
which you need low-level data access (such as when you are not using the
Spring Data Repository abstraction).

Be careful to qualify the `GemfireTemplate` for the Region to which you
need data access, especially given that you probably have more than one
Region defined in your Spring Boot application.

See [Data Access with GemfireTemplate](templates.html) for more details.
