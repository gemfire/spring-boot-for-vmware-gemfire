# Multi-Site Caching with Spring

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

- [Background](#geode-samples-caching-multisite-background)
  - [WAN Topology](#geode-samples-caching-multisite-wan-background)
- [Example](#geode-samples-caching-multisite-example)
  - [`Customer`
    class](#geode-samples-caching-multisite-example-customer)
  - [`CustomerService`
    class](#geode-samples-caching-multisite-example-customerservice)
  - [`CustomerController`
    class](#geode-samples-caching-multisite-example-customercontroller)
  - [Spring Boot, VMware GemFire `ClientCache`
    application](#geode-samples-caching-multisite-example-client-app)
  - [Spring Boot, VMware GemFire `CacheServer`
    application](#geode-samples-caching-multisite-example-server-app)
  - [Cluster/Server
    Configuration](#geode-samples-caching-multisite-example-server-configuration)
- [Run the Example](#geode-samples-caching-multisite-example-run)
  - [Start the
    Clusters](#geode-samples-caching-multisite-example-run-clusters)
  - [Start the
    Clients](#geode-samples-caching-multisite-example-run-clients)
- [Summary](#geode-samples-caching-multisite-summary)


This guide walks you through building a simple Spring Boot application
using [Spring’s Cache
Abstraction](https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache)
backed by VMware GemFire as the *caching provider* for *Multi-Site
Caching*.

It is assumed that the reader is familiar with the Spring *programming
model*. While a user only requires a cursory knowledge of Spring’s
*Cache Abstraction*, thorough knowledge of VMware GemFire’s multi-site
data management capabilities using a [WAN
topology](https://geode.apache.org/docs/guide/1.15/topologies_and_comm/multi_site_configuration/chapter_overview.html)
would be beneficial.

Additionally, this sample builds on the concepts introduced in the
[Look-Aside Caching](caching-look-aside.html) sample. If you are new to
caching concepts, or caching in general, it would be helpful to start by
reading the guide on *Look-Aside Caching* before continuing with this
guide.

Let’s begin.

Refer to the <a
href="../index.html#geode-caching-provider-multi-site-caching">Multi-Site
Caching</a> section in the <a
href="../index.html#geode-caching-provider">Caching with VMware
GemFire</a> chapter in the reference documentation for more
information.


## Background

Without a doubt, applying 1 or more patterns of caching to your
application is a powerful technique to improve the users' experience
while using your application.

*Look-Aside Caching* is easily the most common pattern of caching
because it is the least invasive form of caching. You can easily enable
or disable *Look-Aside Caching* without a single code change to your
application, especially when using Spring’s *Cache Abstraction*. Only a
configuration change is needed. You also see many implementations of
*Look-Aside Caching* in other technologies, beyond just software, such
as CPU design.

The general pattern of *Look-Aside Caching* appears as follows:

General Pattern for Look-Aside Caching

``` highlight
class CustomerService {

    private CustomerRepository<Customer, ?> customerRepository;

    // ...

    Customer findBy(Account account) {

        Customer customerByAccount = cache.get(account.getNumber());

        if (customerByAccount == null) {
            customerByAccount = customerRepository.findByAccountNumber(account.getNumber());
            cache.put(account.getNumber(), customer);
        }

        return customerByAccount;
    }
}
```

The cache could easily be implemented using a
`java.util.concurrent.ConcurrentHashMap`:

Look-Aside Cache using a `ConcurrentHashMap`

``` highlight
import java.util.concurrent.ConcurrentHashMap;

class CustomerService {

    private static final Map<?, Customer> cache = new ConcurrentHashMap<>();

    private CustomerRepository<Customer, ?> customerRepository;

    // ...

    Customer findBy(Account account) {

        return cache.computeIfAbsent(account.getNumber(), accountNumber ->
            customerRepository.findByAccountNumber(accountNumber));
    }
}
```

Spring’s *Cache Abstraction* makes the development task of applying the
*Look-Aside Caching* pattern, to your application service methods even
easier:

Look-Aside Caching with Spring

``` highlight
@Service
class CustomerService {

    // A Spring Data [Crud]Repository interface (DAO) extension used to invoke basic data access operations
    // and simple queries on Customers
    @Autowired
    private CustomerRepository<Customer, ?> customerRepository;

    // ...

    @Cacheable(name = "CustomersByAccount", key="#account.number")
    @NonNull Customer findBy(@NonNull Account account) {
        return customerRepository.findByAccountNumber(account.getNumber());
    }


}
```

Not only is Spring’s *Cache Abstraction* useful for applying the
*Look-Aside Caching* pattern to application components, such as
application service methods, it also provides an appropriate abstraction
allowing you to use any *caching provider* implementation you want,
matching the unique requirements (or SLAs) of your use case.

Various caching providers include, but are not limited to:
`java.util.concurrent.ConcurrentMap`, VMware GemFire, Caffeine,
Couchbase, Ehcache, Hazelcast, Infinispan, Redis, or any *JCache*
(JSR-107) provider implementation. See the core Spring Framework’s
documentation on [Configuring the Cache
Storage](https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache-store-configuration)
along with Spring Boot’s [Supported Cache
Providers](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-caching-provider)
for more details.

<p class="note"><strong>Note:</strong>
Not only is it easy to swap the underlying
<em>caching provider</em> in the Spring-based code snippet above, it is
also a simple matter to change the underlying <em>System of Record</em>
(SOR), or persistent, data management technology given the Spring Data
<em>Repository</em> interface is a common abstraction for persistent
storage (e.g. JDBC, JPA or R2DBC for an RDBMS, MongoDB for Document
store, Neo4j for a Graph store, VMware GemFire for a Key/Value store,
and so on). Some data management technologies can be used as both a
<em>System or Record</em> (i.e. persistent data store) as well as a
<em>Cache</em>, e.g. VMware GemFire.
</p>

See the Spring Boot for VMware GemFire (SBDG)
chapter on <a href="../index.html#geode-caching-provider">Caching</a>
for more information on how VMware GemFire can be used to enable
different caching patterns in your Spring Boot applications.

### WAN Topology

Not all *caching providers* are equal. As noted above, VMware GemFire
can be used as a *System of Record* (SOR; i.e. persistent data store) as
well as a *Cache*. Not only that, but VMware GemFire can be configured
to run in different topologies: client/server, peer-to-peer (P2P) and
WAN, or a multi-site configuration.

The *Look-Aside Caching* pattern is not only the least invasive pattern
of caching, it can also be easily extended or composed with other
caching patterns, typically without any code changes to the application.
Of course, this all depends on the capabilities of the underlying
*caching provider*, or more generally, data management technology. With
VMware GemFire, this is very easy.

When *Look-Aside Caching* is extended or combined with other caching
patterns (e.g. *Inline*, *Near*, *Multi-Site*, or a combination of), the
value realized from your caching solution is greatly enhanced.

By implementing *Multi-Site Caching* with VMware GemFire’s WAN topology
and configuration, your application’s data is geographically
distributed.

![Multi Site Caching Pattern](./images/Multi-Site-Caching-Pattern.png)

<p class="note"><strong>Note:</strong>
In the image above, you see 2 sites replicating and
sharing data across a Wide Area Network (WAN), such as an Intranet
(VPN), or even over the Internet. This replication can be 1-way
(<em>Active-Passive</em>) or can be 2-way (<em>Active-Active</em>). By
default, it uses asynchronous communication across sites. Guaranteed
message delivery along with persistent message queues can be configured.
From a Spring Boot application’s perspective, this simply appears as
<em>Look-Aside Caching</em>, but all the logistics of data replication
and sharing between the sites over WAN is managed by VMware
GemFire.
</p>

You can imagine that having data redundantly distributed geographical is
useful in Disaster Recovery (DR) situations. However, *Multi-Site
Caching* has many other useful benefits.

The 2 most common patterns, or uses for Multi-Site, WAN topologies are
*Active-Active* and *Active-Passive*.

*Active-Passive* is akin to the Disaster Recovery (DR) scenario
mentioned above. If site A goes down for whatever reason (e.g. a natural
disaster, or a major network outage) and becomes unreachable, then with
a flip of the switch, all user requests are routed to the other cluster,
site B, operating in a different data center, which is often times also
in a different geographic location. This pattern is also useful for
*Blue-Green* deployments.

With *Active-Active*, the data and operational load are uniformly
distributed based on locality. For example, you might service European,
APJ and the Americas through different data centers, with different
clusters. Obviously, this does not necessarily have to be so widely
dispersed, and could be further focused by region within (nation)
states. However, whatever the capacity, you can imagine this arrangement
being useful to distribute load, reduce latency and improve throughput
across your system. This pattern is useful for increasing capacity and
*High Availability* (HA).

However, there are many factors and trade-offs to consider, such as the
level of consistency, redundancy (e.g. is all data replicated to other
sites or is some data filtered), how is the data and related data
accessed (e.g. queries), etc.

These concerns are well beyond the scope of this document since
requirements and SLAs vary greatly from use case to use case. Instead,
this guide will focus on giving you a basic understanding of outcome we
are trying to achieve by using the *Multi-Site Caching* pattern along
with giving you the configuration required to setup the arrangement.

## Example

For our example, we are going to build on the Spring code snippet above,
using the `CustomerService` class with the *Look-Aside Caching* pattern
applied, then enhance the caching solution with an *Active-Active*,
Multi-Site WAN topology.

However, instead of looking up `Customers` by `Account` number, we are
simply going to lookup `Customers` by "name". Given this requirement,
our `Customer` class has a very simple design:

### `Customer` class

Customer class

``` highlight
@Region("Customers")
@EqualsAndHashCode
@ToString(of = "name")
@RequiredArgsConstructor(staticName = "newCustomer")
public class Customer {

    @Id @Getter @NonNull
    private Long id;

    @Getter @NonNull
    private String name;

}
```

<p class="note"><strong>Note:</strong>
The <code>Customer</code> class uses <a
href="https://projectlombok.org/">Project Lombok</a> to simplify the
implementation.
</p>

The `Customer` class is mapped to the "Customers" `Region` using the SDG
`@Region` mapping annotation. The `@Region` annotation is very similar
in purpose to the JPA `@Entity` and `@Table` annotations. A `Customer`
is very simply defined in terms of an `id` and `name`, which will be
used to lookup a `Customer`.

### `CustomerService` class

Next, we implement a `CustomerService` class to service interactions on
`Customers`:

CustomerService class

``` highlight
@Service
public class CustomerService {

    private static final long SLEEP_IN_SECONDS = 5;

    private final AtomicBoolean cacheMiss = new AtomicBoolean(false);

    private final AtomicLong customerId = new AtomicLong(0L);

    private volatile Long sleepInSeconds;

    @Cacheable("CustomersByName")
    public Customer findBy(String name) {
        setCacheMiss();
        ThreadUtils.safeSleep(name, Duration.ofSeconds(getSleepInSeconds()));
        return Customer.newCustomer(this.customerId.incrementAndGet(), name);
    }

    public boolean isCacheMiss() {
        return this.cacheMiss.compareAndSet(true, false);
    }

    protected void setCacheMiss() {
        this.cacheMiss.set(true);
    }

    public Long getSleepInSeconds() {

        Long sleepInSeconds = this.sleepInSeconds;

        return sleepInSeconds != null ? sleepInSeconds : SLEEP_IN_SECONDS;
    }

    public void setSleepInSeconds(Long seconds) {
        this.sleepInSeconds = seconds;
    }
}
```

The `CustomerService` class is a Spring `@Service` component and bean in
the `ApplicationContext`. It has a single `findBy(:String):Customer`
operation used to lookup a `Customer` by "name":

CustomerService.findBy(..) method

``` highlight
    @Cacheable("CustomersByName")
    public Customer findBy(String name) {
        setCacheMiss();
        ThreadUtils.safeSleep(name, Duration.ofSeconds(getSleepInSeconds()));
        return Customer.newCustomer(this.customerId.incrementAndGet(), name);
    }
```

The `findBy(..)` method is annotated with Spring’s `@Cacheable`
annotation, declaring the "CustomersByName" cache to cache the results
of the `findBy(..)` method. Since a `Customer’s` "name" is not likely to
change often, it is a prime candidate for caching.

`@Cacheable` works by first searching for the `Customer` by "name" in
the "CustomerByName" cache. If an entry is found, then the cached value
(i.e. `Customer`) is returned immediately, without invoking the
`findBy(..)` method. Otherwise, the `findBy(..)` method is invoked to
lookup the `Customer` by "name". When the `findBy(..)` method returns,
assuming it does not return a `null` value, then `@Cacheable` logic will
store the `Customer` in the cache keyed by the `Customer’s` "name" (i.e.
the "name" argument passed to the `findBy(..)` method).

To make the `findBy(..)` operation appear to be expensive (either time
or resource consuming), we add a safe Thread sleep call. Otherwise, the
`findBy(..)` method simply constructs a new `Customer` with the given
"name" and returns it.

The `CustomerService` class contains a `isCacheMiss()` method to
determine whether the `Customer` was found in the cache mapped to the
given "name", or if the `findBy(..)` method had to be invoked (i.e. a
*cache miss*).

What is not apparent from looking at the `findBy(..)` service method is
how the *Look-Aside Cache* pattern is decorated, or enhanced by
*Multi-Site (WAN) Caching*. It turns out to be all in the configuration
as we will see further below.

### `CustomerController` class

Finally, we add `CustomerController` class exposing a simple REST
interface to the Web application:

CustomerController class

``` highlight
@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private Environment environment;

    @GetMapping("/customers/{name}")
    public CustomerHolder searchBy(@PathVariable String name) {

        return CustomerHolder.from(this.customerService.findBy(name))
            .setCacheMiss(this.customerService.isCacheMiss());
    }

    @GetMapping("/ping")
    public String pingPong() {
        return "PONG";
    }

    @GetMapping("/")
    public String home() {

        return String.format("%s is running!",
            environment.getProperty("spring.application.name", "UNKNOWN"));
    }

    public static class CustomerHolder {

        public static CustomerHolder from(Customer customer) {
            return new CustomerHolder(customer);
        }

        private boolean cacheMiss = true;

        private final Customer customer;

        protected CustomerHolder(Customer customer) {

            Assert.notNull(customer, "Customer must not be null");

            this.customer = customer;
        }

        public CustomerHolder setCacheMiss(boolean cacheMiss) {
            this.cacheMiss = cacheMiss;
            return this;

        }

        public boolean isCacheMiss() {
            return this.cacheMiss;
        }

        public Customer getCustomer() {
            return customer;
        }
    }
}
```

The `CustomerController` is a Spring Web MVC `@RestController`, which
uses the `CustomerService` class, and allows users to search for
`Customers` by "name" using a Web browser.

### Spring Boot, VMware GemFire `ClientCache` application

So far so good, but we need a Spring Boot, Java main class to configure
and bootstrap our application:

`ClientCache` application

``` highlight
@SpringBootApplication
@SuppressWarnings("unused")
public class BootGeodeMultiSiteCachingClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootGeodeMultiSiteCachingClientApplication.class, args);
    }

    @Configuration
    @EnableCachingDefinedRegions
    static class GeodeClientConfiguration { }

}
```

The main Java class is annotated with `@SpringBootApplication` making it
a proper Spring Boot application. Additionally, we declare SDG’s
`@EnableCachingDefinedRegions` annotation to create VMware GemFire
`Regions` to back the caches used by the application (e.g.
"CustomersByName"), which are declared on the application’s components
(e.g. `CustomerService`) using Spring’s caching annotations, such as
`@Cacheable`.

It is also possible to annotate your Spring
application components with JSR-107, <em>JCache</em> API annotations
since Spring recognizes <em>JCache</em> annotations as well. See the <a
href="https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache-jsr-107">reference
documentation</a> for further guidance.

Since SBDG’s `org.springframework.geode:spring-geode-starter` is on the
application classpath, SBDG will auto-configure a `ClientCache` instance
by default, as
[documented](../index.html#geode-clientcache-applications).

The `@EnableCachingDefinedRegions` annotation will create client PROXY
`Regions` by default, which forwards all data access operations to a
server. For that, we need configuration to connect the client to a
server, or a cluster, which we declare in a Spring Boot
`application.properties` file:

Client `application.properties`

``` highlight
server.port=8080
spring.application.name=BootGeodeMultiSiteCachingClientApplication-Site1
spring.data.gemfire.pool.locators=localhost[11235]
```

The `spring.data.gemfire.pool.locators` property configures the
connection from the client to the embedded Locator running in the
cluster/server, which in this case is listening on host/port:
"localhost\[11235\]". We will see the server-side configuration below.

It is possible to connect the client directly to a
server in the cluster by configuring the
<code>spring.data.gemfire.pool.servers</code> property. However, doing
so severely limits the client’s ability to leverage features such as
single-hop data access and fail-over. It is more common (and
recommended) to connect clients indirectly to cluster servers using
Locators.

It is also possible and recommended that you start
multiple Locators in the cluster and have your clients connect to
multiple Locators for redundancy. The
<code>spring.data.gemfire.pool.locators</code> property accepts multiple
hosts/ports using a comma-delimited format:
<code>host1[port1],host2[port2],...,hostN[portN]</code>.

Since the Spring Boot application is a Web application, we set the
server port of the embedded Web Server, which runs Apache Tomcat by
default when `org.springframework.boot:spring-boot-starter-web` is added
to the application classpath.

### Spring Boot, VMware GemFire `CacheServer` application

Of course, to make *Multi-Site Caching* possible, we need to employ the
client/server and WAN topologies.

Refer to the documentation on <a
href="https://geode.apache.org/docs/guide/1.15/topologies_and_comm/cs_configuration/chapter_overview.html">Client/Server
Configuration</a> as well as <a
href="https://geode.apache.org/docs/guide/1.15/topologies_and_comm/multi_site_configuration/chapter_overview.html">Multi-site
(WAN) Configuration</a> for more details.

Naturally, we use Spring Boot to configure and bootstrap an VMware
GemFire server:

`CacheServer` application

``` highlight
@SpringBootApplication
@SuppressWarnings("unused")
public class BootGeodeMultiSiteCachingServerApplication {

    private static final boolean PERSISTENT = false;

    private static final int GATEWAY_RECEIVER_END_PORT = 29779;
    private static final int GATEWAY_RECEIVER_START_PORT = 13339;

    private static final String CUSTOMERS_BY_NAME_REGION = "CustomersByName";
    private static final String GATEWAY_RECEIVER_HOSTNAME_FOR_SENDERS = "localhost";

    public static void main(String[] args) {

        new SpringApplicationBuilder(BootGeodeMultiSiteCachingServerApplication.class)
            .web(WebApplicationType.NONE)
            .build()
            .run(args);
    }

    @CacheServerApplication(name = "BootGeodeMultiSiteCachingServerApplication", port = 0)
    static class GeodeServerConfiguration {

        @Bean(CUSTOMERS_BY_NAME_REGION)
        ReplicatedRegionFactoryBean<String, Customer> customersByNameRegion(Cache cache,
                @Autowired(required = false) List<RegionConfigurer> regionConfigurers) {

            ReplicatedRegionFactoryBean<String, Customer> customersByName = new ReplicatedRegionFactoryBean<>();

            customersByName.setCache(cache);
            customersByName.setPersistent(PERSISTENT);
            customersByName.setRegionConfigurers(regionConfigurers);

            return customersByName;
        }

        @Bean
        ApplicationRunner geodeClusterObjectsBootstrappedAssertionRunner(Environment environment, Cache cache,
                Region<?, ?> customersByName, GatewayReceiver gatewayReceiver, GatewaySender gatewaySender) {

            return args -> {

                assertThat(cache).isNotNull();
                assertThat(cache.getName()).startsWith(BootGeodeMultiSiteCachingServerApplication.class.getSimpleName());
                assertThat(customersByName).isNotNull();
                assertThat(customersByName.getAttributes()).isNotNull();
                assertThat(customersByName.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.REPLICATE);
                assertThat(customersByName.getAttributes().getGatewaySenderIds()).containsExactly(gatewaySender.getId());
                assertThat(customersByName.getName()).isEqualTo(CUSTOMERS_BY_NAME_REGION);
                assertThat(customersByName.getRegionService()).isEqualTo(cache);
                assertThat(cache.getRegion(RegionUtils.toRegionPath(CUSTOMERS_BY_NAME_REGION))).isEqualTo(customersByName);
                assertThat(gatewayReceiver).isNotNull();
                assertThat(gatewayReceiver.isRunning()).isTrue();
                assertThat(cache.getGatewayReceivers()).containsExactly(gatewayReceiver);
                assertThat(gatewaySender).isNotNull();
                assertThat(gatewaySender.isRunning()).isTrue();
                assertThat(cache.getGatewaySenders().stream().map(GatewaySender::getId).collect(Collectors.toSet()))
                    .containsExactly(gatewaySender.getId());

                System.err.printf("Apache Geode Cluster [%s] configured and bootstrapped successfully!%n",
                    environment.getProperty("spring.application.name", "UNKNOWN"));
            };
        }
    }

    @Configuration
    @EnableLocator
    @EnableManager(start = true)
    @Profile("locator-manager")
    static class GeodeLocatorManagerConfiguration { }

    @Configuration
    @Profile("gateway-receiver")
    static class GeodeGatewayReceiverConfiguration {

        @Bean
        GatewayReceiverFactoryBean gatewayReceiver(Cache cache) {

            GatewayReceiverFactoryBean gatewayReceiver = new GatewayReceiverFactoryBean(cache);

            gatewayReceiver.setHostnameForSenders(GATEWAY_RECEIVER_HOSTNAME_FOR_SENDERS);
            gatewayReceiver.setStartPort(GATEWAY_RECEIVER_START_PORT);
            gatewayReceiver.setEndPort(GATEWAY_RECEIVER_END_PORT);

            return gatewayReceiver;
        }
    }

    @Configuration
    @Profile("gateway-sender")
    static class GeodeGatewaySenderConfiguration {

        @Bean
        GatewaySenderFactoryBean customersByNameGatewaySender(Cache cache,
                @Value("${geode.distributed-system.remote.id:1}") int remoteDistributedSystemId) {

            GatewaySenderFactoryBean gatewaySender = new GatewaySenderFactoryBean(cache);

            gatewaySender.setPersistent(PERSISTENT);
            gatewaySender.setRemoteDistributedSystemId(remoteDistributedSystemId);

            return gatewaySender;
        }

        @Bean
        RegionConfigurer customersByNameConfigurer(GatewaySender gatewaySender) {

            return new RegionConfigurer() {

                @Override
                public void configure(String beanName, PeerRegionFactoryBean<?, ?> regionBean) {

                    if (CUSTOMERS_BY_NAME_REGION.equals(beanName)) {
                        regionBean.setGatewaySenders(ArrayUtils.asArray(gatewaySender));
                    }
                }
            };
        }
    }
}
```

Technically, this server is a mini-cluster since it contains an embedded
Locator along with the CacheServer in the same JVM process. So, you are
getting 2 for the price of 1:

![Multi Node To Single Node
Cluster](./images/Multi-Node-To-Single-Node-Cluster.png)

<p class="note"><strong>Note:</strong>
In the cluster on the left, the Locator and Server
are separate, standalone JVM processes. On the right, the Locator and
Server are part of the same JVM process.
</p>

Typically, for high availability (HA) and resiliency purposes, you
should use redundant, standalone Locator JVM processes. However, for
example and testing purposes, using an embedded Locator is quite useful.

Like our client, the server needs additional configuration to setup the
server to participate in a WAN topology along with just configuring the
cluster:

Server `application.properties`

``` highlight
gemfire.distributed-system-id=10
gemfire.remote-locators=localhost[12480]
geode.distributed-system.remote.id=20
spring.application.name=BootGeodeMultiSiteCachingServerApplication-Site1
#spring.profiles.include=locator-manager,gateway-receiver,gateway-sender
spring.data.gemfire.locator.port=11235
spring.data.gemfire.manager.port=1199
```

Here, we see the declaration of the `gemfire.distributed-system-id`, set
to `10`, which identifies this cluster in the Multi-Site/Multi-Cluster,
WAN topology. The `distributed-system-id` can be any `String` as long as
it’s unique among the clusters in the Multi-Site configuration.

We configure this cluster to connect to the remote cluster by using the
`gemfire.remote-locators` property, set to `localhost[12480]`, the
host/port of the embedded Locator in cluster \#2. We also indicate the
remote cluster’s `distributed-system-id` (i.e. `20`), which is required
when setting up the `GatewaySender` from this cluster (#1) to the
`GatewayReceiver` of the remote cluster (#2, identified as `20`).

Each member in the cluster must have a unique name, therefore we set the
`spring.application.name` property, which also serves to name our peer
cache instance and member of the cluster. This is especially necessary
when running the Locator and CacheServer as standalone processes in the
same cluster.

To allow us to connect to the cluster from *Gfsh* (VMware GemFire Shell
tool), we additionally configure the Manager port. Technically, the
Manager allows any JMX compliant application (e.g. *JConsole*,
*JVisualVM*, etc) to connect to the cluster. The Manager, like the
Locator, is an embedded service, which could also be configured to run
as standalone JVM process (recommended). By default, in most production
VMware GemFire clusters, the standalone Locators also serve as Managers.

Until cluster \#2 comes online and cluster \#1 is able to connect to
cluster \#2, you will see messages in the log file like:

``` highlight
....  Locator discovery task could not exchange
locator information localhost[11235] with localhost[12480] after 6 retry attempts. Retrying in 10000 ms.
```

After both clusters are online and available, and cluster \#1 is able to
connect to cluster \#2, you will then see the following log message:

``` highlight
....  Locator discovery task exchanged locator
information localhost[11235] with localhost[12480]: {20=[localhost[12480]], 10=[localhost[11235]]}.
```

We can reuse the `BootGeodeMultiSiteCachingServerApplication` class to
configure and bootstrap additional clusters (e.g. cluster \#2 in our
example), simply by varying the configuration.

The following is the Spring Boot `application.properties` for cluster
\#2 using the same class:

Cluster \#2 Spring Boot `application.properties`

``` highlight
gemfire.distributed-system-id=20
gemfire.remote-locators=localhost[11235]
geode.distributed-system.remote.id=10
spring.application.name=BootGeodeMultiSiteCachingServerApplication-Site2
#spring.profiles.include=locator-manager,gateway-receiver,gateway-sender
spring.data.gemfire.locator.port=12480
spring.data.gemfire.manager.port=2299
```

Compare and contrast this with the Spring Boot `application.properties`
for cluster \#1 above.

### Cluster/Server Configuration

Let’s break down the `BootGeodeMultiSiteCachingServerApplication` class
configuration in more detail.

#### `CacheServer` and "*CustomersByName*" `Region` Configuration

This first bit of configuration creates a peer `Cache`, a `CacheServer`
and the "CustomersByName" REPLICATE `Region`:

CacheServer & Region

``` highlight
    @CacheServerApplication(name = "BootGeodeMultiSiteCachingServerApplication", port = 0)
    static class GeodeServerConfiguration {

        @Bean(CUSTOMERS_BY_NAME_REGION)
        ReplicatedRegionFactoryBean<String, Customer> customersByNameRegion(Cache cache,
                @Autowired(required = false) List<RegionConfigurer> regionConfigurers) {

            ReplicatedRegionFactoryBean<String, Customer> customersByName = new ReplicatedRegionFactoryBean<>();

            customersByName.setCache(cache);
            customersByName.setPersistent(PERSISTENT);
            customersByName.setRegionConfigurers(regionConfigurers);

            return customersByName;
        }

        @Bean
        ApplicationRunner geodeClusterObjectsBootstrappedAssertionRunner(Environment environment, Cache cache,
                Region<?, ?> customersByName, GatewayReceiver gatewayReceiver, GatewaySender gatewaySender) {

            return args -> {

                assertThat(cache).isNotNull();
                assertThat(cache.getName()).startsWith(BootGeodeMultiSiteCachingServerApplication.class.getSimpleName());
                assertThat(customersByName).isNotNull();
                assertThat(customersByName.getAttributes()).isNotNull();
                assertThat(customersByName.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.REPLICATE);
                assertThat(customersByName.getAttributes().getGatewaySenderIds()).containsExactly(gatewaySender.getId());
                assertThat(customersByName.getName()).isEqualTo(CUSTOMERS_BY_NAME_REGION);
                assertThat(customersByName.getRegionService()).isEqualTo(cache);
                assertThat(cache.getRegion(RegionUtils.toRegionPath(CUSTOMERS_BY_NAME_REGION))).isEqualTo(customersByName);
                assertThat(gatewayReceiver).isNotNull();
                assertThat(gatewayReceiver.isRunning()).isTrue();
                assertThat(cache.getGatewayReceivers()).containsExactly(gatewayReceiver);
                assertThat(gatewaySender).isNotNull();
                assertThat(gatewaySender.isRunning()).isTrue();
                assertThat(cache.getGatewaySenders().stream().map(GatewaySender::getId).collect(Collectors.toSet()))
                    .containsExactly(gatewaySender.getId());

                System.err.printf("Apache Geode Cluster [%s] configured and bootstrapped successfully!%n",
                    environment.getProperty("spring.application.name", "UNKNOWN"));
            };
        }
    }
```

The `CacheServer` port is set to the ephemeral port (i.e. `0`) to let
the system allocate a port. Since the client is connecting to the
cluster via a Locator, the Locator sends meta-data about the cluster to
the client informing the client of the available `CacheServers`, which
server is hosting what data, the port(s) the `CacheServer(s)` are
listening on, and so on.

The name of the client and server-side `Region` backing the cache named
in the `@Cacheable` annotation declared on the
`CustomerService.findBy(..)` method must match. The client-side
"CustomersByName" `Region` is a PROXY, and therefore forwards all data
access operations to the matching server-side REPLICATE `Region` by the
same name (i.e. "CustomersByName").

The <code>DataPolicy</code> of the server-side,
"CustomersByName" <code>Region</code> could have been PARTITION, thereby
sharding the data across the servers in the cluster that host the
"CustomersByName" <code>Region</code>. However, it is common for
"reference" data, such as "cached" data, to be stored in a REPLICATE
<code>Region</code>. Although, if the data is transactional in nature,
then it is recommended that you use a PARTITION
<code>Region</code>.

#### `Locator` and `Manager` Configuration

The next bit of configuration enables an embedded Locator and starts a
Manager service inside the server:

Locator & Manager

``` highlight
    @Configuration
    @EnableLocator
    @EnableManager(start = true)
    @Profile("locator-manager")
    static class GeodeLocatorManagerConfiguration { }
```

If you are starting up a multi-node cluster, then you can choose whether
to start an embedded Locator and Manager on a node-by-node basis. If you
do, you must vary the port numbers or configure the Locator and Manager
using the ephemeral port.

We’ll see below how configuring a Manager can be useful for inspecting
the cluster using *Gfsh*. But first, let’s talk about the final bit of
configuration that enables *Multi-Site Caching* with *Gateways* over
WAN.

#### `GatewaySender` and `GatewayReceiver` Configuration

The final bit of configuration configures a `GatewaySender` for sending
`Region` events from this cluster (i.e. cluster \#1) to a remote cluster
(e.g. cluster \#2). VMware GemFire Gateways are the essential component
for enabling *Multi-Site Caching* using a WAN topology:

Gateway Sender & Receiver

``` highlight
    @Configuration
    @Profile("gateway-receiver")
    static class GeodeGatewayReceiverConfiguration {

        @Bean
        GatewayReceiverFactoryBean gatewayReceiver(Cache cache) {

            GatewayReceiverFactoryBean gatewayReceiver = new GatewayReceiverFactoryBean(cache);

            gatewayReceiver.setHostnameForSenders(GATEWAY_RECEIVER_HOSTNAME_FOR_SENDERS);
            gatewayReceiver.setStartPort(GATEWAY_RECEIVER_START_PORT);
            gatewayReceiver.setEndPort(GATEWAY_RECEIVER_END_PORT);

            return gatewayReceiver;
        }
    }

    @Configuration
    @Profile("gateway-sender")
    static class GeodeGatewaySenderConfiguration {

        @Bean
        GatewaySenderFactoryBean customersByNameGatewaySender(Cache cache,
                @Value("${geode.distributed-system.remote.id:1}") int remoteDistributedSystemId) {

            GatewaySenderFactoryBean gatewaySender = new GatewaySenderFactoryBean(cache);

            gatewaySender.setPersistent(PERSISTENT);
            gatewaySender.setRemoteDistributedSystemId(remoteDistributedSystemId);

            return gatewaySender;
        }

        @Bean
        RegionConfigurer customersByNameConfigurer(GatewaySender gatewaySender) {

            return new RegionConfigurer() {

                @Override
                public void configure(String beanName, PeerRegionFactoryBean<?, ?> regionBean) {

                    if (CUSTOMERS_BY_NAME_REGION.equals(beanName)) {
                        regionBean.setGatewaySenders(ArrayUtils.asArray(gatewaySender));
                    }
                }
            };
        }
    }
```

Just as the Locator communicates cluster meta-data to the clients
allowing clients to connect to and communicate with servers, and
specifically `CacheServers` in the cluster, the remote Locator endpoint
communicates cluster meta-data between sites.

While a `GatewaySender` is configured per `Region`, a `GatewayReceiver`
is setup per server, and the Gateway events are routed to the
appropriate server objects, such as `Regions`. `GatewaySenders` are
`Region` specific since you might have different [event
filters](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/wan/GatewayEventFilter.html)
coupled with [event substitution
filtering](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/wan/GatewayEventSubstitutionFilter.html),
or be using different
[transports](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/wan/GatewayTransportFilter.html),
etc.

You can even control the *concurrency-level* along with the [order of
events](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/wan/GatewaySender.OrderPolicy.html)
passing through the Gateway(s). In fact, there are many aspects of
Gateways you can control, different configurations you can use, conflict
resolution policies, etc, in order to properly address the unique
requirements (or SLAs) of your application use case(s), that are quite
frankly, well beyond the scope of this guide. Therefore, you are
encouraged to follow the VMware GemFire [User
Guide](https://geode.apache.org/docs/guide/1.15/topologies_and_comm/multi_site_configuration/chapter_overview.html)
for further guidance.

Although, there is 1 aspect of the Gateway configuration we want to
address here, and that is *Active-Active* vs. *Active-Passive*.

Currently, the example is setup to use *Active-Active* replication,
where all clusters are actors in the overall system architecture.
However, it is a simple matter to setup the system architecture using an
*Active-Passive* WAN Gateway pattern.

You do this by limiting the `GatewaySender` configuration to
cluster/site \#1, for example. That is, you do not configure a
`GatewaySender` on the "CustomersByName" `Region` in cluster/site \#2.
Cluster \#2 still requires a `GatewayReceiver` so Gateway events sent
from cluster \#1 are received by and replicated in cluster \#2. This
arrangement is commonly used to position cluster \#2 for standby in the
event that cluster \#1 goes down. As such, no clients can connect
directly to cluster \#2.

The configuration declares Spring Profiles for each side of the Gateway,
the receiving side (`GatewayReceiver`) along with the sending side
(`GatewaySender`). The sending side clearly does not require a
`GatewayReceiver` when it is the "*Active*" cluster in the
*Active-Passive* architecture. *Active-Passive* replication is 1-way.

Now that we have talked about the configuration in more detail, let’s
run the example and have a look at the cluster using *Gfsh*.


## Run the Example

For this exercise, it would be helpful to have an installation of VMware
GemFire installed on your system. Follow the instructions in the [User
Guide](https://geode.apache.org/docs/guide/1.15/prereq_and_install.html)
to get started.

Once you have installed VMware GemFire and set your environment
variables accordingly, you can start *Gfsh*:

Starting & Using Gfsh

``` highlight
$ echo $GEODE_HOME
/Users/jblum/pivdev/apache-geode-1.12.0

$ gfsh
    _________________________     __
   / _____/ ______/ ______/ /____/ /
  / /  __/ /___  /_____  / _____  /
 / /__/ / ____/  _____/ / /    / /
/______/_/      /______/_/    /_/    1.12.0

Monitor and Manage VMware GemFire
gfsh>
```

### Start the Clusters

Before we can connect, we must start the clusters. As mentioned earlier,
you can use the `BootGeodeMultiSiteCachingServerApplication` class to
configure and bootstrap both clusters by simply varying the
configuration. The configuration for each cluster has been neatly
encapsulated in a Spring Boot `application.properties` file denoted by a
Spring Profile, i.e. `server-site1` for cluster \#1 and `server-site2`
for cluster \#2.

Therefore, to start a cluster, simply run the
`BootGeodeMultiSiteCachingServerApplication` class from your IDE and
enable the Spring Profile for the cluster you want start, e.g. to start
cluster \#1 use: `-Dspring.profiles.active=server-site1`.

To start cluster \#2, simply create another run configuration in your
IDE with the `BootGeodeMultiSiteCachingServerApplication` class with the
Spring Profile set to `server-site2`.

When the cluster starts up, you should see log output similar to (log
output formatted to fit this guide):

Cluster log output on startup

``` highlight
/Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/bin/java -server -ea
    -Dspring.profiles.active=server-site1 -classpath ....
    example.app.caching.multisite.server.BootGeodeMultiSiteCachingServerApplication

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::             (v2.3.0.M4)

... Starting BootGeodeMultiSiteCachingServerApplication on jblum-mbpro-2.local with PID 47528
    (/Users/jblum/pivdev/spring-boot-data-geode/spring-geode-samples/caching/multi-site/out/production/classes
    started by jblum in
    /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-samples/caching/multi-site/target/site-1/server)
.... The following profiles are active: server-site1,locator-manager,gateway-receiver,gateway-sender
.... Failed to connect to localhost[40404]
.... Failed to connect to localhost[10334]
.... Bootstrapping Spring Data Gemfire repositories in DEFAULT mode.
.... Finished Spring Data repository scanning in 8ms. Found 0 Gemfire repository interfaces.
.... @Bean method PdxConfiguration.pdxDiskStoreAwareBeanFactoryPostProcessor ...
.... @Bean method RegionTemplateAutoConfiguration.regionTemplateBeanFactoryPostProcessor ...
.... Using org.apache.geode.logging.internal.SimpleLoggingProvider for service
         org.apache.geode.logging.internal.spi.LoggingProvider
....
---------------------------------------------------------------------------

  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with this
  work for additional information regarding copyright ownership.

  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with the
  License.  You may obtain a copy of the License at

  https://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
  License for the specific language governing permissions and limitations
  under the License.

---------------------------------------------------------------------------
Build-Date: 2020-03-27 11:09:15 -0700
Build-Id: echobravo 0
Build-Java-Vendor: AdoptOpenJDK
Build-Java-Version: 1.8.0_242
Build-Platform: Mac OS X 10.14.6 x86_64
Product-Name: VMware GemFire
Product-Version: 1.12.0
Source-Date: 2020-03-26 14:00:52 -0700
Source-Repository: release/1.12.0
Source-Revision: 57f17bfa7589b41aea6c05ea8bcddba40285c228
Running on: /10.99.199.24, 8 cpu(s), x86_64 Mac OS X 10.13.6
Communications version: 115
Process ID: 47528
User: jblum
Current dir: /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-samples/caching/multi-site/target/site-1/server
Home dir: /Users/jblum
Command Line Parameters:
  -ea
  -Dspring.profiles.active=server-site1
  -javaagent:/Applications/IntelliJ IDEA 19.2.4 CE.app/Contents/lib/idea_rt.jar=51666
        :/Applications/IntelliJ IDEA 19.2.4 CE.app/Contents/bin
  -Dfile.encoding=UTF-8
Class Path:
  ...
Library Path:
  ...
System Properties:
    PID = 47528
    awt.toolkit = sun.lwawt.macosx.LWCToolkit
    file.encoding = UTF-8
    file.encoding.pkg = sun.io
    file.separator = /
    ftp.nonProxyHosts = local|*.local|169.254/16|*.169.254/16
    gopherProxySet = false
    http.nonProxyHosts = local|*.local|169.254/16|*.169.254/16
    java.awt.graphicsenv = sun.awt.CGraphicsEnvironment
    java.awt.headless = true
    java.awt.printerjob = sun.lwawt.macosx.CPrinterJob
    java.class.version = 52.0
    java.endorsed.dirs = /Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/jre/lib/endorsed
    java.ext.dirs = /Users/jblum/Library/Java/Extensions:...
    java.home = /Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/jre
    java.io.tmpdir = /var/folders/ly/d_6wcpgx7qv146hbwnp7zvfr0000gn/T/
    java.runtime.name = Java(TM) SE Runtime Environment
    java.runtime.version = 1.8.0_241-b07
    java.specification.name = Java Platform API Specification
    java.specification.vendor = Oracle Corporation
    java.specification.version = 1.8
    java.vendor = Oracle Corporation
    java.vendor.url = https://java.oracle.com/
    java.vendor.url.bug = https://bugreport.sun.com/bugreport/
    java.version = 1.8.0_241
    java.vm.info = mixed mode
    java.vm.name = Java HotSpot(TM) 64-Bit Server VM
    java.vm.specification.name = Java Virtual Machine Specification
    java.vm.specification.vendor = Oracle Corporation
    java.vm.specification.version = 1.8
    java.vm.vendor = Oracle Corporation
    java.vm.version = 25.241-b07
    line.separator =

    os.version = 10.13.6
    path.separator = :
    socksNonProxyHosts = local|*.local|169.254/16|*.169.254/16
    spring.beaninfo.ignore = true
    spring.data.gemfire.cache.client.region.shortcut = LOCAL
    spring.profiles.active = server-site1
    sun.arch.data.model = 64
    sun.boot.class.path = /Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/jre/lib/resources.jar:...
    sun.boot.library.path = /Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/jre/lib
    sun.cpu.endian = little
    sun.cpu.isalist =
    sun.io.unicode.encoding = UnicodeBig
    sun.java.command = example.app.caching.multisite.server.BootGeodeMultiSiteCachingServerApplication
    sun.java.launcher = SUN_STANDARD
    sun.jnu.encoding = UTF-8
    sun.management.compiler = HotSpot 64-Bit Tiered Compilers
    sun.nio.ch.bugLevel =
    sun.os.patch.level = unknown
    user.country = US
    user.language = en
    user.timezone = America/Los_Angeles
Log4J 2 Configuration:
    org.apache.geode.logging.internal.SimpleLoggingProvider
---------------------------------------------------------------------------

....  Startup Configuration:
### GemFire Properties defined with api ###
disable-auto-reconnect=true
distributed-system-id=10
jmx-manager=true
jmx-manager-port=1199
jmx-manager-start=true
jmx-manager-update-rate=2000
log-level=config
mcast-port=0
name=BootGeodeMultiSiteCachingServerApplication-Site1
remote-locators=localhost[12480]
start-locator=localhost[11235]
use-cluster-configuration=false
### GemFire Properties using default values ###
....

....  initializing InternalDataSerializer with 4 services
....  Starting peer location for Distribution Locator on localhost/127.0.0.1
....  Locator was created at Sun Apr 19 12:16:31 PDT 2020
....  Listening on port 11235 bound on address localhost/127.0.0.1
....  GemFire peer location service starting.  Other locators: localhost[11235]  Locators preferred as coordinators:...
....  Peer locator attempting to recover from localhost/127.0.0.1:11235
....  Peer locator was unable to recover state from this locator
....  Peer locator recovering from /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-samples/caching/multi-site/..
....  Peer locator recovered membership is View[10.99.199.24...
....  Serial Queue info : THROTTLE_PERCENT: 0.75 SERIAL_QUEUE_BYTE_LIMIT :41943040 SERIAL_QUEUE_THROTTLE :31457280...
....  Starting membership services
....  Established local address 10.99.199.24(BootGeodeMultiSiteCachingServerApplication-Site1:47528)<ec>:41000
....  This member is hosting a locator will be preferred as a membership coordinator
....  JGroups channel created (took 93ms)
....  GemFire P2P Listener started on /10.99.199.24:53169
....  Started failure detection server thread on /10.99.199.24:53490.
....  Peer locator is connecting to local membership services with ID 10.99.199.24...
....  Peer locator: coordinator from view is null
....  Peer locator: coordinator from registrations is 10.99.199.24...
....  received FindCoordinatorResponse(coordinator=10.99.199.24...
....  findCoordinator chose 10.99.199.24(BootGeodeMultiSiteCachingServerApplication-Site1:47528)<ec>:41000 ...
....  Discovery state after looking for membership coordinator is locatorsContacted=1; findInViewResponses=0; ...
....  found possible coordinator 10.99.199.24(BootGeodeMultiSiteCachingServerApplication-Site1:47528)<ec>:41000
....  This member is becoming the membership coordinator with address 10.99.199.24...
....  Location services has received notification that this node is becoming membership coordinator
....  received new view: View[10.99.199.24(BootGeodeMultiSiteCachingServerApplication-Site1:47528)<ec><v0>:41000|0]...
....  Peer locator received new membership view: View[10.99.199.24...
....  ViewCreator starting on:10.99.199.24(BootGeodeMultiSiteCachingServerApplication-Site1:47528)<ec><v0>:41000
....  View Creator thread is starting
....  Finished joining (took 12ms).
....  Starting DistributionManager 10.99.199.24(BootGeodeMultiSiteCachingServerApplication-Site1:47528)<ec><v0>:41000...
....  Initial (distribution manager) view, View[10.99.199.24...
....  no recipients for new view aside from myself
....  Did not hear back from any other system. I am the first one.
....  DistributionManager 10.99.199.24(BootGeodeMultiSiteCachingServerApplication-Site1:47528)<ec><v0>:41000...
....  Starting server location for Distribution Locator on localhost/127.0.0.1[11235]
....  Disabling statistic archival.
....  Initialized cache service org.apache.geode.cache.lucene.internal.LuceneServiceImpl
....  Initialized cache service org.apache.geode.management.internal.cli.remote.OnlineCommandProcessor
....  Initialized cache service org.apache.geode.cache.query.internal.QueryConfigurationServiceImpl
....  Initializing region _monitoringRegion_10.99.199.24<v0>41000
....  Initialization of region _monitoringRegion_10.99.199.24<v0>41000 completed
....  GEODE_HOME:null
.... > null
....  GEODE_HOME environment variable not set; HTTP service will not start.
....  geode-web war file was not found
....  GEODE_HOME:null
.... > null
....  GEODE_HOME environment variable not set; HTTP service will not start.
....  geode-pulse war file was not found
....  Registered AccessControlMBean on GemFire:service=AccessControl,type=Distributed
....  Registered FileUploaderMBean on GemFire:service=FileUploader,type=Distributed
....  Loading previously deployed jars
....  Initializing region PdxTypes
....  Initialization of region PdxTypes completed
....  Connected to Distributed System [BootGeodeMultiSiteCachingServerApplication-Site1]
      as Member [10.99.199.24(BootGeodeMultiSiteCachingServerApplication-Site1:47528)<ec><v0>:41000]
      in Group(s) [[]] with Role(s) [[]] on Host [10.99.199.24] having PID [47528]
....  Created new VMware GemFire version [1.12.0] Cache [BootGeodeMultiSiteCachingServerApplication-Site1]
....  Cache server connection listener bound to address 10.99.199.24-0.0.0.0/0.0.0.0:27566 with backlog 1280.
....  ClientHealthMonitorThread maximum allowed time between pings: 60000
....  Handshaker max Pool size: 4
....  CacheServer Configuration:  port=27566 max-connections=800 max-threads=0 notify-by-subscription=true...
....  The GatewayReceiver started on port : 27566
....  Initializing region gatewayEventIdIndexMetaData
....  Initialization of region gatewayEventIdIndexMetaData completed
....  SerialGatewaySender{id=customersByNameGatewaySender,remoteDsId=20,isRunning =false,isPrimary =false} : Starting...
....  SerialGatewaySender{id=customersByNameGatewaySender,remoteDsId=20,isRunning =false,isPrimary =false} : Becoming...
....  Created disk store DEFAULT with unique id 1319d10707e44ad1-82c32e1a4fbbab77
....  recovery region initialization took 3 ms
....  Recovered values for disk store DEFAULT with unique id 1319d107-07e4-4ad1-82c3-2e1a4fbbab77
....  Initializing region customersByNameGatewaySender.0_SERIAL_GATEWAY_SENDER_QUEUE
....  Initialization of region customersByNameGatewaySender.0_SERIAL_GATEWAY_SENDER_QUEUE completed
....  Initializing region customersByNameGatewaySender.1_SERIAL_GATEWAY_SENDER_QUEUE
....  Initialization of region customersByNameGatewaySender.1_SERIAL_GATEWAY_SENDER_QUEUE completed
....  Initializing region customersByNameGatewaySender.2_SERIAL_GATEWAY_SENDER_QUEUE
....  Initialization of region customersByNameGatewaySender.2_SERIAL_GATEWAY_SENDER_QUEUE completed
....  Initializing region customersByNameGatewaySender.3_SERIAL_GATEWAY_SENDER_QUEUE
....  Initialization of region customersByNameGatewaySender.3_SERIAL_GATEWAY_SENDER_QUEUE completed
....  Initializing region customersByNameGatewaySender.4_SERIAL_GATEWAY_SENDER_QUEUE
....  Initialization of region customersByNameGatewaySender.4_SERIAL_GATEWAY_SENDER_QUEUE completed
....  Remote locator host port information for remote site 20 is not available in local locator localhost[11235].
....  GatewaySender customersByNameGatewaySender could not get remote locator information for remote site 20.
....  Started  SerialGatewaySender{id=customersByNameGatewaySender,remoteDsId=20,isRunning =true,isPrimary =true}
....  Falling back to creating Region [CustomersByName] in Cache [BootGeodeMultiSiteCachingServerApplication-Site1]
....  Created Region [CustomersByName]
....  Region CustomersByName is being created with scope DISTRIBUTED_NO_ACK but enable-network-partition-detection...
....  Initializing region CustomersByName
....  Initialization of region CustomersByName completed
....  Cache server connection listener bound to address 10.99.199.24-0.0.0.0/0.0.0.0:51682 with backlog 1280.
....  Handshaker max Pool size: 4
....  CacheServer Configuration:  port=51682 max-connections=800 max-threads=0 notify-by-subscription=true
      socket-buffer-size=32768 maximum-time-between-pings=60000 maximum-message-count=230000 message-time-to-live=180
      eviction-policy=none capacity=1 overflow directory=. groups=[] loadProbe=ConnectionCountProbe
      loadPollInterval=5000 tcpNoDelay=true
....  Started BootGeodeMultiSiteCachingServerApplication in 5.543 seconds (JVM running for 6.26)
VMware GemFire Cluster [BootGeodeMultiSiteCachingServerApplication-Site1] configured and bootstrapped successfully!
```

You should see similar output when you start cluster \#2.

We can now go to *Gfsh* and connect to the clusters to inspect the
configuration:

Review cluster configuration in Gfsh

``` highlight
gfsh>connect --locator=localhost[11235]
Connecting to Locator at [host=localhost, port=11235] ..
Connecting to Manager at [host=10.99.199.24, port=1199] ..
Successfully connected to: [host=10.99.199.24, port=1199]


Cluster-10 gfsh>list members
Member Count : 1

                      Name                       | Id
------------------------------------------------ | -----------------------------------------------------------------
BootGeodeMultiSiteCachingServerApplication-Site1 | 10.99.199.24(BootGeodeMultiSiteCachingServerApplication-Site1:...


Cluster-10 gfsh>describe member --name=BootGeodeMultiSiteCachingServerApplication-Site1
Name        : BootGeodeMultiSiteCachingServerApplication-Site1
Id          : 10.99.199.24(BootGeodeMultiSiteCachingServerApplication-Site1:47528)<ec><v0>:41000
Host        : 10.99.199.24
Regions     : CustomersByName
PID         : 47528
Groups      :
Used Heap   : 160M
Max Heap    : 3641M
Working Dir : /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-samples/caching/multi-site/target/site-1/server
Log file    : /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-samples/caching/multi-site/target/site-1/server
Locators    : localhost[11235]

Cache Server Information
Server Bind              :
Server Port              : 51682
Running                  : true

Client Connections : 0


Cluster-10 gfsh>list regions
List of regions
---------------
CustomersByName


Cluster-10 gfsh>describe region --name=/CustomersByName
Name            : CustomersByName
Data Policy     : replicate
Hosting Members : BootGeodeMultiSiteCachingServerApplication-Site1

Non-Default Attributes Shared By Hosting Members

 Type  |       Name        | Value
------ | ----------------- | ----------------------------
Region | data-policy       | REPLICATE
       | size              | 0
       | gateway-sender-id | customersByNameGatewaySender
```

Even though the `GatewaySender` and `GatewayReceiver` were configured
correctly, *Gfsh* apparently is not aware of it, at least not by
`list gateways` command, anyway:

``` highlight
Cluster-10 gfsh>list gateways
GatewaySenders or GatewayReceivers are not available in cluster
```

Interestingly, the `describe region` command for the "CustomersByName"
`Region` does appropriately show the `Region` has a `GatewaySender`
identified as "customersByNameGatewaySender", as we expect!

### Start the Clients

We can now start the clients similarly to how we started the clusters.

We use the same `BootGeodeMultiSiteCachingClientApplication` class to
start both clients. Each client is configured to connect to 1 of the 2
clusters, which has been neatly encapsulated in a Spring Boot
`application.properties` file identified by a Spring Profile:
`client-site1` and `client-site2`.

Therefore, to run the client, simply create an IDE run configuration
using the `BootGeodeMultiSiteCachingClientApplication` class and enable
the appropriate Spring Profile, for example,
`-Dspring-profiles-active=client-site1` to connect to cluster \#1. Use
Spring Profile `client-site2` to connect to cluster \#2.

When the client starts, you will see log output similar to (log output
was formatted to fit this guide):

Client log output on startup

``` highlight
/Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/bin/java -server -ea
    -Dspring.profiles.active=client-site1 -classpath ...
    example.app.caching.multisite.client.BootGeodeMultiSiteCachingClientApplication

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::             (v2.3.0.M4)

....  Starting BootGeodeMultiSiteCachingClientApplication on jblum-mbpro-2.local with PID 47756...
....  The following profiles are active: client-site1
....  Failed to connect to localhost[40404]
....  Failed to connect to localhost[10334]
....  Bootstrapping Spring Data Gemfire repositories in DEFAULT mode.
....  Finished Spring Data repository scanning in 6ms. Found 0 Gemfire repository interfaces.
....  @Bean method PdxConfiguration.pdxDiskStoreAwareBeanFactoryPostProcessor is non-static and returns an object...
....  @Bean method RegionTemplateAutoConfiguration.regionTemplateBeanFactoryPostProcessor is non-static and returns...
....  Bean 'org.springframework.data.gemfire.config.annotation.CachingDefinedRegionsConfiguration'...
....  Bean 'org.springframework.data.gemfire.config.annotation.ContinuousQueryConfiguration'...
....  Tomcat initialized with port(s): 8080 (http)
....  Initializing ProtocolHandler ["http-nio-8080"]
....  Starting service [Tomcat]
....  Starting Servlet engine: [Apache Tomcat/9.0.33]
....  Initializing Spring embedded WebApplicationContext
....  Root WebApplicationContext: initialization completed in 1605 ms
....  Use of PoolFactory.setThreadLocalConnections is deprecated and ignored.
....  Using org.apache.geode.logging.internal.SimpleLoggingProvider for service
      org.apache.geode.logging.internal.spi.LoggingProvider
....
---------------------------------------------------------------------------

  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with this
  work for additional information regarding copyright ownership.

  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with the
  License.  You may obtain a copy of the License at

  https://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
  License for the specific language governing permissions and limitations
  under the License.

---------------------------------------------------------------------------
Build-Date: 2020-03-27 11:09:15 -0700
Build-Id: echobravo 0
Build-Java-Vendor: AdoptOpenJDK
Build-Java-Version: 1.8.0_242
Build-Platform: Mac OS X 10.14.6 x86_64
Product-Name: VMware GemFire
Product-Version: 1.12.0
Source-Date: 2020-03-26 14:00:52 -0700
Source-Repository: release/1.12.0
Source-Revision: 57f17bfa7589b41aea6c05ea8bcddba40285c228
Running on: /10.99.199.24, 8 cpu(s), x86_64 Mac OS X 10.13.6
Communications version: 115
Process ID: 47756
User: jblum
Current dir: /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-samples/caching/multi-site/target/site-1/client
Home dir: /Users/jblum
Command Line Parameters:
  -ea
  -Dspring.profiles.active=client-site1
  -javaagent:/Applications/IntelliJ IDEA 19.2.4 CE.app/Contents/lib/idea_rt.jar=52433:...
  -Dfile.encoding=UTF-8
Class Path:
  ...
Library Path:
  ...
System Properties:
    PID = 47756
    awt.toolkit = sun.lwawt.macosx.LWCToolkit
    catalina.base = /private/var/folders/ly/d_6wcpgx7qv146hbwnp7zvfr0000gn/T/tomcat.8377743273120206593.8080
    catalina.home = /private/var/folders/ly/d_6wcpgx7qv146hbwnp7zvfr0000gn/T/tomcat.8377743273120206593.8080
    catalina.useNaming = false
    file.encoding = UTF-8
    file.encoding.pkg = sun.io
    file.separator = /
    ftp.nonProxyHosts = local|*.local|169.254/16|*.169.254/16
    gopherProxySet = false
    http.nonProxyHosts = local|*.local|169.254/16|*.169.254/16
    java.awt.graphicsenv = sun.awt.CGraphicsEnvironment
    java.awt.headless = true
    java.awt.printerjob = sun.lwawt.macosx.CPrinterJob
    java.class.version = 52.0
    java.endorsed.dirs = /Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/jre/lib/endorsed
    java.ext.dirs = /Users/jblum/Library/Java/Extensions:/Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/...
    java.home = /Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/jre
    java.io.tmpdir = /var/folders/ly/d_6wcpgx7qv146hbwnp7zvfr0000gn/T/
    java.runtime.name = Java(TM) SE Runtime Environment
    java.runtime.version = 1.8.0_241-b07
    java.specification.name = Java Platform API Specification
    java.specification.vendor = Oracle Corporation
    java.specification.version = 1.8
    java.vendor = Oracle Corporation
    java.vendor.url = https://java.oracle.com/
    java.vendor.url.bug = https://bugreport.sun.com/bugreport/
    java.version = 1.8.0_241
    java.vm.info = mixed mode
    java.vm.name = Java HotSpot(TM) 64-Bit Server VM
    java.vm.specification.name = Java Virtual Machine Specification
    java.vm.specification.vendor = Oracle Corporation
    java.vm.specification.version = 1.8
    java.vm.vendor = Oracle Corporation
    java.vm.version = 25.241-b07
    line.separator =

    os.version = 10.13.6
    path.separator = :
    socksNonProxyHosts = local|*.local|169.254/16|*.169.254/16
    spring.beaninfo.ignore = true
    spring.profiles.active = client-site1
    sun.arch.data.model = 64
    sun.boot.class.path = /Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/jre/lib/resources.jar:...
    sun.boot.library.path = /Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/jre/lib
    sun.cpu.endian = little
    sun.cpu.isalist =
    sun.io.unicode.encoding = UnicodeBig
    sun.java.command = example.app.caching.multisite.client.BootGeodeMultiSiteCachingClientApplication
    sun.java.launcher = SUN_STANDARD
    sun.jnu.encoding = UTF-8
    sun.management.compiler = HotSpot 64-Bit Tiered Compilers
    sun.nio.ch.bugLevel =
    sun.os.patch.level = unknown
    user.country = US
    user.language = en
    user.timezone = America/Los_Angeles
Log4J 2 Configuration:
    org.apache.geode.logging.internal.SimpleLoggingProvider
---------------------------------------------------------------------------

....  Startup Configuration:
### GemFire Properties defined with api ###
locators=
log-level=config
mcast-port=0
name=BootGeodeMultiSiteCachingClientApplication-Site1
### GemFire Properties using default values ###
...

....  initializing InternalDataSerializer with 4 services
....  [ThreadsMonitor] New Monitor object and process were created.

....  Disabling statistic archival.
....  Running in client mode
....  Initialized cache service org.apache.geode.cache.lucene.internal.LuceneServiceImpl
....  Initialized cache service org.apache.geode.management.internal.cli.remote.OnlineCommandProcessor
....  Initialized cache service org.apache.geode.cache.query.internal.QueryConfigurationServiceImpl
....  Connected to Distributed System [BootGeodeMultiSiteCachingClientApplication-Site1]
      as Member [10.99.199.24(BootGeodeMultiSiteCachingClientApplication-Site1:47756:loner):0:333e0094:...
      in Group(s) [[]] with Role(s) [[]] on Host [10.99.199.24] having PID [47756]
....  Created new VMware GemFire version [1.12.0] Cache [BootGeodeMultiSiteCachingClientApplication-Site1]
....  Falling back to creating Region [CustomersByName] in Cache [BootGeodeMultiSiteCachingClientApplication-Site1]
....  Falling back to creating Region [CustomersByName] in Cache [BootGeodeMultiSiteCachingClientApplication-Site1]
....  Creating client Region [CustomersByName]
....  AutoConnectionSource UpdateLocatorListTask started with interval=10000 ms.
....  Updating membership port.  Port changed from 0 to 52447.  ID is now 10.99.199.24...
....  Pool DEFAULT started with multiuser-authentication=false
....  Cache Client Updater Thread  on 10.99.199.24(BootGeodeMultiSiteCachingServerApplication-Site1:47528)<ec><v0>:...
....  Initializing ExecutorService 'applicationTaskExecutor'
....  Starting ProtocolHandler ["http-nio-8080"]
....  Tomcat started on port(s): 8080 (http) with context path ''
....  Started BootGeodeMultiSiteCachingClientApplication in 4.594 seconds (JVM running for 5.276)
```

After the client has successfully started, you can see that the client
has connected to the cluster in *Gfsh*:

Client connected to cluster

``` highlight
list clients
Client List

                                                             Client Name / ID                                                              | Server Name / ID
------------------------------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------
10.99.199.24(BootGeodeMultiSiteCachingClientApplication-Site1:47756:loner):52447:333e0094:BootGeodeMultiSiteCachingClientApplication-Site1 | member=BootGeodeMultiSiteCachingServerApplication-Site1,port=51682
```

We are now ready to test our application end-to-end from a Web browser.
You can navigate to the REST (API) interface of client \#1 by going to:
<a href="http://localhost:8080" class="bare">http://localhost:8080</a>.
Client \#2 is available from:
<a href="http://localhost:9090" class="bare">http://localhost:9090</a>.

![Multi Site Caching Client Application 1
Home](./images/Multi-Site-Caching-Client-Application-1-Home.png)

Let’s start by creating a `Customer` from client \#1. Goto
<a href="http://localhost:8080/customers/JonDoe"
class="bare">http://localhost:8080/customers/JonDoe</a>:

![Multi Site Caching Client Application 1 Customer JonDoe
CacheMiss](./images/Multi-Site-Caching-Client-Application-1-Customer-JonDoe-CacheMiss.png)

On first access, the operation results in a cache miss and the
`Customer` (e.g. "JonDoe") is looked up. To simulate the effects of a
resource expensive operation, we add a 5 second delay, which if you
recall, was present in our `CustomerService.findBy(..)` method:

CustomerService.findBy(..) method

``` highlight
    @Cacheable("CustomersByName")
    public Customer findBy(String name) {
        setCacheMiss();
        ThreadUtils.safeSleep(name, Duration.ofSeconds(getSleepInSeconds()));
        return Customer.newCustomer(this.customerId.incrementAndGet(), name);
    }
```

On subsequent access, the operation results in a cache hit when given
the same argument (e.g. "JonDoe"), and we again witness the effects that
caching now has on our application (i.e. no 5 second delay; the result
is returned immediately).

Simply hit refresh in your Web browser to resubmit the HTTP request and
receive a response:

![Multi Site Caching Client Application 1 Customer JonDoe
CacheHit](./images/Multi-Site-Caching-Client-Application-1-Customer-JonDoe-CacheHit.png)

This time, the access was a cache hit! Of course, this is only half of
the equation. What happens when we access "JonDoe" from site \#2 using
client \#2? Well, "JonDoe" has already been replicated from cluster \#1
to cluster \#2 and therefore, the operation results in a cache hit:

![Multi Site Caching Client Application 2 Customer JonDoe
CacheHit](./images/Multi-Site-Caching-Client-Application-2-Customer-JonDoe-CacheHit.png)

Because we are using an *Active-Active* system architecture and pattern,
we can simulate these effects both ways. If we try to access "JaneDoe"
from client \#2, the first time results in a cache miss:

![Multi Site Caching Client Application 2 Customer JaneDoe
CacheMiss](./images/Multi-Site-Caching-Client-Application-2-Customer-JaneDoe-CacheMiss.png)

Of course, if we hit refresh in our Web browser, then the subsequent
access of "JaneDoe" from client \#2 should result in a cache hit.
However, without hitting refresh, let’s immediately go back to client
\#1 and try to access "JaneDoe". The result is a cache hit since the
"JaneDoe" has already been replicated between the 2 sites over the WAN
Gateways:

![Multi Site Caching Client Application 1 Customer JaneDoe
CacheHit](./images/Multi-Site-Caching-Client-Application-1-Customer-JaneDoe-CacheHit.png)

In addition to testing in a Web browser, you can also query the data
using *Gfsh*:

Querying "CustomersByName" from Gfsh

``` highlight
Cluster-10 gfsh>describe region --name=CustomersByName
Name            : CustomersByName
Data Policy     : replicate
Hosting Members : BootGeodeMultiSiteCachingServerApplication-Site1

Non-Default Attributes Shared By Hosting Members

 Type  |       Name        | Value
------ | ----------------- | ----------------------------
Region | data-policy       | REPLICATE
       | size              | 2
       | gateway-sender-id | customersByNameGatewaySender


Cluster-10 gfsh>query --query="SELECT c.name FROM /CustomersByName c"
Result : true
Limit  : 100
Rows   : 2

Result
-------
JonDoe
JaneDoe
```

Keep in mind that our `Customer` class did not implement
`java.io.Serializable`, using *Java Serialization*. We also did not
include the `Customer` application domain model class on the cluster
servers' classpath. Therefore, it is not possible to deserialize an
instance of `Customer` as a `Customer` on the servers in the cluster.

Not having to implement `java.io.Serializable` nor include your
application domain model types on the servers' classpath is actually a
powerful feature of VMware GemFire. When no de/serialization strategy
has been explicitly configured, SBDG will configure VMware GemFire’s PDX
Serialization framework.

PDX allows you to query objects in serialized form, without causing a
deserialization, as long as you know the structure of your application
domain model types. Using PDX can be helpful in situations where your
application domain model types refer to 3rd party library types you
cannot control, and that may not implement `java.io.Serializable`.

You should refer to the VMware GemFire User Guide on
more details on <a
href="https://geode.apache.org/docs/guide/1.15/developing/data_serialization/gemfire_pdx_serialization.html">PDX</a>.
You can also refer to SBDG’s support of <a
href="../index.html#geode-data-serialization">PDX
Serialization</a>.

You can try other experiments, too. For example, you can rerun this
example with the *Active-Passive* pattern, which we leave as an exercise
for the curious reader.

## Summary

You have now just learned and witnessed first-hand the power of
*Look-Aside Caching* enhanced with *Multi-Site Caching*, implemented
with VMware GemFire WAN Gateway functionality. This is but a simple
example. WAN Gateway functionality can accommodate a wide-range of
different use cases and complex configuration.

Imagine if timely and accurate (i.e. "consistent") information is a
major concern for your application use case and your application is
backed by an RDBMS for its *System of Record* (SOR). How do you keep the
remote database clusters in sync with one another?

1 way would be to combine *Look-Aside Caching* with *Inline Caching*
within the cluster and use *Multi-Site Caching* between the clusters,
like so:

![Look Aside Near Inline Multi Site
Caching](./images/Look-Aside--Near--Inline--Multi-Site-Caching.png)

In this image, we also depicted the use of *Near Caching* to reduce
network traffic between the client(s) and the servers in the cluster.
The system architecture could optionally use the *Active-Active* WAN
Gateway pattern and the cluster on the right could optionally serve
application clients, or not, which might be the case in an
*Active-Passive* configuration. The choice is yours and you are only
limited by your imagination and constrained by your application
requirements. Whatever the case, you have extreme power and flexibility
at your fingertips.

Indeed, when you combine and apply multiple patterns of caching
(*Look-Aside*, *Near*, *Inline* and now, *Multi-Site Caching*) to your
applications, you can greatly enhance your end-users experience.

This concludes the series on caching.

