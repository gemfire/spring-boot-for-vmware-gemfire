---
Title: Data Access with GemfireTemplate
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



There are several ways to access data stored in VMware GemFire.





For instance, you can use the
https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/Region.html\[Region API\]
directly. If you are driven by the application’s domain context, you can
use the power of https://docs.spring.io/spring-data/commons/docs/current/reference/html/#repositories\[Spring
Data Repositories\] instead.





While the Region API offers flexibility, it couples your application to
VMware GemFire, which is usually undesirable and unnecessary.
While using Spring Data Repositories provides a very powerful and
convenient abstraction, you give up the flexibility provided by a
lower-level Region API.





A good compromise is to use the [Template software design
pattern](https://en.wikipedia.org/wiki/Template_method_pattern). This
pattern is consistently and widely used throughout the entire Spring
portfolio.





For example, the Spring Framework provides
https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html\[`JdbcTemplate`\]
and
https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/jms/core/JmsTemplate.html\[`JmsTemplate`\].





Other Spring Data modules, such as Spring Data Redis, offer the
[`RedisTemplate`](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/RedisTemplate.html),
and Spring Data for VMware GemFire (SDG) itself offers the
{spring-data-gemfire-javadoc}/org/springframework/data/gemfire/GemfireTemplate.html\[`GemfireTemplate`\].





The `GemfireTemplate` provides a highly consistent and familiar API to
perform data access operations on VMware GemFire cache
`Regions`.





`GemfireTemplate` offers:





- A simple and convenient data access API to perform basic CRUD and
  simple query operations on cache Regions.

- Use of Spring Framework’s consistent data access
  https://docs.spring.io/spring/docs/current/spring-framework-reference/data-access.html#dao-exceptions\[Exception
  hierarchy\].

- Automatic enlistment in the presence of local cache transactions.

- Consistency and protection from
  https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/Region.html\[Region
  API\] breaking changes.





Given these advantages, Spring Boot for VMware GemFire (SBDG)
auto-configures `GemfireTemplate` beans for each Region present in the
VMware GemFire cache.





Additionally, SBDG is careful not to create a `GemfireTemplate` if you
have already declared a `GemfireTemplate` bean in the Spring
`ApplicationContext` for a given Region.





### Explicitly Declared Regions



Consider an explicitly declared Region bean definition:





1.  Explicitly Declared Region Bean Definition











``` highlight
@Configuration
class GeodeConfiguration {

    @Bean("Example")
    ClientRegionFactoryBean<?, ?> exampleRegion(GemFireCache gemfireCache) {
        // ...
    }
}
```











SBDG automatically creates a `GemfireTemplate` bean for the `Example`
Region by using the bean name `exampleTemplate`. SBDG names the
`GemfireTemplate` bean after the Region by converting the first letter
in the Region’s name to lower case and appending `Template` to the bean
name.





In a managed Data Access Object (DAO), you can inject the Template:











``` highlight
@Repository
class ExampleDataAccessObject {

    @Autowired
    @Qualifier("exampleTemplate")
    private GemfireTemplate exampleTemplate;

}
```











You should use the `@Qualifier` annotation to qualify which
`GemfireTemplate` bean you are specifically referring, especially if you
have more than one Region bean definition.







### Entity-defined Regions



SBDG auto-configures `GemfireTemplate` beans for entity-defined Regions.





Consider the following entity class:







Example 1. Customer class









``` highlight
@Region("Customers")
class Customer {
    // ...
}
```











Further consider the following configuration:







Example 2. Apache Geode Configuration









``` highlight
@Configuration
@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
class GeodeConfiguration {
    // ...
}
```











SBDG auto-configures a `GemfireTemplate` bean for the `Customers` Region
named `customersTemplate`, which you can then inject into an application
component:







Example 3. CustomerService application component









``` highlight
@Service
class CustomerService {

    @Bean
    @Qualifier("customersTemplate")
    private GemfireTemplate customersTemplate;

}
```











Again, be careful to qualify the `GemfireTemplate` bean injection if you
have multiple Regions, whether declared explicitly or implicitly, such
as when you use the `@EnableEntityDefineRegions` annotation.







### Caching-defined Regions



SBDG auto-configures `GemfireTemplate` beans for caching-defined
Regions.





When you use Spring Framework’s
https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache\[Cache Abstraction\]
backed by VMware GemFire, one requirement is to configure
Regions for each of the caches specified in the
https://docs.spring.io/spring/docs/current/spring-framework-referenceintegration.html#cache-annotations\[caching
annotations\] of your application service components.





Fortunately, SBDG makes enabling and configuring caching easy and
[automatic](#geode-caching-provider).





Consider the following cacheable application service component:







Example 4. Cacheable `CustomerService` class









``` highlight
@Service
class CacheableCustomerService {

    @Bean
    @Qualifier("customersByNameTemplate")
    private GemfireTemplate customersByNameTemplate;

    @Cacheable("CustomersByName")
    public Customer findBy(String name) {
        return toCustomer(customersByNameTemplate.query("name = " + name));
    }
}
```











Further consider the following configuration:







Example 5. Apache Geode Configuration









``` highlight
@Configuration
@EnableCachingDefinedRegions
class GeodeConfiguration {

    @Bean
    public CustomerService customerService() {
        return new CustomerService();
    }
}
```











SBDG auto-configures a `GemfireTemplate` bean named
`customersByNameTemplate` to perform data access operations on the
`CustomersByName` (`@Cacheable`) Region. You can then inject the bean
into any managed application component, as shown in the preceding
application service component example.





Again, be careful to qualify the `GemfireTemplate` bean injection if you
have multiple Regions, whether declared explicitly or implicitly, such
as when you use the `@EnableCachingDefineRegions` annotation.





<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon">
Warning
</td>
<td class="content">Autowiring (that is, injecting)
<code>GemfireTemplate</code> beans auto-configured by SBDG for
caching-defined Regions into your application components does not always
work. This has to do with the Spring container bean creation process. In
those cases, you may need to lazily lookup the
<code>GemfireTemplate</code> by using
<code>applicationContext.getBean("customersByNameTemplate", GemfireTemplate.class)</code>.
This is not ideal, but it works when autowiring does not.</td>
</tr>
</tbody>
</table>







### Native-defined Regions



SBDG even auto-configures `GemfireTemplate` beans for Regions that have
been defined with VMware GemFire native configuration metadata,
such as `cache.xml`.





Consider the following VMware GemFire native `cache.xml`:







Example 6. Client `cache.xml`









``` highlight
<?xml version="1.0" encoding="UTF-8"?>
<client-cache xmlns="http://geode.apache.org/schema/cache"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://geode.apache.org/schema/cache http://geode.apache.org/schema/cache/cache-1.0.xsd"
              version="1.0">

    <region name="Example" refid="LOCAL"/>

</client-cache>
```











Further consider the following Spring configuration:







Example 7. Apache Geode Configuration









``` highlight
@Configuration
@EnableGemFireProperties(cacheXmlFile = "cache.xml")
class GeodeConfiguration {
    // ...
}
```











SBDG auto-configures a `GemfireTemplate` bean named `exampleTemplate`
after the `Example` Region defined in `cache.xml`. You can inject this
template as you would any other Spring-managed bean:







Example 8. Injecting the `GemfireTemplate`









``` highlight
@Service
class ExampleService {

    @Autowired
    @Qualifier("exampleTemplate")
    private GemfireTemplate exampleTemplate;

}
```











The rules described earlier apply when multiple Regions are present.







### Template Creation Rules



Fortunately, SBDG is careful not to create a `GemfireTemplate` bean for
a Region if a template by the same name already exists.





For example, consider the following configuration:







Example 9. Apache Geode Configuration









``` highlight
@Configuration
@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
class GeodeConfiguration {

    @Bean
    public GemfireTemplate customersTemplate(GemFireCache cache) {
        return new GemfireTemplate(cache.getRegion("/Customers"));
    }
}
```











Further consider the following example:







Example 10. Customer class









``` highlight
@Region("Customers")
class Customer {
    // ...
}
```











Because you explicitly defined and declared the `customersTemplate`
bean, SBDG does not automatically create a template for the `Customers`
Region. This applies regardless of how the Region was created, whether
by using `@EnableEntityDefinedRegions`, `@EnableCachingDefinedRegions`,
explicitly declaring Regions, or natively defining Regions.





Even if you name the template differently from the Region for which the
template was configured, SBDG conserves resources and does not create
the template.





For example, suppose you named the `GemfireTemplate` bean
`vipCustomersTemplate`, even though the Region name is `Customers`,
based on the `@Region` annotated `Customer` class, which specified the
`Customers` Region.





With the following configuration, SBDG is still careful not to create
the template:







Example 11. Apache Geode Configuration









``` highlight
@Configuration
@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
class GeodeConfiguration {

    @Bean
    public GemfireTemplate vipCustomersTemplate(GemFireCache cache) {
        return new GemfireTemplate(cache.getRegion("/Customers"));
    }
}
```











SBDG identifies that your `vipCustomersTemplate` is the template used
with the `Customers` Region, and SBDG does not create the
`customersTemplate` bean, which would result in two `GemfireTemplate`
beans for the same Region.





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
<td class="content">The name of your Spring bean defined in Java
configuration is the name of the method if the Spring bean is not
explicitly named by using the <code>name</code> attribute or the
<code>value</code> attribute of the <code>@Bean</code> annotation.</td>
</tr>
</tbody>
</table>











<div id="footer">

<div id="footer-text">

Last updated 2022-10-10 12:13:06 -0700




