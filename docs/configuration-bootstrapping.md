# Running a VMware GemFire cluster with Spring Boot from your IDE

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

As described in
[geode-clientcache-applications](#geode-clientcache-applications),
you can configure and run a small VMware GemFire cluster from
inside your IDE using Spring Boot. This is extremely helpful during
development because it enables you to manually run, test, and debug your
applications quickly and easily.

Spring Boot for VMware GemFire includes such a class:

Example 11. Spring Boot application class used to configure and
bootstrap a VMware GemFire server


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

This class is a proper Spring Boot application that you can use to
configure and bootstrap multiple VMware GemFire servers and join
them together to form a small cluster. You only need to modify the
runtime configuration of this class to startup multiple servers.

Initially, you will need to start a single (primary) server with an
embedded Locator and Manager.

The Locator enables members in the cluster to locate one another and
lets new members join the cluster as a peer. The Locator also lets
clients connect to the servers in the cluster. When the cache client’s
connection pool is configured to use Locators, the pool of connections
can intelligently route data requests directly to the server hosting the
data (a.k.a. single-hop access), especially when the data is
partitioned/sharded across multiple servers in the cluster.
Locator-based connection pools include support for load balancing
connections and handling automatic fail-over in the event of failed
connections, among other things.

The Manager lets you connect to this server using Gfsh
(VMware GemFire's
[command-line shell tool](https://geode.apache.org/docs/guide/115/tools_modules/gfsh/chapter_overview.html)).

To start your primary server, create a run configuration in your IDE for
the `SpringBootApacheGeodeCacheServerApplication` class using the
following, recommended JRE command-line options:

Example 12. Server 1 run profile configuration


``` highlight
-server -ea -Dspring.profiles.active=
```

Run the class. You should see output similar to the following:

Example 13. Server 1 output on startup


``` highlight
/Library/Java/JavaVirtualMachines/jdk1.8.0_152.jdk/Contents/Home/bin/java -server -ea -Dspring.profiles.active= "-javaagent:/Applications/IntelliJ IDEA 17 CE.app/Contents/lib/idea_rt.jar=62866:
...
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See https://www.slf4j.org/codes.html#StaticLoggerBinder for further details.

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.0.3.RELEASE)

[info 2018/06/24 21:42:28.183 PDT <main> tid=0x1] Starting SpringBootApacheGeodeCacheServerApplication on jblum-mbpro-2.local with PID 41795 (/Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build/classes/main started by jblum in /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build)

[info 2018/06/24 21:42:28.186 PDT <main> tid=0x1] No active profile set, falling back to default profiles: default

[info 2018/06/24 21:42:28.278 PDT <main> tid=0x1] Refreshing org.springframework.context.annotation.AnnotationConfigApplicationContext@6fa51cd4: startup date [Sun Jun 24 21:42:28 PDT 2018]; root of context hierarchy

[warn 2018/06/24 21:42:28.962 PDT <main> tid=0x1] @Bean method PdxConfiguration.pdxDiskStoreAwareBeanFactoryPostProcessor is non-static and returns an object assignable to Spring's BeanFactoryPostProcessor interface. This will result in a failure to process annotations such as @Autowired, @Resource and @PostConstruct within the method's declaring @Configuration class. Add the 'static' modifier to this method to avoid these container lifecycle issues; see @Bean javadoc for complete details.

[info 2018/06/24 21:42:30.036 PDT <main> tid=0x1]
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
Build-Date: 2017-09-16 07:20:46 -0700
Build-Id: abaker 0
Build-Java-Version: 1.8.0_121
Build-Platform: Mac OS X 10.12.3 x86_64
Product-Name: Apache Geode
Product-Version: 1.2.1
Source-Date: 2017-09-08 11:57:38 -0700
Source-Repository: release/1.2.1
Source-Revision: 0b881b515eb1dcea974f0f5c1b40da03d42af9cf
Native version: native code unavailable
Running on: /10.0.0.121, 8 cpu(s), x86_64 Mac OS X 10.10.5
Communications version: 65
Process ID: 41795
User: jblum
Current dir: /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build
Home dir: /Users/jblum
Command Line Parameters:
  -ea
  -Dspring.profiles.active=
  -javaagent:/Applications/IntelliJ IDEA 17 CE.app/Contents/lib/idea_rt.jar=62866:/Applications/IntelliJ IDEA 17 CE.app/Contents/bin
  -Dfile.encoding=UTF-8
Class Path:
  /Library/Java/JavaVirtualMachines/jdk1.8.0_152.jdk/Contents/Home/jre/lib/charsets.jar
  ...
Library Path:
  /Users/jblum/Library/Java/Extensions
  /Library/Java/Extensions
  /Network/Library/Java/Extensions
  /System/Library/Java/Extensions
  /usr/lib/java
  .
System Properties:
    PID = 41795
  ...
[info 2018/06/24 21:42:30.045 PDT <main> tid=0x1] Startup Configuration:
 ### GemFire Properties defined with api ###
disable-auto-reconnect=true
jmx-manager=true
jmx-manager-port=1099
jmx-manager-start=true
jmx-manager-update-rate=2000
log-level=config
mcast-port=0
name=SpringBootApacheGeodeCacheServerApplication
start-locator=localhost[10334]
use-cluster-configuration=false
### GemFire Properties using default values ###
ack-severe-alert-threshold=0
...

[info 2018/06/24 21:42:30.090 PDT <main> tid=0x1] Starting peer location for Distribution Locator on localhost/127.0.0.1

[info 2018/06/24 21:42:30.093 PDT <main> tid=0x1] Starting Distribution Locator on localhost/127.0.0.1

[info 2018/06/24 21:42:30.094 PDT <main> tid=0x1] Locator was created at Sun Jun 24 21:42:30 PDT 2018

[info 2018/06/24 21:42:30.094 PDT <main> tid=0x1] Listening on port 10334 bound on address localhost/127.0.0.1

...

[info 2018/06/24 21:42:30.685 PDT <main> tid=0x1] Initializing region _monitoringRegion_10.0.0.121<v0>1024

[info 2018/06/24 21:42:30.688 PDT <main> tid=0x1] Initialization of region _monitoringRegion_10.0.0.121<v0>1024 completed

...

[info 2018/06/24 21:42:31.570 PDT <main> tid=0x1] CacheServer Configuration:   port=40404 max-connections=800 max-threads=0 notify-by-subscription=true socket-buffer-size=32768 maximum-time-between-pings=60000 maximum-message-count=230000 message-time-to-live=180 eviction-policy=none capacity=1 overflow directory=. groups=[] loadProbe=ConnectionCountProbe loadPollInterval=5000 tcpNoDelay=true

[info 2018/06/24 21:42:31.588 PDT <main> tid=0x1] Started SpringBootApacheGeodeCacheServerApplication in 3.77 seconds (JVM running for 5.429)
```

You can now connect to this server by using Gfsh:

Example 14. Connect with Gfsh


``` highlight
$ echo $GEMFIRE
/Users/jblum/pivdev/apache-geode-1.2.1
jblum-mbpro-2:lab jblum$
jblum-mbpro-2:lab jblum$ gfsh
    _________________________     __
   / _____/ ______/ ______/ /____/ /
  / /  __/ /___  /_____  / _____  /
 / /__/ / ____/  _____/ / /    / /
/______/_/      /______/_/    /_/    1.2.1

Monitor and Manage Apache Geode

gfsh>connect
Connecting to Locator at [host=localhost, port=10334] ..
Connecting to Manager at [host=10.0.0.121, port=1099] ..
Successfully connected to: [host=10.0.0.121, port=1099]


gfsh>list members
                   Name                     | Id
------------------------------------------- | --------------------------------------------------------------------------
SpringBootApacheGeodeCacheServerApplication | 10.0.0.121(SpringBootApacheGeodeCacheServerApplication:41795)<ec><v0>:1024


gfsh>describe member --name=SpringBootApacheGeodeCacheServerApplication
Name        : SpringBootApacheGeodeCacheServerApplication
Id          : 10.0.0.121(SpringBootApacheGeodeCacheServerApplication:41795)<ec><v0>:1024
Host        : 10.0.0.121
Regions     :
PID         : 41795
Groups      :
Used Heap   : 184M
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

Now you can run additional servers to scale-out your cluster.

To do so, you must vary the name of the members you add to your cluster
as peers. VMware GemFire requires members in a cluster to be named
and for the names of each member in the cluster to be unique.

Additionally, since we are running multiple instances of our
`SpringBootApacheGeodeCacheServerApplication` class, which also embeds a
`CacheServer` component enabling cache clients to connect. Therefore,
you must vary the ports used by the embedded services.

Fortunately, you do not need to run another embedded Locator or Manager
(you need only one of each in this case). Therefore, you can switch
profiles from non-clustered to using the Spring "clustered" profile,
which includes different configuration (the `ClusterConfiguration`
class) to connect another server as a peer member in the cluster, which
currently has only one member, as shown in Gfsh with the `list members`
command (shown earlier).

To add another server, set the member name and `CacheServer` port to
different values with the following run configuration:

Example 15. Run profile configuration for server 2


``` highlight
-server -ea -Dspring.profiles.active=clustered -Dspring.data.gemfire.name=ServerTwo -Dspring.data.gemfire.cache.server.port=41414
```

Notice that we explicitly activated the "clustered" Spring profile,
which enables the configuration provided in the nested
`ClusteredConfiguration` class while disabling the configuration
provided in the `LonerConfiguration` class.

The `ClusteredConfiguration` class is also annotated with
`@UseLocators`, which sets the VMware GemFire `locators` property
to "localhost[10334]". By default, it assumes that the Locator runs on
localhost, listening on the default Locator port of 10334. You can
adjust your `locators` connection endpoint if your Locators run
elsewhere in your network by using the `locators` attribute of the
`@UseLocators` annotation.

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
<td class="content">In production environments, it is common to run
multiple Locators in separate processes. Running multiple Locators
provides redundancy in case a Locator fails. If all Locators in your
cluster fail, then your cluster will continue to run, but no other
members will be able to join the cluster, which is important when
scaling out the cluster. Clients also will not be able to connect.
Restart the Locators if this happens.</td>
</tr>
</tbody>
</table>

Also, we set the `spring.data.gemfire.name` property to `ServerTwo`,
adjusting the name of our member when it joins the cluster as a peer.

Finally, we set the `spring.data.gemfire.cache.server.port` property to
`41414` to vary the `CacheServer` port used by `ServerTwo`. The default
`CacheServer` port is `40404`. If we had not set this property before
starting `ServerTwo`, we would have encounter a
`java.net.BindException`.

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
<td class="content">Both <code>spring.data.gemfire.name</code> and
<code>spring.data.gemfire.cache.server.port</code> are well-known
properties used by SDG to dynamically configure VMware GemFire with
a Spring Boot <code>application.properties</code> file or by using Java
System properties. You can find these properties in the annotation
Javadoc in SDG’s annotation-based configuration model. For example, see
the Javadoc for the
https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#port--[<code>spring.data.gemfire.cache.server.port</code>
property]. Most SDG annotations include corresponding properties that
can be defined in Spring Boot <code>application.properties</code>, which
is explained in detail in the
https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config-properties/#bootstrap-annotation-config-properties[documentation].</td>
</tr>
</tbody>
</table>

After starting our second server, `ServerTwo`, we should see output
similar to the following at the command-line and in Gfsh when we again
`list members` and `describe member`:

Example 16. Gfsh output after starting server 2


``` highlight
gfsh>list members
                   Name                     | Id
------------------------------------------- | --------------------------------------------------------------------------
SpringBootApacheGeodeCacheServerApplication | 10.0.0.121(SpringBootApacheGeodeCacheServerApplication:41795)<ec><v0>:1024
ServerTwo                                   | 10.0.0.121(ServerTwo:41933)<v1>:1025


gfsh>describe member --name=ServerTwo
Name        : ServerTwo
Id          : 10.0.0.121(ServerTwo:41933)<v1>:1025
Host        : 10.0.0.121
Regions     :
PID         : 41933
Groups      :
Used Heap   : 165M
Max Heap    : 3641M
Working Dir : /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build
Log file    : /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build
Locators    : localhost[10334]

Cache Server Information
Server Bind              :
Server Port              : 41414
Running                  : true
Client Connections       : 0
```

When we list the members of the cluster, we see `ServerTwo`, and when we
`describe` `ServerTwo`, we see that its `CacheServer` port is
appropriately set to `41414`.

We can add one more server, `ServerThree`, by using the following run
configuration:

Example 17. Add server three to our cluster


``` highlight
-server -ea -Dspring.profiles.active=clustered -Dspring.data.gemfire.name=ServerThree -Dspring.data.gemfire.cache.server.port=42424
```

We again see similar output at the command-line and in Gfsh:

Example 18. Gfsh output after starting server 3


``` highlight
gfsh>list members
                   Name                     | Id
------------------------------------------- | --------------------------------------------------------------------------
SpringBootApacheGeodeCacheServerApplication | 10.0.0.121(SpringBootApacheGeodeCacheServerApplication:41795)<ec><v0>:1024
ServerTwo                                   | 10.0.0.121(ServerTwo:41933)<v1>:1025
ServerThree                                 | 10.0.0.121(ServerThree:41965)<v2>:1026


gfsh>describe member --name=ServerThree
Name        : ServerThree
Id          : 10.0.0.121(ServerThree:41965)<v2>:1026
Host        : 10.0.0.121
Regions     :
PID         : 41965
Groups      :
Used Heap   : 180M
Max Heap    : 3641M
Working Dir : /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build
Log file    : /Users/jblum/pivdev/spring-boot-data-geode/spring-geode-docs/build
Locators    : localhost[10334]

Cache Server Information
Server Bind              :
Server Port              : 42424
Running                  : true
Client Connections       : 0
```

Congratulations. You have just started a small VMware GemFire
cluster with 3 members by using Spring Boot from inside your IDE.

Now you can build and run a Spring Boot, VMware GemFire
`ClientCache` application that connects to this cluster. To do so,
include and use Spring Boot for VMware GemFire.

