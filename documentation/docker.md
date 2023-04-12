---
Title: Docker
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



The state of modern software application development is moving towards
[containerization](https://www.docker.com/resources/what-container).
Containers offer a controlled environment to predictably build (compile,
configure and package), run, and manage your applications in a reliable
and repeatable manner, regardless of context. In many situations, the
intrinsic benefit of using containers is obvious.





Understandably, [Docker’s](https://www.docker.com/) popularity took off
like wildfire, given its highly powerful and simplified model for
creating, using and managing containers to run packaged applications.





Docker’s ecosystem is also quite impressive, with the advent of
[Testcontainers](https://www.testcontainers.org) and Spring Boot’s now
https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#building-docker-images\[dedicated
support\] to create packaged Spring Boot applications in [Docker
images](https://docs.docker.com/get-started/overview/#docker-objects)
that are then later run in a Docker container.





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
<td class="content">See also
https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#containers-deployment[“Deploying
to Containers”] to learn more.</td>
</tr>
</tbody>
</table>





[vmware-gemfire-name] can also run in a controlled, containerized
environment. The goal of this chapter is to get you started running
[vmware-gemfire-name] in a container and interfacing to a containerized
[vmware-gemfire-name] cluster from your Spring Boot,
[vmware-gemfire-name] client applications.





This chapter does not cover how to run your Spring Boot,
[vmware-gemfire-name] client applications in a container, since that is
already covered by Spring Boot (again, see the Spring Boot documentation
for
https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#building-docker-images\[Docker
images\] and
https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#containers-deployment\[container
deployment\], along with Docker’s
[documentation](https://docs.docker.com/get-started/overview/)).
Instead, our focus is on how to run an [vmware-gemfire-name] cluster in
a container and connect to it from a Spring Boot, [vmware-gemfire-name]
client application, regardless of whether the application runs in a
container or not.





### Acquiring the [vmware-gemfire-name] Docker Image



To run an [vmware-gemfire-name] cluster inside a Docker container, you
must first acquire the Docker image. You can get the
[vmware-gemfire-name] Docker image from [Docker
Hub](https://hub.docker.com/r/apachegeode/geode/).





While [vmware-gemfire-name]'s https://docs.vmware.com/en/VMware-GemFire/index.html\[official
documentation\] is less than clear on how to use [vmware-gemfire-name]
in Docker, we find a bit of relief in the
{apache-geode-wiki}/How+to+use+[vmware-gemfire-short-name]+on+Docker\[Wiki\]. However, for a
complete and comprehensive write up, see the instructions in the
[README](https://github.com/markito/geode-docker#building-the-container-image)
from this [GitHub Repo](https://github.com/markito/geode-docker).





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
<td class="content">You must have <a
href="https://docs.docker.com/get-docker">Docker</a> installed on your
computer to complete the following steps.</td>
</tr>
</tbody>
</table>





Effectively, the high-level steps are as follows:





1\) Acquire the [vmware-gemfire-name] Docker image from Docker Hub by
using the `docker pull` command (shown with typical output) from the
command-line:







Example 1. Download/Install the [vmware-gemfire-name] Docker Image









``` highlight
$ docker pull apachegeode/geode
Using default tag: latest
latest: Pulling from apachegeode/geode
Digest: sha256:6a6218f22a2895bb706175727c7d76f654f9162acac22b2d950d09a2649f9cf4
Status: Image is up to date for apachegeode/geode:latest
docker.io/apachegeode/geode:latest
```











Instead of pulling from the `nightly` tag as suggested, the Spring team
highly recommends that you pull from the `latest` tag, which pulls a
stable, production-ready [vmware-gemfire-name] Docker image based on
the latest [vmware-gemfire-name] GA version.





2\) Verify that the [vmware-gemfire-name] Docker image was downloaded
and installed successfully:











``` highlight
$ docker image ls
REPOSITORY                       TAG                          IMAGE ID            CREATED             SIZE
apachegeode/geode                latest                       a2e210950712        2 months ago        224MB
cloudfoundry/run                 base-cnb                     3a7d172559c2        8 weeks ago         71.2MB
open-liberty                     19.0.0.9-webProfile8         dece75feff1a        3 months ago        364MB
tomee                            11-jre-8.0.0-M3-webprofile   0d03e4d395e6        3 months ago        678MB
...
```











Now you are ready to run [vmware-gemfire-name] in a Docker container.







### Running [vmware-gemfire-name] in a Docker Container



Now that you have acquired the [vmware-gemfire-name] Docker image, you
can run [vmware-gemfire-name] in a Docker container. Use the following
`docker run` command to start [vmware-gemfire-name] in a Docker
container:







Example 2. Start the [vmware-gemfire-name] Docker Container









``` highlight
$ docker run -it -p 10334:10334 -p 40404:40404 -p 1099:1099 -p 7070:7070 -p 7575:7575 apachegeode/geode
    _________________________     __
   / _____/ ______/ ______/ /____/ /
  / /  __/ /___  /_____  / _____  /
 / /__/ / ____/  _____/ / /    / /
/______/_/      /______/_/    /_/    1.12.0

Monitor and Manage [vmware-gemfire-name]
gfsh>
```











Since the [vmware-gemfire-name] Docker container was started in
interactive mode, you must open a separate command-line shell to verify
that the [vmware-gemfire-name] Docker container is in fact running:







Example 3. Verify the [vmware-gemfire-name] Docker Container is Running









``` highlight
$ docker container ls
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS                                                                                                                                  NAMES
3b30b9ffc5dc        apachegeode/geode   "gfsh"              44 seconds ago      Up 43 seconds       0.0.0.0:1099->1099/tcp, 0.0.0.0:7070->7070/tcp, 0.0.0.0:7575->7575/tcp, 0.0.0.0:10334->10334/tcp, 0.0.0.0:40404->40404/tcp, 8080/tcp   awesome_khorana
```











You know that the [vmware-gemfire-name] Docker container is running
since we ended up at a Gfsh command prompt in the interactive shell.





We also mapped ports between the Docker container and the host system,
exposing well-known ports used by [vmware-gemfire-name] server-side
cluster processes, such as Locators and CacheServers:



<table class="tableblock frame-all grid-all" style="width: 30%;">
<caption>Table 1. [vmware-gemfire-name] Ports</caption>
<colgroup>
<col style="width: 66%" />
<col style="width: 33%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Process</th>
<th class="tableblock halign-left valign-top">Port</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>HTTP</p></td>
<td class="tableblock halign-left valign-top"><p>7070</p></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>Locator</p></td>
<td class="tableblock halign-left valign-top"><p>10334</p></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>Manager</p></td>
<td class="tableblock halign-left valign-top"><p>1099</p></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>Server</p></td>
<td class="tableblock halign-left valign-top"><p>40404</p></td>
</tr>
</tbody>
</table>

Table 1. [vmware-gemfire-name] Ports



It is unfortunate that the [vmware-gemfire-name] Docker image gives you
only a Gfsh command prompt, leaving you with the task of provisioning a
cluster. It would have been more useful to provide preconfigured Docker
images with different [vmware-gemfire-name] cluster configurations,
such as one Locator and one server or two Locators and four servers, and
so on. However, we can start the cluster ourselves.







### Start an [vmware-gemfire-name] Cluster in Docker



From inside the [vmware-gemfire-name] Docker container, we can start a
Locator and a server:







Example 4. Start [vmware-gemfire-name] Locator & Server









``` highlight
gfsh>start locator --name=LocatorOne --log-level=config --hostname-for-clients=localhost
Starting a [vmware-gemfire-short-name] Locator in /LocatorOne...
.........
Locator in /LocatorOne on 3b30b9ffc5dc[10334] as LocatorOne is currently online.
Process ID: 167
Uptime: 9 seconds
GemFire Version: 1.12.0
Java Version: 1.8.0_212
Log File: /LocatorOne/LocatorOne.log
JVM Arguments: -Dgemfire.enable-cluster-configuration=true -Dgemfire.load-cluster-configuration-from-dir=false -Dgemfire.log-level=config -Dgemfire.launcher.registerSignalHandlers=true -Djava.awt.headless=true -Dsun.rmi.dgc.server.gcInterval=9223372036854775806
Class-Path: /geode/lib/geode-core-1.12.0.jar:/geode/lib/geode-dependencies.jar

Successfully connected to: JMX Manager [host=3b30b9ffc5dc, port=1099]

Cluster configuration service is up and running.


gfsh>start server --name=ServerOne --log-level=config --hostname-for-clients=localhost
Starting a [vmware-gemfire-short-name] Server in /ServerOne...
.......
Server in /ServerOne on 3b30b9ffc5dc[40404] as ServerOne is currently online.
Process ID: 267
Uptime: 7 seconds
GemFire Version: 1.12.0
Java Version: 1.8.0_212
Log File: /ServerOne/ServerOne.log
JVM Arguments: -Dgemfire.default.locators=172.17.0.2[10334] -Dgemfire.start-dev-rest-api=false -Dgemfire.use-cluster-configuration=true -Dgemfire.log-level=config -Dgemfire.launcher.registerSignalHandlers=true -Djava.awt.headless=true -Dsun.rmi.dgc.server.gcInterval=9223372036854775806
Class-Path: /geode/lib/geode-core-1.12.0.jar:/geode/lib/geode-dependencies.jar


gfsh>list members
Member Count : 2

   Name    | Id
---------- | --------------------------------------------------------------
LocatorOne | 172.17.0.2(LocatorOne:167:locator)<ec><v0>:41000 [Coordinator]
ServerOne  | 172.17.0.2(ServerOne:267)<v1>:41001


gfsh>describe member --name=LocatorOne
Name        : LocatorOne
Id          : 172.17.0.2(LocatorOne:167:locator)<ec><v0>:41000
Host        : 3b30b9ffc5dc
Regions     :
PID         : 167
Groups      :
Used Heap   : 50M
Max Heap    : 443M
Working Dir : /LocatorOne
Log file    : /LocatorOne/LocatorOne.log
Locators    : 172.17.0.2[10334]


gfsh>describe member --name=ServerOne
Name        : ServerOne
Id          : 172.17.0.2(ServerOne:267)<v1>:41001
Host        : 3b30b9ffc5dc
Regions     :
PID         : 267
Groups      :
Used Heap   : 77M
Max Heap    : 443M
Working Dir : /ServerOne
Log file    : /ServerOne/ServerOne.log
Locators    : 172.17.0.2[10334]

Cache Server Information
Server Bind              :
Server Port              : 40404
Running                  : true

Client Connections : 0
```











We now have an [vmware-gemfire-name] cluster running with one Locator
and one server inside a Docker container. We deliberately started the
cluster with a minimal configuration. For example, we have no Regions in
which to store data:











``` highlight
gfsh>list regions
No Regions Found
```











However, that is OK. Once more, we want to show the full power of [spring-boot-gemfire-name]
and let the Spring Boot application drive the configuration of the
[vmware-gemfire-name] cluster that runs in the Docker container, as
required by the application.





Let’s have a quick look at our Spring Boot application.







### Spring Boot, [vmware-gemfire-name] Client Application Explained



The Spring Boot, [vmware-gemfire-name] `ClientCache` application we use
to connect to our [vmware-gemfire-name] cluster that runs in the Docker
container appears as follows:







Example 5. Spring Boot, [vmware-gemfire-name] Docker client application









``` highlight
@SpringBootApplication
@EnableClusterAware
@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
@UseMemberName("SpringBootGemFireDockerClientCacheApplication")
public class SpringBootGemFireDockerClientCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootGemFireDockerClientCacheApplication.class, args);
    }

    @Bean
    @SuppressWarnings("unused")
    ApplicationRunner runner(GemFireCache cache, CustomerRepository customerRepository) {

        return args -> {

            assertClientCacheAndConfigureMappingPdxSerializer(cache);
            assertThat(customerRepository.count()).isEqualTo(0);

            Customer jonDoe = Customer.newCustomer(1L, "Jon Doe");

            log("Saving Customer [%s]...%n", jonDoe);

            jonDoe = customerRepository.save(jonDoe);

            assertThat(jonDoe).isNotNull();
            assertThat(jonDoe.getId()).isEqualTo(1L);
            assertThat(jonDoe.getName()).isEqualTo("Jon Doe");
            assertThat(customerRepository.count()).isEqualTo(1);

            log("Querying for Customer [SELECT * FROM /Customers WHERE name LIKE '%s']...%n", "%Doe");

            Customer queriedJonDoe = customerRepository.findByNameLike("%Doe");

            assertThat(queriedJonDoe).isEqualTo(jonDoe);

            log("Customer was [%s]%n", queriedJonDoe);
        };
    }

    private void assertClientCacheAndConfigureMappingPdxSerializer(GemFireCache cache) {

        assertThat(cache).isNotNull();
        assertThat(cache.getName())
            .isEqualTo(SpringBootGemFireDockerClientCacheApplication.class.getSimpleName());
        assertThat(cache.getPdxSerializer()).isInstanceOf(MappingPdxSerializer.class);

        MappingPdxSerializer serializer = (MappingPdxSerializer) cache.getPdxSerializer();

        serializer.setIncludeTypeFilters(type -> Optional.ofNullable(type)
            .map(Class::getPackage)
            .map(Package::getName)
            .filter(packageName -> packageName.startsWith(this.getClass().getPackage().getName()))
            .isPresent());
    }

    private void log(String message, Object... args) {
        System.err.printf(message, args);
        System.err.flush();
    }
}
```











Our `Customer` application domain model object type is defined as:







Example 6. `Customer` class









``` highlight
@Region("Customers")
class Customer {

    @Id
    private Long id;

    private String name;

}
```











Also, we define a Spring Data CRUD Repository to persist and access
`Customers` stored in the `/Customers` Region:







Example 7. `CustomerRepository` interface









``` highlight
interface CustomerRepository extends CrudRepository<Customer, Long> {

    Customer findByNameLike(String name);

}
```











Our main class is annotated with `@SpringBootApplication`, making it be
a proper Spring Boot application.





We additionally annotate the main class with [spring-boot-gemfire-name]’s
`@EnableClusterAware` annotation to automatically detect the
[vmware-gemfire-name] cluster that runs in the Docker container and to
push cluster configuration metadata from the application to the cluster
as required by the application.





Specifically, the application requires that a Region called “Customers”,
as defined by the `@Region` mapping annotation on the `Customer`
application domain model class, exists on the servers in the cluster, to
store `Customer` data.





We use the [spring-data-gemfire-name] `@EnableEntityDefinedRegions` annotation to define the
matching client `PROXY` “Customers” Region.





Optionally, we have also annotated our main class with [spring-boot-gemfire-name]’s
`@UseMemberName` annotation to give the `ClientCache` a name, which we
assert in the
`assertClientCacheAndConfigureMappingPdxSerializer(:ClientCache)`
method.





The primary work performed by this application is done in the Spring
Boot `ApplicationRunner` bean definition. We create a `Customer`
instance (`Jon Doe`), save it to the “Customers” Region that is managed
by the server in the cluster, and then query for `Jon Doe` using OQL,
asserting that the result is equal to what we expect.





We log the output from the application’s operations to see the
application in action.







### Running the Spring Boot, [vmware-gemfire-name] client application



When you run the Spring Boot, [vmware-gemfire-name] client application,
you should see output similar to the following:







Example 8. Application log output









``` highlight
/Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/bin/java ...
    org.springframework.geode.docs.example.app.docker.SpringBootGemFireDockerClientCacheApplication

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.3.0.RELEASE)

Saving Customer [Customer(name=Jon Doe)]...
Querying for Customer [SELECT * FROM /Customers WHERE name LIKE '%Doe']...
Customer was [Customer(name=Jon Doe)]

Process finished with exit code 0
```











When we review the configuration of the cluster, we see that the
`/Customers` Region was created when the application ran:







Example 9. /Customers Region Configuration









``` highlight
gfsh>list regions
List of regions
---------------
Customers


gfsh>describe region --name=/Customers
Name            : Customers
Data Policy     : partition
Hosting Members : ServerOne

Non-Default Attributes Shared By Hosting Members

 Type  |    Name     | Value
------ | ----------- | ---------
Region | size        | 1
       | data-policy | PARTITION
```











Our `/Customers` Region contains a value (`Jon Doe`), and we can verify
this by running the following OQL Query with Gfsh:







Example 10. Query the `/Customers` Region









``` highlight
gfsh>query --query="SELECT customer.name FROM /Customers customer"
Result : true
Limit  : 100
Rows   : 1

Result
-------
Jon Doe
```











Our application ran successfully.







### Conclusion



In this chapter, we saw how to connect a Spring Boot,
[vmware-gemfire-name] `ClientCache` application to an
[vmware-gemfire-name] cluster that runs in a Docker container.





Later, we provide more information on how to scale up, or rather scale
out, our [vmware-gemfire-name] cluster that runs in Docker.
Additionally, we provide details on how you can use
[vmware-gemfire-name]'s Docker image with Testcontainers when you write
integration tests, which formally became part of the Spring Test for
[vmware-gemfire-name] (STDG) project.











<div id="footer">

<div id="footer-text">

Last updated 2022-10-10 12:15:33 -0700




