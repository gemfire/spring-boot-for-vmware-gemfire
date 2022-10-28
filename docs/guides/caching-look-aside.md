# Look-Aside Caching with Spring

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

- [Background](#geode-samples-caching-lookaside-background)
- [Example (with additional
  background)](#geode-samples-caching-lookaside-example)
  - [Counter Service
    Application](#geode-samples-caching-lookaside-example-counterservice-application)
  - [Caching-enabled
    CounterService](#geode-samples-caching-lookaside-example-counterservice-cacheableservice)
  - [CounterController](#geode-samples-caching-lookaside-example-counterservice-controller)
  - [Counter Service
    Configuration](#geode-samples-caching-lookaside-example-counterservice-configuration)
- [Run the Example](#geode-samples-caching-lookaside-example-run)
  - [Running the Example using
    Client/Server](#geode-samples-caching-lookaside-example-run-clientserver)
- [Conclusion](#geode-samples-caching-lookaside-conclusion)


This guide walks you through building a simple Spring Boot application
using [Spring’s Cache
Abstraction](https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache)
backed by VMware GemFire as the caching provider for Look-Aside Caching.

It is assumed that the reader is familiar with the Spring *programming
model*. No prior knowledge of Spring’s *Cache Abstraction* or VMware
GemFire is required to utilize caching in your Spring Boot applications.

Let’s begin.

Refer to the <a
href="../index.html#geode-caching-provider-look-aside-caching">Look-Aside
Caching</a> section in the <a
href="../index.html#geode-caching-provider">Caching with VMware
GemFire</a> chapter in the reference documentation for more
information.

<div id="index-link" class="paragraph">

[Index](../index.html)

[Back to Samples](../index.html#geode-samples)


## Background

Caching is an effective software design pattern for reducing the
resource consumption used by your application as well as improving
efficiency by increasing throughput and reducing latency.

The fundamental premise of caching is, when given the same arguments, if
a service call yields the same results every time, then it is a good
candidate for caching.

Indeed, if I am searching for a customer record by account number and
the search always yields the same customer for a given account number,
then adding caching to the search operation will improve the overall
user experience. After all, the account number may be a form of customer
identity. We can save compute resources by caching the customer’s
information, which is especially useful if the customer’s information is
used in multiple workflows of the application during the interactions
with the customer.

While there are different patterns of caching, the ***Look-Aside
Caching*** pattern is the most frequently used.

*Look-Aside Caching* is a pattern of caching where the input of a
cacheable operation is used as the key for looking up any cached results
from a prior invocation of the operation when given the same input. In
*Look-Aside Caching*, the cache is consulted first, before the operation
is invoked, and if a computation for the given input has already been
computed and cached, then the value from the cache is returned.
Otherwise, if no value has been cached for the given input, or the
previous cache result expired, or was evicted, then the operation will
be invoked and the result of the operation is cached using the input as
the key and the result as a value.

It should be apparent that the data structure of a cache is a key/value
store, or a `Map`. Indeed it is quite common for most cache
implementations to even implement the `java.util.Map` interface.
However, many cache implementations are quite a bit more sophisticated,
providing distribution (to scale-out), replication (HA) and even
persistence along with other capabilities.

For example, I may have a `CustomerService` class that looks up a
`Customer` by `AccountNumber`:

Cacheable CustomerService class

``` highlight
@Service
class CustomerService {

  @Cacheable("CustomersByAccountNumber")
  Customer findBy(AccountNumber accountNumber) {
    // ...
  }
}
```

If I have already looked up a `Customer` (e.g. "Jon Doe") with a given
`AccountNumber` (e.g. "abc123"), then when the `findBy(..)` method is
called with the same `AccountNumber` (i.e. "abc123") again, we would
expect the same result (i.e. `Customer` "Jon Doe") to be returned.

The *Look-Aside Caching* pattern can be depicted in the following
diagram:

![Look Aside Caching Pattern](./images/Look-Aside-Caching-Pattern.png)

In the diagram above, we see that the caching provider (e.g. VMware
GemFire) is consulted first, \#2, after the client initiated the
request, \#1. If the result of the cacheable operation for the given
input has already been computed and stored in the cache (a *cache hit*),
then the result is simply returned, \#3, and passed back to the caller,
\#6.

However, if the cacheable operation has never been invoked with the
given input, or the previous computation of the operation for the given
input expired, or was evicted, then the cacheable operation is invoked
(*cache miss*). This cacheable operation may access some external data
source to perform its computation, \#3 (red). After the operation
completes, it returns the result, but not before the caching
infrastructure stores the result along with the input in the cache, \#4
& \#5. After the result is cached, the value is returned to the caller,
\#3 (green). Any subsequent invocation of the cacheable operation with
the same input should yield the same result as stored in the cache,
providing the cache entry (input→result) has not expired or been
evicted.

Spring’s [Cache
Abstraction](https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache)
is just that, a very elegant implementation of the *Look-Aside Caching*
pattern. Details of how Spring’s *Cache Abstraction* works
under-the-hood is beyond the scope of this document. In a nutshell, it
relies on Spring AOP and is not unlike Spring’s Transaction Management.

Different caching providers have different capabilities. You should
choose the caching provider that gives you what you require to handle
your application needs and use cases correctly.

If used appropriately, caching can greatly improve your application’s
end-user experience.

Instead of using <a
href="https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/cache/annotation/package-summary.html">Spring’s
Cache Annotations</a>, you may instead use JSR-107, JCache API
Annotations, which is <a
href="https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache-jsr-107">supported</a>
by Spring’s <em>Caching Abstraction</em>.

See Spring Boot’s documentation for a complete list
of <a
href="https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-caching.html#boot-features-caching-provider">supported
caching providers</a>.


## Example (with additional background)

To make the effects of Spring’s *Cache Abstraction* using VMware GemFire
as the cache provider apparent in your application, we show how to
enable and use caching with your application in a very small, simple
example.

The example Spring Boot application implements a Counter Service, which
simply maintains a collection of named counters. The application
provides a REST-ful Web interface to increment a counter, get the
current cached count for a named counter, and the ability to reset a
named counter to 0.

Typically, caching is used to offset the costs associated with expensive
operations, such as disk or network I/O. Indeed, both an operation’s
throughput and latency is bound by an I/O operation since compute is
many orders of magnitude faster than disk, network, etc.

While developers have been quick to throw more Threads at the problem,
trying to do more work in parallel, this opens the door to a whole new
set of problems (concurrency), usually at the expense of using more
resources, which does not always yield the desired results.

Opportunities for caching are often overlooked, yet is very effective at
minimizing the over utilization of resources by leveraging reuse. In an
ever increasing Microservices based world, caching will become even more
important as it serves a very important role in the applications
architecture, not the least of which is, resiliency.

Of course, you still must tune your cache. Most caches keep information
in memory, and since memory is finite, you must utilize strategies to
manage memory effectively, such as eviction, expiration, or even
Off-Heap (i.e. native memory) for JVM-based caches. For example,
evicting/expiring entries based on use (*Least Recently Used*, or LRU)
is 1 of many effective strategies.

Each caching provider’s capabilities are different in this regard. The
choice should not only be based on what capabilities you need now, but
capabilities (e.g. distributed compute, streaming) you may need in the
future. So, choose wisely.

### Counter Service Application

Let’s have a look at the Counter Service application.

We start with a simple, Spring Boot, Servlet-based, Web application:

SpringBootApplication

``` highlight
@SpringBootApplication
public class BootGeodeLookAsideCachingApplication {

    public static void main(String[] args) {

        new SpringApplicationBuilder(BootGeodeLookAsideCachingApplication.class)
            .web(WebApplicationType.SERVLET)
            .build()
            .run(args);
    }
}
```

With the `org.springframework.geode:spring-geode-starter` dependency on
your application classpath:

spring-goede-starter dependency

``` highlight
<dependency>
  <groupId>org.springframework.geode</groupId>
  <artifactId>spring-geode-starter</artifactId>
</dependency>
```

And the `BootGeodeLookAsideCachingApplication` class annotated with
`@SpringBootApplication`, you have everything you need to begin using
Spring’s *Cache Abstraction* in your application with VMware GemFire as
the caching provider.

As an application developer, all you need do is focus on where in your
application caching would be most beneficial.

Let’s do that.

### Caching-enabled CounterService

Next, we define the operations our `CounterService` and add caching:

CounterService

``` highlight
@Service
public class CounterService {

    private ConcurrentMap<String, AtomicLong> namedCounterMap = new ConcurrentHashMap<>();

    @Cacheable("Counters")
    public long getCachedCount(String counterName) {
        return getCount(counterName);
    }

    @CachePut("Counters")
    public long getCount(String counterName) {

        AtomicLong counter = this.namedCounterMap.get(counterName);

        if (counter == null) {

            counter = new AtomicLong(0L);

            AtomicLong existingCounter = this.namedCounterMap.putIfAbsent(counterName, counter);

            counter = existingCounter != null ? existingCounter : counter;
        }

        return counter.incrementAndGet();
    }

    @CacheEvict("Counters")
    public void resetCounter(String counterName) {
        this.namedCounterMap.remove(counterName);
    }
}
```

The primary function of the `CounterService` is to maintain a collection
of named counters, incrementing the count each time a named counter is
accessed, and returning the current (cached) count. There is an
additional operation to reset a named counter to 0.

All `CounterService` operations perform a cache function.

The `@Cacheable` `getCachedCount(:String)` method is our ***look-aside
cache*** operation. That is, the "Counters" cache is consulted for the
named counter before the method is invoked. If a count has already been
established for the named counter, then the cached count is returned and
the method will not be invoked. Otherwise the `getCachedCount(:String)`
method is invoked and proceeds to call the `getCount(:String)` method.

The `@CachePut` annotated `getCount(:String)` method is always invoked,
but the result is cached. If a cache entry already exists, then it is
updated (or in this case, replaced). This method always has the effect
of incrementing the named counter.

Finally, we have a `@CacheEvict` annotated `resetCache(:String)` method,
which will reset the named counter to 0 and evict the cache entry for
the named counter.

Each of the Spring’s Cache annotations can be
replaced with the corresponding JSR-107 - JCache API annotations as <a
href="https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache-jsr-107">documented
here</a>, and the application will work just the same.

### CounterController

Then, we include a Spring Web MVC Controller to access our Counter
Service application from a Web browser:

CounterController

``` highlight
@RestController
public class CounterController {

    private static final String HEADER_ONE = "<h1>%s</h1>";

    private final CounterService counterService;

    public CounterController(CounterService counterService) {

        Assert.notNull(counterService, "CounterService is required");

        this.counterService = counterService;
    }

    @GetMapping("/")
    public String home() {
        return String.format(HEADER_ONE, "Look-Aside Caching Example");
    }

    @GetMapping("/ping")
    public String ping() {
        return String.format(HEADER_ONE, "PONG");
    }

    @GetMapping("counter/{name}")
    public String getCount(@PathVariable("name") String counterName) {
        return String.format(HEADER_ONE, this.counterService.getCount(counterName));
    }

    @GetMapping("counter/{name}/cached")
    public String getCachedCount(@PathVariable("name") String counterName) {
        return String.format(HEADER_ONE, this.counterService.getCachedCount(counterName));
    }

    @GetMapping("counter/{name}/reset")
    public String resetCounter(@PathVariable("name") String counterName) {
        this.counterService.resetCounter(counterName);
        return String.format(HEADER_ONE, "0");
    }
}
```

Essentially, we just inject our `CounterService` application class and
wrap the service operations in Web service endpoints, accessible by URL
using HTTP:

<table class="tableblock frame-all grid-all stretch">
<caption>Table 1. Counter Web service endpoints</caption>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">URL</th>
<th class="tableblock halign-left valign-top">Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>/ping</code></p></td>
<td class="tableblock halign-left valign-top"><p>Heartbeat request to
test that our application is alive and running.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>/counter/{name}</code></p></td>
<td class="tableblock halign-left valign-top"><p>Increments the "named"
counter.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>/counter/{name}/cached</code></p></td>
<td class="tableblock halign-left valign-top"><p>Returns the current,
cached count for the "named" counter.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>/counter/{name}/reset</code></p></td>
<td class="tableblock halign-left valign-top"><p>Resets the count for
the "named" counter.</p></td>
</tr>
</tbody>
</table>

Table 1. Counter Web service endpoints

The base URL is <a href="http://localhost:8080"
class="bare"><code>http://localhost:8080</code></a>.

After running the `BootGeodeLookAsideCachingApplication` class, if you
open a Web browser and navigate to <a href="http://localhost:8080/ping"
class="bare"><code>http://localhost:8080/ping</code></a>, you should see
the content "**PONG**".

### Counter Service Configuration

While Spring Boot for VMware GemFire, SBDG, takes care of enabling
Spring’s caching infrastructure for you, configuring VMware GemFire as a
caching provider, you still must define and declare your individual
caches.

No Spring caching provider is fully configured by Spring or Spring Boot
for that matter. Part of the reason for this is that there are many
different ways to configure the caches.

Remember earlier we mentioned tuning a cache with eviction or expiration
policies, perhaps using Off-Heap memory, overflowing entries to disk,
making caches persistent, are few of the ways to tune or configure a
cache. You might be using a client/server or even a WAN topology and you
might need to configure things like conflation, filters, compression,
security (e.g. SSL), and so on.

However, this is a lot to think about and you may just simply want to
get up and running as quickly as possible. While SBDG is not opinionated
about this out-of-the-box, we do provide assistance to make this task
easy:

GeodeConfiguration

``` highlight
@Configuration
@EnableClusterAware
@EnableCachingDefinedRegions
public class GeodeConfiguration { }
```

The only thing of real significance here is the
`@EnableCachingDefinedRegions` annotation. This Spring Data for VMware
GemFire (SDG) annotation is responsible for introspecting our Spring
Boot application on Spring container startup, identifying all the
caching annotations (both Spring Cache annotations as wells JSR-107,
JCache annotations) used in our application components, and creating the
appropriate caches.

If you were not using SDG’s `@EnablingCachingDefinedRegions` annotation,
then you would need to define the Region using the equivalent
*JavaConfig*:

"Counters" Region definition using JavaConfig

``` highlight
@Configuration
class GeodeConfiguration {

  @Bean("Counters")
  public ClientRegionFactoryBean<Object, Object> countersRegion(GemFireCache gemfireCache) {

    ClientRegionFactoryBean<Object, Object> countersRegion = new ClientRegionFactoryBean<>();

    countersRegion.setCache(gemfireCache);
    countersRegion.setShortcut(ClientRegionShortcut.LOCAL);

    return countersRegion;
  }
}
```

Or using XML:

"Counters" Region definiton using XML

``` highlight
<gfe:client-region id="Counters" shortcut="LOCAL"/>
```

In VMware GemFire terminology, each cache identified in 1 of the caching
annotations by name, will have an VMware GemFire Region created for it.

In our case, SBDG provides us a `ClientCache` instance by default, so we
will be creating client `LOCAL` Regions. The client "Counters" Region is
`LOCAL` since we do not (yet) have a cluster of servers running.

However, it would be very simple to convert this application into using
a client/server topology by simply starting a cluster of servers.

#### Client/Server Configuration

To use the client/server topology, you need to start a cluster with 1 or
more servers using the default configuration. You can start the cluster
using the VMware GemFire Shell tool (*Gfsh*) and create the "Counters"
Region on the servers.

Of course, you technically do not even need to create the "Counters"
Region on the server. The `@EnableClusterAware` annotation is
meta-annotated with SDG’s `@EnableClusterConfiguration(..)` annotation,
which will create the necessary server-side, "Counters" Region for you.

After starting a cluster with a Locator & Server using *Gfsh*:

``` highlight
$ gfsh
    _________________________     __
   / _____/ ______/ ______/ /____/ /
  / /  __/ /___  /_____  / _____  /
 / /__/ / ____/  _____/ / /    / /
/______/_/      /______/_/    /_/    1.2.1

Monitor and Manage VMware GemFire

gfsh>start locator --name=LocatorOne --log-level=config
Starting a Geode Locator in /Users/jblum/pivdev/lab/LocatorOne...
....


gfsh>start server --name=ServerOne --log-level=config
Starting a Geode Server in /Users/jblum/pivdev/lab/ServerOne...
.....


gfsh>list members
   Name    | Id
---------- | ---------------------------------------------------
LocatorOne | 10.99.199.24(LocatorOne:40824:locator)<ec><v0>:1024
ServerOne  | 10.99.199.24(ServerOne:40855)<v1>:1025


gfsh>list regions
No Regions Found
```

The application configuration (i.e. `GeodeConfiguration`) is already set
to go:

Using client/server

``` highlight
@Configuration
@EnableClusterAware
@EnableCachingDefinedRegions
public class GeodeConfiguration { }
```

After (re-)starting the application, we will see that the "Counters"
Region has been created in the cluster, and specifically on
"*ServerOne*":

"Counters" Region

``` highlight
gfsh>list regions
List of regions
---------------
Counters


gfsh>describe region --name=/Counters
..........................................................
Name            : Counters
Data Policy     : partition
Hosting Members : ServerOne

Non-Default Attributes Shared By Hosting Members

 Type  |    Name     | Value
------ | ----------- | ---------
Region | size        | 0
       | data-policy | PARTITION
```

We will refer to the client/server approach further below, when running
the example.

Refer to VMware GemFire’s documentation to learn more about the
[client/server
topology](https://geode.apache.org/docs/guide/%7Bapache-geode-doc-version%7D/topologies_and_comm/cs_configuration/chapter_overview.html).

Refer to SDG’s documentation to learn more about [Cluster
Configuration](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config-cluster).

Refer to SBDG’s documentation to learn about the
[`@EnableClusterAware`](../index.html#geode-configuration-declarative-annotations-productivity-enableclusteraware)
annotation.


## Run the Example

Now it is time to run the example.

You can run the `BootGeodeLookAsideCachingApplication` class from your
IDE (e.g. IntelliJ IDEA) by creating a simple run profile configuration.
No additional JVM arguments, System Properties or program argument are
required to run the example.

Alternatively, you can run the example using the `gradlew` command from
the command-line as follows:

Run the example with `gradlew`

``` highlight
$ gradlew :spring-geode-samples-caching-lookaside:bootRun
```

The program output will appear as follows:

Run the `BootGeodeLookAsideCachingApplication` class

``` highlight
/Library/Java/JavaVirtualMachines/jdk1.8.0_192.jdk/Contents/Home/bin/java -server -ea ...
    example.app.caching.lookaside.BootGeodeLookAsideCachingApplication

[info 2019/05/06 12:09:57.356 PDT <background-preinit> tid=0xd] HV000001: Hibernate Validator 6.0.16.Final


  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.0.9.RELEASE)

[info 2019/05/06 12:09:57.531 PDT <main> tid=0x1] Starting BootGeodeLookAsideCachingApplication on jblum-mbpro-2.local with PID 40871...

[info 2019/05/06 12:09:57.532 PDT <main> tid=0x1] No active profile set, falling back to default profiles: default

[info 2019/05/06 12:09:57.582 PDT <main> tid=0x1] Refreshing org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext@2eea88a1: startup date [Mon May 06 12:09:57 PDT 2019]; root of context hierarchy

...

[info 2019/05/06 12:09:59.234 PDT <main> tid=0x1] Tomcat initialized with port(s): 8080 (http)

2019-05-06 12:09:59.267  INFO 40871 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2019-05-06 12:09:59.269  INFO 40871 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet Engine: Apache Tomcat/8.5.39
2019-05-06 12:09:59.280  INFO 40871 --- [ost-startStop-1] o.a.catalina.core.AprLifecycleListener   : The APR based Apache Tomcat Native library which allows optimal performance in production environments was not found on the java.library.path: [/Users/jblum/Library/Java/Extensions:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java:.]
2019-05-06 12:09:59.381  INFO 40871 --- [ost-startStop-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
[info 2019/05/06 12:09:59.381 PDT <localhost-startStop-1> tid=0x10] Root WebApplicationContext: initialization completed in 1800 ms

[info 2019/05/06 12:09:59.440 PDT <localhost-startStop-1> tid=0x10] Servlet dispatcherServlet mapped to [/]

...

2019-05-06 12:10:26.116  INFO 40871 --- [nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring FrameworkServlet 'dispatcherServlet'
```

Then open your Web browser and navigate to
<a href="http://locahost:8080" class="bare">http://locahost:8080</a> or
`ping` Web service endpoint at <a href="http://localhost:8080/ping"
class="bare">http://localhost:8080/ping</a>:

![LookAsideCachingApplication
Ping](./images/LookAsideCachingApplication-Ping.png)

After that, we can create and increment counters, for example:

<a href="http://localhost:8080/counter/A"
class="bare"><code>http://localhost:8080/counter/A</code></a>

**1**

If you constantly hit the refresh button, you will see 2, 3, 4, 5, …​ and
so on. While the named counter’s (i.e. "A") new count is being cached,
we are not returning the cached value.

If you navigate to:

<a href="http://localhost:8080/counter/A/cached"
class="bare"><code>http://localhost:8080/counter/A/cached</code></a>

The count for the named counter (e.g. "A") will remain fixed on whatever
the last count was (e.g. "5").

You can begin a new named counter (e.g. "B") without affecting the
exiting named counter (i.e. "A"), by navigating to:

<a href="http://localhost:8080/counter/B"
class="bare"><code>http://localhost:8080/counter/B</code></a>

**1**

And again, after refreshing the page multiple times:

**3**

If you navigate to:

<a href="http://localhost:8080/counter/B/reset"
class="bare"><code>http://localhost:8080/counter/B/reset</code></a>

**0**

This resets the count of counter "B" to 0. However, this does not affect
the count of counter "A", which we can reevaluate by navigating to:

<a href="http://localhost:8080/counter/A/cached"
class="bare"><code>http://localhost:8080/counter/A/cached</code></a>

**5**

This is an extremely simple application, but shows the effects of
caching.

### Running the Example using Client/Server

If you are using the client/server topology, the effects of caching are
no different. However, after running the example application you can
evaluate the state of the "Counters" Region using *Gfsh*, like so:

Describing and Querying the "Counters" Region on the Server

``` highlight
gfsh>describe region --name=/Counters
..........................................................
Name            : Counters
Data Policy     : partition
Hosting Members : ServerOne

Non-Default Attributes Shared By Hosting Members

 Type  |    Name     | Value
------ | ----------- | ---------
Region | size        | 2
       | data-policy | PARTITION


gfsh>query --query="SELECT entries.key, entries.value FROM /Counters.entrySet entries"
Result : true
Limit  : 100
Rows   : 2

key | value
--- | -----
A   | 5
B   | 2
```


## Conclusion

As you have learned, Spring makes enabling and using caching in your
application really easy.

With SBDG, using VMware GemFire as your caching provider in Spring’s
*Cache Abstraction* is as easy as making sure
`org.springframework.geode:spring-geode-starter` is on your
application’s classpath. You just need to focus on areas of your
application that would benefit from caching.

You have now successfully used the ***Look-Aside Caching*** pattern in
your Spring Boot application.

Later we will cover more advanced forms of the *Look-Aside Caching*
pattern (e.g. using Eviction/Expiration policies) as well as take a look
at other caching patterns, like *Inline Caching*, *Multi-Site Caching*
and *Near Caching*.

