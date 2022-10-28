---
title: Caching with VMware GemFire
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

One of the easiest, quickest and least invasive ways to start using
VMware GemFire in your Spring Boot applications is to use
VMware GemFire as a
[caching provider](https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache-store-configuration)
in [Spring’s Cache Abstraction](https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache). SDG
[enables](https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache-store-configuration-gemfire)
VMware GemFire to function as a caching provider in Spring’s
Cache Abstraction.

See the <em>Spring Data for VMware GemFire
Reference Guide</em> for more details on the
[support](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#apis:spring-cache-abstraction)
and
[configuration](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config-caching)
of VMware GemFire as a caching provider in Spring’s Cache
Abstraction.

<p class="note"><strong>Note:</strong>
Make sure you thoroughly understand the
[concepts](https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache-strategies)
behind Spring’s Cache Abstraction before you continue.
</p>


See also the relevant section on
[caching](https://docs.spring.io/spring-boot/docs/current/reference/html/#boot-features-caching) in Spring Boot’s
reference documentation. Spring Boot even provides auto-configuration
support for a few of the simple
[caching providers](https://docs.spring.io/spring-boot/docs/current/reference/html/#_supported_cache_providers).


Indeed, caching can be an effective software design pattern to avoid the
cost of invoking a potentially expensive operation when, given the same
input, the operation yields the same output, every time.

Some classic examples of caching include, but are not limited to,
looking up a customer by name or account number, looking up a book by
ISBN, geocoding a physical address, and caching the calculation of a
person’s credit score when the person applies for a financial loan.

If you need the proven power of an enterprise-class caching solution,
with strong consistency, high availability, low latency, and multi-site
(WAN) capabilities, then you should consider
[VMware GemFire](https://geode.apache.org/). Alternatively,
VMWare, Inc. offers a commercial solution, built on
VMware GemFire, called VMware GemFire.

Spring’s [declarative, annotation-based
caching](https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache-annotations)
makes it simple to get started with caching, which is as easy as annotating your application
components with the appropriate Spring cache annotations.

Spring’s declarative, annotation-based caching also
[supports](https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache-jsr-107) JSR-107
JCache annotations.


For example, suppose you want to cache the results of determining a
person’s eligibility when applying for a loan. A person’s financial
status is unlikely to change in the time that the computer runs the
algorithms to compute a person’s eligibility after all the financial
information for the person has been collected, submitted for review and
processed.

Our application might consist of a financial loan service to process a
person’s eligibility over a given period of time:

Example 1. Spring application service component applicable to caching



``` highlight
@Service
class FinancialLoanApplicationService {

    @Cacheable("EligibilityDecisions")
    EligibilityDecision processEligibility(Person person, Timespan timespan) {
        // ...
    }
}
```

Notice the `@Cacheable` annotation declared on the
`processEligibility(:Person, :Timespan)` method of our service class.

When the `FinancialLoanApplicationService.processEligibility(..)` method
is called, Spring’s caching infrastructure first consults the
"EligibilityDecisions" cache to determine if a decision has already been
computed for the given person within the given span of time. If the
person’s eligibility in the given time frame has already been
determined, the existing decision is returned from the cache. Otherwise,
the `processEligibility(..)` method is invoked and the result of the
method is cached when the method returns, before returning the decision
to the caller.

Spring Boot for VMware GemFire auto-configures
VMware GemFire as the caching provider when
VMware GemFire is declared on the application classpath and when
no other caching provider (such as Redis) has been configured.

If Spring Boot for VMware GemFire detects that another cache
provider has already been configured, then VMware GemFire will
not function as the caching provider for the application. This lets you
configure another store, such as Redis, as the caching provider and
perhaps use VMware GemFire as your application’s persistent
store.

The only other requirement to enable caching in a Spring Boot
application is for the declared caches (as specified in Spring’s or
JSR-107’s caching annotations) to have been created and already exist,
especially before the operation on which caching was applied is invoked.
This means the backend data store must provide the data structure that
serves as the cache. For VMware GemFire, this means a cache
`Region`.

To configure the necessary Regions that back the caches declared in
Spring’s cache annotations, use Spring Data for VMware GemFire's
[`@EnableCachingDefinedRegions`](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableCachingDefinedRegions.html)
annotation.

The following listing shows a complete Spring Boot application:

Example 2. Spring Boot cache enabled application using
VMware GemFire



``` highlight
package example.app;

@SpringBootApplication
@EnableCachingDefinedRegions
class FinancialLoanApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinancialLoanApplication.class, args);
    }
}
```

<p class="note"><strong>Note:</strong>
The <code>FinancialLoanApplicationService</code> is
picked up by Spring’s classpath component scan, since this class is
annotated with Spring’s <code>@Service</code> stereotype
annotation.
</p>


You can set the <code>DataPolicy</code> of the
Region created through the <code>@EnableCachingDefinedRegions</code>
annotation by setting the <code>clientRegionShortcut</code> attribute to
a valid enumerated value.

<p class="note"><strong>Note:</strong>
Spring Boot for VMware GemFire does not
recognize nor apply the <code>spring.cache.cache-names</code> property.
Instead, you should use SDG’s <code>@EnableCachingDefinedRegions</code>
on an appropriate Spring Boot application <code>@Configuration</code>
class.
</p>


### Look-Aside Caching, Near Caching, Inline Caching, and Multi-Site Caching

Four different types of caching patterns can be applied with Spring when
using VMware GemFire for your application caching needs:

- Look-aside caching

- Near caching

- \[Async\] Inline caching

- Multi-site caching

Typically, when most users think of caching, they think of Look-aside
caching. This is the default caching pattern applied by Spring’s Cache
Abstraction.

In a nutshell, Near caching keeps the data closer to where the data is
used, thereby improving on performance due to lower latencies when data
is needed (no extra network hops). This also improves application
throughput — that is, the amount of work completed in a given period of
time.

Within Inline caching\_, developers have a choice between synchronous
(read/write-through) and asynchronous (write-behind) configurations
depending on the application use case and requirements. Synchronous,
read/write-through Inline caching is necessary if consistency is a
concern. Asynchronous, write-behind Inline caching is applicable if
throughput and low-latency are a priority.

Within Multi-site caching, there are active-active and active-passive
arrangements. More details on Multi-site caching will be presented in a
later release.

#### Look-Aside Caching

See the corresponding sample <a
href="guides/caching-look-aside.html">guide</a> and
[code](https://github.com/spring-projects/spring-boot-data-geode/tree/1.7.4/spring-geode-samples/intro/getting-started/caching/look-aside) to see Look-aside caching
with VMware GemFire in action.


The caching pattern demonstrated in the preceding example is a form of
[Look-aside
caching](https://content.pivotal.io/blog/an-introduction-to-look-aside-vs-inline-caching-patterns)
(or "*Cache Aside*").

Essentially, the data of interest is searched for in the cache first,
before calling a potentially expensive operation, such as an operation
that makes an IO- or network-bound request that results in either a
blocking or a latency-sensitive computation.

If the data can be found in the cache (stored in-memory to reduce
latency), the data is returned without ever invoking the expensive
operation. If the data cannot be found in the cache, the operation must
be invoked. However, before returning, the result of the operation is
cached for subsequent requests when the same input is requested again by
another caller, resulting in much improved response times.

The typical Look-aside caching pattern applied in your Spring
application code looks similar to the following:

Example 3. Look-Aside Caching Pattern Applied



``` highlight
@Service
class CustomerService {

  private final CustomerRepository customerRepository;

  @Cacheable("Customers")
  Customer findByAcccount(Account account) {

    // pre-processing logic here

    Customer customer = customerRepository.findByAccoundNumber(account.getNumber());

    // post-processing logic here

    return customer;
  }
}
```

In this design, the `CustomerRepository` is perhaps a JDBC- or
JPA/Hibernate-backed implementation that accesses the external data
source (for example, an RDBMS) directly. The `@Cacheable` annotation
wraps, or "decorates", the `findByAccount(:Account):Customer` operation
(method) to provide caching behavior.

<p class="note"><strong>Note:</strong>
This operation may be expensive because it may
validate the customer’s account before looking up the customer, pull
multiple bits of information to retrieve the customer record, and so
on — hence the need for caching.


#### Near Caching

See the corresponding sample <a
href="guides/caching-near.html">guide</a> and
[code](https://github.com/spring-projects/spring-boot-data-geode/tree/1.7.4/spring-geode-samples/intro/getting-started/caching/near) to see Near caching with
VMware GemFire in action.


Near caching is another pattern of caching where the cache is collocated
with the application. This is useful when the caching technology is
configured in a client/server arrangement.

We already mentioned that Spring Boot for VMware GemFire
[provides](clientcache-applications.html#geode-clientcache-applications)
an auto-configured `ClientCache` instance by default. A `ClientCache`
instance is most effective when the data access operations, including
cache access, are distributed to the servers in a cluster that is
accessible to the client and, in most cases, multiple clients. This lets
other cache client applications access the same data. However, this also
means the application incurs a network hop penalty to evaluate the
presence of the data in the cache.

To help avoid the cost of this network hop in a client/server topology,
a local cache can be established to maintain a subset of the data in the
corresponding server-side cache (that is, a Region). Therefore, the
client cache contains only the data of interest to the application. This
"local" cache (that is, a client-side Region) is consulted before
forwarding the lookup request to the server.

To enable Near caching when using VMware GemFire, change the
Region’s (that is the `Cache` in Spring’s Cache Abstraction) data
management policy from `PROXY` (the default) to `CACHING_PROXY`:

Example 4. Enable Near Caching with VMware GemFire



``` highlight
@SpringBootApplication
@EnableCachingDefinedRegions(clientRegionShortcut = ClientRegionShortcut.CACHING_PROXY)
class FinancialLoanApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinancialLoanApplication.class, args);
    }
}
```

<p class="note"><strong>Note:</strong>
The default client Region data management policy is
[<code>ClientRegionShortcut.PROXY</code>](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/client/ClientRegionShortcut.html#PROXY).
As a result, all data access operations are immediately forwarded to the
server.
</p>


See also the VMware GemFire documentation
concerning
[client/server event distribution](https://geode.apache.org/docs/guide/115/developing/events/how_client_server_distribution_works.html) and, specifically,
“Client Interest Registration on the Server,” which applies when you use client
<code>CACHING_PROXY</code> Regions to manage state in addition to the
corresponding server-side Region. This is necessary to receive updates
on entries in the Region that might have been changed by other clients
that have access to the same data.


#### Inline Caching

The next pattern of caching covered in this chapter is Inline caching.

You can apply two different configurations of Inline caching to your
Spring Boot applications when you use the Inline caching pattern:
synchronous (read/write-through) and asynchronous (write-behind).

<p class="note"><strong>Note:</strong>
Asynchronous (currently) offers only write
capabilities, from the cache to the external data source. There is no
option to asynchronously and automatically load the cache when the value
becomes available in the external data source.


##### Synchronous Inline Caching

See the corresponding sample <a
href="guides/caching-inline.html">guide</a> and
[code](https://github.com/spring-projects/spring-boot-data-geode/tree/1.7.4/spring-geode-samples/intro/getting-started/caching/inline) to see Inline caching with
VMware GemFire in action.


When employing Inline caching and a cache miss occurs, the application
service method might not be invoked still, since a cache can be
configured to invoke a loader to load the missing entry from an external
data source.

With VMware GemFire, you can configure the cache (or, to use
VMware GemFire terminology, the Region) with a
[`CacheLoader`](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/CacheLoader.html).
A `CacheLoader` is implemented to retrieve missing values from an
external data source when a cache miss occurs. The external data source
could be an RDBMS or any other type of data store (for example, another
NoSQL data store, such as Apache Cassandra, MongoDB, or Neo4j).

See VMware GemFire's User Guide on
[data loaders](https://geode.apache.org/docs/guide/115/developing/outside_data_sources/how_data_loaders_work.html) for more details.


Likewise, you can also configure an VMware GemFire Region with a
[`CacheWriter`](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/CacheWriter.html).
A `CacheWriter` is responsible for writing an entry that has been put
into the Region to the backend data store, such as an RDBMS. This is
referred to as a write-through operation, because it is synchronous. If
the backend data store fails to be updated, the entry is not stored in
the Region. This helps to ensure consistency between the backend data
store and the VMware GemFire Region.

You can also implement Inline caching using
asynchronous write-behind operations by registering an
[<code>AsyncEventListener</code>](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/asyncqueue/AsyncEventListener.html)
on an
[<code>AsyncEventQueue</code>](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/asyncqueue/AsyncEventQueue.html)
attached to a server-side Region. See VMware GemFire's User
Guide for more
[details](https://geode.apache.org/docs/guide/115/developing/events/implementing_write_behind_event_handler.html).
We cover asynchronous write-behind Inline caching in the next
section.


The typical pattern of Inline caching when applied to application code
looks similar to the following:

Example 5. Inline Caching Pattern Applied



``` highlight
@Service
class CustomerService {

  private CustomerRepository customerRepository;

  Customer findByAccount(Account account) {

      // pre-processing logic here

      Customer customer = customerRepository.findByAccountNumber(account.getNumber());

      // post-processing logic here.

      return customer;
  }
}
```

The main difference is that no Spring or JSR-107 caching annotations are
applied to the application’s service methods, and the
`CustomerRepository` accesses VMware GemFire directly and the
RDBMS indirectly.

###### Implementing CacheLoaders and CacheWriters for Inline Caching

You can use Spring to configure a `CacheLoader` or `CacheWriter` as a
bean in the Spring `ApplicationContext` and then wire the loader or
writer to a Region. Given that the `CacheLoader` or `CacheWriter` is a
Spring bean like any other bean in the Spring `ApplicationContext`, you
can inject any `DataSource` you like into the loader or writer.

While you can configure client Regions with `CacheLoaders` and
`CacheWriters`, it is more common to configure the corresponding
server-side Region:

``` highlight
@SpringBootApplication
@CacheServerApplication
class FinancialLoanApplicationServer {

    public static void main(String[] args) {
        SpringApplication.run(FinancialLoanApplicationServer.class, args);
    }

    @Bean("EligibilityDecisions")
    PartitionedRegionFactoryBean<Object, Object> eligibilityDecisionsRegion(
            GemFireCache gemfireCache, CacheLoader eligibilityDecisionLoader,
            CacheWriter eligibilityDecisionWriter) {

        PartitionedRegionFactoryBean<?, EligibilityDecision> eligibilityDecisionsRegion =
            new PartitionedRegionFactoryBean<>();

        eligibilityDecisionsRegion.setCache(gemfireCache);
        eligibilityDecisionsRegion.setCacheLoader(eligibilityDecisionLoader);
        eligibilityDecisionsRegion.setCacheWriter(eligibilityDecisionWriter);
        eligibilityDecisionsRegion.setPersistent(false);

        return eligibilityDecisionsRegion;
    }

    @Bean
    CacheLoader<?, EligibilityDecision> eligibilityDecisionLoader(
            DataSource dataSource) {

        return new EligibilityDecisionLoader(dataSource);
    }

    @Bean
    CacheWriter<?, EligibilityDecision> eligibilityDecisionWriter(
            DataSource dataSource) {

        return new EligibilityDecisionWriter(dataSource);
    }

    @Bean
    DataSource dataSource() {
      // ...
    }
}
```

Then you could implement the
[`CacheLoader`](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/CacheLoader.html)
and
[`CacheWriter`](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/CacheWriter.html)
interfaces, as appropriate:

Example 6. EligibilityDecisionLoader



``` highlight
class EligibilityDecisionLoader implements CacheLoader<?, EligibilityDecision> {

  private final DataSource dataSource;

  EligibilityDecisionLoader(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public EligibilityDecision load(LoadHelper<?, EligibilityDecision> helper) {

    Object key = helper.getKey();

    // Use the configured DataSource to load the EligibilityDecision identified by the key
    // from a backend, external data store.
  }
}
```

SBDG provides the
<code>org.springframework.geode.cache.support.CacheLoaderSupport</code>
<code>@FunctionalInterface</code> to conveniently implement application
<code>CacheLoaders</code>.


If the configured `CacheLoader` still cannot resolve the value, the
cache lookup operation results in a cache miss and the application
service method is then invoked to compute the value:

Example 7. EligibilityDecisionWriter



``` highlight
class EligibilityDecisionWriter implements CacheWriter<?, EligibilityDecision> {

  private final DataSource dataSource;

  EligibilityDecisionWriter(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void beforeCreate(EntryEvent<?, EligiblityDecision> entryEvent) {
    // Use configured DataSource to save (e.g. INSERT) the entry value into the backend data store
  }

  public void beforeUpdate(EntryEvent<?, EligiblityDecision> entryEvent) {
    // Use the configured DataSource to save (e.g. UPDATE or UPSERT) the entry value into the backend data store
  }

  public void beforeDestroy(EntryEvent<?, EligiblityDecision> entryEvent) {
    // Use the configured DataSource to delete (i.e. DELETE) the entry value from the backend data store
  }

  // ...
}
```

SBDG provides the
<code>org.springframework.geode.cache.support.CacheWriterSupport</code>
interface to conveniently implement application
<code>CacheWriters</code>.

<p class="note"><strong>Note:</strong>
Your <code>CacheWriter</code> implementation can use
any data access technology to interface with your backend data store
(for example JDBC, Spring’s <code>JdbcTemplate</code>, JPA with
Hibernate, and others). It is not limited to using only a
<code>javax.sql.DataSource</code>. In fact, we present another, more
useful and convenient approach to implementing Inline caching in the
next section.
</p>


###### Inline Caching with Spring Data Repositories

Spring Boot for VMware GemFire offers dedicated support to
configure Inline caching with Spring Data Repositories.

This is powerful, because it lets you:

- Access any backend data store supported by Spring Data (such as Redis
  for key-value or other distributed data structures, MongoDB for
  documents, Neo4j for graphs, Elasticsearch for search, and so on).

- Use complex mapping strategies (such as ORM provided by JPA with
  Hibernate).

We believe that users should store data where it is most easily
accessible. If you access and process documents, then MongoDB,
Couchbase, or another document store is probably going to be the most
logical choice to manage your application’s documents.

However, this does not mean that you have to give up
VMware GemFire in your application/system architecture. You can
use each data store for what it is good at. While MongoDB is excellent
at handling documents, VMware GemFire is a valuable choice for
consistency, high-availability/low-latency, high-throughput, multi-site,
scale-out application use cases.

As such, using VMware GemFire's `CacheLoader` and `CacheWriter`
provides a nice integration point between itself and other data stores
to best serve your application’s use case and requirements.

Suppose you use JPA and Hibernate to access data managed in an Oracle
database. Then, you can configure VMware GemFire to
read/write-through to the backend Oracle database when performing cache
(Region) operations by delegating to a Spring Data JPA Repository.

The configuration might look something like:

Example 8. Inline caching configuration using SBDG



``` highlight
@SpringBootApplication
@EntityScan(basePackageClasses = Customer.class)
@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
@EnableJpaRepositories(basePackageClasses = CustomerRepository.class)
class SpringBootOracleDatabaseApacheGeodeApplication {

  @Bean
  InlineCachingRegionConfigurer<Customer, Long> inlineCachingForCustomersRegionConfigurer(
      CustomerRepository customerRepository) {

    return new InlineCachingRegionConfigurer<>(customerRepository, Predicate.isEqual("Customers"));
  }
}
```

SBDG provides the `InlineCachingRegionConfigurer<ENTITY, ID>` interface.

Given a `Predicate` to express the criteria used to match the target
Region by name and a Spring Data `CrudRepository`, the
`InlineCachingRegionConfigurer` configures and adapts the Spring Data
`CrudRepository` as a `CacheLoader` and `CacheWriter` registered on the
Region (for example, "Customers") to enable Inline caching
functionality.

You need only declare `InlineCachingRegionConfigurer` as a bean in the
Spring `ApplicationContext` and make the association between the Region
(by name) and the appropriate Spring Data `CrudRepository`.

In this example, we used JPA and Spring Data JPA to store and retrieve
data stored in the cache (Region) to and from a backend database.
However, you can inject any Spring Data Repository for any data store
(Redis, MongoDB, and others) that supports the Spring Data Repository
abstraction.

If you want only to support one-way data access
operations when you use Inline caching, you can use either the
<code>RepositoryCacheLoaderRegionConfigurer</code> for reads or the
<code>RepositoryCacheWriterRegionConfigurer</code> for writes, instead
of the <code>InlineCachingRegionConfigurer</code>, which supports both
reads and writes.


To see a similar implementation of Inline caching
with a database (an in-memory HSQLDB database) in action, see the
[<code>InlineCachingWithDatabaseIntegrationTests</code>](https://github.com/spring-projects/spring-boot-data-geode/blob/master/spring-geode/src/test/java/org/springframework/geode/cache/inline/database/InlineCachingWithDatabaseIntegrationTests.java)
test class from the SBDG test suite. A dedicated sample will be provided
in a future release.




##### Asynchronous Inline Caching

See the corresponding sample <a
href="guides/caching-inline-async.html">guide</a> and
[code](https://github.com/spring-projects/spring-boot-data-geode/tree/1.7.4/spring-geode-samples/intro/getting-started/caching/inline-async) to see asynchronous
Inline caching with VMware GemFire in action.


If consistency between the cache and your external data source is not a
concern, and you need only write from the cache to the backend data
store periodically, you can employ asynchronous (write-behind) Inline
caching.

As the term, "write-behind", implies, a write to the backend data store
is asynchronous and not strictly tied to the cache operation. As a
result, the backend data store is in an "eventually consistent" state,
since the cache is primarily used by the application at runtime to
access and manage data. In this case, the backend data store is used to
persist the state of the cache (and that of the application) at periodic
intervals.

If multiple applications are updating the backend data store
concurrently, you could combine a `CacheLoader` to synchronously read
through to the backend data store and keep the cache up-to-date as well
as asynchronously write behind from the cache to the backend data store
when the cache is updated to eventually inform other interested
applications of data changes. In this capacity, the backend data store
is still the primary System of Record (SoR).

If data processing is not time sensitive, you can gain a performance
advantage from quantity-based or time-based batch updates.

###### Implementing an AsyncEventListener for Inline Caching

If you were to configure asynchronous, write-behind Inline caching by
hand, you would need to do the following yourself:

1.  Implement an `AsyncEventListener` to write to an external data
    source on cache events.

2.  Configure and register the listener with an `AsyncEventQueue` (AEQ).

3.  Create a Region to serve as the source of cache events and attach
    the AEQ to the Region.

The advantage of this approach is that you have access to and control
over low-level configuration details. The disadvantage is that with more
moving parts, it is easier to make errors.

Following on from our synchronous, read/write-through, Inline caching
examples from the prior sections, our `AsyncEventListener`
implementation might appear as follows:

Example 9. Example `AsyncEventListener` for Asynchronous, Write-Behind
Inline Caching



``` highlight
@Component
class ExampleAsyncEventListener implements AsyncEventListener {

    private final DataSource dataSource;

    ExampleAsyncEventListener(DataSoruce dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean processEvents(List<AsyncEvent> events) {

        // Iterate over the ordered AsyncEvents and use the configured DataSource
        // to write to the external, backend DataSource

    }
}
```

<p class="note"><strong>Note:</strong>
Instead of directly injecting a
<code>DataSource</code> into your <code>AsyncEventListener</code>, you
could use JDBC, Spring’s <code>JdbcTemplate</code>, JPA and Hibernate,
or another data access API or framework. Later in this chapter, we show
how SBDG simplifies the <code>AsyncEventListener</code> implementation
by using Spring Data Repositories.
</p>


Then we need to register this listener with an `AsyncEventQueue` (step 2
from the procedure shown earlier) and attach it to the target Region
that will be the source of the cache events we want to persist
asynchronously (step 3):

Example 10. Create and Configure an `AsyncEventQueue`



``` highlight
@Configuration
@PeerCacheApplication
class GeodeConfiguration {

    @Bean
    DataSource exampleDataSource() {
        // Construct and configure a data store specific DataSource
    }

    @Bean
    ExampleAsyncEventListener exampleAsyncEventListener(DataSource dataSource) {
        return new ExampleAsyncEventListener(dataSource);
    }

    @Bean
    AsyncEventQueueFactoryBean exampleAsyncEventQueue(Cache peerCache, ExampleAsyncEventListener listener) {

        AsyncEventQueueFactoryBean asyncEventQueue = new AsyncEventQueueFactoryBean(peerCache, listener);

        asyncEventQueue.setBatchConflationEnabled(true);
        asyncEventQueue.setBatchSize(50);
        asyncEventQueue.setBatchTimeInterval(15000); // 15 seconds
        asyncEventQueue.setMaximumQueueMemory(64); // 64 MB
        // ...

        return asyncEventQueue;
    }

    @Bean("Example")
    PartitionedRegionFactoryBean<?, ?> exampleRegion(Cache peerCache, AsyncEventQueue queue) {

        PartitionedRegionFactoryBean<?, ?> exampleRegion = new PartitionedRegionFactoryBean<>();

        exampleRegion.setAsyncEventQueues(ArrayUtils.asArray(queue));
        exampleRegion.setCache(peerCache);
        // ...

        return exampleRegion;
    }
}
```

While this approach affords you a lot of control over the low-level
configuration, in addition to your `AsyncEventListener` implementation,
this is a lot of boilerplate code.

See the Javadoc for SDG’s
https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/wan/AsyncEventQueueFactoryBean.html[<code>AsyncEventQueueFactoryBean</code>]
for more detail on the configuration of the AEQ.


See VMware GemFire's
[User Guide](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-events-implementing_write_behind_event_handler.html) for more details on AEQs and listeners.


Fortunately, with SBDG, there is a better way.

###### Asynchronous Inline Caching with Spring Data Repositories

The implementation and configuration of the `AsyncEventListener` as well
as the AEQ shown in the [preceding
section](#geode-caching-provider-inline-caching-asynchronous-asynceventlistener)
can be simplified as follows:

Example 11. Using SBDG to configure Asynchronous, Write-Behind Inline
Caching



``` highlight
@SpringBootApplication
@EntityScan(basePackageClasses = ExampleEntity.class)
@EnableJpaRepositories(basePackageClasses = ExampleRepository.class)
@EnableEntityDefinedRegions(basePackageClasses = ExampleEnity.class)
class ExampleSpringBootApacheGeodeAsyncInlineCachingApplication {

    @Bean
    AsyncInlineCachingRegionConfigurer asyncInlineCachingRegionConfigurer(
            CrudRepository<ExampleEntity, Long> repository) {

        return AsyncInlineCachingRegionConfigurer.create(repository, "Example")
            .withQueueBatchConflationEnabled()
            .withQueueBatchSize(50)
            .withQueueBatchTimeInterval(Duration.ofSeconds(15))
            .withQueueMaxMemory(64);
    }
}
```

The `AsyncInlineCachingRegionConfigurer.create(..)` method is overloaded
to accept a `Predicate` in place of the `String` to programmatically
express more powerful matching logic and identify the target Region (by
name) on which to configure asynchronous Inline caching functionality.

The `AsyncInlineCachingRegionConfigurer` uses the [Builder software
design pattern](https://en.wikipedia.org/wiki/Builder_pattern) and
`withQueue*(..)` builder methods to configure the underlying
`AsyncEventQueue` (AEQ) when the queue’s configuration deviates from the
defaults, as specified by VMware GemFire.

Under the hood, the `AsyncInlineCachingRegionConfigurer` constructs a
new instance of the `RepositoryAsyncEventListener` class initialized
with the given Spring Data `CrudRepository`. The `RegionConfigurer` then
registers the listener with the AEQ and attaches it to the target
`Region`.

With the power of Spring Boot auto-configuration and SBDG, the
configuration is much more concise and intuitive.

###### About `RepositoryAsyncEventListener`

The SBDG `RepositoryAsyncEventListener` class is the magic ingredient
behind the integration of the cache with an external data source.

The listener is a specialized
[adapter](https://en.wikipedia.org/wiki/Adapter_pattern) that processes
`AsyncEvents` by invoking an appropriate `CrudRepository` method based
on the cache operation. The listener requires an instance of
`CrudRepository`. The listener supports any external data source
supported by Spring Data’s Repository abstraction.

Backend data store, data access operations (such as INSERT, UPDATE,
DELETE, and so on) triggered by cache events are performed
asynchronously from the cache operation. This means the state of the
cache and backend data store will be "eventually consistent".

Given the complex nature of "eventually consistent" systems and
asynchronous concurrent processing, the `RepositoryAsyncEventListener`
lets you register a custom `AsyncEventErrorHandler` to handle the errors
that occur during processing of `AsyncEvents`, perhaps due to a faulty
backend data store data access operation (such as
`OptimisticLockingFailureException`), in an application-relevant way.

The `AsyncEventErrorHandler` interface is a
`java.util.function.Function` implementation and `@FunctionalInterface`
defined as:

Example 12. AsyncEventErrorHandler interface definition



``` highlight
@FunctionalInterface
interface AsyncEventErrorHandler implements Function<AsyncEventError, Boolean> { }
```

The `AsyncEventError` class encapsulates `AsyncEvent` along with the
`Throwable` that was thrown while processing the `AsyncEvent`.

Since the `AsyncEventErrorHandler` interface implements `Function`, you
should override the `apply(:AsyncEventError)` method to handle the error
with application-specific actions. The handler returns a `Boolean` to
indicate whether it was able to handle the error or not:

Example 13. Custom `AsyncEventErrorHandler` implementation



``` highlight
class CustomAsyncEventErrorHandler implements AsyncEventErrorHandler {

    @Override
    public Boolean apply(AsyncEventError error) {

        if (error.getCause() instanceof OptimisticLockingFailureException) {
            // handle optimistic locking failure if you can
            return true; // if error was successfully handled
        }
        else if (error.getCause() instanceof IncorrectResultSizeDataAccessException) {
            // handle no row or too many row update if you can
            return true; // if error was successfully handled
        }
        else {
            // ...
        }

        return false;
    }
}
```

You can configure the `RepositoryAsyncEventListener` with your custom
`AsyncEventErrorHandler` by using the
`AsyncInlineCachingRegionConfigurer`:

Example 14. Configuring a custom `AsyncEventErrorHandler`



``` highlight
@Configuration
class GeodeConfiguration {

    @Bean
    CustomAsyncEventErrorHandler customAsyncEventErrorHandler() {
        return new CustomAsyncEventErrorHandler();
    }

    @Bean
    AsyncInlineCachingRegionConfigurer asyncInlineCachingRegionConfigurer(
            CrudRepository<?, ?> repository,
            CustomAsyncEventErrorHandler errorHandler) {

        return AsyncInlineCachingRegionConfigurer.create(repository, "Example")
            .withAsyncEventErrorHandler(errorHandler);
    }
}
```

Also, since `AsyncEventErrorHandler` implements `Function`, you can
[compose](https://en.wikipedia.org/wiki/Composite_pattern) multiple
error handlers by using
[`Function.andThen(:Function)`](https://docs.oracle.com/en/java/javase/19/#andThen-java.util.function.Function-).

By default, the `RepositoryAsyncEventListener` handles `CREATE`,
`UPDATE`, and `REMOVE` cache event, entry operations.

`CREATE` and `UPDATE` translate to `CrudRepository.save(entity)`. The
`entity` is derived from `AsyncEvent.getDeserializedValue()`.

`REMOVE` translates to `CrudRepository.delete(entity)`. The `entity` is
derived from `AsyncEvent.getDeserializedValue()`.

The cache
[`Operation`](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/Operation.html)
to `CrudRepository` method is supported by the
`AsyncEventOperationRepositoryFunction` interface, which implements
`java.util.function.Function` and is a `@FunctionalInterface`.

This interface becomes useful if and when you want to implement
`CrudRepository` method invocations for other `AsyncEvent` `Operations`
not handled by SBDG’s `RepositoryAsyncEventListener`.

The `AsyncEventOperationRepositoryFunction` interface is defined as
follows:

Example 15. AsyncEventOperationRepositoryFunction interface definition



``` highlight
@FunctionalInterface
interface AsyncEventOperationRepositoryFunction<T, ID> implements Function<AsyncEvent<ID, T>,  Boolean> {

    default boolean canProcess(AsyncEvent<ID, T> event) {
        return false;
    }
}
```

`T` is the class type of the entity and `ID` is the class type of the
entity’s identifier (ID), possibly declared with Spring Data’s
[`org.springframework.data.annotation.Id`](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/annotation/Id.html)
annotation.

For convenience, SBDG provides the
`AbstractAsyncEventOperationRepositoryFunction` class for extension,
where you can provide implementations for the
`cacheProcess(:AsyncEvent)` and `doRepositoryOp(entity)` methods.

<p class="note"><strong>Note:</strong>
The
<code>AsyncEventOperationRepositoryFunction.apply(:AsyncEvent)</code>
method is already implemented in terms of
<code>canProcess(:AsyncEvent)</code>,
<code>resolveEntity(:AsyncEvent)</code>,
<code>doRepositoryOp(entity)</code>, and catching and handling any
<code>Throwable</code> (errors) by calling the configured
<code>AsyncEventErrorHandler</code>.
</p>


For example, you may want to handle
[`Operation.INVALIDATE`](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/Operation.html#INVALIDATE)
cache events as well, deleting the entity from the backend data store by
invoking the `CrudRepository.delete(entity)` method:

Example 16. Handling `AsyncEvent`, `Operation.INVALIDATE`



``` highlight
@Component
class InvalidateAsyncEventRepositoryFunction
        extends RepositoryAsyncEventListener.AbstractAsyncEventOperationRepositoryFunction<?, ?> {

    InvalidateAsyncEventRepositoryFunction(RepositoryAsyncEventListener<?, ?> listener) {
        super(listener);
    }

    @Override
    public boolean canProcess(AsyncEvent<?, ?> event) {
        return event != null && Operation.INVALIDATE.equals(event.getOperation());
    }


    @Override
    protected Object doRepositoryOperation(Object entity) {
        getRepository().delete(entity);
        return null;
    }
}
```

You can then register your user-defined,
`AsyncEventOperationRepositoryFunction` (that is,
`InvalidateAsyncEventRepositoryFunction`) with the
`RepositoryAsyncEventListener` by using the
`AsyncInlineCachingRegionConfigurer`:

Example 17. Configuring a user-defined
`AsyncEventOperationRepositoryFunction`



``` highlight
import org.springframework.geode.cache.RepositoryAsyncEventListener;

@Configuration
class GeodeConfiguration {

    @Bean
    AsyncInlineCachingRegionConfigurer asyncInlineCachingRegionConfigurer(
            CrudRepository<?, ?> repository,
            CustomAsyncEventErrorHandler errorHandler ) {

        return AsyncInlineCachingRegionConfigurer.create(repository, "ExampleRegion")
            .applyToListener(listener -> {

                if (listener instanceof RepositoryAsyncEventListener) {

                    RepositoryAsyncEventListener<?, ?> repositoryListener =
                        (RepositoryAsyncEventListener<?, ?>) listener;

                    repositoryListener.register(new InvalidAsyncEventRepositoryFunction(repositoryListener));
                }

                return listener;
            });
    }
}
```

This same technique can be applied to `CREATE`, `UPDATE`, and `REMOVE`
cache operations as well, effectively overriding the default behavior
for these cache operations handled by SBDG.

###### About `AsyncInlineCachingRegionConfigurer`

As we saw in the previous section, you can intercept and post-process
the essential components that are constructed and configured by the
`AsyncInlineCachingRegionConfigurer` class during initialization.

SBDG’s lets you intercept and post-process the `AsyncEventListener`
(such as `RepositoryAsyncEventListener`), the `AsyncEventQueueFactory`
and even the `AsyncEventQueue` created by the
`AsyncInlineCachingRegionConfigurer` (a SDG
[`RegionConfigurer`](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/RegionConfigurer.html))
during Spring `ApplicationContext` bean initialization.

The `AsyncInlineCachingRegionConfigurer` class provides the following
builder methods to intercept and post-process any of the following
VMware GemFire objects:

- `applyToListener(:Function<AsyncEventListener, AsyncEventListener>)`

- `applyToQueue(:Function<AsyncEventQueue, AsyncEventQueue>)`

- `applyToQueueFactory(:Function<AsyncEventQueueFactory, AsyncEventQueueFactory>)`

All of these `apply*` methods accept a `java.util.function.Function`
that applies the logic of the `Function` to the VMware GemFire
object (such as `AsyncEventListener`), returning the object as a result.

<p class="note"><strong>Note:</strong>
The VMware GemFire object returned by the
<code>Function</code> may be the same object, a proxy, or a completely
new object. Essentially, the returned object can be anything you want.
This is the fundamental premise behind Aspect-Oriented Programming (AOP)
and the <a
href="https://en.wikipedia.org/wiki/Decorator_pattern">Decorator
software design pattern</a>.


The `apply*` methods and the supplied `Function` let you decorate,
enhance, post-process, or otherwise modify the VMware GemFire
objects created by the configurer.

The `AsyncInlineCachingRegionConfigurer` strictly adheres to the
[open/close
principle](https://en.wikipedia.org/wiki/Open%E2%80%93closed_principle)
and is, therefore, flexibly extensible.

#### Multi-Site Caching

The final pattern of caching presented in this chapter is Multi-site
caching.

As described earlier, there are two configuration arrangements,
depending on your application usage patterns, requirements and user
demographic: active-active and active-passive.

Multi-site caching, along with active-active and active-passive
configuration arrangements, are described in more detail in the sample
[guide](guides/caching-multi-site.html). Also, be sure to review the
sample [code](https://github.com/spring-projects/spring-boot-data-geode/tree/1.7.4/spring-geode-samples/intro/getting-started/caching/multi-site).


### Advanced Caching Configuration

VMware GemFire supports additional caching capabilities to
manage the entries stored in the cache.

As you can imagine, given that cache entries are stored in-memory, it
becomes important to manage and monitor the available memory used by the
cache. After all, by default, VMware GemFire stores data in the
JVM Heap.

You can employ several techniques to more effectively manage memory,
such as using
[eviction](https://geode.apache.org/docs/guide/115/developing/eviction/chapter_overview.html),
possibly
[overflowing data to disk](https://geode.apache.org/docs/guide/115/developing/storing_data_on_disk/chapter_overview.html), configuring both entry Idle-Timeout\_ (TTI) and
Time-to-Live\_ (TTL)
[expiration policies](https://geode.apache.org/docs/guide/115/developing/expiration/chapter_overview.html), configuring
[compression](https://geode.apache.org/docs/guide/115/managing/region_compression.html), and
using
[off-heap](https://geode.apache.org/docs/guide/115/managing/heap_use/off_heap_management.html)
or main memory.

You can use several other strategies as well, as described in
[Managing Heap and Off-heap Memory](https://geode.apache.org/docs/guide/115/managing/heap_use/heap_management.html).

While this is beyond the scope of this document, know that Spring Data
for VMware GemFire makes all of these
[configuration options](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config-regions) available to you.

### Disable Caching

There may be cases where you do not want your Spring Boot application to
cache application state with
[Spring’s Cache Abstraction](https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache) using VMware GemFire. In certain cases, you may
use another Spring supported caching provider, such as Redis, to cache
and manage your application state. In other cases, you may not want to
use Spring’s Cache Abstraction at all.

Either way, you can specifically call out your Spring Cache Abstraction
provider by using the `spring.cache.type` property in
`application.properties`:

Example 18. Use Redis as the Spring Cache Abstraction Provider



``` highlight
#application.properties

spring.cache.type=redis
...
```

If you prefer not to use Spring’s Cache Abstraction to manage your
Spring Boot application’s state at all, then set the `spring.cache.type`
property to "none":

Example 19. Disable Spring’s Cache Abstraction



``` highlight
#application.properties

spring.cache.type=none
...
```

See the Spring Boot
[documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-caching.html#boot-features-caching-provider-none)
for more detail.

You can include multiple caching providers on the
classpath of your Spring Boot application. For instance, you might use
Redis to cache your application’s state while using
VMware GemFire as your application’s persistent data store (that
is, the System of Record (SOR)).

<p class="note"><strong>Note:</strong>
Spring Boot does not properly recognize
<code>spring.cache.type=[gemfire|geode]</code>, even though Spring Boot
for VMware GemFire is set up to handle either of these property
values (that is, either <code>gemfire</code> or
<code>geode</code>).
</p>

