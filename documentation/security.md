---
Title: Security
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



This chapter covers security configuration for [vmware-gemfire-name],
which includes both authentication and authorization (collectively,
auth) as well as Transport Layer Security (TLS) using SSL.





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
<td class="content">Securing data at rest is not supported by
[vmware-gemfire-name].</td>
</tr>
</tbody>
</table>





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
<td class="content">See the corresponding sample <a
href="guides/boot-security.html">guide</a> and
 [code](https://github.com/gemfire/spring-boot-for-vmware-gemfire/tree/9.15-2.7/spring-gemfire-samples/boot/security) to see Spring Boot Security for
[vmware-gemfire-name] in action.</td>
</tr>
</tbody>
</table>





### Authentication and Authorization



[vmware-gemfire-name] employs username- and password-based
[authentication](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/managing-security-authentication_overview.html)
and role-based
[authorization](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/managing-security-authorization_overview.html)
to secure your client to server data exchanges and operations.





[spring-data-gemfire-name] provides
[first-class support([spring-data-gemfire-docs]/#bootstrap-annotation-config-security)] for [vmware-gemfire-name]'s Security framework, which is
based on the
[`SecurityManager`](https://gemfire.docs.pivotal.io/apidocs/tgf-915/index.html?org/apache/geode/security/SecurityManager.html)
interface. Additionally, [vmware-gemfire-name]'s Security framework is
integrated with [Apache Shiro](https://shiro.apache.org/).





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
<td class="content">[spring-boot-gemfire-name] will eventually provide support for and
integration with <a
href="https://spring.io/projects/spring-security">Spring
Security</a>.</td>
</tr>
</tbody>
</table>





When you use [spring-boot-gemfire-name], which builds [spring-data-gemfire-name], it makes short work of enabling auth in
both your clients and servers.





#### Auth for Servers



The easiest and most standard way to enable auth in the servers of your
cluster is to simply define one or more Apache Shiro
[Realms](https://shiro.apache.org/realm.html) as beans in the Spring
`ApplicationContext`.





Consider the following example:







Example 1. Declaring an Apache Shiro Realm









``` highlight
@Configuration
class ApacheGeodeSecurityConfiguration {

    @Bean
    DefaultLdapRealm ldapRealm() {
        return new DefaultLdapRealm();
    }

    // ...
}
```











When an Apache Shiro Realm (such as `DefaultLdapRealm`) is declared and
registered in the Spring `ApplicationContext` as a Spring bean, Spring
Boot automatically detects this `Realm` bean (or `Realm` beans if more
than one is configured), and the servers in the [vmware-gemfire-name]
cluster are automatically configured with authentication and
authorization enabled.





Alternatively, you can provide a custom, application-specific
implementation of [vmware-gemfire-name]'s
[`SecurityManager`](https://gemfire.docs.pivotal.io/apidocs/tgf-915/index.html?org/apache/geode/security/SecurityManager.html)
interface, declared and registered as a bean in the Spring
`ApplicationContext`:







Example 2. Declaring a custom [vmware-gemfire-name] `SecurityManager`









``` highlight
@Configuration
class ApacheGeodeSecurityConfiguration {

    @Bean
    CustomSecurityManager customSecurityManager() {
        return new CustomSecurityManager();
    }

    // ...
}
```











Spring Boot discovers your custom, application-specific
`SecurityManager` implementation and configures the servers in the
[vmware-gemfire-name] cluster with authentication and authorization
enabled.





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
<td class="content">The Spring team recommends that you use Apache Shiro
to manage the authentication and authorization of your servers over
implementing [vmware-gemfire-name]'s <code>SecurityManager</code>
interface.</td>
</tr>
</tbody>
</table>







#### Auth for Clients



When servers in an [vmware-gemfire-name] cluster have been configured
with authentication and authorization enabled, clients must authenticate
when connecting.





[spring-boot-gemfire-name] makes this easy, regardless of
whether you run your Spring Boot `ClientCache` applications in a local,
non-managed environment or run in a cloud-managed environment.





##### Non-Managed Auth for Clients



To enable auth for clients that connect to a secure
[vmware-gemfire-name] cluster, you need only set a username and
password in Spring Boot `application.properties`:







Example 3. Spring Boot `application.properties` for the client









``` highlight
# Spring Boot client application.properties

spring.data.gemfire.security.username = jdoe
spring.data.gemfire.security.password = p@55w0rd
```











[spring-boot-gemfire-name] handles the rest.







##### Managed Auth for Clients



Enabling auth for clients that connect to a {pivotal-cloudcache-name}
service instance (PCC) in Tanzu Application Service (PCF) is even
easier: You need do nothing.





If your Spring Boot application uses [spring-boot-gemfire-name] and is bound to PCC, when you
deploy (that is, `cf push`) your application to PCF, [spring-boot-gemfire-name] extracts the required auth credentials from the
environment that you set up when you provisioned a PCC service instance
in your PCF organization and space. PCC automatically assigns two users
with roles of `cluster_operator` and `developer`, respectively, to any
Spring Boot application bound to the PCC service instance.





By default, [spring-boot-gemfire-name] auto-configures your Spring Boot application to run
with the user that has the `cluster_operator` role. This ensures that
your Spring Boot application has the necessary permission
(authorization) to perform all data access operations on the servers in
the PCC cluster, including, for example, pushing configuration metadata
from the client to the servers in the PCC cluster.





See the [Running Spring Boot applications as a specific
user](#cloudfoundry-cloudcache-security-auth-runtime-user-configuration)
section in the [Pivotal CloudFoundry](#cloudfoundry) chapter for
additional details on user authentication and authorization.





See the [chapter](#cloudfoundry) (titled “Pivotal CloudFoundry”) for
more general details.





See the {pivotal-cloudcache-docs}/security.html\[Pivotal Cloud Cache
documentation\] for security details when you use PCC and PCF.











### <a id='geode-security-ssl'></a>Transport Layer Security using SSL



Securing data in motion is also essential to the integrity of your
Spring \[Boot\] applications.





For instance, it would not do much good to send usernames and passwords
over plain text socket connections between your clients and servers nor
to send other sensitive data over those same connections.





Therefore, [vmware-gemfire-name] supports SSL between clients and
servers, between JMX clients (such as Gfsh) and the Manager, between
HTTP clients when you use the Developer REST API or Pulse, between peers
in the cluster, and when you use the WAN Gateway to connect multiple
sites (clusters).





[spring-data-gemfire-name] provides [first-class
support]([spring-data-gemfire-docs]/#bootstrap-annotation-config-ssl)
for configuring and enabling SSL as well. Still, Spring Boot makes it
even easier to configure and enable SSL, especially during development.





[vmware-gemfire-name] requires certain properties to be configured.
These properties translate to the appropriate `javax.net.ssl.*`
properties required by the JRE to create secure socket connections by
using
[JSSE](https://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/JSSERefGuide.html).





However, ensuring that you have set all the required SSL properties
correctly is an error prone and tedious task. Therefore, [spring-boot-gemfire-name] applies some basic conventions for you.





You can create a `trusted.keystore` as a JKS-based `KeyStore` file and
place it in one of three well-known locations:





- In your application JAR file at the root of the classpath.

- In your Spring Boot application’s working directory.

- In your user home directory (as defined by the `user.home` Java System
  property).





When this file is named `trusted.keystore` and is placed in one of these
three well-known locations, [spring-boot-gemfire-name]
automatically configures your client to use SSL socket connections.





If you use Spring Boot to configure and bootstrap an
[vmware-gemfire-name] server:







Example 4. Spring Boot configured and bootstrapped
[vmware-gemfire-name] server









``` highlight
@SpringBootApplication
@CacheServerApplication
class SpringBootGemFireCacheServerApplication {
    // ...
}
```











Then Spring Boot also applies the same procedure to enable SSL on the
servers (between peers).





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
<td class="content">During development, it is convenient to
<strong>not</strong> set a <code>trusted.keystore</code> password when
accessing the keys in the JKS file. However, it is highly recommended
that you secure the <code>trusted.keystore</code> file when deploying
your application to a production environment.</td>
</tr>
</tbody>
</table>





If your `trusted.keystore` file is secured with a password, you need to
additionally specify the following property:







Example 5. Accessing a secure `trusted.keystore`









``` highlight
# Spring Boot application.properties

spring.data.gemfire.security.ssl.keystore.password=p@55w0rd!
```











You can also configure the location of the keystore and truststore
files, if they are separate and have not been placed in one of the
default, well-known locations searched by Spring Boot:







Example 6. Accessing a secure `trusted.keystore` by location









``` highlight
# Spring Boot application.properties

spring.data.gemfire.security.ssl.keystore = /absolute/file/system/path/to/keystore.jks
spring.data.gemfire.security.ssl.keystore.password = keystorePassword
spring.data.gemfire.security.ssl.truststore = /absolute/file/system/path/to/truststore.jks
spring.data.gemfire.security.ssl.truststore.password = truststorePassword
```











See the [spring-data-gemfire-name]
[spring-data-gemfire-javadoc]/org/springframework/data/gemfire/config/annotation/EnableSsl.html\[`EnableSsl`\]
annotation for all the configuration attributes and the corresponding
properties expressed in `application.properties`.







### Securing Data at Rest



Currently, neither [vmware-gemfire-name] nor Spring Boot nor [spring-data-gemfire-name] offer any support for securing your data
while at rest (for example, when your data has been overflowed or
persisted to disk).





To secure data at rest when using [vmware-gemfire-name], with or
without Spring, you must employ third-party solutions, such as disk
encryption, which is usually highly contextual and technology-specific.





For example, to secure data at rest when you use Amazon EC2, see
[Instance Store
Encryption](https://aws.amazon.com/blogs/security/how-to-protect-data-at-rest-with-amazon-ec2-instance-store-encryption/).











<div id="footer">

<div id="footer-text">

Last updated 2022-10-10 12:14:24 -0700




