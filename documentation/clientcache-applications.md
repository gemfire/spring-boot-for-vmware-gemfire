---
title: Building ClientCache Applications
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

The first opinionated option provided to you by [spring-boot-gemfire-name] is a
[`ClientCache`](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/client/ClientCache.html)
instance that you get by declaring [spring-boot-gemfire-name] on your application classpath.

It is assumed that most application developers who use Spring Boot to
build applications backed by [vmware-gemfire-name] are building cache
client applications deployed in a [vmware-gemfire-name]
[Client/Server Topology](https://geode.apache.org/docs/guide/115/topologies_and_comm/cs_configuration/chapter_overview.html).
The client/server topology is the most common and
traditional architecture employed by enterprise applications that use
[vmware-gemfire-name].

For example, you can begin building a Spring Boot [vmware-gemfire-name]
`ClientCache` application by declaring the `spring-geode-starter` on
your application’s classpath:

Example 1. [spring-boot-gemfire-name] on the application
classpath


``` highlight
<dependency>
  <groupId>org.springframework.geode</groupId>
  <artifactId>spring-geode-starter</artifactId>
</dependency>
```

Then you configure and bootstrap your Spring Boot,
[vmware-gemfire-name] `ClientCache` application with the following main
application class:

Example 2. Spring Boot, [vmware-gemfire-name] `ClientCache` Application


``` highlight
@SpringBootApplication
public class SpringBootApacheGeodeClientCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApacheGeodeClientCacheApplication.class, args);
    }
}
```

Your application now has a `ClientCache` instance that can connect to an
[vmware-gemfire-name] server running on `localhost` and listening on
the default `CacheServer` port, `40404`.

By default, a [vmware-gemfire-name] server (that is, `CacheServer`)
must be running for the application to use the `ClientCache` instance.
However, it is perfectly valid to create a `ClientCache` instance and
perform data access operations by using `LOCAL` Regions. This is useful
during development.

To develop with <code>LOCAL</code> Regions,
configure your cache Regions with the
[<code>ClientRegionShortcut.LOCAL</code>](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/client/ClientRegionShortcut.html#LOCAL)
data management policy.

When you are ready to switch from your local development environment
(IDE) to a client/server architecture in a managed environment, change
the data management policy of the client Region from `LOCAL` back to the
default (`PROXY`) or even a `CACHING_PROXY`, which causes the data to be
sent to and received from one or more servers.

Compare and contrast the preceding configuration
with the [spring-data-gemfire-name]
[approach](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config-geode-applications).

It is uncommon to ever need a direct reference to the `ClientCache`
instance provided by [spring-boot-gemfire-name] injected into your application components (for
example, `@Service` or `@Repository` beans defined in a Spring
`ApplicationContext`), whether you are configuring additional
[vmware-gemfire-name] objects (Regions, Indexes, and so on) or are
using those objects indirectly in your applications. However, it is
possible to do so if and when needed.

For example, perhaps you want to perform some additional `ClientCache`
initialization in a Spring Boot
[`ApplicationRunner`](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/ApplicationRunner.html)
on startup:

Example 3. Injecting a `GemFireCache` reference


``` highlight
@SpringBootApplication
public class SpringBootApacheGeodeClientCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApacheGeodeClientCacheApplication.class, args);
    }

    @Bean
    ApplicationRunner runAdditionalClientCacheInitialization(GemFireCache gemfireCache) {

        return args -> {

            ClientCache clientCache = (ClientCache) gemfireCache;

            // perform additional ClientCache initialization as needed
        };
    }
}
```

### Building Embedded (Peer & Server) Cache Applications

What if you want to build an embedded peer `Cache` application instead?

Perhaps you need an actual peer cache member, configured and
bootstrapped with Spring Boot, along with the ability to join this
member to an existing cluster (of data servers) as a peer node.

Remember the second goal in Spring Boot’s
[documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/#getting-started-introducing-spring-boot):

> 
>
> Be opinionated out of the box but get out of the way quickly as
> requirements start to diverge from the defaults.
>
> 

Here, we focus on the second part of the goal: "*get out of the way
quickly as requirements start to diverge from the defaults*".

If your application requirements demand you use Spring Boot to configure
and bootstrap an embedded peer `Cache` instance, declare your intention
with either [spring-data-gemfire-name]’s
[`@PeerCacheApplication`](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/PeerCacheApplication.html)
annotation, or, if you also need to enable connections from
`ClientCache` applications, use [spring-data-gemfire-name]’s
[`@CacheServerApplication`](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html)
annotation:

Example 4. Spring Boot, [vmware-gemfire-name] `CacheServer` Application


``` highlight
@SpringBootApplication
@CacheServerApplication(name = "SpringBootApacheGeodeCacheServerApplication")
public class SpringBootApacheGeodeCacheServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApacheGeodeCacheServerApplication.class, args);
    }
}
```

A [vmware-gemfire-name] server is not necessarily
a <code>CacheServer</code> capable of serving cache clients. It is
merely a peer member node in a [vmware-gemfire-name] cluster (that is,
a distributed system) that stores and manages data.

By explicitly declaring the `@CacheServerApplication` annotation, you
tell Spring Boot that you do not want the default `ClientCache` instance
but rather want an embedded peer `Cache` instance with a `CacheServer`
component, which enables connections from `ClientCache` applications.

You can also enable two other [vmware-gemfire-name] services: \* An
embedded *Locator*, which allows clients or even other peers to locate
servers in the cluster. \* An embedded *Manager*, which allows the
[vmware-gemfire-name] application process to be managed and monitored
by using
[Gfsh](https://geode.apache.org/docs/guide/115/tools_modules/gfsh/chapter_overview.html),
[vmware-gemfire-name]'s command-line shell tool:

Example 5. Spring Boot [vmware-gemfire-name] `CacheServer` Application
with *Locator* and *Manager* services enabled


``` highlight
@SpringBootApplication
@CacheServerApplication(name = "SpringBootApacheGeodeCacheServerApplication")
@EnableLocator
@EnableManager
public class SpringBootApacheGeodeCacheServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApacheGeodeCacheServerApplication.class, args);
    }
}
```

Then you can use Gfsh to connect to and manage this server:

``` highlight
$ echo $GEMFIRE
/Users/jblum/pivdev/apache-geode-1.2.1

$ gfsh
    _________________________     __
   / _____/ ______/ ______/ /____/ /
  / /  __/ /___  /_____  / _____  /
 / /__/ / ____/  _____/ / /    / /
/______/_/      /______/_/    /_/    1.2.1

Monitor and Manage [vmware-gemfire-name]

gfsh>connect
Connecting to Locator at [host=localhost, port=10334] ..
Connecting to Manager at [host=10.0.0.121, port=1099] ..
Successfully connected to: [host=10.0.0.121, port=1099]


gfsh>list members
                   Name                     | Id
------------------------------------------- | --------------------------------------------------------------------------
SpringBootApacheGeodeCacheServerApplication | 10.0.0.121(SpringBootApacheGeodeCacheServerApplication:29798)<ec><v0>:1024


gfsh>describe member --name=SpringBootApacheGeodeCacheServerApplication
Name        : SpringBootApacheGeodeCacheServerApplication
Id          : 10.0.0.121(SpringBootApacheGeodeCacheServerApplication:29798)<ec><v0>:1024
Host        : 10.0.0.121
Regions     :
PID         : 29798
Groups      :
Used Heap   : 168M
Max Heap    : 3641M
Working Dir : /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build
Log file    : /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build
Locators    : localhost[10334]

Cache Server Information
Server Bind              :
Server Port              : 40404
Running                  : true
Client Connections       : 0
```

You can even start additional servers in Gfsh. These additional servers
connect to your Spring Boot configured and bootstrapped
[vmware-gemfire-name] `CacheServer` application. These additional
servers started in Gfsh know about the Spring Boot,
[vmware-gemfire-name] server because of the embedded Locator service,
which is running on `localhost` and listening on the default Locator
port, `10334`:

``` highlight
gfsh>start server --name=GfshServer --log-level=config --disable-default-server
Starting a Geode Server in /Users/jblum/pivdev/lab/GfshServer...
...
Server in /Users/jblum/pivdev/lab/GfshServer on 10.0.0.121 as GfshServer is currently online.
Process ID: 30031
Uptime: 3 seconds
GemFire Version: 1.2.1
Java Version: 1.8.0_152
Log File: /Users/jblum/pivdev/lab/GfshServer/GfshServer.log
JVM Arguments: -Dgemfire.default.locators=10.0.0.121:127.0.0.1[10334] -Dgemfire.use-cluster-configuration=true -Dgemfire.start-dev-rest-api=false -Dgemfire.log-level=config -XX:OnOutOfMemoryError=kill -KILL %p -Dgemfire.launcher.registerSignalHandlers=true -Djava.awt.headless=true -Dsun.rmi.dgc.server.gcInterval=9223372036854775806
Class-Path: /Users/jblum/pivdev/apache-geode-1.2.1/lib/geode-core-1.2.1.jar:/Users/jblum/pivdev/apache-geode-1.2.1/lib/geode-dependencies.jar


gfsh>list members
                   Name                     | Id
------------------------------------------- | --------------------------------------------------------------------------
SpringBootApacheGeodeCacheServerApplication | 10.0.0.121(SpringBootApacheGeodeCacheServerApplication:29798)<ec><v0>:1024
GfshServer                                  | 10.0.0.121(GfshServer:30031)<v1>:1025
```

Perhaps you want to start the other way around. You may need to connect
a Spring Boot configured and bootstrapped [vmware-gemfire-name] server
application to an existing cluster. You can start the cluster in Gfsh
with the following commands (shown with partial typical output):

``` highlight
gfsh>start locator --name=GfshLocator --port=11235 --log-level=config
Starting a Geode Locator in /Users/jblum/pivdev/lab/GfshLocator...
...
Locator in /Users/jblum/pivdev/lab/GfshLocator on 10.0.0.121[11235] as GfshLocator is currently online.
Process ID: 30245
Uptime: 3 seconds
GemFire Version: 1.2.1
Java Version: 1.8.0_152
Log File: /Users/jblum/pivdev/lab/GfshLocator/GfshLocator.log
JVM Arguments: -Dgemfire.log-level=config -Dgemfire.enable-cluster-configuration=true -Dgemfire.load-cluster-configuration-from-dir=false -Dgemfire.launcher.registerSignalHandlers=true -Djava.awt.headless=true -Dsun.rmi.dgc.server.gcInterval=9223372036854775806
Class-Path: /Users/jblum/pivdev/apache-geode-1.2.1/lib/geode-core-1.2.1.jar:/Users/jblum/pivdev/apache-geode-1.2.1/lib/geode-dependencies.jar

Successfully connected to: JMX Manager [host=10.0.0.121, port=1099]

Cluster configuration service is up and running.


gfsh>start server --name=GfshServer --log-level=config --disable-default-server
Starting a Geode Server in /Users/jblum/pivdev/lab/GfshServer...
....
Server in /Users/jblum/pivdev/lab/GfshServer on 10.0.0.121 as GfshServer is currently online.
Process ID: 30270
Uptime: 4 seconds
GemFire Version: 1.2.1
Java Version: 1.8.0_152
Log File: /Users/jblum/pivdev/lab/GfshServer/GfshServer.log
JVM Arguments: -Dgemfire.default.locators=10.0.0.121[11235] -Dgemfire.use-cluster-configuration=true -Dgemfire.start-dev-rest-api=false -Dgemfire.log-level=config -XX:OnOutOfMemoryError=kill -KILL %p -Dgemfire.launcher.registerSignalHandlers=true -Djava.awt.headless=true -Dsun.rmi.dgc.server.gcInterval=9223372036854775806
Class-Path: /Users/jblum/pivdev/apache-geode-1.2.1/lib/geode-core-1.2.1.jar:/Users/jblum/pivdev/apache-geode-1.2.1/lib/geode-dependencies.jar


gfsh>list members
   Name     | Id
----------- | --------------------------------------------------
GfshLocator | 10.0.0.121(GfshLocator:30245:locator)<ec><v0>:1024
GfshServer  | 10.0.0.121(GfshServer:30270)<v1>:1025
```

Then modify the `SpringBootApacheGeodeCacheServerApplication` class to
connect to the existing cluster:

Example 6. Spring Boot [vmware-gemfire-name] `CacheServer` Application
connecting to an external cluster


``` highlight
@SpringBootApplication
@CacheServerApplication(name = "SpringBootApacheGeodeCacheServerApplication", locators = "localhost[11235]")
public class SpringBootApacheGeodeCacheServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApacheGeodeClientCacheApplication.class, args);
    }
}
```

Notice that the
<code>SpringBootApacheGeodeCacheServerApplication</code> class,
<code>@CacheServerApplication</code> annotation’s <code>locators</code>
property are configured with the host and port
(<code>localhost[11235]</code>), on which the Locator was started by
using Gfsh.

After running your Spring Boot [vmware-gemfire-name] `CacheServer`
application again and executing the `list members` command in Gfsh
again, you should see output similar to the following:

``` highlight
gfsh>list members
                   Name                     | Id
------------------------------------------- | ----------------------------------------------------------------------
GfshLocator                                 | 10.0.0.121(GfshLocator:30245:locator)<ec><v0>:1024
GfshServer                                  | 10.0.0.121(GfshServer:30270)<v1>:1025
SpringBootApacheGeodeCacheServerApplication | 10.0.0.121(SpringBootApacheGeodeCacheServerApplication:30279)<v2>:1026


gfsh>describe member --name=SpringBootApacheGeodeCacheServerApplication
Name        : SpringBootApacheGeodeCacheServerApplication
Id          : 10.0.0.121(SpringBootApacheGeodeCacheServerApplication:30279)<v2>:1026
Host        : 10.0.0.121
Regions     :
PID         : 30279
Groups      :
Used Heap   : 165M
Max Heap    : 3641M
Working Dir : /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build
Log file    : /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build
Locators    : localhost[11235]

Cache Server Information
Server Bind              :
Server Port              : 40404
Running                  : true
Client Connections       : 0
```

In both scenarios, the Spring Boot configured and bootstrapped
[vmware-gemfire-name] server, the Gfsh Locator and Gfsh server formed a
cluster.

While you can use either approach and Spring does not care, it is far
more convenient to use Spring Boot and your IDE to form a small cluster
while developing. Spring profiles make it far simpler and much faster to
configure and start a small cluster.

Also, this approach enables rapidly prototyping, testing, and debugging
your entire end-to-end application and system architecture right from
the comfort and familiarity of your IDE. No additional tooling (such as
Gfsh) or knowledge is required to get started quickly and easily. Just
build and run.

<p class="note"><strong>Note:</strong>
Be careful to vary your port numbers for the
embedded services, like the <code>CacheServer</code>, Locators, and the
Manager, especially if you start multiple instances on the same machine.
Otherwise, you are likely to run into a
<code>java.net.BindException</code> caused by port conflicts.
</p>

See [Running a [vmware-gemfire-name] cluster with Spring Boot from your IDE](https://docs-staging.vmware.com/en/Spring-Boot-for-GemFire/1.7/sbfg/GUID-appendix.html#geode-cluster-configuration-bootstrapping)
 for more details.

### Building Locator Applications

In addition to `ClientCache`, `CacheServer`, and peer `Cache`
applications, [spring-data-gemfire-name], and by extension [spring-boot-gemfire-name], now supports Spring Boot
[vmware-gemfire-name] Locator applications.

A [vmware-gemfire-name] Locator is a location-based service or, more
typically, a standalone process that lets clients locate a cluster of
[vmware-gemfire-name] servers to manage data. Many cache clients can
connect to the same cluster to share data. Running multiple clients is
common in a Microservices architecture where you need to scale-up the
number of application instances to satisfy the demand.

A [vmware-gemfire-name] Locator is also used by joining members of an
existing cluster to scale-out and increase capacity of the logically
pooled system resources (memory, CPU, network and disk). A Locator
maintains metadata that is sent to the clients to enable such
capabilities as single-hop data access to route data access operations
to the data node in the cluster maintaining the data of interests. A
Locator also maintains load information for servers in the cluster,
which enables the load to be uniformly distributed across the cluster
while also providing fail-over services to a redundant member if the
primary fails. A Locator provides many more benefits, and we encourage
you to read the
[documentation](https://geode.apache.org/docs/guide/115/configuring/running/running_the_locator.html)
for more details.

As shown earlier, you can embed a Locator service within either a Spring
Boot peer `Cache` or a `CacheServer` application by using the [spring-data-gemfire-name]
`@EnableLocator` annotation:

Example 7. Embedded Locator Service


``` highlight
@SpringBootApplication
@CacheServerApplication
@EnableLocator
class SpringBootCacheServerWithEmbeddedLocatorApplication {
    // ...
}
```

However, it is more common to start standalone Locator JVM processes.
This is useful when you want to increase the resiliency of your cluster
in the face of network and process failures, which are bound to happen.
If a Locator JVM process crashes or gets severed from the cluster due to
a network failure or partition, having multiple Locators provides a
higher degree of availability (HA) through redundancy.

Even if all Locators in the cluster go down, the cluster still remains
intact. You cannot add more peer members (that is, scale-up the number
of data nodes in the cluster) or connect any more clients, but the
cluster is fine. If all the locators in the cluster go down, it is safe
to restart them only after a thorough diagnosis.

<p class="note"><strong>Note:</strong>
Once a client receives metadata about the cluster of
servers, all data-access operations are sent directly to servers in the
cluster, not a Locator. Therefore, existing, connected clients remain
connected and operable.
</p>

To configure and bootstrap Spring Boot [vmware-gemfire-name] Locator
applications as standalone JVM processes, use the following
configuration:

Example 8. Standalone Locator Process


``` highlight
@SpringBootApplication
@LocatorApplication
class SpringBootApacheGeodeLocatorApplication {
    // ...
}
```

Instead of using the `@EnableLocator` annotation, you now use the
`@LocatorApplication` annotation.

The `@LocatorApplication` annotation works in the same way as the
`@PeerCacheApplication` and `@CacheServerApplication` annotations,
bootstrapping a [vmware-gemfire-name] process and overriding the
default `ClientCache` instance provided by [spring-boot-gemfire-name].

<p class="note"><strong>Note:</strong>
If your <code>@SpringBootApplication</code> class is
annotated with <code>@LocatorApplication</code>, it must be a
<code>Locator</code> and not a <code>ClientCache</code>,
<code>CacheServer</code>, or peer <code>Cache</code> application. If you
need the application to function as a peer <code>Cache</code>, perhaps
with embedded <code>CacheServer</code> components and an embedded
Locator, you need to follow the approach shown earlier: using the
<code>@EnableLocator</code> annotation with either the
<code>@PeerCacheApplication</code> or
<code>@CacheServerApplication</code> annotation.
</p>

With our Spring Boot [vmware-gemfire-name] Locator application, we can
connect both Spring Boot configured and bootstrapped peer members (peer
`Cache`, `CacheServer` and `Locator` applications) as well as Gfsh
started Locators and servers.

First, we need to start two Locators by using our Spring Boot
[vmware-gemfire-name] Locator application class:

Example 9. SpringBootApacheGeodeLocatorApplication class


``` highlight
@UseLocators
@SpringBootApplication
@LocatorApplication(name = "SpringBootApacheGeodeLocatorApplication")
public class SpringBootApacheGeodeLocatorApplication {

    public static void main(String[] args) {

        new SpringApplicationBuilder(SpringBootApacheGeodeLocatorApplication.class)
            .web(WebApplicationType.NONE)
            .build()
            .run(args);

        System.err.println("Press <enter> to exit!");

        new Scanner(System.in).nextLine();
    }

    @Configuration
    @EnableManager(start = true)
    @Profile("manager")
    @SuppressWarnings("unused")
    static class ManagerConfiguration { }

}
```

We also need to vary the configuration for each Locator application
instance.

[vmware-gemfire-name] requires each peer member in the cluster to be
uniquely named. We can set the name of the Locator by using the
`spring.data.gemfire.locator.name` [spring-data-gemfire-name] property set as a JVM System
Property in your IDE’s run configuration profile for the main
application class:
`-Dspring.data.gemfire.locator.name=SpringLocatorOne`. We name the
second Locator application instance `SpringLocatorTwo`.

Additionally, we must vary the port numbers that the Locators use to
listen for connections. By default, a [vmware-gemfire-name] Locator
listens on port `10334`. We can set the Locator port by using the
`spring.data.gemfire.locator.port` [spring-data-gemfire-name] property.

For our first Locator application instance (`SpringLocatorOne`), we also
enable the "manager" profile so that we can connect to the Locator by
using Gfsh.

Our IDE run configuration profile for our first Locator application
instance appears as:

`-server -ea -Dspring.profiles.active=manager -Dspring.data.gemfire.locator.name=SpringLocatorOne -Dlogback.log.level=INFO`

And our IDE run configuration profile for our second Locator application
instance appears as:

`-server -ea -Dspring.profiles.active= -Dspring.data.gemfire.locator.name=SpringLocatorTwo -Dspring.data.gemfire.locator.port=11235 -Dlogback.log.level=INFO`

You should see log output similar to the following when you start a
Locator application instance:

Example 10. Spring Boot [vmware-gemfire-name] Locator log output


``` highlight
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::  (v2.2.0.BUILD-SNAPSHOT)

2019-09-01 11:02:48,707  INFO .SpringBootApacheGeodeLocatorApplication:  55 - Starting SpringBootApacheGeodeLocatorApplication on jblum-mbpro-2.local with PID 30077 (/Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/out/production/classes started by jblum in /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build)
2019-09-01 11:02:48,711  INFO .SpringBootApacheGeodeLocatorApplication: 651 - No active profile set, falling back to default profiles: default
2019-09-01 11:02:49,374  INFO xt.annotation.ConfigurationClassEnhancer: 355 - @Bean method LocatorApplicationConfiguration.exclusiveLocatorApplicationBeanFactoryPostProcessor is non-static and returns an object assignable to Spring's BeanFactoryPostProcessor interface. This will result in a failure to process annotations such as @Autowired, @Resource and @PostConstruct within the method's declaring @Configuration class. Add the 'static' modifier to this method to avoid these container lifecycle issues; see @Bean javadoc for complete details.
2019-09-01 11:02:49,919  INFO ode.distributed.internal.InternalLocator: 530 - Starting peer location for Distribution Locator on 10.99.199.24[11235]
2019-09-01 11:02:49,925  INFO ode.distributed.internal.InternalLocator: 498 - Starting Distribution Locator on 10.99.199.24[11235]
2019-09-01 11:02:49,926  INFO distributed.internal.tcpserver.TcpServer: 242 - Locator was created at Sun Sep 01 11:02:49 PDT 2019
2019-09-01 11:02:49,927  INFO distributed.internal.tcpserver.TcpServer: 243 - Listening on port 11235 bound on address 0.0.0.0/0.0.0.0
2019-09-01 11:02:49,928  INFO ternal.membership.gms.locator.GMSLocator: 162 - [vmware-gemfire-short-name] peer location service starting.  Other locators: localhost[10334]  Locators preferred as coordinators: true  Network partition detection enabled: true  View persistence file: /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build/locator11235view.dat
2019-09-01 11:02:49,928  INFO ternal.membership.gms.locator.GMSLocator: 416 - Peer locator attempting to recover from localhost/127.0.0.1:10334
2019-09-01 11:02:49,963  INFO ternal.membership.gms.locator.GMSLocator: 422 - Peer locator recovered initial membership of View[10.99.199.24(SpringLocatorOne:30043:locator)<ec><v0>:41000|0] members: [10.99.199.24(SpringLocatorOne:30043:locator)<ec><v0>:41000]
2019-09-01 11:02:49,963  INFO ternal.membership.gms.locator.GMSLocator: 407 - Peer locator recovered state from LocatorAddress [socketInetAddress=localhost/127.0.0.1:10334, hostname=localhost, isIpString=false]
2019-09-01 11:02:49,965  INFO ode.distributed.internal.InternalLocator: 644 - Starting distributed system
2019-09-01 11:02:50,007  INFO he.geode.internal.logging.LoggingSession:  82 -
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
Build-Date: 2019-04-19 11:49:13 -0700
Build-Id: onichols 0
Build-Java-Version: 1.8.0_192
Build-Platform: Mac OS X 10.14.4 x86_64
Product-Name: [vmware-gemfire-name]
Product-Version: 1.9.0
Source-Date: 2019-04-19 11:11:31 -0700
Source-Repository: release/1.9.0
Source-Revision: c0a73d1cb84986d432003bd12e70175520e63597
Native version: native code unavailable
Running on: 10.99.199.24/10.99.199.24, 8 cpu(s), x86_64 Mac OS X 10.13.6
Communications version: 100
Process ID: 30077
User: jblum
Current dir: /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build
Home dir: /Users/jblum
Command Line Parameters:
  -ea
  -Dspring.profiles.active=
  -Dspring.data.gemfire.locator.name=SpringLocatorTwo
  -Dspring.data.gemfire.locator.port=11235
  -Dlogback.log.level=INFO
  -javaagent:/Applications/IntelliJ IDEA 19 CE.app/Contents/lib/idea_rt.jar=51961:/Applications/IntelliJ IDEA 19 CE.app/Contents/bin
  -Dfile.encoding=UTF-8
Class Path:
...
..
.
2019-09-01 11:02:54,112  INFO ode.distributed.internal.InternalLocator: 661 - Locator started on 10.99.199.24[11235]
2019-09-01 11:02:54,113  INFO ode.distributed.internal.InternalLocator: 769 - Starting server location for Distribution Locator on 10.99.199.24[11235]
2019-09-01 11:02:54,134  INFO nt.internal.locator.wan.LocatorDiscovery: 138 - Locator discovery task exchanged locator information 10.99.199.24[11235] with localhost[10334]: {-1=[10.99.199.24[10334]]}.
2019-09-01 11:02:54,242  INFO .SpringBootApacheGeodeLocatorApplication:  61 - Started SpringBootApacheGeodeLocatorApplication in 6.137470354 seconds (JVM running for 6.667)
Press <enter> to exit!
```

Next, start up the second Locator application instance (you should see
log output similar to the preceding list). Then connect to the cluster
of Locators by using Gfsh:

Example 11. Cluster of Locators


``` highlight
$ echo $GEMFIRE
/Users/jblum/pivdev/apache-geode-1.9.0

$ gfsh
    _________________________     __
   / _____/ ______/ ______/ /____/ /
  / /  __/ /___  /_____  / _____  /
 / /__/ / ____/  _____/ / /    / /
/______/_/      /______/_/    /_/    1.9.0

Monitor and Manage [vmware-gemfire-name]

gfsh>connect
Connecting to Locator at [host=localhost, port=10334] ..
Connecting to Manager at [host=10.99.199.24, port=1099] ..
Successfully connected to: [host=10.99.199.24, port=1099]

gfsh>list members
      Name       | Id
---------------- | ------------------------------------------------------------------------
SpringLocatorOne | 10.99.199.24(SpringLocatorOne:30043:locator)<ec><v0>:41000 [Coordinator]
SpringLocatorTwo | 10.99.199.24(SpringLocatorTwo:30077:locator)<ec><v1>:41001
```

By using our `SpringBootApacheGeodeCacheServerApplication` main class
from the previous section, we can configure and bootstrap an
[vmware-gemfire-name] `CacheServer` application with Spring Boot and
connect it to our cluster of Locators:

Example 12. SpringBootApacheGeodeCacheServerApplication class


``` highlight
@SpringBootApplication
@CacheServerApplication(name = "SpringBootApacheGeodeCacheServerApplication")
@SuppressWarnings("unused")
public class SpringBootApacheGeodeCacheServerApplication {

    public static void main(String[] args) {

        new SpringApplicationBuilder(SpringBootApacheGeodeCacheServerApplication.class)
            .web(WebApplicationType.NONE)
            .build()
            .run(args);
    }

    @Configuration
    @UseLocators
    @Profile("clustered")
    static class ClusteredConfiguration { }

    @Configuration
    @EnableLocator
    @EnableManager(start = true)
    @Profile("!clustered")
    static class LonerConfiguration { }

}
```

To do so, enable the "clustered" profile by using an IDE run profile
configuration similar to:

`-server -ea -Dspring.profiles.active=clustered -Dspring.data.gemfire.name=SpringServer -Dspring.data.gemfire.cache.server.port=41414 -Dlogback.log.level=INFO`

After the server starts up, you should see the new peer member in the
cluster:

Example 13. Cluster with Spring Boot configured and bootstrapped
[vmware-gemfire-name] `CacheServer`


``` highlight
gfsh>list members
      Name       | Id
---------------- | ------------------------------------------------------------------------
SpringLocatorOne | 10.99.199.24(SpringLocatorOne:30043:locator)<ec><v0>:41000 [Coordinator]
SpringLocatorTwo | 10.99.199.24(SpringLocatorTwo:30077:locator)<ec><v1>:41001
SpringServer     | 10.99.199.24(SpringServer:30216)<v2>:41002
```

Finally, we can even start additional Locators and servers connected to
this cluster by using Gfsh:

Example 14. Gfsh started Locators and Servers


``` highlight
gfsh>start locator --name=GfshLocator --port=12345 --log-level=config
Starting a Geode Locator in /Users/jblum/pivdev/lab/GfshLocator...
......
Locator in /Users/jblum/pivdev/lab/GfshLocator on 10.99.199.24[12345] as GfshLocator is currently online.
Process ID: 30259
Uptime: 5 seconds
GemFire Version: 1.9.0
Java Version: 1.8.0_192
Log File: /Users/jblum/pivdev/lab/GfshLocator/GfshLocator.log
JVM Arguments: -Dgemfire.default.locators=10.99.199.24[11235],10.99.199.24[10334] -Dgemfire.enable-cluster-configuration=true -Dgemfire.load-cluster-configuration-from-dir=false -Dgemfire.log-level=config -Dgemfire.launcher.registerSignalHandlers=true -Djava.awt.headless=true -Dsun.rmi.dgc.server.gcInterval=9223372036854775806
Class-Path: /Users/jblum/pivdev/apache-geode-1.9.0/lib/geode-core-1.9.0.jar:/Users/jblum/pivdev/apache-geode-1.9.0/lib/geode-dependencies.jar

gfsh>start server --name=GfshServer --server-port=45454 --log-level=config
Starting a Geode Server in /Users/jblum/pivdev/lab/GfshServer...
...
Server in /Users/jblum/pivdev/lab/GfshServer on 10.99.199.24[45454] as GfshServer is currently online.
Process ID: 30295
Uptime: 2 seconds
GemFire Version: 1.9.0
Java Version: 1.8.0_192
Log File: /Users/jblum/pivdev/lab/GfshServer/GfshServer.log
JVM Arguments: -Dgemfire.default.locators=10.99.199.24[11235],10.99.199.24[12345],10.99.199.24[10334] -Dgemfire.start-dev-rest-api=false -Dgemfire.use-cluster-configuration=true -Dgemfire.log-level=config -XX:OnOutOfMemoryError=kill -KILL %p -Dgemfire.launcher.registerSignalHandlers=true -Djava.awt.headless=true -Dsun.rmi.dgc.server.gcInterval=9223372036854775806
Class-Path: /Users/jblum/pivdev/apache-geode-1.9.0/lib/geode-core-1.9.0.jar:/Users/jblum/pivdev/apache-geode-1.9.0/lib/geode-dependencies.jar

gfsh>list members
      Name       | Id
---------------- | ------------------------------------------------------------------------
SpringLocatorOne | 10.99.199.24(SpringLocatorOne:30043:locator)<ec><v0>:41000 [Coordinator]
SpringLocatorTwo | 10.99.199.24(SpringLocatorTwo:30077:locator)<ec><v1>:41001
SpringServer     | 10.99.199.24(SpringServer:30216)<v2>:41002
GfshLocator      | 10.99.199.24(GfshLocator:30259:locator)<ec><v3>:41003
GfshServer       | 10.99.199.24(GfshServer:30295)<v4>:41004
```

You must be careful to vary the ports and name of your peer members
appropriately. Spring, and [spring-boot-gemfire-name]
in particular, make doing so easy.

### Building Manager Applications

As discussed in the previous sections, you can enable a Spring Boot
configured and bootstrapped [vmware-gemfire-name] peer member node in
the cluster to function as a Manager.

A [vmware-gemfire-name] Manager is a peer member node in the cluster
that runs the management service, letting the cluster be managed and
monitored with JMX-based tools, such as Gfsh, JConsole, or JVisualVM.
Any tool using the JMX API can connect to and manage an
[vmware-gemfire-name] cluster for whatever purpose.

Like Locators, the cluster may have more than one Manager for
redundancy. Only server-side, peer member nodes in the cluster may
function Managers. Therefore, a `ClientCache` application cannot be a
Manager.

To create a Manager, use the [spring-data-gemfire-name] `@EnableManager` annotation.

The three primary uses of the `@EnableManager` annotation to create a
Manager are:

1 - CacheServer Manager Application

``` highlight
@SpringBootApplication
@CacheServerApplication(name = "CacheServerManagerApplication")
@EnableManager(start = true)
class CacheServerManagerApplication {
    // ...
}
```

2 - Peer Cache Manager Application

``` highlight
@SpringBootApplication
@PeerCacheApplication(name = "PeerCacheManagerApplication")
@EnableManager(start = "true")
class PeerCacheManagerApplication {
    // ...
}
```

3 - Locator Manager Application

``` highlight
@SpringBootApplication
@LocatorApplication(name = "LocatorManagerApplication")
@EnableManager(start = true)
class LocatorManagerApplication {
    // ...
}
```

\#1 creates a peer `Cache` instance with a `CacheServer` component that
accepts client connections along with an embedded Manager that lets JMX
clients connect.

\#2 creates only a peer `Cache` instance along with an embedded Manager.
As a peer `Cache` with no `CacheServer` component, clients are not able
to connect to this node. It is merely a server managing data.

\#3 creates a Locator instance with an embedded Manager.

In all configuration arrangements, the Manager is configured to start
immediately.

See the Javadoc for the
[<code>@EnableManager</code> annotation](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableManager.html) for additional configuration options.

As of [vmware-gemfire-name] 1.11.0, you must include additional
[vmware-gemfire-name] dependencies on your Spring Boot application
classpath to make your application a proper [vmware-gemfire-name]
Manager in the cluster, particularly if you also enable the embedded
HTTP service in the Manager.

The required dependencies are:

Example 15. Additional Manager dependencies expressed in Gradle


``` highlight
runtime "org.apache.geode:geode-http-service"
runtime "org.apache.geode:geode-web"
runtime "org.springframework.boot:spring-boot-starter-jetty"
```

The embedded HTTP service (implemented with the Eclipse Jetty Servlet
Container), runs the Management (Admin) REST API, which is used by
[vmware-gemfire-name] tooling, such as Gfsh, to connect to an
[vmware-gemfire-name] cluster over HTTP. In addition, it also enables
the [vmware-gemfire-name]
[Pulse](https://geode.apache.org/docs/guide/115/tools_modules/pulse/pulse-overview.html)
Monitoring Tool (and Web application) to run.

Even if you do not start the embedded HTTP service, a Manager still
requires the `geode-http-service`, `geode-web` and
`spring-boot-starter-jetty` dependencies.

Optionally, you may also include the `geode-pulse` dependency, as
follows:

Example 16. Additional, optional Manager depdendencies expressed in
Gradle


``` highlight
runtime "org.apache.geode:geode-pulse"
```

The `geode-pulse` dependency is only required if you want the Manager to
automatically start the [vmware-gemfire-name]
[Pulse](https://geode.apache.org/docs/guide/115/tools_modules/pulse/pulse-overview.html)
Monitoring Tool. Pulse enables you to view the nodes of your
[vmware-gemfire-name] cluster and monitor them in realtime.
