# Spring Boot Security for VMware GemFire

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

- [Background](#geode-samples-boot-security-background)
- [Securing a Client Application](#geode-samples-boot-security-client)
- [Securing a Server Application](#geode-samples-boot-security-server)
- [Example](#geode-samples-boot-security-example)
  - [What it Does](#geode-samples-boot-security-example-behavior)
  - [Classes](#_classes)
  - [Running the Example](#geode-samples-boot-security-example-run)

This guide walks you through building a simple Spring Boot application
enabled with Security, specifically Auth and TLS using SSL. You should
already be familiar with Spring Boot and VMware GemFire.

Refer to the <a
href="../index.html#geode-security">Security</a> chapter in the
reference documentation for more information.

## Background

Security is critical to most applications. It is important to be able to
control who or what can access your application and what the subject is
allowed to do. This is where Auth<sup>2</sup> (Authentication &
Authorization) comes in.

Authentication is used to verify a client’s identity (human or
application) in exchange for some sort of credentials. Once
authenticated, a client must be authorized before they can perform any
actions. Authorization checks the permissions required to perform an
action (e.g. read data, modify data, change configuration, and so on)
against the permissions assigned to the client’s identity

Of course, sending passwords and other sensitive information as plain
text over the wire is not very secure, so we also need to enable SSL/TLS
to encrypt the information as it is transmitted. Now, our applications
are secure.

<p class="caution"><strong>Caution:</strong>
Neither VMware GemFire nor SBDG provides any support for
<em>securing</em> <a
href="https://en.wikipedia.org/wiki/Data_at_rest"><em>data at
rest</em></a>, such as with <em>disk encryption</em>. This concern is
typically left to hardware-based solutions.
</p>

See the Spring Boot for VMware GemFire (SBDG)
chapter on <a href="../index.html#geode-security">Security</a> for more
information.


## Securing a Client Application

Enabling auth on the client is mostly taken care of by Spring Boot’s
Auto-configuration.

For more details on Spring Boot’s Auto-configuration
applied to Security, and securing the client and server, see <a
href="boot-configuration.html#geode-samples-boot-configuration-clientserver-security">here</a>.

In Spring Boot `application.properties`, set the
`spring.data.gemfire.security.username` and
`spring.data.gemfire.security.password` properties to the username and
password your application will use to authenticate.

Enabling SSL on the client requires you to put a `trusted.keystore` file
(a *Java KeyStore*) in a well-known place, such as your application’s
working directory or your home directory, and Auto-configuration will do
the rest.

If your `trusted.keystore` has a password (as it should), you will need
to specify it using the
`spring.data.gemfire.security.ssl.keystore.password` property in your
Spring Boot `application.properties` file. You can generate a Keystore
using [Java
Keytool](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/keytool.html).

See Spring Boot for VMware GemFire’s (SBDG) chapter
on <a href="../index.html#geode-security-auth-clients">Auth for
Clients</a> for more information.


## Securing a Server Application

Auto-configuration does not do as much for you when configuring auth on
the server as it does on the client. In order to enable auth, you need
to do two things.

First, annotate your configuration class with `@EnableSecurity`. Second,
because VMware GemFire’s security is integrated with Apache Shiro,
define at least one Shiro Realm as a bean in your Spring
`ApplicationContext`.

Example Shiro Realm bean:

``` highlight
    @Bean
    PropertiesRealm shiroRealm() {

        PropertiesRealm propertiesRealm = new PropertiesRealm();

        propertiesRealm.setResourcePath("classpath:shiro.properties");
        propertiesRealm.setPermissionResolver(new GeodePermissionResolver());

        return propertiesRealm;
    }
```

You can find more information on Apache Shiro and how to configure a
Realm [here](https://shiro.apache.org/realm).

Enabling SSL on the server is essentially the same as for the client,
just put your `trusted.keystore` file (a *Java KeyStore*) in a
well-known place, like your application’s working directory or your home
directory. If your `trusted.keystore` has a password (as it should), you
will need to specify it using the
`spring.data.gemfire.security.ssl.keystore.password` property in your
Spring Boot `application.properties` file. You can generate a Keystore
using [Java
Keytool](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/keytool.html).

See Spring Boot for VMware GemFire’s (SBDG) chapter
on <a href="../index.html#geode-security-auth-servers">Auth for
Servers</a> for more information.


## Example

To demonstrate the proper way to configure a Spring Boot application
with security, we put together a simple example. The example is made up
of two main parts:

A client - `BootGeodeSecurityClientApplication`.

A server - `BootGeodeSecurityServerApplication`.

### What it Does

The example is very minimal and only performs some basic data access
operations in a secure context. The server starts up, and then the
client connects to the server and tries to do two things:

1.  Write a new value into Customers, which succeeds.

2.  Read a value from Customers, which fails because the user that the
    client authenticates with is only authorized to write data, not read
    it.

This behavior may change depending on the credentials used to
authenticate. For example, running with “*cluster_operator*” credentials
on the platform will result in both read and write operations
succeeding.

### Classes

#### BootGeodeSecurityClientApplication

Spring Boot, VMware GemFire Client Application

``` highlight
@SpringBootApplication
@EnableClusterAware
@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
public class BootGeodeSecurityClientApplication {

    private static final Logger logger = LoggerFactory.getLogger("example.app.security");

    public static void main(String[] args) {

        new SpringApplicationBuilder(BootGeodeSecurityClientApplication.class)
            .web(WebApplicationType.SERVLET)
            .build()
            .run(args);
    }

    @Bean
    ApplicationRunner runner(@Qualifier("customersTemplate") GemfireTemplate customersTemplate) {

        return args -> {

            Customer williamEvans = Customer.newCustomer(2L, "William Evans");

            customersTemplate.put(williamEvans.getId(), williamEvans);

            logger.info("Successfully put [{}] in Region [{}]",
                williamEvans, customersTemplate.getRegion().getName());

            try {
                logger.info("Attempting to read from Region [{}]...", customersTemplate.getRegion().getName());
                customersTemplate.get(2L);
            }
            catch (Exception cause) {
                logger.info("Read failed because \"{}\"", cause.getCause().getCause().getMessage());
            }
        };
    }
}
```

This class is a Spring Boot, VMware GemFire client application (i.e.
`ClientCache`) configured to authenticate when connecting to a cluster
of servers using connections secured with SSL.

The `@SpringBootApplication` annotation declares the application to be a
Spring Boot application. With SBDG on the application classpath, a
`ClientCache` instance will be auto-configured automatically, making the
application a cache client capable of connecting to the cluster.

Finally, we declare a `ApplicationRunner` bean to perform some basic
data access operations secured by the server to observe the effects of
security.

Because SDBG auto-configures a
<code>ClientCache</code> instance by default, you do not need to
explicitly annotate your <code>@SpringBootApplication</code> class with
SDG’s <code>@ClientCacheApplication</code> annotation. In fact doing so
disables some of the auto-configuration, like security, applied by SBDG
OOTB. The same is true when you declare one of the
[<code>@PeerCacheApplication</code>,
<code>@CacheServerApplication</code>] annotations, which changes your
<code>@SpringBootApplication</code> class completely, from a client to a
server-side VMware GemFire process. Therefore, be careful! See the
relevant <a
href="../index.html#geode-clientcache-applications">chapter</a> in the
reference documentation for more details.

#### BootGeodeSecurityServerApplication

Spring Boot, VMware GemFire Server Application

``` highlight
@SpringBootApplication
@CacheServerApplication
@EnableSecurity
public class BootGeodeSecurityServerApplication {

    public static void main(String[] args) {

        new SpringApplicationBuilder(BootGeodeSecurityServerApplication.class)
            .web(WebApplicationType.NONE)
            .build()
            .run(args);
    }

    @Bean
    PropertiesRealm shiroRealm() {

        PropertiesRealm propertiesRealm = new PropertiesRealm();

        propertiesRealm.setResourcePath("classpath:shiro.properties");
        propertiesRealm.setPermissionResolver(new GeodePermissionResolver());

        return propertiesRealm;
    }
}
```

This class is a Spring Boot, VMware GemFire server application (i.e.
`CacheServer`) that requires clients (i.e. `ClientCache`) to
authenticate when connecting to the server and to communicate using SSL.

Unlike the client application class above, we annotate this
`@SpringBootApplication` class with `@CacheServerApplication` to
override the default `ClientCache` auto-configured by SBDG OOTB. This
makes the application a VMware GemFire server on startup, capable of
serving clients.

We must additionally annotate the server application class with SBDG’s
`@EnableSecurity` annotation to enable VMware GemFire Security on the
server-side. By explicitly declaring a `PropertiesRealm` bean, we are
using Apache Shiro as the auth provider, supplying the security
credentials (users, roles and permissions) via a Java Properties file:

Apache Shiro Properties file containing the security credentials
configuration

``` highlight
# Assign user 'jdoe' the role of 'viewer' having 'DATA:WRITE' permissions.
user.jdoe = p@55w0rd, viewer
role.viewer = DATA:WRITE
```

In addition to the auth (authentication/authorization) configuration, we
must additionally supply a Java Keystore file to encrypt the connection
between the client and server using SSL, as discussed above. All you
need to do is create a Java Keystore file and put it in your application
classpath root. SBDG will [take care of the
rest](../index.html#geode-security-ssl).

Of course, if you have secured your Java Keystore file with a password
(as you should) then you must additionally supply the password in
`application.properties`, like so:

Application.properties containing Auth (username/password) and SSL
configuration

``` highlight
# Spring Boot client application.properties

spring.data.gemfire.management.use-http=false
spring.data.gemfire.security.username = jdoe
spring.data.gemfire.security.password = p@55w0rd
spring.data.gemfire.security.ssl.keystore.password=s3cr3t
```

The SSL related configuration is used by both the client and server.

#### Customer

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

This is a simple application domain class to represent a customer. The
`Customer` class is annotated with SDG’s `@Region` mapping annotation to
declare that the "*Customers*" `Region` will contain `Customer` objects
that will be accessed securely from the client.

#### SecurityController

SecurityController class

``` highlight
@RestController
public class SecurityController {

    @Autowired
    private Environment environment;

    @GetMapping("/message")
    public String getMessage() {
        return String.format("I'm using SSL with this Keystore: %s",
            this.environment.getProperty("spring.data.gemfire.security.ssl.keystore"));
    }
}
```

This class is a Spring `RestController` exposing an REST service
endpoint at “*/message*” to verify the clients use of SSL.


### Running the Example

#### Running Locally

To run the example, first start the `BootGeodeSecurityServerApplication`
and then run `BootGeodeSecurityClientApplication`.

In the terminal you should see the following output:

Output when running locally

``` highlight
Successfully put [Customer(name=William Evans)] in Region [Customers]
Attempting to read from Region [Customers]...
Read failed because "jdoe not authorized for DATA:READ:Customers:2"
```

You can also hit the endpoint at
[localhost:8080/message](http://localhost:8080/message) to verify the
application is using SSL.

#### Running on VMware Tanzu GemFire \[VMs\]

In order for this sample to work, your VMware Tanzu GemFire \[VMs\] tile
must be setup to work with TLS. Instructions to enable TLS for the
VMware Tanzu GemFire \[VMs\] tile can be found
[here](https://docs.pivotal.io/p-cloud-cache/1-11/prepare-TLS.html).

Once TLS has been enabled, create your service instance with the
`-c '{"tls":true}'` flag.

For example:

Create Service Instance enabled with TLS

``` highlight
cf create-service p-cloudcache [plan-name] [service-instance-name] -c '{"tls":true}'
```

Replace `[plan-name]` with the plan you are selecting and
`[service-instance-name]` with the desired name of your service.

Update `manifest.yml` with the `[service-instance-name]`

``` highlight
services:
- [your-service-instance-name]
```

Before deploying the application to the platform, you must update the
username and password in the `application.properties` file with the
correct credentials for your service instance.

Once your service instance is created you’ll need to create a
service-key for the service.

Create Service Key

``` highlight
cf create-service-key [service-instance-name] [service-key-name]
```

Replace `[service-instance-name]` with the name of your service instance
(from above). `[service-key-name]` is what you would like to call this
service key.

Once the service key is created, access the credentials in the service
with the following command:

Review Service Key Details

``` highlight
cf service-key [service-instance-name] [service-key-name]
```

Replace `[service-instance-name]` with the name of your service instance
and `[service-key-name]` with the name of your service key (from the
previous step above).

In the output, look for the “users” section. For this example, we used
the “*cluster_operator*” user credentials.

VCAP_SERVICES credentials block

``` highlight
{
...
 "users": [
  {
   "password": "xxxxxxxxxxxxxxxxxxxxxxxx",
   "roles": [
    "cluster_operator"
   ],
   "username": "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
  },
  {
   "password": "xxxxxxxxxxxxxxxxxxxxxx",
   "roles": [
    "developer"
   ],
   "username": "xxxxxxxxxxxxxxxxxxxxxxxxxx"
  },
  {
   "password": "xxxxxxxxxxxxxxxxxxxxx",
   "roles": [
    "readonly"
   ],
   "username": "xxxxxxxxxxxxxxxx"
  }
 ],
 "wan": {}
}
```

Now build the sample with Gradle:

Build with Gradle

``` highlight
$ gradlew :spring-geode-samples-boot-security:build
```

Then push the application to the platform using `cf push`.

Push to CF

``` highlight
$ cf push <app-name> -u none -p ~/spring-boot-data-geode/spring-geode-samples/boot/security/build/libs/spring-geode-samples-boot-security-{spring-boot-data-geode-version}.jar
...
```

Once the app is running, check the logs with
`cf logs security-app --recent` and you should see output like the
following:

Log output from the platform

``` highlight
Successfully put [Customer(name=William Evans)] in Region [Customers]
Attempting to read from Region [Customers]...
Read failed because "jdoe not authorized for DATA:READ:Customers:2"
```

You can also hit the endpoint at
<https://security-app.apps.%3Ccf-instance%3E.cf-app.com/message>.

Replace `<cf-instance>` with the name of your CloudFoundry instance to
verify that the application is using SSL.

Congratulations! You have taken your first steps towards securing an
VMware GemFire application with Spring Boot.


