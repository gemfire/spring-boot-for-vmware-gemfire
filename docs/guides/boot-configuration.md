# Spring Boot Auto-configuration for VMware GemFire

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

Table of Contents

- [Application Domain
  Classes](#geode-samples-boot-configuration-app-domain-classes)
  - [`Customer` class](#_customer_class)
  - [`CustomerRepository` interface](#_customerrepository_interface)
  - [`CustomerServiceApplication` (Spring Boot main
    class)](#_customerserviceapplication_spring_boot_main_class)
- [Running the Example](#geode-samples-boot-configuration-run)
- [Auto-configuration for VMware GemFire, Take
  One](#geode-samples-boot-configuration-autoconfig)
  - [Cache instance](#_cache_instance)
  - [Repository instance](#_repository_instance)
  - [Entity-defined Regions](#_entity_defined_regions)
- [Switching to
  Client/Server](#geode-samples-boot-configuration-clientserver)
- [Auto-configuration for VMware GemFire, Take
  Two](#geode-samples-boot-configuration-clientserver-autoconfig)
- [Securing the Client &
  Server](#geode-samples-boot-configuration-clientserver-security)
  - [Securing the
    server](#geode-samples-boot-configuration-clientserver-security-server)
  - [Securing the
    client](#geode-samples-boot-configuration-clientserver-security-client)
- [Conclusion](#geode-samples-boot-configuration-conclusion)

This guide walks you through building a simple Customer Service, Spring
Boot application using VMware GemFire to manage Customer interactions.
You should already be familiar with Spring Boot and VMware GemFire.

By the end of this lesson, you should have a better understanding of
what Spring Boot for VMware GemFire’s (SBDG) *auto-configuration*
support actually does.

This guide compliments the [Auto-configuration vs. Annotation-based
configuration](../index.html#geode-auto-configuration-annotations)
chapter with concrete examples.

Let’s begin.

This guide builds on the <a
href="https://www.youtube.com/watch?v=OvY5wzCtOV0"><em>Simplifying
VMware GemFire with Spring Data</em></a> presentation by John Blum
during the 2017 SpringOne Platform conference. While this example as
well as the example presented in the talk both use Spring Boot, only
this example is using Spring Boot for VMware GemFire (SBDG). This guide
improves on the example from the presentation by using SBDG.

Refer to the <a
href="../index.html#geode-configuration-auto">Auto-configuration</a>
chapter in the reference documentation for more information.


## Application Domain Classes

We will build the Spring Boot, Customer Service application from the
ground up.

### `Customer` class

Like any sensible application development project, we begin by modeling
the data our application needs to manage, namely a `Customer`. For this
example, the `Customer` class is implemented as follows:

Customer class

``` highlight
@Region("Customers")
@EqualsAndHashCode
@ToString(of = "name")
@RequiredArgsConstructor(staticName = "newCustomer")
public class Customer {

    @Id @NonNull @Getter
    private Long id;

    @NonNull @Getter
    private String name;

}
```

The `Customer` class uses [Project Lombok](https://projectlombok.org/)
to simplify the implementation so we can focus on the details we care
about. Lombok is useful for testing or prototyping purposes. However,
using Project Lombok is optional and in most production applications,
and I would not recommend it.

Additionally, the `Customer` class is annotated with Spring Data Geode’s
(SDG) `@Region` annotation. `@Region` is a mapping annotation declaring
the VMware GemFire cache `Region` in which `Customer` data will be
persisted.

Finally, the `org.springframework.data.annotation.Id` annotation was
used to designate the `Customer.id` field as the identifier for
`Customer` objects. The identifier is the Key used in the Entry stored
in the "Customers"\`Region\`. A `Region` is a distributed version of
`java.util.Map`.

If the <code>@Region</code> annotation is not
explicitly declared, then SDG uses the simple name of the class, which
in this case is "Customer", to identify the <code>Region</code>.
However, there is another reason we explicitly annotated the
<code>Customer</code> class with <code>@Region</code>, which we will
cover below.

### `CustomerRepository` interface

Next, we create a *Data Access Object* (DAO) to persist `Customers` to
VMware GemFire. We create the DAO using Spring Data’s *Repository*
abstraction:

CustomerRepository inteface

``` highlight
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    Customer findByNameLike(String name);

}
```

`CustomerRepository` is a Spring Data `CrudRepository`. `CrudRepository`
provides basic CRUD (CREATE, READ, UPDATE, and DELETE) data access
operations along with the ability to define simple queries on
`Customers`.

Spring Data Geode will create a proxy implementation for your
application-specific *Repository* interfaces, implementing any query
methods you may have explicitly defined on the interface in addition to
the data access operations provided in the `CrudRepository` interface
extension.

In addition to the base `CrudRepository` operations,
`CustomerRepository` has additionally defined a
`findByNameLike(:String):Customer` query method. The VMware GemFire OQL
query is derived from the method declaration.

<p class="note"><strong>Note:</strong>
Though it is beyond the scope of this document,
Spring Data’s <em>Repository</em> infrastructure is capable of
generating data store specific queries (e.g. VMware GemFire OQL) for
<em>Repository</em> interface query method declarations just by
introspecting the method signature. The query methods must conform to
specific conventions. Alternatively, users may use <code>@Query</code>
to annotate query methods to specify the raw query instead (i.e. OQL for
VMware GemFire, SQL for JDBC, possibly HQL for JPA, and so on).
</p>

### `CustomerServiceApplication` (Spring Boot main class)

Now that we have created the basic domain classes of our Customer
Service application, we need a main application class to drive the
interactions with Customers:

CustomerServiceApplication class

``` highlight
@SpringBootApplication
@EnableEntityDefinedRegions(basePackageClasses = Customer.class, clientRegionShortcut = ClientRegionShortcut.LOCAL)
public class CustomerServiceApplication {

    public static void main(String[] args) {

        new SpringApplicationBuilder(CustomerServiceApplication.class)
            .web(WebApplicationType.NONE)
            .build()
            .run(args);
    }

    @Bean
    ApplicationRunner runner(CustomerRepository customerRepository) {

        return args -> {

            assertThat(customerRepository.count()).isEqualTo(0);

            Customer jonDoe = Customer.newCustomer(1L, "Jon Doe");

            System.err.printf("Saving Customer [%s]%n", jonDoe);

            jonDoe = customerRepository.save(jonDoe);

            assertThat(jonDoe).isNotNull();
            assertThat(jonDoe.getId()).isEqualTo(1L);
            assertThat(jonDoe.getName()).isEqualTo("Jon Doe");
            assertThat(customerRepository.count()).isEqualTo(1);

            System.err.println("Querying for Customer [SELECT * FROM /Customers WHERE name LIKE '%Doe']");

            Customer queriedJonDoe = customerRepository.findByNameLike("%Doe");

            assertThat(queriedJonDoe).isEqualTo(jonDoe);

            System.err.printf("Customer was [%s]%n", queriedJonDoe);
        };
    }
}
```

The `CustomerServiceApplication` class is annotated with
`@SpringBootApplication`. Therefore, the main class is a proper Spring
Boot application equipped with all the features of Spring Boot (e.g.
*auto-configuration*).

Additionally, we use Spring Boot’s `SpringApplicationBuilder` in the
`main` method to configure and bootstrap the Customer Service
application.

Then, we declare a Spring Boot `ApplicationRunner` bean, which is
invoked by Spring Boot after the Spring container (i.e.
`ApplicationContext`) has been properly initialized and started. Our
`ApplicationRunner` defines the Customer interactions performed by our
Customer Service application.

Specifically, the runner creates a new `Customer` object ("Jon Doe"),
saves him to the "Customers" Region, and then queries for "Jon Doe"
using an OQL query with the predicate: `name LIKE '%Doe'`.

<a class="note"><strong>Note:</strong>
<code>%</code> is the wildcard for OQL text
searches.

## Running the Example

You can run the `CustomerServiceApplication` class from your IDE (e.g.
IntelliJ IDEA) or from the command-line with the `gradlew` command.

There is nothing special you must do to run the
`CustomerServiceApplication` class from inside your IDE. Simply create a
run profile configuration and run it.

There is also nothing special about running the
`CustomerServiceApplication` class from the command-line using
`gradlew`. Simply execute it with `bootRun`:

`$ gradlew :spring-geode-samples-boot-configuration:bootRun`

If you wish to adjust the log levels for either VMware GemFire or Spring
Boot while running the example, then you can set the log level for the
individual Loggers (i.e. `org.apache` or `org.springframework`) in
`src/main/resources/logback.xml`:

spring-geode-samples/boot/configuration/src/main/resources/logback.xml

``` highlight
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="false">

    <statusListener class="ch.qos.logback.core.status.NopStatusListener"/>

    <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d %5p %40.40c:%4L - %m%n</pattern>
        </encoder>
    </appender>

    <logger name="ch.qos.logback" level="${logback.log.level:-ERROR}"/>

    <logger name="org.apache" level="${logback.log.level:-ERROR}"/>

    <logger name="org.springframework" level="${logback.log.level:-ERROR}"/>

    <root level="${logback.log.level:-ERROR}">
        <appender-ref ref="console"/>
    </root>

</configuration>
```

## Auto-configuration for VMware GemFire, Take One

"*With great power comes great responsibility.*" - Uncle Ben

While it is not apparent (yet), there is a lot of hidden, intrinsic
power provided by Spring Boot Data Geode (SBDG) in this example.

### Cache instance

First, in order to put anything into VMware GemFire you need a cache
instance. A cache instance is also required to create `Regions` which
ultimately store the application’s data (state). Again, a `Region` is
just a Key/Value data structure, like `java.util.Map`, mapping a Key to
a Value, or an Object. A `Region` is actually much more than a simple
`Map` since it is distributed. However, since `Region` implements
`java.util.Map`, it can be treated as such.

<p class="note"><strong>Note:</strong>
A complete discussion of <code>Region</code> and it
concepts are beyond the scope of this document. You may learn more by
reading VMware GemFire’s User Guide on <a
href="https://geode.apache.org/docs/guide/1.15/developing/region_options/chapter_overview.html">Regions</a>.
</p>

SBDG is opinionated and assumes most VMware GemFire applications will be
client applications in VMware GemFire’s [client/server
topology](https://geode.apache.org/docs/guide/1.15/topologies_and_comm/cs_configuration/chapter_overview.html).
Therefore, SBDG auto-configures a `ClientCache` instance by default.

The intrinsic `ClientCache` *auto-configuration* provided by SBDG can be
made apparent by disabling it:

Disabling ClientCache Auto-configuration

``` highlight
@SpringBootApplication(exclude = ClientCacheAutoConfiguration.class)
@EnableEntityDefinedRegions(basePackageClasses = Customer.class, clientRegionShortcut = ClientRegionShortcut.LOCAL)
public class CustomerServiceApplication {
  // ...
}
```

Note the `exclude` on the `ClientCacheAutoConfiguration.class`.

With the correct log level set, you will see an error message similar
to:

Error resulting from no ClientCache instance

``` highlight
16:20:47.543 [main] DEBUG o.s.b.d.LoggingFailureAnalysisReporter - Application failed to start due to an exception
org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'example.app.crm.repo.CustomerRepository' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {}
    at org.springframework.beans.factory.support.DefaultListableBeanFactory.raiseNoMatchingBeanFound(DefaultListableBeanFactory.java:1509) ~[spring-beans-5.0.13.RELEASE.jar:5.0.13.RELEASE]
    at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1104) ~[spring-beans-5.0.13.RELEASE.jar:5.0.13.RELEASE]
    at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1065) ~[spring-beans-5.0.13.RELEASE.jar:5.0.13.RELEASE]
    at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:819) ~[spring-beans-5.0.13.RELEASE.jar:5.0.13.RELEASE]
    ...
16:20:47.548 [main] ERROR o.s.b.d.LoggingFailureAnalysisReporter -

***************************
APPLICATION FAILED TO START
***************************

Description:

Parameter 0 of method runner in example.app.crm.CustomerServiceApplication required a bean of type 'example.app.crm.repo.CustomerRepository' that could not be found.
```

Essentially, the `CustomerRepository` could not be injected into our
`CustomerServiceApplication` class, `ApplicationRunner` bean method
because the `CustomerRepository`, which depends on the "Customers"
Region, could not be created. The `CustomerRepository` could not be
created because the "Customers" Region could not be created. The
"Customers" Region could not be created because there was no cache
instance available (e.g. `ClientCache`) to create `Regions`, resulting
in a trickling effect.

The `ClientCache` *auto-configuration* is equivalent to the following:

Equivalent ClientCache configuration

``` highlight
@SpringBootApplication
@ClientCacheApplication
@EnableEntityDefinedRegions(basePackageClasses = Customer.class, clientRegionShortcut = ClientRegionShortcut.LOCAL)
public class CustomerServiceApplication {
  // ...
}
```

That is, you would need to explicitly declare the
`@ClientCacheApplication` annotation if you were not using SBDG.

### Repository instance

We are also using the Spring Data (Geode) *Repository* infrastructure in
the Customer Service application. This should be evident from our
declaration and definition of the application-specific
`CustomerRepository` interface.

If we disable the Spring Data *Repository* *auto-configuration*:

Disabling Spring Data Repositories Auto-configuration

``` highlight
@SpringBootApplication(exclude = RepositoriesAutoConfiguration.class)
@EnableEntityDefinedRegions(basePackageClasses = Customer.class, clientRegionShortcut = ClientRegionShortcut.LOCAL)
public class CustomerServiceApplication {
  // ...
}
```

The application would throw a similar error on startup:

Error resulting from no proxied `CustomerRepository` instance

``` highlight
17:31:21.231 [main] DEBUG o.s.b.d.LoggingFailureAnalysisReporter - Application failed to start due to an exception
org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'example.app.crm.repo.CustomerRepository' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {}
    at org.springframework.beans.factory.support.DefaultListableBeanFactory.raiseNoMatchingBeanFound(DefaultListableBeanFactory.java:1509) ~[spring-beans-5.0.13.RELEASE.jar:5.0.13.RELEASE]
    at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1104) ~[spring-beans-5.0.13.RELEASE.jar:5.0.13.RELEASE]
    at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1065) ~[spring-beans-5.0.13.RELEASE.jar:5.0.13.RELEASE]
    at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:819) ~[spring-beans-5.0.13.RELEASE.jar:5.0.13.RELEASE]
    ...
17:31:21.235 [main] ERROR o.s.b.d.LoggingFailureAnalysisReporter -

***************************
APPLICATION FAILED TO START
***************************

Description:

Parameter 0 of method runner in example.app.crm.CustomerServiceApplication required a bean of type 'example.app.crm.repo.CustomerRepository' that could not be found.
```

In this case, there was simply no proxy implementation for the
`CustomerRepository` interface provided by the framework since the
*auto-configuration* was disabled. The `ClientCache` and "Customers"
`Region` do exist in this case, though.

The Spring Data *Repository auto-configuration* even takes care of
locating our application *Repository* interface definitions for us.

Without *auto-configuration*, you would need to explicitly:

Equivalent Spring Data Repositories configuration

``` highlight
@SpringBootApplication(exclude = RepositoriesAutoConfiguration.class)
@EnableEntityDefinedRegions(basePackageClasses = Customer.class, clientRegionShortcut = ClientRegionShortcut.LOCAL)
@EnableGemfireRepositories(basePackageClasses = CustomerRepository.class)
public class CustomerServiceApplication {
  // ...
}
```

That is, you would need to explicitly declare the
`@EnableGemfireRepositories` annotation and set the `basePackages`
attribute, or the equivalent, type-safe `basePackageClasses` attribute,
to the package containing your application *Repository* interfaces, if
you were not using SBDG.

### Entity-defined Regions

So far, the only explicit declaration of configuration in our Customer
Service application is the `@EnableEntityDefinedRegions` annotation.

As was alluded to above, there was another reason we explicitly declared
the `@Region` annotation on our `Customer` class.

We could have defined the client `LOCAL` "Customers" Region using Spring
JavaConfig, explicitly:

JavaConfig Bean Definition for the "Customers" Region

``` highlight
@Configuration
class ApplicationConfiguration {

    @Bean("Customers")
    public ClientRegionFactoryBean<Long, Customer> customersRegion(GemFireCache gemfireCache) {

        ClientRegionFactoryBean<Long, Customer> customersRegion = new ClientRegionFactoryBean<>();

        customersRegion.setCache(gemfireCache);
        customersRegion.setShortcut(ClientRegionShortcut.LOCAL);

        return customersRegion;
    }
}
```

Or, even define the "Customers" Region using Spring XML, explicitly:

XML Bean Definition for the "Customers" Region

``` highlight
<gfe:client-region id="Customers" shortcut="LOCAL"/>
```

But, using SDG’s `@EnableEntityDefinedRegions` annotation is very
convenient and can scan for the Regions (whether client or server (peer)
Regions) required by your application based the entity classes
themselves (e.g. `Customer`):

Annotation-based config for the "Customers" Region

``` highlight
@EnableEntityDefinedRegions(basePackageClasses = Customer.class, clientRegionShortcut = ClientRegionShortcut.LOCAL)
class CustomerServiceApplication { }
```

The `basePackageClasses` attribute is an alternative to `basePackages`,
and a type-safe way to target the packages (and subpackages) containing
the entity classes that your application will persist to VMware GemFire.
You only need to choose one class from each top-level package for where
you want the scan to begin. Spring Data Geode uses this class to
determine the package to begin the scan. 'basePackageClasses\` accepts
an array of `Class` types so you can specify multiple independent
top-level packages. The annotation also includes the ability to filter
types.

However, the `@EnableEntityDefinedRegions` annotation only works when
the entity class (e.g. `Customer`) is explicitly annotated with the
`@Region` annotation (e.g. `@Region("Customers")`), otherwise it ignores
the class.

You will also notice that the data policy type (i.e.
`clientRegionShort`, or simply `shortcut`) is set to `LOCAL` in our
example. Why?

Well, initially we just want to get up and running as quickly as
possible, without a lot of ceremony and fuss. By using a client `LOCAL`
Region to begin with, we are not required to start a cluster of servers
for the client to be able to store data.

While client `LOCAL` Regions can be useful for some purposes (e.g. local
processing, querying and aggregating of data), it is more common for a
client to persist data in a cluster of servers, and for that data to be
shared by multiple clients (instances) in the application architecture,
especially as the application is scaled out to handle demand.

## Switching to Client/Server

We continue with our example by switching from a local context to a
client/server topology.

If you are rapidly prototyping and developing your application and
simply want to lift off the ground quickly, then it is useful to start
locally and gradually migrate towards a client/server architecture.

To switch to client/server, all you need to do is remove the
`clientRegionShortcut` attribute configuration from the
`@EnableEntityDefinedRegions` annotation declaration:

Client/Server Topology Region Configuration

``` highlight
@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
class CustomerServiceApplication { }
```

The default value for the `clientRegionShortcut` attribute is
`ClientRegionShortcut.PROXY`. This means no data is stored locally. All
data is sent from the client to one or more servers in a cluster.

However, if we try to run the application, it will fail:

NoAvailableServersException

``` highlight
Caused by: org.apache.geode.cache.client.NoAvailableServersException
    at org.apache.geode.cache.client.internal.pooling.ConnectionManagerImpl.borrowConnection(ConnectionManagerImpl.java:234) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.OpExecutorImpl.execute(OpExecutorImpl.java:136) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.OpExecutorImpl.execute(OpExecutorImpl.java:115) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.PoolImpl.execute(PoolImpl.java:763) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.QueryOp.execute(QueryOp.java:58) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.ServerProxy.query(ServerProxy.java:70) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.query.internal.DefaultQuery.executeOnServer(DefaultQuery.java:456) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.query.internal.DefaultQuery.execute(DefaultQuery.java:338) ~[geode-core-1.2.1.jar:?]
    at org.springframework.data.gemfire.GemfireTemplate.find(GemfireTemplate.java:311) ~[spring-data-geode-2.0.14.RELEASE.jar:2.0.14.RELEASE]
    at org.springframework.data.gemfire.repository.support.SimpleGemfireRepository.count(SimpleGemfireRepository.java:129) ~[spring-data-geode-2.0.14.RELEASE.jar:2.0.14.RELEASE]
    ...
    at example.app.crm.CustomerServiceApplication.lambda$runner$0(CustomerServiceApplication.java:59) ~[classes/:?]
    at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:783) ~[spring-boot-2.0.9.RELEASE.jar:2.0.9.RELEASE]
```

The client is expecting there to be a cluster of servers to communicate
with and to store/access data from. Clearly, there are no servers or
cluster running yet.

There are several ways in which to start a cluster. For example, you may
use Spring to configure and bootstrap the cluster, which has been
demonstrated
[here](../index.html#geode-cluster-configuration-bootstrapping).

Although, for this example, we are going to use the tools provided with
VMware GemFire, i.e. *Gfsh* (VMware GemFire Shell) for reasons that will
become apparent later.

You need to <a
href="https://geode.apache.org/releases/">download</a> and <a
href="https://geode.apache.org/docs/guide/18/prereq_and_install.html">install</a>
a full distribution of VMware GemFire to make use of the provided tools.
After installation, you will need to set the <code>GEODE</code>
environment variable to the location of your installation. Additionally,
add <code>$GEODE/bin</code> to your system <code>$PATH</code>.

Once VMware GemFire has been successfully installed, you can open a
command prompt (terminal) and do:

Running Gfsh

``` highlight
$ echo $GEODE
/Users/jblum/pivdev/apache-geode-1.2.1


$ gfsh
    _________________________     __
   / _____/ ______/ ______/ /____/ /
  / /  __/ /___  /_____  / _____  /
 / /__/ / ____/  _____/ / /    / /
/______/_/      /______/_/    /_/    1.2.1

Monitor and Manage VMware GemFire
gfsh>
```

You are set to go.

For your convenience, a *Gfsh* shell script is provided to start a
cluster:

Gfsh shell script

``` highlight
# Gfsh shell script to start a simple GemFire/Geode cluster

start locator --name=LocatorOne --log-level=config
start server --name=ServerOne --log-level=config
```

Specifically, we are starting 1 Locator and 1 Server, all running with
the default ports.

Execute the *Gfsh* shell script using:

Run Gfsh shell script

``` highlight
gfsh>run --file=/path/to/spring-boot-data-geode/samples/boot/configuration/src/main/resources/geode/bin/start-simple-cluster.gfsh
1. Executing - start locator --name=LocatorOne --log-level=config

Starting a Geode Locator in /Users/jblum/pivdev/lab/LocatorOne...
....
Locator in /Users/jblum/pivdev/lab/LocatorOne on 10.99.199.24[10334] as LocatorOne is currently online.
Process ID: 68425
Uptime: 2 seconds
Geode Version: 1.2.1
Java Version: 1.8.0_192
Log File: /Users/jblum/pivdev/lab/LocatorOne/LocatorOne.log
JVM Arguments: -Dgemfire.log-level=config -Dgemfire.enable-cluster-configuration=true -Dgemfire.load-cluster-configuration-from-dir=false -Dgemfire.launcher.registerSignalHandlers=true -Djava.awt.headless=true -Dsun.rmi.dgc.server.gcInterval=9223372036854775806
Class-Path: /Users/jblum/pivdev/apache-geode-1.2.1/lib/geode-core-1.2.1.jar:/Users/jblum/pivdev/apache-geode-1.2.1/lib/geode-dependencies.jar

Successfully connected to: JMX Manager [host=10.99.199.24, port=1099]

Cluster configuration service is up and running.

2. Executing - start server --name=ServerOne --log-level=config

Starting a Geode Server in /Users/jblum/pivdev/lab/ServerOne...
.....
Server in /Users/jblum/pivdev/lab/ServerOne on 10.99.199.24[40404] as ServerOne is currently online.
Process ID: 68434
Uptime: 2 seconds
Geode Version: 1.2.1
Java Version: 1.8.0_192
Log File: /Users/jblum/pivdev/lab/ServerOne/ServerOne.log
JVM Arguments: -Dgemfire.default.locators=10.99.199.24[10334] -Dgemfire.use-cluster-configuration=true -Dgemfire.start-dev-rest-api=false -Dgemfire.log-level=config -XX:OnOutOfMemoryError=kill -KILL %p -Dgemfire.launcher.registerSignalHandlers=true -Djava.awt.headless=true -Dsun.rmi.dgc.server.gcInterval=9223372036854775806
Class-Path: /Users/jblum/pivdev/apache-geode-1.2.1/lib/geode-core-1.2.1.jar:/Users/jblum/pivdev/apache-geode-1.2.1/lib/geode-dependencies.jar
```

You will need to change the path to the
<code>spring-boot-data-geode/samples/boot/configuration</code> directory
in the <code>run --file=…​</code> <em>Gfsh</em> command above based on
where you git cloned the <code>spring-boot-data-geode</code> project to
your computer.

Now, our simple cluster with an VMware GemFire Locator and (Cache)
Server is running. We can verify by listing and describing the members:

List and Describe Members

``` highlight
gfsh>list members
   Name    | Id
---------- | ---------------------------------------------------
LocatorOne | 10.99.199.24(LocatorOne:68425:locator)<ec><v0>:1024
ServerOne  | 10.99.199.24(ServerOne:68434)<v1>:1025


gfsh>describe member --name=ServerOne
Name        : ServerOne
Id          : 10.99.199.24(ServerOne:68434)<v1>:1025
Host        : 10.99.199.24
Regions     :
PID         : 68434
Groups      :
Used Heap   : 27M
Max Heap    : 3641M
Working Dir : /Users/jblum/pivdev/lab/ServerOne
Log file    : /Users/jblum/pivdev/lab/ServerOne/ServerOne.log
Locators    : 10.99.199.24[10334]

Cache Server Information
Server Bind              : null
Server Port              : 40404
Running                  : true
Client Connections       : 0
```

What happens if we try to run the application now?

RegionNotFoundException

``` highlight
17:42:16.873 [main] ERROR o.s.b.SpringApplication - Application run failed
java.lang.IllegalStateException: Failed to execute ApplicationRunner
    ...
    at example.app.crm.CustomerServiceApplication.main(CustomerServiceApplication.java:51) [classes/:?]
Caused by: org.springframework.dao.DataAccessResourceFailureException: remote server on 10.99.199.24(SpringBasedCacheClientApplication:68473:loner):51142:f9f4573d:SpringBasedCacheClientApplication: While performing a remote query; nested exception is org.apache.geode.cache.client.ServerOperationException: remote server on 10.99.199.24(SpringBasedCacheClientApplication:68473:loner):51142:f9f4573d:SpringBasedCacheClientApplication: While performing a remote query
    at org.springframework.data.gemfire.GemfireCacheUtils.convertGemfireAccessException(GemfireCacheUtils.java:230) ~[spring-data-geode-2.0.14.RELEASE.jar:2.0.14.RELEASE]
    at org.springframework.data.gemfire.GemfireAccessor.convertGemFireAccessException(GemfireAccessor.java:91) ~[spring-data-geode-2.0.14.RELEASE.jar:2.0.14.RELEASE]
    at org.springframework.data.gemfire.GemfireTemplate.find(GemfireTemplate.java:329) ~[spring-data-geode-2.0.14.RELEASE.jar:2.0.14.RELEASE]
    at org.springframework.data.gemfire.repository.support.SimpleGemfireRepository.count(SimpleGemfireRepository.java:129) ~[spring-data-geode-2.0.14.RELEASE.jar:2.0.14.RELEASE]
    ...
    at example.app.crm.CustomerServiceApplication.lambda$runner$0(CustomerServiceApplication.java:59) ~[classes/:?]
    at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:783) ~[spring-boot-2.0.9.RELEASE.jar:2.0.9.RELEASE]
    ... 3 more
Caused by: org.apache.geode.cache.client.ServerOperationException: remote server on 10.99.199.24(SpringBasedCacheClientApplication:68473:loner):51142:f9f4573d:SpringBasedCacheClientApplication: While performing a remote query
    at org.apache.geode.cache.client.internal.AbstractOp.processChunkedResponse(AbstractOp.java:352) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.QueryOp$QueryOpImpl.processResponse(QueryOp.java:170) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.AbstractOp.processResponse(AbstractOp.java:230) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.AbstractOp.attempt(AbstractOp.java:394) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.AbstractOp.attemptReadResponse(AbstractOp.java:203) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.ConnectionImpl.execute(ConnectionImpl.java:275) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.pooling.PooledConnection.execute(PooledConnection.java:332) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.OpExecutorImpl.executeWithPossibleReAuthentication(OpExecutorImpl.java:900) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.OpExecutorImpl.execute(OpExecutorImpl.java:158) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.OpExecutorImpl.execute(OpExecutorImpl.java:115) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.PoolImpl.execute(PoolImpl.java:763) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.QueryOp.execute(QueryOp.java:58) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.ServerProxy.query(ServerProxy.java:70) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.query.internal.DefaultQuery.executeOnServer(DefaultQuery.java:456) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.query.internal.DefaultQuery.execute(DefaultQuery.java:338) ~[geode-core-1.2.1.jar:?]
    at org.springframework.data.gemfire.GemfireTemplate.find(GemfireTemplate.java:311) ~[spring-data-geode-2.0.14.RELEASE.jar:2.0.14.RELEASE]
    at org.springframework.data.gemfire.repository.support.SimpleGemfireRepository.count(SimpleGemfireRepository.java:129) ~[spring-data-geode-2.0.14.RELEASE.jar:2.0.14.RELEASE]
    ...
    at example.app.crm.CustomerServiceApplication.lambda$runner$0(CustomerServiceApplication.java:59) ~[classes/:?]
    at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:783) ~[spring-boot-2.0.9.RELEASE.jar:2.0.9.RELEASE]
    ... 3 more
Caused by: org.apache.geode.cache.query.RegionNotFoundException: Region not found:  /Customers
    at org.apache.geode.cache.query.internal.DefaultQuery.checkQueryOnPR(DefaultQuery.java:599) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.query.internal.DefaultQuery.execute(DefaultQuery.java:348) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.query.internal.DefaultQuery.execute(DefaultQuery.java:319) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.tier.sockets.BaseCommandQuery.processQueryUsingParams(BaseCommandQuery.java:121) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.tier.sockets.BaseCommandQuery.processQuery(BaseCommandQuery.java:65) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.tier.sockets.command.Query.cmdExecute(Query.java:91) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.tier.sockets.BaseCommand.execute(BaseCommand.java:165) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.tier.sockets.ServerConnection.doNormalMsg(ServerConnection.java:791) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.tier.sockets.ServerConnection.doOneMessage(ServerConnection.java:922) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.tier.sockets.ServerConnection.run(ServerConnection.java:1180) ~[geode-core-1.2.1.jar:?]
    ...
```

The application fails to run because we (deliberately) did not create a
corresponding, server-side, "Customers" Region. In order for a client to
send data via a client `PROXY` Region (a Region with no local state) to
a server in a cluster, at least one server in the cluster must have a
matching Region by name (i.e. "Customers").

Indeed, there are no Regions in the cluster:

List Regions

``` highlight
gfsh>list regions
No Regions Found
```

Of course, you could create the matching server-side, "Customers" Region
using *Gfsh*:

``` highlight
gfsh>create region --name=Customers --type=PARTITION
```

But, what if you have hundreds of application domain objects each
requiring a Region for persistence? It is not an unusual or unreasonable
requirement in any practical enterprise scale application.

While it is not a "convention" in Spring Boot for VMware GemFire (SBDG),
Spring Data for VMware GemFire (SDG) comes to our rescue. We simply only
need to enable cluster configuration from the client:

Enable Cluster Configuration

``` highlight
@SpringBootApplication
@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
@EnableClusterConfiguration(useHttp = true)
public class CustomerServiceApplication {
  // ...
}
```

That is, we additionally annotate our Customer Service application class
with SDG’s `@EnableClusterConfiguration` annotation. We have also set
the `useHttp` attribute to `true`. This sends the configuration metadata
from the client to the cluster via VMware GemFire’s Management REST API.

This is useful when your VMware GemFire cluster may be running behind a
firewall, such as on public cloud infrastructure. However, there are
other benefits to using HTTP as well. As stated, the client sends
configuration metadata to VMware GemFire’s Management REST interface,
which is a facade for the server-side Cluster Configuration Service. If
another peer (e.g. server) is added to the cluster as a member, then
this member will get the same configuration. If the entire cluster goes
down, it will have the same configuration when it is restarted.

SDG is careful not to stomp on existing Regions since those Regions may
have data already. Declaring the `@EnableClusterConfiguration`
annotation is a useful development-time feature, but it is recommended
that you explicitly define and declare your Regions in production
environments, either using *Gfsh* or Spring confg.

<p class="note"><strong>Note:</strong>
It is now possible to replace the SDG
<code>@EnableClusterConfiguration</code> annotation with SBDG’s
<code>@EnableClusterAware</code> annotation, which has the same effect
of pushing configuration metadata from the client to the server (or
cluster). Additionally, SBDG’s <code>@EnableClusterAware</code>
annotation makes it unnecessary to explicitly have to configure the
<code>clientRegionShortcut</code> on the SDG
<code>@EnableEntityDefinedRegions</code> annotation (or similar
annotation, e.g. SDG’s <code>@EnableCachingDefinedRegions</code>).
Finally, because the SBDG <code>@EnableClusterAware</code> annotation is
meta-annotated with SDG’s
<code>@EnableClusterConfiguration annotation</code> is automatically
configures the <code>useHttp</code> attribute to <code>true</code>.
</p>

Now, we can run our application again, and this time, it works!

Client/Server Run Successful

``` highlight
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.0.9.RELEASE)

Saving Customer [Customer(name=Jon Doe)]
Querying for Customer [SELECT * FROM /Customers WHERE name LIKE '%Doe']
Customer was [Customer(name=Jon Doe)]

Process finished with exit code 0
```

In the cluster (server-side), we will also see that the "Customers"
Region was created successfully:

List & Describe Regions

``` highlight
gfsh>list regions
List of regions
---------------
Customers


gfsh>describe region --name=/Customers
..........................................................
Name            : Customers
Data Policy     : partition
Hosting Members : ServerOne

Non-Default Attributes Shared By Hosting Members

 Type  |    Name     | Value
------ | ----------- | ---------
Region | size        | 1
       | data-policy | PARTITION
```

We see that the "Customers" Region has a size of 1, containing "Jon
Doe".

We can verify this by querying the "Customers" Region:

Query for all Customers

``` highlight
gfsh>query --query="SELECT customer.name FROM /Customers customer"
Result : true
Limit  : 100
Rows   : 1

Result
-------
Jon Doe
```

That was easy!



## Auto-configuration for VMware GemFire, Take Two

What may not be apparent in this example up to this point is how the
data got from the client to the server. Certainly, our client did send
`Jon Doe` to the server, but our `Customer` class is not
`java.io.Serializable`. So, how was an instance of `Customer` streamed
and sent from the client to the server then (it is using a Socket)?

Any object sent over a network, between two Java processes, or streamed
to/from disk, must be serializable, no exceptions!

Furthermore, when we started the cluster, we did not include any
application domain classes on the classpath of any server in the
cluster.

As further evidence, we an adjust our query slightly:

Invalid Query

``` highlight
gfsh>query --query="SELECT * FROM /Customers"
Message : Could not create an instance of a class example.app.crm.model.Customer
Result  : false
```

If you tried to perform a `get`, you would hit a similar error:

Region.get(key)

``` highlight
gfsh>get --region=/Customers --key=1 --key-class=java.lang.Long
Message : Could not create an instance of a class example.app.crm.model.Customer
Result  : false
```

So, how was the data sent then? How were we able to access the data
stored in the server(s) on the cluster with the OQL query
`SELECT customer.name FROM /Customers customer` as seen above?

Well, VMware GemFire provides 2 proprietary serialization formats in
addition to *Java Serialization*: [Data
Serialization](https://geode.apache.org/docs/guide/1.15/developing/data_serialization/gemfire_data_serialization.html)
and
[PDX](https://geode.apache.org/docs/guide/1.15/developing/data_serialization/gemfire_pdx_serialization.html),
or *Portable Data Exchange*.

While *Data Serialization* is more efficient, PDX is more flexible (i.e.
"portable"). PDX enables data to be queried in serialized form and is
the format used to support both Java and Native Clients (C++, C#)
simultaneously. Therefore, PDX is auto-configured in Spring Boot Data
Geode (SBDG) by default.

This is convenient since you may not want to implement
`java.io.Serializable` for all your application domain model types that
you store in VMware GemFire. In other cases, you may not even have
control over the types referred to by your application domain model
types to make them `Serializable`, such as when using a 3rd party
library.

So, SBDG auto-configures PDX and uses Spring Data Geode’s
`MappingPdxSerializer` as the `PdxSerializer` to de/serialize all
application domain model types.

If we disable PDX *auto-configuration*, we will see the effects of
trying to serialize a non-serializable type, `Customer`.

First, let’s back up a few steps and destroy the server-side "Customers"
Region:

Destroy "Customers" Region

``` highlight
gfsh>destroy region --name=/Customers
"/Customers"  destroyed successfully.


gfsh>list regions
No Regions Found
```

Then, we disable PDX *auto-configuration*:

Disable PDX Auto-configuration

``` highlight
@SpringBootApplication(exclude = PdxSerializationAutoConfiguration.class)
@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
@EnableClusterConfiguration(useHttp = true)
public class CustomerServiceApplication {
  // ...
}
```

When we re-run the application, we get the error we would expect:

NotSerializableException

``` highlight
Caused by: java.io.NotSerializableException: example.app.crm.model.Customer
    at java.io.ObjectOutputStream.writeObject0(ObjectOutputStream.java:1184) ~[?:1.8.0_192]
    at java.io.ObjectOutputStream.writeObject(ObjectOutputStream.java:348) ~[?:1.8.0_192]
    at org.apache.geode.internal.InternalDataSerializer.writeSerializableObject(InternalDataSerializer.java:2248) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.InternalDataSerializer.basicWriteObject(InternalDataSerializer.java:2123) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.DataSerializer.writeObject(DataSerializer.java:2936) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.util.BlobHelper.serializeTo(BlobHelper.java:66) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.tier.sockets.Message.serializeAndAddPart(Message.java:396) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.tier.sockets.Message.addObjPart(Message.java:340) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.tier.sockets.Message.addObjPart(Message.java:319) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.PutOp$PutOpImpl.<init>(PutOp.java:281) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.PutOp.execute(PutOp.java:66) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.ServerRegionProxy.put(ServerRegionProxy.java:162) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.LocalRegion.serverPut(LocalRegion.java:3006) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.LocalRegion.cacheWriteBeforePut(LocalRegion.java:3115) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.ProxyRegionMap.basicPut(ProxyRegionMap.java:222) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.LocalRegion.virtualPut(LocalRegion.java:5628) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.LocalRegionDataView.putEntry(LocalRegionDataView.java:151) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.LocalRegion.basicPut(LocalRegion.java:5057) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.LocalRegion.validatedPut(LocalRegion.java:1595) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.LocalRegion.put(LocalRegion.java:1582) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.AbstractRegion.put(AbstractRegion.java:325) ~[geode-core-1.2.1.jar:?]
    at org.springframework.data.gemfire.GemfireTemplate.put(GemfireTemplate.java:193) ~[spring-data-geode-2.0.14.RELEASE.jar:2.0.14.RELEASE]
    at org.springframework.data.gemfire.repository.support.SimpleGemfireRepository.save(SimpleGemfireRepository.java:86) ~[spring-data-geode-2.0.14.RELEASE.jar:2.0.14.RELEASE]
    ...
    at example.app.crm.CustomerServiceApplication.lambda$runner$0(CustomerServiceApplication.java:70) ~[spring-samples-boot-configuration-1.0.0.RELEASE.jar]
    at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:783) ~[spring-boot-2.0.9.RELEASE.jar:2.0.9.RELEASE]
    ...
```

Our "Customers" Region is recreated, but is empty:

Empty "Customers" Region

``` highlight
gfsh>list regions
List of regions
---------------
Customers


gfsh>describe region --name=/Customers
..........................................................
Name            : Customers
Data Policy     : partition
Hosting Members : ServerOne

Non-Default Attributes Shared By Hosting Members

 Type  |    Name     | Value
------ | ----------- | ---------
Region | size        | 0
       | data-policy | PARTITION
```

So, SBDG takes care of all your serialization needs without you having
to configure serialization or implement `java.io.Serializable` in all
your application domain model types, including types your application
domain model types might refer to, which may not be possible.

If you were not using SBDG, then you would need to enable PDX
serialization explicitly.

The PDX *auto-configuration* provided by SBDG is equivalent to:

Equivalent PDX Configuration

``` highlight
@SpringBootApplication
@ClientCacheApplication
@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
@EnableClusterConfiguration(useHttp = true)
@EnablePdx
public class CustomerServiceApplication {
  // ...
}
```

In addition to the `@ClientCacheApplication` annotation, you would need
to annotate the `CustomerServiceApplication` class with SDG’s
`@EnablePdx` annotation, which is responsible for configuring PDX
serialization and registering SDG’s `MappingPdxSerializer`.



## Securing the Client & Server

The last bit of *auto-configuration* provided by SBDG that we will look
at in this guide involves Security, and specifically,
Authentication/Authorization (Auth) along with Transport Layer Security
(TLS) using SSL.

In today’s age, Security is no laughing matter and making sure your
applications are secure is a first-class concern. This is why SBDG takes
Security very seriously and attempts to make this as simple as possible.
You are definitely encouraged to read the relevant
[chapter](../security.html#geode-security) in this
Reference Documentation on the provided Security \_auto-configuration
support.

We will now expand on our example to secure the client and server
processes, with both Auth and TLS using SSL, and then see how SBDG helps
us properly configure these concerns, easily and reliably.

### Securing the server

First, we must secure the cluster (i.e. the Locator and Server).

In a nutshell, when using the VMware GemFire API (with no help from
Spring), you must do the following:

1.  (Auth) Implement the `org.apache.geode.security.SecurityManager`
    interface.

2.  (Auth) Configure your custom `SecurityManager` using the VMware
    GemFire `security-manager` property in `gemfire.properties`.

3.  (Auth) Either create a `gfsecurity.properties` file and set the
    `security-username` and `security-password` properties, or…​

4.  (Auth) Implement the `org.apache.geode.security.AuthInitialize`
    interface and set the `security-peer-auth-init` property in
    `gemfire.properties` as described in [Implementing
    Authentication](https://geode.apache.org/docs/guide/1.15/managing/security/implementing_authentication.html)
    of the VMware GemFire User Guide.

5.  (SSL) Then, you must create Java KeyStore (jks) files for both the
    keystore and truststore used to configure the SSL Socket.

6.  (SSL) Configure the Java KeyStores using the VMware GemFire
    `ssl-keystore` and `ssl-truststore` properties in
    `gemfire.properties`.

7.  (SSL) If you secured your Java KeyStores (recommended) then you must
    additionally set the `ssl-keystore-password` and
    `ssl-truststore-password` properties.

8.  (SSL) Optionally, configure the VMware GemFire components that
    should be enabled with SSL using the `ssl-enabled-components`
    property (e.g. `locator` and `server` for client/server and Locator
    connections).

9.  Then launch the cluster, and its members using *Gfsh* in the proper
    order.

This is a lot of tedious work and if you get any bit of the
configuration wrong, then either your servers will fail to start
correctly, or worse, they will not be secure.

Fortunately, this sample provides *Gfsh* shell scripts to get you going:

Gfsh shell script to start a secure cluster

``` highlight
# Gfsh shell script to start a secure GemFire/Geode cluster

set variable --name=CLASSPATH --value=${SBDG_HOME}/apache-geode-extensions/build/libs/apache-geode-extensions-@project-version@.jar
set variable --name=GEMFIRE_PROPERTIES --value=${SBDG_HOME}/spring-geode-samples/boot/configuration/build/resources/main/geode/config/gemfire.properties

start locator --name=LocatorOne --classpath=${CLASSPATH} --properties-file=${GEMFIRE_PROPERTIES}
connect --user=test --password=test
start server --name=ServerOne --classpath=${CLASSPATH} --properties-file=${GEMFIRE_PROPERTIES}
```

<p class="note"><strong>Note:</strong>
SBDG does provide server-side, peer Security
<em>auto-configuration</em> support. However, you must then configure
and bootstrap your VMware GemFire servers with Spring. Again, an example
of configuring/bootstrapping VMware GemFire servers with Spring is
provided <a
href="../configuration-auto.html#geode-cluster-configuration-bootstrapping">here</a>.
</p>


### Securing the client

#### Authentication

If you were to run the client, Customer Service application when SSL is
not enabled, the application would throw the following error on startup:

AuthenticationRequiredException

``` highlight
15:26:10.598 [main] ERROR o.a.g.i.c.GemFireCacheImpl - org.apache.geode.security.AuthenticationRequiredException: No security credentials are provided
15:26:10.607 [main] ERROR o.s.b.SpringApplication - Application run failed
org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'runner' defined in example.app.crm.CustomerServiceApplication: Unsatisfied dependency expressed through method 'runner' parameter 0; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'customerRepository': Initialization of bean failed; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'Customers': Cannot resolve reference to bean 'gemfireCache' while setting bean property 'cache'; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'gemfireCache': FactoryBean threw exception on object creation; nested exception is java.lang.RuntimeException: Error occurred when initializing peer cache
    ....
    at org.springframework.boot.SpringApplication.run(SpringApplication.java:307) [spring-boot-2.0.9.RELEASE.jar:2.0.9.RELEASE]
    at example.app.crm.CustomerServiceApplication.main(CustomerServiceApplication.java:51) [main/:?]
....
Caused by: org.apache.geode.security.AuthenticationRequiredException: No security credentials are provided
    at org.apache.geode.internal.cache.tier.sockets.HandShake.readMessage(HandShake.java:1396) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.tier.sockets.HandShake.handshakeWithServer(HandShake.java:1251) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.ConnectionImpl.connect(ConnectionImpl.java:117) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.ConnectionFactoryImpl.createClientToServerConnection(ConnectionFactoryImpl.java:136) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.QueueManagerImpl.initializeConnections(QueueManagerImpl.java:466) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.QueueManagerImpl.start(QueueManagerImpl.java:303) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.PoolImpl.start(PoolImpl.java:343) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.PoolImpl.finishCreate(PoolImpl.java:173) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.PoolImpl.create(PoolImpl.java:159) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.PoolFactoryImpl.create(PoolFactoryImpl.java:321) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.GemFireCacheImpl.determineDefaultPool(GemFireCacheImpl.java:2922) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.GemFireCacheImpl.initializeDeclarativeCache(GemFireCacheImpl.java:1369) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.GemFireCacheImpl.initialize(GemFireCacheImpl.java:1195) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.GemFireCacheImpl.basicCreate(GemFireCacheImpl.java:758) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.GemFireCacheImpl.createClient(GemFireCacheImpl.java:731) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.ClientCacheFactory.basicCreate(ClientCacheFactory.java:262) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.ClientCacheFactory.create(ClientCacheFactory.java:212) ~[geode-core-1.2.1.jar:?]
    at org.springframework.data.gemfire.client.ClientCacheFactoryBean.createCache(ClientCacheFactoryBean.java:400) ~[spring-data-geode-2.0.14.RELEASE.jar:2.0.14.RELEASE]
    ...
```

Even though SBDG provides *auto-configuration* support for client
Security, and specifically Auth in this case, you still must supply a
username and password, minimally.

This is as easy as setting a username/password in Spring Boot
`application.properties` using Spring Data Geode’s (SDG) well-known and
documented properties:

Application security configuration properties

``` highlight
# Security configuration for Apache Geode using Spring Boot and Spring Data for Apache Geode (SDG) properties

spring.boot.data.gemfire.security.ssl.keystore.name=example-trusted-keystore.jks
spring.data.gemfire.security.username=test
spring.data.gemfire.security.password=test
spring.data.gemfire.security.ssl.keystore.password=s3cr3t
spring.data.gemfire.security.ssl.truststore.password=s3cr3t
```

The act of setting a username and password triggers the client Security
*auto-configuration* provided by SBDG. There are many steps to
configuring client Security in VMware GemFire properly, as there was on
the server. All you need to worry about is supplying the credentials.
Easy!

To include the `application-security.properties`, simply enable the
Spring "security" profile in your run configuration when running the
`CustomerServiceApplication` class:

Enable Spring "security" Profile

``` highlight
-Dspring.profiles.active=security
```

By doing so, the `application-security.properties` file containing the
configured username/password properties is included on application
startup and our application is able to authenticate with the cluster
successfully.

To illustrate that there is more to configuring Authentication than
simply setting a username/password, if you were to disable the client
Security *auto-configuration*:

Disabling Client Security Auto-configuration

``` highlight
@SpringBootApplication(exclude = ClientSecurityAutoConfiguration.class)
@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
@EnableClusterConfiguration(useHttp = true)
public class CustomerServiceApplication {
  // ...
}
```

Then, our application would not be able to authenticate with the
cluster, and again, an error would be thrown:

AuthenticationRequiredException

``` highlight
Caused by: org.apache.geode.security.AuthenticationRequiredException: No security credentials are provided
    at org.apache.geode.internal.cache.tier.sockets.HandShake.readMessage(HandShake.java:1396) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.tier.sockets.HandShake.handshakeWithServer(HandShake.java:1251) ~[geode-core-1.2.1.jar:?]
    ....
```

Without the support of SBDG’s client Security *auto-configuration*, you
would need to explicitly enable Security:

Explicitly Enable Security

``` highlight
@SpringBootApplication
@ClientCacheApplication
@EnableSecurity
@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
@EnableClusterConfiguration(useHttp = true)
public class CustomerServiceApplication {
  // ...
}
```

That is, in addition to the `@ClientCacheApplication` annotation, you
would still need to 1) set the username/password properties in Spring
Boot `application.properties` and 2) explicitly declare the
`@EnableSecurity` annotation.

Therefore, SBDG (with help from SDG, under-the-hood) does the heavy
lifting, automatically for you.

#### TLS with SSL

What about SSL?

With either SBDG SSL *auto-configuration* disabled:

Disable SSL Auto-configuration

``` highlight
@SpringBootApplication(exclude = SslAutoConfiguration.class)
@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
@EnableClusterConfiguration(useHttp = true)
public class CustomerServiceApplication {
  // ...
}
```

Or optionally, no explicit Java KeyStore configuration, iff necessary,
such as:

Java KeyStore Configuration for SSL using SBDG

``` highlight
spring.boot.data.gemfire.security.ssl.keystore.name=myTrustedKeyStore.jks
spring.data.gemfire.security.ssl.keystore.password=s3cr3t
spring.data.gemfire.security.ssl.truststore.password=s3cr3t
```

Or possibly:

Java KeyStore Configuration for SSL using SDG

``` highlight
spring.data.gemfire.security.ssl.keystore=/file/system/path/to/trusted-keystore.jks
spring.data.gemfire.security.ssl.keystore.password=s3cr3t
spring.data.gemfire.security.ssl.keystore.type=JKS
spring.data.gemfire.security.ssl.truststore=/file/system/path/to/trusted-keystore.jks
spring.data.gemfire.security.ssl.truststore.password=s3cr3t
```

Then, the application will throw the following error:

Connectivity Exception

``` highlight
Caused by: org.apache.geode.security.AuthenticationRequiredException: Server expecting SSL connection
    at org.apache.geode.internal.cache.tier.sockets.HandShake.handshakeWithServer(HandShake.java:1222) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.ConnectionImpl.connect(ConnectionImpl.java:117) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.ConnectionFactoryImpl.createClientToServerConnection(ConnectionFactoryImpl.java:136) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.QueueManagerImpl.initializeConnections(QueueManagerImpl.java:466) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.PoolImpl.start(PoolImpl.java:343) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.QueueManagerImpl.start(QueueManagerImpl.java:303) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.PoolImpl.finishCreate(PoolImpl.java:173) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.internal.PoolImpl.create(PoolImpl.java:159) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.PoolFactoryImpl.create(PoolFactoryImpl.java:321) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.GemFireCacheImpl.determineDefaultPool(GemFireCacheImpl.java:2922) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.GemFireCacheImpl.initializeDeclarativeCache(GemFireCacheImpl.java:1369) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.GemFireCacheImpl.initialize(GemFireCacheImpl.java:1195) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.GemFireCacheImpl.basicCreate(GemFireCacheImpl.java:758) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.internal.cache.GemFireCacheImpl.createClient(GemFireCacheImpl.java:731) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.ClientCacheFactory.basicCreate(ClientCacheFactory.java:262) ~[geode-core-1.2.1.jar:?]
    at org.apache.geode.cache.client.ClientCacheFactory.create(ClientCacheFactory.java:212) ~[geode-core-1.2.1.jar:?]
```

With very minimal to no configuration, SBDG can automatically configure
SSL, as explained in [Transport Layer Security using
SSL](../index.html#geode-security-ssl) section under Security. In fact,
no configuration is actually required if the trusted Java KeyStore file
is named "trusted.keystore", is in the root of the classpath and the JKS
file is unsecured, i.e. not protected by a password.

However, if the you named your Java KeyStore (JKS) file something other
than "trusted.keystore", then you can set the
`spring-boot-data-gemfire.security.ssl.keystore.name` property:

Delcaring the Java KeyStore filename

``` highlight
spring.boot.data.gemfire.security.ssl.keystore.name=myTrustedKeyStore.jks
```

If you Java KeyStore (JKS) file is secure, then you can specify the
password:

Java KeyStore Configuration for SSL using SBDG

``` highlight
spring.data.gemfire.security.ssl.keystore.password=s3cr3t
spring.data.gemfire.security.ssl.truststore.password=s3cr3t
```

Or, if the Java KeyStore files for SSL are completely of a different
variety:

Complete Java KeyStore Configuration for SSL using SDG

``` highlight
spring.data.gemfire.security.ssl.keystore=/file/system/path/to/trusted-keystore.pks11
spring.data.gemfire.security.ssl.keystore.password=s3cr3t
spring.data.gemfire.security.ssl.keystore.type=PKS11
spring.data.gemfire.security.ssl.truststore=/file/system/path/to/trusted-keystore.pks11
spring.data.gemfire.security.ssl.truststore.password=differentS3cr3t
```

Again, you can customize your configuration as much as needed or let
SBDG handle things by following the defaults.

The SBDG SSL *auto-configuration* is equivalent to the following in SDG:

SDG SSL Configuration

``` highlight
@SpringBootApplication
@ClientCacheApplication
@EnableSsl
@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
@EnableClusterConfiguration(useHttp = true)
public class CustomerServiceApplication {
  // ...
}
```

In addition to the `@ClientCacheApplication` annotation, you must
additional declare the `@EnableSsl` annotation along with the
`spring.data.gemfire.security.ssl.keystore` and
`spring.data.gemfire.security.ssl.truststore` properties in Spring Boot
`application.properties`.

In total, it is just simpler to start with the defaults and then
customize bits of the configuration as your UC and application
requirements grow.



## Conclusion

Hopefully this guide has now given you a better understanding of what
the *auto-configuration* support provided by Spring Boot for VMware
GemFire (SBDG) is giving you when developing VMware GemFire applications
with Spring.

In this guide, we have seen that SBDG provides *auto-configuration*
support for the following Spring Data for VMware GemFire’s (SDG)
annotations:

- `@ClientCacheApplication`

- `@EnableGemfireRepositories`

- `@EnablePdx`

- `@EnableSecurity`

- `@EnableSsl`

While we also presented these additional SDG annotations, which are not
auto-configured by SDG:

- `@EnableEntityDefinedRegions`

- `@EnableClusterConfiguration`

They are optional and were shown for pure convenience.

Technically, the only annotation you are required to declare when SBDG
is on the classpath, is `@SpringBootApplication`, leaving our Customer
Service application declaration as simple as:

Basic CustomerServiceApplication class

``` highlight
@SpringBootApplication
public class CustomerServiceApplication {
  // ...
}
```

That is it! That is all! However, this guide is by no means complete.

This guide does not cover all the *auto-configuration* provided by SBDG.
SBDG additionally provides *auto-configuration* for Spring’s Cache
Abstraction, Continuous Query (CQ), Function Execution &
Implementations, `GemfireTemplates` and Spring Session. However, the
concepts and effects are similar to what has been presented above.

We leave it as an exercise for you to explore and understand the
remaining *auto-configuration* bits using this guide as a reference for
your learning purposes.


