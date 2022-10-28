---
Title: Spring Data Repositories
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



Using Spring Data Repositories with VMware GemFire makes short
work of data access operations when you use VMware GemFire as
your System of Record (SoR) to persist your application’s state.





https://docs.spring.io/spring-data/commons/docs/current/reference/html/#repositories\[Spring Data
Repositories\] provide a convenient and powerful way to define basic
CRUD and simple query data access operations by specifying the contract
of those data access operations in a Java interface.





Spring Boot for VMware GemFire auto-configures the Spring Data
for VMware GemFire
https://docs.spring.io/spring-data/geode/docs/current/reference/html/#gemfire-repositories\[Repository
extension\] when either is declared on your application’s classpath. You
need not do anything special to enable it. You can start coding your
application-specific Repository interfaces.





The following example defines a `Customer` class to model customers and
map it to the VMware GemFire `Customers` Region by using the SDG
https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/mapping/annotation/Region.html\[`@Region`\]
mapping annotation:







Example 1. `Customer` entity class









``` highlight
package example.app.crm.model;

@Region("Customers")
class Customer {

    @Id
    private Long id;

    private String name;

}
```











The following example shows how to declare your Repository (a.k.a.
{wikipedia-docs}/Data_access_object\[Data Access Object (DAO)\]) for
`Customers`:







Example 2. `CustomerRepository` for peristing and accessing `Customers`









``` highlight
package example.app.crm.repo;

interface CustomerRepository extends CrudRepository<Customer, Long> {

    List<Customer> findByLastNameLikeOrderByLastNameDescFirstNameAsc(String customerLastNameWildcard);

}
```











Then you can use the `CustomerRepository` in an application service
class:







Example 3. Inject and use the `CustomerRepository`









``` highlight
package example.app;

@SpringBootApplication
@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
class SpringBootApacheGeodeClientCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApacheGeodeClientCacheApplication.class, args);
    }

    @Bean
    ApplicationRunner runner(CustomerRepository customerRepository) {

        // Matches Williams, Wilson, etc.
        List<Customer> customers =
            customerRepository.findByLastNameLikeOrderByLastNameDescFirstNameAsc("Wil%");

        // process the list of matching customers...
    }
}
```











See Spring Data Commons'
https://docs.spring.io/spring-data/commons/docs/current/reference/html/#repositories\[Repositories
abstraction\] and Spring Data for VMware GemFire's
https://docs.spring.io/spring-data/geode/docs/current/reference/html/#gemfire-repositories\[Repositories
extension\] for more detail.









<div id="footer">

<div id="footer-text">

Last updated 2022-10-10 12:13:15 -0700




