---
Title: Testing
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



Spring Boot for VMware GemFire (SBDG), with help from [Spring
Test for VMware GemFire
(STDG)](https://github.com/spring-projects/spring-test-data-geode),
offers first-class support for both unit and integration testing with
VMware GemFire in your Spring Boot applications.





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
<td class="content">See the Spring Test for Apache Geode (STDG) <a
href="https://github.com/spring-projects/spring-test-data-geode/#stdg-in-a-nutshell">documentation</a>
for more details.</td>
</tr>
</tbody>
</table>





### Unit Testing



Unit testing with VMware GemFire using mock objects in a Spring
Boot Test requires only that you declare the STDG
`@EnableGemFireMockObjects` annotation in your test configuration:







Example 1. Unit Test with VMware GemFire using Spring Boot









``` highlight
@SpringBootTest
@RunWith(SpringRunner.class)
public class SpringBootApacheGeodeUnitTest extends IntegrationTestsSupport {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void saveAndFindUserIsSuccessful() {

        User jonDoe = User.as("jonDoe");

        assertThat(this.userRepository.save(jonDoe)).isNotNull();

        User jonDoeFoundById = this.userRepository.findById(jonDoe.getName()).orElse(null);

        assertThat(jonDoeFoundById).isEqualTo(jonDoe);
    }

    @SpringBootApplication
    @EnableGemFireMockObjects
    @EnableEntityDefinedRegions(basePackageClasses = User.class)
    static class TestConfiguration { }

}

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "as")
@Region("Users")
class User {

    @Id
    @lombok.NonNull
    private String name;

}

interface UserRepository extends CrudRepository<User, String> { }
```











This test class is not a “pure” unit test, particularly since it
bootstraps an actual Spring `ApplicationContext` using Spring Boot.
However, it does mock all VMware GemFire objects, such as the
`Users` `Region` declared by the `User` application entity class, which
was annotated with SDG’s `@Region` mapping annotation.





This test class conveniently uses Spring Boot’s auto-configuration to
auto-configure an VMware GemFire `ClientCache` instance. In
addition, SDG’s `@EnableEntityDefinedRegions` annotation was used to
conveniently create the VMware GemFire "Users\` `Region` to
store instances of `User`.





Finally, Spring Data’s Repository abstraction was used to conveniently
perform basic CRUD (such as `save`) and simple (OQL) query (such as
`findById`) data access operations on the `Users` `Region`.





Even though the VMware GemFire objects (such as the `Users`
`Region`) are “mock objects”, you can still perform many of the data
access operations required by your Spring Boot application’s components
in an VMware GemFire API-agnostic way — that is, by using
Spring’s powerful programming model and constructs.





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
<td class="content">By extending STDG’s
<code>org.springframework.data.gemfire.tests.integration.IntegrationTestSupport</code>
class, you ensure that all VMware GemFire mock objects and
resources are properly released after the test class runs, thereby
preventing any interference with downstream tests.</td>
</tr>
</tbody>
</table>





While STDG tries to [mock the functionality and
behavior](https://github.com/spring-projects/spring-test-data-geode/#mock-regions-with-data)
for many `Region` operations, it is not pragmatic to mock them all. For
example, it would not be practical to mock `Region` query operations
involving complex OQL statements that have sophisticated predicates.





If such functional testing is required, the test might be better suited
as an integration test. Alternatively, you can follow the advice in this
section about [unsupported Region
operations](https://github.com/spring-projects/spring-test-data-geode/#mocking-unsupported-region-operations).





In general, STDG provides the following capabilities when mocking
VMware GemFire objects:





- [Mock Object Scope & Lifecycle
  Management](https://github.com/spring-projects/spring-test-data-geode#mock-object-scope—​lifecycle-management)

- [Support for Mock Regions with
  Data](https://github.com/spring-projects/spring-test-data-geode#mock-regions-with-data)

- [Support for Mocking Region
  Callbacks](https://github.com/spring-projects/spring-test-data-geode#mock-region-callbacks)

- [Support for Mocking Unsupported Region
  Operations](https://github.com/spring-projects/spring-test-data-geode#mocking-unsupported-region-operations)





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
<td class="content">See the documentation on <a
href="https://github.com/spring-projects/spring-test-data-geode/#unit-testing-with-stdg">Unit
Testing with STDG</a> for more details.</td>
</tr>
</tbody>
</table>







### Integration Testing



Integration testing with VMware GemFire in a Spring Boot Test is
as simple as **not** declaring STDG’s `@EnableGemFireMockObjects`
annotation in your test configuration. You may then want to use SBDG’s
`@EnableClusterAware` annotation to conditionally detect the presence of
a VMware GemFire cluster:







Example 2. Using `@EnableClusterAware` in test configuration









``` highlight
@SpringBootApplication
@EnableClusterAware
@EnableEntityDefinedRegions(basePackageClasses = User.class)
static class TestConfiguration { }
```











The SBDG `@EnableClusterAware` annotation conveniently toggles your
auto-configured `ClientCache` instance between local-only mode and
client/server. It even pushes configuration metadata (such as `Region`
definitions) up to the servers in the cluster that are required by the
application to store data.





In most cases, in addition to testing with “live” VMware GemFire
objects (such as Regions), we also want to test in a client/server
capacity. This unlocks the full capabilities of the
VMware GemFire data management system in a Spring context and
gets you as close as possible to production from the comfort of your
IDE.





Building on our example from the section on [Unit
Testing](#geode-testing-unit), you can modify the test to use “live”
VMware GemFire objects in a client/server topology as follows:







Example 3. Integration Test with VMware GemFire using Spring
Boot









``` highlight
@ActiveProfiles("client")
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.data.gemfire.management.use-http=false")
public class SpringBootApacheGeodeIntegrationTest extends ForkingClientServerIntegrationTestsSupport {

    @BeforeClass
    public static void startGeodeServer() throws IOException {
        startGemFireServer(TestGeodeServerConfiguration.class);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    public void saveAndFindUserIsSuccessful() {

        User jonDoe = User.as("jonDoe");

        assertThat(this.userRepository.save(jonDoe)).isNotNull();

        User jonDoeFoundById = this.userRepository.findById(jonDoe.getName()).orElse(null);

        assertThat(jonDoeFoundById).isEqualTo(jonDoe);
        assertThat(jonDoeFoundById).isNotSameAs(jonDoe);
    }

    @SpringBootApplication
    @EnableClusterAware
    @EnableEntityDefinedRegions(basePackageClasses = User.class)
    @Profile("client")
    static class TestGeodeClientConfiguration { }

    @CacheServerApplication
    @Profile("server")
    static class TestGeodeServerConfiguration {

        public static void main(String[] args) {

            new SpringApplicationBuilder(TestGeodeServerConfiguration.class)
                .web(WebApplicationType.NONE)
                .profiles("server")
                .build()
                .run(args);
        }
    }
}

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "as")
@Region("Users")
class User {

    @Id
    @lombok.NonNull
    private String name;

}

interface UserRepository extends CrudRepository<User, String> { }
```











The application client/server-based integration test class extend STDG’s
`org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport`
class. This ensures that all VMware GemFire objects and
resources are properly cleaned up after the test class runs. In
addition, it coordinates the client and server components of the test
(for example connecting the client to the server using a random port).





The VMware GemFire server is started in a `@BeforeClass` setup
method:







Start the VMware GemFire server





``` highlight
class SpringBootApacheGeodeIntegrationTest extends ForkingClientServerIntegrationTestsSupport {

  @BeforeClass
  public static void startGeodeServer() throws IOException {
    startGemFireServer(TestGeodeServerConfiguration.class);
  }
}
```







STDG lets you configure the VMware GemFire server with Spring
configuration, specified in the `TestGeodeServerConfiguration` class.
The Java class needs to provide a `main` method. It uses the
`SpringApplicationBuilder` to bootstrap the VMware GemFire
`CacheServer` application:







Example 4. VMware GemFire server configuration









``` highlight
@CacheServerApplication
@Profile("server")
static class TestGeodeServerConfiguration {

  public static void main(String[] args) {

    new SpringApplicationBuilder(TestGeodeServerConfiguration.class)
      .web(WebApplicationType.NONE)
      .profiles("server")
      .build()
      .run(args);
  }
}
```











In this case, we provide minimal configuration, since the configuration
is determined and pushed up to the server by the client. For example, we
do not need to explicitly create the `Users` `Region` on the server
since it is implicitly handled for you by the SBDG/STDG frameworks from
the client.





We take advantage of Spring profiles in the test setup to distinguish
between the client and server configuration. Keep in mind that the test
is the “client” in this arrangement.





The STDG framework does what the supporting class demands: “forking” the
Spring Boot-based, VMware GemFire `CacheServer` application in a
separate JVM process. Subsequently, the STDG framework stops the server
upon completion of the tests in the test class.





You are free to start your servers or cluster however you choose. STDG
provides this capability as a convenience for you, since it is a common
concern.





This test class is simple. STDG can handle much more complex test
scenarios.





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
<td class="content">Review SBDG’s test suite to witness the full power
and functionality of the STDG framework for yourself.</td>
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
Note
</td>
<td class="content">See the documentation on <a
href="https://github.com/spring-projects/spring-test-data-geode/#integration-testing-with-stdg">Integration
Testing with STDG</a> for more details.</td>
</tr>
</tbody>
</table>











<div id="footer">

<div id="footer-text">

Last updated 2022-10-10 12:14:35 -0700




