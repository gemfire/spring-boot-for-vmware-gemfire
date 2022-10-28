---
Title: Pivotal CloudFoundry
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

<p class="note"><strong>Note:</strong>
As of the VMware, Inc. acquisition of Pivotal
Software, Inc., Pivotal CloudFoundry (PCF) is now known as VMware Tanzu
Application Service (TAS) for VMs. Also, Pivotal Cloud Cache (PCC) has
been rebranded as VMware Tanzu GemFire for VMS. This documentation will
eventually be updated to reflect the rebranding.
</p>

In most cases, when you deploy (that is, `cf push`) your Spring Boot
applications to Pivotal CloudFoundry (PCF), you bind your application to
one or more instances of the Pivotal Cloud Cache (PCC) service.

In a nutshell, https://pivotal.io/pivotal-cloud-cache\[Pivotal Cloud Cache\] (PCC)
is a managed version of
[VMware GemFire](https://pivotal.io/pivotal-gemfire) that runs in
[Pivotal CloudFoundry](https://pivotal.io/platform\) (PCF). When
running in or across cloud environments (such as AWS, Azure, GCP, or
PWS), PCC with PCF offers several advantages over trying to run and
manage your own standalone VMware GemFire clusters. It handles
many of the infrastructure-related, operational concerns so that you
need not do so.

### Running a Spring Boot application as a specific user

By default, Spring Boot applications run as a `cluster_operator`
role-based user in Pivotal CloudFoundry when the application is bound to
a Pivotal Cloud Cache service instance.

A `cluster_operator` has full system privileges (that is, authorization)
to do whatever that user wishes to involving the PCC service instance. A
`cluster_operator` has read and write access to all the data, can modify
the schema (for example, create and destroy Regions, add and remove
Indexes, change eviction or expiration policies, and so on), start and
stop servers in the PCC cluster, or even modify permissions.


About cluster_operator as the default user

One of the reasons why Spring Boot applications default to running as a
`cluster_operator` is to allow configuration metadata to be sent from
the client to the server. Enabling configuration metadata to be sent
from the client to the server is a useful development-time feature and
is as simple as annotating your main `@SpringBootApplication` class with
the `@EnableClusterConfiguration` annotation:

Example 1. Using `@EnableClusterConfiguration`


``` highlight
@SpringBootApplication
@EnableClusterConfiguration(useHttp = true)
class SpringBootApacheGeodeClientCacheApplication {  }
```

With `@EnableClusterConfiguration`, Region and OQL Index configuration
metadata that is defined on the client can be sent to servers in the PCC
cluster. VMware GemFire requires matching Regions by name on
both the client and the servers in order for clients to send and receive
data to and from the cluster.

For example, when you declare the Region where an application entity is
persisted by using the `@Region` mapping annotation and declare the
`@EnableEntityDefinedRegions` annotation on the main
`@SpringBootApplication` class in conjunction with the
`@EnableClusterConfiguration` annotation, not only does SBDG create the
required client Region, but it also sends the configuration metadata for
this Region to the servers in the cluster to create the matching,
required server Region, where the data for your application entity is
managed.


However...

> 
>
> With great power comes great responsibility. - Uncle Ben
>
> 

Not all Spring Boot applications using PCC need to change the schema or
even modify data. Rather, certain applications may need only read
access. Therefore, it is ideal to be able to configure your Spring Boot
applications to run with a different user at runtime other than the
auto-configured `cluster_operator`, by default.

A prerequisite for running a Spring Boot application in PCC with a
specific user is to create a user with restricted permissions by using
Pivotal CloudFoundry AppsManager while provisioning the PCC service
instance to which the Spring Boot application is bound.

Configuration metadata for the PCC service instance might appear as
follows:

Example 2. Pivotal Cloud Cache configuration metadata


``` highlight
{
  "p-cloudcache":[{
    "credentials": {
      "distributed_system_id": "0",
      "locators": [ "localhost[55221]" ],
      "urls": {
        "gfsh": "https://cloudcache-12345.services.cf.pws.com/gemfire/v1",
        "pulse": "https://cloudcache-12345.services.cf.pws.com/pulse"
      },
      "users": [{
        "password": "*****",
        "roles": [ "cluster_operator" ],
        "username": "cluster_operator_user"
      }, {
        "password": "*****",
        "roles": [ "developer" ],
        "username": "developer_user"
      }, {
        "password": "*****",
        "roles": [ "read-only-user" ],
        "username": "guest"
      }],
      "wan": {
        "sender_credentials": {
          "active": {
            "password": "*****",
            "username": "gateway-sender-user"
          }
        }
      }
    },
    "name": "jblum-pcc",
    "plan": "small",
    "tags": [ "gemfire", "cloudcache", "database", "pivotal" ]
  }]
}
```

In the PCC service instance configuration metadata shown in the
preceding example, we see a `guest` user with the `read-only-user` role.
If the `read-only-user` role is properly configured with read-only
permissions as the name implies, we could configure our Spring Boot
application to run as `guest` with read-only access:

Example 3. Configuring a Spring Boot application to run as a specific
user


``` highlight
# Spring Boot application.properties for PCF when using PCC

spring.data.gemfire.security.username=guest
```

The
<code>spring.data.gemfire.security.username</code> property corresponds
directly to the SDG <code>@EnableSecurity</code> annotation’s
<code>securityUsername</code> attribute. See the
[Javadoc](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSecurity.html#securityUsername--)
for more details.

The `spring.data.gemfire.security.username` property is the same
property used by Spring Data for VMware GemFire (SDG) to
configure the runtime user of your Spring Data application when you
connect to an externally managed VMware GemFire cluster.

In this case, SBDG uses the configured username to look up the
authentication credentials of the user to set the username and password
used by the Spring Boot `ClientCache` application when connecting to PCC
while running in PCF.

If the username is not valid, an `IllegalStateException` is thrown.

By using [Spring profiles](https://docs.spring.io/spring-boot/docs/current/reference/html/#boot-features-profiles), it would be a simple matter to configure the Spring Boot
application to run with a different user depending on environment.

See the VMware GemFire for TAS documentation on
[security](https://docs.vmware.com/en/VMware-Tanzu-GemFire-for-VMs/1.14/tgf-vms/GUID-content-security.html) for configuring
users with assigned roles and permissions.

#### Overriding Authentication Auto-configuration

It should be understood that auto-configuration for client
authentication is available only for managed environments, such as
Pivotal CloudFoundry. When running in externally managed environments,
you must explicitly set a username and password to authenticate, as
described in
[Non-Managed Auth for Clients](https://docs.spring.io/spring-boot/docs/current/reference/html/#boot-features-profiles).

To completely override the auto-configuration of client authentication,
you can set both a username and a password:

Example 4. Overriding Security Authentication Auto-configuration with
explicit username and password


``` highlight
# Spring Boot application.properties

spring.data.gemfire.security.username=MyUser
spring.data.gemfire.security.password=MyPassword
```

In this case, SBDG’s auto-configuration for authentication is
effectively disabled and security credentials are not extracted from the
environment.


### Targeting Specific Pivotal Cloud Cache Service Instances

It is possible to provision multiple instances of the Pivotal Cloud
Cache service in your Pivotal CloudFoundry environment. You can then
bind multiple PCC service instances to your Spring Boot application.

However, Spring Boot for VMware GemFire (SBDG) only
auto-configures one PCC service instance for your Spring Boot
application. This does not mean that it is not possible to use multiple
PCC service instances with your Spring Boot application, just that SBDG
only auto-configures one service instance for you.

You must select which PCC service instance your Spring Boot application
automatically auto-configures for you when you have multiple instances
and want to target a specific PCC service instance to use.

To do so, declare the following SBDG property in Spring Boot
`application.properties`:

Example 5. Spring Boot application.properties targeting a specific PCC
service instance by name


``` highlight
# Spring Boot application.properties

spring.boot.data.gemfire.cloud.cloudfoundry.service.cloudcache.name=pccServiceInstanceTwo
```

The
`spring.boot.data.gemfire.cloud.cloudfoundry.service.cloudcache.name`
property tells SBDG which PCC service instance to auto-configure.

If the PCC service instance identified by the property does not exist,
SBDG throws an `IllegalStateException` stating the PCC service instance
by name could not be found.

If you did not set the property and your Spring Boot application is
bound to multiple PCC service instances, SBDG auto-configures the first
PCC service instance it finds by name, alphabetically.

If you did not set the property and no PCC service instance is found,
SBDG logs a warning.

### Using Multiple Pivotal Cloud Cache Service Instances

If you want to use multiple PCC service instances with your Spring Boot
application, you need to configure multiple connection `Pools` connected
to each PCC service instance used by your Spring Boot application.

The configuration would be similar to the following:

Example 6. Multiple Pivotal Cloud Cache Service Instance Configuration


``` highlight
@Configuration
@EnablePools(pools = {
  @EnablePool(name = "PccOne"),
  @EnablePool(name = "PccTwo"),
  ...,
  @EnablePool(name = "PccN")
})
class PccConfiguration {
  // ...
}
```

You would then externalize the configuration for the individually
declared `Pools` in Spring Boot `application.properties`:

Example 7. Configuring Locator-based Pool connections


``` highlight
# Spring Boot `application.properties`

spring.data.gemfire.pool.pccone.locators=pccOneHost1[port1], pccOneHost2[port2], ..., pccOneHostN[portN]

spring.data.gemfire.pool.pcctwo.locators=pccTwoHost1[port1], pccTwoHost2[port2], ..., pccTwoHostN[portN]
```

<p class="note"><strong>Note:</strong>
Though less common, you can also configure the
<code>Pool</code> of connections to target specific servers in the
cluster by setting the
<code>spring.data.gemfire.pool.&lt;named-pool&gt;.severs</code>
property.
</p>

Keep in mind that properties in Spring Boot
<code>application.properties</code> can refer to other properties:
<code>property=${otherProperty}</code>. This lets you further
externalize properties by using Java System properties or environment
variables.

A client Region is then assigned the Pool of connections that are used
to send data to and from the specific PCC service instance (cluster):

Example 8. Assigning a Pool to a client Region


``` highlight
@Configuration
class GeodeConfiguration {

  @Bean("Example")
  ClientRegionFactoryBean exampleRegion(GemFireCache gemfireCache,
      @Qualifier("PccTwo") Pool poolForPccTwo) {

    ClientRegionFactoryBean exampleRegion = new ClientRegionFactoryBean();

    exampleRegion.setCache(gemfireCache);
    exampleRegion.setPool(poolForPccTwo);
    exampleRegion.setShortcut(ClientRegionShortcut.PROXY);

    return exampleRegion;
  }
}
```

You can configure as many Pools and client Regions as your application
needs. Again, the `Pool` determines the Pivotal Cloud Cache service
instance and cluster in which the data for the client Region resides.

<p class="note"><strong>Note:</strong>
By default, SBDG configures all <code>Pools</code>
declared in a Spring Boot <code>ClientCache</code> application to
connect to and use a single PCC service instance. This may be a targeted
PCC service instance when you use the
<code>spring.boot.data.gemfire.cloud.cloudfoundry.service.cloudcache.name</code>
property as discussed <a
href="#cloudfoundry-cloudcache-multiinstance-using">earlier</a>.
</p>

### Hybrid Pivotal CloudFoundry and VMware GemFire Spring Boot Applications

Sometimes, it is desirable to deploy (that is, `cf push`) and run your
Spring Boot applications in Pivotal CloudFoundry but still connect your
Spring Boot applications to an externally managed, standalone
VMware GemFire cluster.

Spring Boot for VMware GemFire (SBDG) makes this a non-event and
honors its "*little to no code or configuration changes necessary*"
goal. Regardless of your runtime choice, it should just work!

To help guide you through this process, we cover the following topics:

1.  Install and Run PCFDev.

2.  Start an VMware GemFire cluster.

3.  Create a User-Provided Service (CUPS).

4.  Push and Bind a Spring Boot application.

5.  Run the Spring Boot application.

#### Running PCFDev

For this exercise, we use [PCF
Dev](https://docs.pivotal.io/pcf-dev/install-osx.html).

PCF Dev, much like PCF, is an elastic application runtime for deploying,
running, and managing your Spring Boot applications. However, it does so
in the confines of your local development environment — that is, your
workstation.

Additionally, PCF Dev provides several services, such as MySQL, Redis,
and RabbitMQ. You Spring Boot application can bind to and use these
services to accomplish its tasks.

However, PCF Dev lacks the Pivotal Cloud Cache service that is available
in PCF. This is actually ideal for this exercise since we are trying to
build and run Spring Boot applications in a PCF environment but connect
to an externally managed, standalone VMware GemFire cluster.

As a prerequisite, you need to follow the steps outlined in the
[tutorial](https://pivotal.io/platform/pcf-tutorials/getting-started-with-pivotal-cloud-foundry-dev/introduction)
to get PCF Dev set up and running on your workstation.

To run PCF Dev, execute the following `cf` CLI command, replacing the
path to the TGZ file with the file you acquired from the
[download](https://network.pivotal.io/products/pcfdev):

Example 9. Start PCF Dev


``` highlight
$ cf dev start -f ~/Downloads/Pivotal/CloudFoundry/Dev/pcfdev-v1.2.0-darwin.tgz
```

You should see output similar to the following:

Example 10. Running PCF Dev


``` highlight
Downloading Network Helper...
Progress: |====================>| 100.0%
Installing cfdevd network helper (requires administrator privileges)...
Password:
Setting up IP aliases for the BOSH Director & CF Router (requires administrator privileges)
Downloading Resources...
Progress: |====================>| 100.0%
Setting State...
WARNING: PCF Dev requires 8192 MB of RAM to run. This machine may not have enough free RAM.
Creating the VM...
Starting VPNKit...
Waiting for the VM...
Deploying the BOSH Director...

Deploying PAS...
  Done (14m34s)
Deploying Apps-Manager...
  Done (1m41s)

     ██████╗  ██████╗███████╗██████╗ ███████╗██╗   ██╗
     ██╔══██╗██╔════╝██╔════╝██╔══██╗██╔════╝██║   ██║
     ██████╔╝██║     █████╗  ██║  ██║█████╗  ██║   ██║
     ██╔═══╝ ██║     ██╔══╝  ██║  ██║██╔══╝  ╚██╗ ██╔╝
     ██║     ╚██████╗██║     ██████╔╝███████╗ ╚████╔╝
     ╚═╝      ╚═════╝╚═╝     ╚═════╝ ╚══════╝  ╚═══╝
                 is now running!

    To begin using PCF Dev, please run:
        cf login -a https://api.dev.cfdev.sh --skip-ssl-validation

    Admin user => Email: admin / Password: admin
    Regular user => Email: user / Password: pass

    To access Apps Manager, navigate here: https://apps.dev.cfdev.sh

    To deploy a particular service, please run:
        cf dev deploy-service <service-name> [Available services: mysql,redis,rabbitmq,scs]
```

To use the `cf` CLI tool, you must login to the PCF Dev environment:

Example 11. Login to PCF Dev using `cf` CLI


``` highlight
$ cf login -a https://api.dev.cfdev.sh --skip-ssl-validation
```

You can also access the [PCF Dev Apps
Manager](https://apps.dev.cfdev.sh/) tool from your Web browser at the
following URL:

<a href="https://apps.dev.cfdev.sh/"
class="bare">https://apps.dev.cfdev.sh/</a>

Apps Manager provides a nice UI to manage your org, space, services and
apps. It lets you push and update apps, create services, bind apps to
the services, and start and stop your deployed applications, among many
other things.

#### Running an VMware GemFire Cluster

Now that PCF Dev is set up and running, you need to start an external,
standalone VMware GemFire cluster to which our Spring Boot
application connects and uses to manage its data.

You need to install a {apache-geode-website}/releases/\[distribution\]
of VMware GemFire on your computer. Then you must set the
`$GEODE` environment variable. It is also convenient to add `$GEODE/bin`
to your system `$PATH`.

Afterward, you can launch the Geode Shell (*Gfsh*) tool:

Example 12. Running Gfsh


``` highlight
$ echo $GEODE
/Users/jblum/pivdev/apache-geode-1.6.0

$ gfsh
    _________________________     __
   / _____/ ______/ ______/ /____/ /
  / /  __/ /___  /_____  / _____  /
 / /__/ / ____/  _____/ / /    / /
/______/_/      /______/_/    /_/    1.6.0

Monitor and Manage Apache Geode
gfsh>
```

We have provided the Gfsh shell script that you can use to start the
VMware GemFire cluster:

Example 13. Gfsh shell script to start the VMware GemFire
cluster


``` highlight
#!/bin/gfsh
# Gfsh shell script to configure and bootstrap an Apache Geode cluster.

start locator --name=LocatorOne --log-level=config --classpath=@project-dir@/apache-geode-extensions/build/libs/apache-geode-extensions-@project-version@.jar --J=-Dgemfire.security-manager=org.springframework.geode.security.TestSecurityManager --J=-Dgemfire.http-service-port=8080

start server --name=ServerOne --log-level=config --user=admin --password=admin --classpath=@project-dir@/apache-geode-extensions/build/libs/apache-geode-extensions-@project-version@.jar
```

The `start-cluster.gfsh` shell script starts one Geode Locator and one
Geode server.

A Locator is used by clients to discover and connect to servers in a
cluster to manage its data. A Locator is also used by new servers that
join a cluster as peer members, which lets the cluster be elastically
scaled out (or scaled down, as needed). A Geode server stores the data
for the application.

You can start as many Locators or servers as necessary to meet the
availability and load demands of your application. The more Locators and
servers your cluster has, the more resilient it is to failure. However,
you should size your cluster accordingly, based on your application’s
needs, since there is overhead relative to the cluster size.

You see output similar to the following when starting the Locator and
server:

Example 14. Starting the VMware GemFire cluster


``` highlight
gfsh>start locator --name=LocatorOne --log-level=config --classpath=/Users/jblum/pivdev/spring-boot-data-geode/apache-geode-extensions/build/libs/apache-geode-extensions-1.1.0.BUILD-SNAPSHOT.jar --J=-Dgemfire.security-manager=org.springframework.geode.security.TestSecurityManager --J=-Dgemfire.http-service-port=8080
Starting a Geode Locator in /Users/jblum/pivdev/lab/LocatorOne...
..
Locator in /Users/jblum/pivdev/lab/LocatorOne on 10.99.199.24[10334] as LocatorOne is currently online.
Process ID: 14358
Uptime: 1 minute 1 second
Geode Version: 1.6.0
Java Version: 1.8.0_192
Log File: /Users/jblum/pivdev/lab/LocatorOne/LocatorOne.log
JVM Arguments: -Dgemfire.enable-cluster-configuration=true -Dgemfire.load-cluster-configuration-from-dir=false -Dgemfire.log-level=config -Dgemfire.security-manager=org.springframework.geode.security.TestSecurityManager -Dgemfire.http-service-port=8080 -Dgemfire.launcher.registerSignalHandlers=true -Djava.awt.headless=true -Dsun.rmi.dgc.server.gcInterval=9223372036854775806
Class-Path: /Users/jblum/pivdev/apache-geode-1.6.0/lib/geode-core-1.6.0.jar:/Users/jblum/pivdev/spring-boot-data-geode/apache-geode-extensions/build/libs/apache-geode-extensions-1.1.0.BUILD-SNAPSHOT.jar:/Users/jblum/pivdev/apache-geode-1.6.0/lib/geode-dependencies.jar

Security Manager is enabled - unable to auto-connect. Please use "connect --locator=10.99.199.24[10334] --user --password" to connect Gfsh to the locator.

Authentication required to connect to the Manager.

gfsh>connect
Connecting to Locator at [host=localhost, port=10334] ..
Connecting to Manager at [host=10.99.199.24, port=1099] ..
user: admin
password: *****
Successfully connected to: [host=10.99.199.24, port=1099]

gfsh>start server --name=ServerOne --log-level=config --user=admin --password=admin --classpath=/Users/jblum/pivdev/spring-boot-data-geode/apache-geode-extensions/build/libs/apache-geode-extensions-1.1.0.BUILD-SNAPSHOT.jar
Starting a Geode Server in /Users/jblum/pivdev/lab/ServerOne...
....
Server in /Users/jblum/pivdev/lab/ServerOne on 10.99.199.24[40404] as ServerOne is currently online.
Process ID: 14401
Uptime: 3 seconds
Geode Version: 1.6.0
Java Version: 1.8.0_192
Log File: /Users/jblum/pivdev/lab/ServerOne/ServerOne.log
JVM Arguments: -Dgemfire.default.locators=10.99.199.24[10334] -Dgemfire.security-username=admin -Dgemfire.start-dev-rest-api=false -Dgemfire.security-password=******** -Dgemfire.use-cluster-configuration=true -Dgemfire.log-level=config -XX:OnOutOfMemoryError=kill -KILL %p -Dgemfire.launcher.registerSignalHandlers=true -Djava.awt.headless=true -Dsun.rmi.dgc.server.gcInterval=9223372036854775806
Class-Path: /Users/jblum/pivdev/apache-geode-1.6.0/lib/geode-core-1.6.0.jar:/Users/jblum/pivdev/spring-boot-data-geode/apache-geode-extensions/build/libs/apache-geode-extensions-1.1.0.BUILD-SNAPSHOT.jar:/Users/jblum/pivdev/apache-geode-1.6.0/lib/geode-dependencies.jar
```

Once the cluster has been started successfully, you can list the
members:

Example 15. List members of the cluster


``` highlight
gfsh>list members
   Name    | Id
---------- | -----------------------------------------------------------------
LocatorOne | 10.99.199.24(LocatorOne:14358:locator)<ec><v0>:1024 [Coordinator]
ServerOne  | 10.99.199.24(ServerOne:14401)<v1>:1025
```

Currently, we have not defined any Regions in which to store our
application’s data:

Example 16. No Application Regions


``` highlight
gfsh>list regions
No Regions Found
```

This is deliberate, since we are going to let the application drive its
schema structure, both on the client (application) as well as on the
server-side (cluster). We cover this in more detail later in this
chapter.

#### Creating a User-Provided Service

Now that we have PCF Dev and a small VMware GemFire cluster up
and running, it is time to create a user-provided service to the
external, standalone VMware GemFire cluster that we started in
[step 2](#cloudfoundry-geode-cluster).

As mentioned, PCF Dev offers MySQL, Redis and RabbitMQ services (among
others). However, to use VMware GemFire in the same capacity as
you would Pivotal Cloud Cache when running in a production-grade PCF
environment, you need to create a user-provided service for the
standalone VMware GemFire cluster.

To do so, run the following `cf` CLI command:

Example 17. cf cups command


``` highlight
$ cf cups <service-name> -t "gemfire, cloudcache, database, pivotal" -p '<service-credentials-in-json>'
```

<p class="note"><strong>Note:</strong>
It is important that you specify the tags
(<code>gemfire</code>, <code>cloudcache</code>, <code>database</code>,
<code>pivotal</code>) exactly as shown in the preceding <code>cf</code>
CLI command.
</p>

The argument passed to the `-p` command-line option is a JSON document
(object) containing the credentials for our user-provided service.

The JSON object is as follows:

Example 18. User-Provided Service Crendentials JSON


``` highlight
{
    "locators": [ "<hostname>[<port>]" ],
    "urls": { "gfsh": "https://<hostname>/gemfire/v1" },
    "users": [{ "password": "<password>", "roles": [ "cluster_operator" ], "username": "<username>" }]
}
```

The complete `cf` CLI command would be similar to the following:

Example 19. Example `cf cups` command


``` highlight
cf cups apacheGeodeService -t "gemfire, cloudcache, database, pivotal" \
  -p '{ "locators": [ "10.99.199.24[10334]" ], "urls": { "gfsh": "https://10.99.199.24/gemfire/v1" }, "users": [{ "password": "admin", "roles": [ "cluster_operator" ], "username": "admin" }] }'
```

We replaced the `<hostname>` placeholder with the IP address of our
standalone VMware GemFire Locator. You can find the IP address
in the Gfsh `start locator` command output shown in the preceding
example.

Additionally, the `<port>` placeholder has been replaced with the
default Locator port, `10334`,

Finally, we set the `username` and `password` accordingly.

Spring Boot for VMware GemFire (SBDG)
provides template files in the
<code>{docs-dir}/src/main/resources</code> directory.

Once the service has been created, you can query the details of the
service from the `cf` CLI:

Example 20. Query the CF Dev Services


``` highlight
$ cf services
Getting services in org cfdev-org / space cfdev-space as admin...

name                 service         plan   bound apps      last operation   broker
apacheGeodeService   user-provided          boot-pcc-demo


$ cf service apacheGeodeService
Showing info of service apacheGeodeService in org cfdev-org / space cfdev-space as admin...

name:      apacheGeodeService
service:   user-provided
tags:      gemfire, cloudcache, database, pivotal

bound apps:
name            binding name   status             message
boot-pcc-demo                  create succeeded
```

You can also view the "apacheGeodeService" from Apps Manager, starting
from the `Service` tab in your org and space:

![pcfdev appsmanager org space
services](./images/pcfdev-appsmanager-org-space-services.png)

By clicking on the "apacheGeodeService" service entry in the table, you
can get all the service details, such as the bound apps:

![pcfdev appsmanager org space service
boundapps](./images/pcfdev-appsmanager-org-space-service-boundapps.png)

You can also view and set the configuration:

![pcfdev appsmanager org space service
configuration](./images/pcfdev-appsmanager-org-space-service-configuration.png)

This brief section did not cover all the capabilities of the Apps
Manager. We suggest you explore its UI to see all that is possible.

You can learn more about CUPS in the
{pivotal-cloudfoundry-docs}/devguide/services/user-provided.html[PCF
documentation].

#### Push and Bind a Spring Boot application

Now it is time to push a Spring Boot application to PCF Dev and bind the
application to the `apacheGeodeService`.

Any Spring Boot `ClientCache` application that uses SBDG works for this
purpose. For this example, we use the
[PCCDemo](https://github.com/jxblum/PCCDemo/tree/sbdg-doc-ref)
application, which is available in GitHub.

After cloning the project to your computer, you must run a build to
produce the artifact to push to PCF Dev:

Example 21. Build the PCCDemo application


``` highlight
$ mvn clean package
```

Then you can push the application to PCF Dev with the following `cf` CLI
command:

Example 22. Push the application to PCF Dev


``` highlight
$ cf push boot-pcc-demo -u none --no-start -p target/client-0.0.1-SNAPSHOT.jar
```

Once the application has been successfully deployed to PCF Dev, you can
get the application details:

Example 23. Get details for the deployed application


``` highlight
$ cf apps
Getting apps in org cfdev-org / space cfdev-space as admin...
OK

name            requested state   instances   memory   disk   urls
boot-pcc-demo   stopped           0/1         768M     1G     boot-pcc-demo.dev.cfdev.sh


$ cf app boot-pcc-demo
Showing health and status for app boot-pcc-demo in org cfdev-org / space cfdev-space as admin...

name:              boot-pcc-demo
requested state:   stopped
routes:            boot-pcc-demo.dev.cfdev.sh
last uploaded:     Tue 02 Jul 00:34:09 PDT 2019
stack:             cflinuxfs3
buildpacks:        https://github.com/cloudfoundry/java-buildpack.git

type:           web
instances:      0/1
memory usage:   768M
     state   since                  cpu    memory   disk     details
#0   down    2019-07-02T21:48:25Z   0.0%   0 of 0   0 of 0

type:           task
instances:      0/0
memory usage:   256M

There are no running instances of this process.
```

You can bind the PPCDemo application to the `apacheGeodeService` using
the `cf` CLI command:

Example 24. Bind application to `apacheGeodeService` using CLI


``` highlight
cf bind-service boot-pcc-demo apacheGeodeService
```

Alternatively, you can create a YAML file (`manifest.yml` in
`src/main/resources`) that contains the deployment descriptor:

Example 25. Example YAML deployment descriptor


``` highlight
\---
applications:
  - name: boot-pcc-demo
    memory: 768M
    instances: 1
    path: ./target/client-0.0.1-SNAPSHOT.jar
    services:
      - apacheGeodeService
    buildpacks:
      - https://github.com/cloudfoundry/java-buildpack.git
```

You can also use Apps Manager to view application details and bind and
unbind additional services. Start by navigating to the `App` tab under
your org and space:

![pcfdev appsmanager org space
apps](./images/pcfdev-appsmanager-org-space-apps.png)

From there, you can click on the desired application and navigate to the
`Overview`:

![pcfdev appsmanager org space app
overview](./images/pcfdev-appsmanager-org-space-app-overview.png)

You can also review the application `Settings`. Specifically, we are
looking at the configuration of the applicatinon once it is bound to the
`apacheGeodeService`, as seen in the `VCAP_SERVICES` environment
variable:

![pcfdev appsmanager org space app settings
envvars](./images/pcfdev-appsmanager-org-space-app-settings-envvars.png)

This JSON document structure is not unlike the configuration used to
bind your Spring Boot `ClientCache` application to the Pivotal Cloud
Cache service when deploying the same application to Pivotal
CloudFoundry. This is actually key if you want to minimize the amount of
boilerplate code and configuration changes when you migrate between
different CloudFoundry environments, even [Open Source
CloudFoundry](https://www.cloudfoundry.org/).

Again, SBDG’s goal is to simply the effort for you to build, run, and
manage your application, in whatever context your application lands,
even if it changes later. If you follow the steps in this documentation,
you can realize that goal.

#### Running the Spring Boot application

All that is left to do now is run the application.

You can start the PCCDemo application from the `cf` CLI by using the
following command:

Example 26. Start the Spring Boot application


``` highlight
$ cf start boot-pcc-demo
```

Alternatively, you can also start the application from Apps Manager.
This is convenient, since you can then tail and monitor the application
log file.

![pcfdev appsmanager org space app
logs](./images/pcfdev-appsmanager-org-space-app-logs.png)

Once the application has started, you can click the [VIEW
APP](https://boot-pcc-demo.dev.cfdev.sh/) link in the upper right corner
of the `APP` screen.

![PCCDemo app screenshot](./images/PCCDemo-app-screenshot.png)

You can navigate to any of the application Web Service, Controller
endpoints. For example, if you know the ISBN of a book, you can access
it from your Web browser:

![PCCDemo app book by isbn
screenshot](./images/PCCDemo-app-book-by-isbn-screenshot.png)

You can also access the same data from the Gfsh command-line tool.
However, the first thing to observe is that our application informed the
cluster that it needed a Region called `Books`:

Example 27. Books Region


``` highlight
gfsh>list regions
List of regions
---------------
Books


gfsh>describe region --name=/Books
..........................................................
Name            : Books
Data Policy     : partition
Hosting Members : ServerOne

Non-Default Attributes Shared By Hosting Members

 Type  |    Name     | Value
------ | ----------- | ---------
Region | size        | 1
       | data-policy | PARTITION
```

The PCCDemo app creates fake data on startup, which we can query in
Gfsh:

Example 28. Query Books


``` highlight
gfsh>query --query="SELECT book.isbn, book.title FROM /Books book"
Result : true
Limit  : 100
Rows   : 1

    isbn      | title
------------- | ---------------------
1235432BMF342 | The Torment of Others
```


### Summary

The ability to deploy Spring Boot, VMware GemFire `ClientCache`
applications to Pivotal CloudFoundry yet connect your application to an
externally managed, standalone VMware GemFire cluster is
powerful.

Indeed, this is a useful arrangement and stepping stone for many users
as they begin their journey towards Cloud-Native platforms such as
Pivotal CloudFoundry and using services such as Pivotal Cloud Cache.

Later, when you need to work with real (rather than sample)
applications, you can migrate your Spring Boot applications to a fully
managed and production-grade Pivotal CloudFoundry environment, and SBDG
figures out what to do, leaving you to focus entirely on your
application.

