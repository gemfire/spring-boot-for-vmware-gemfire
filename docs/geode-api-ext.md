---
Title: Apache Geode API Extensions
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



When using the Spring programming model and abstractions, it should not
be necessary to use VMware GemFire
https://geode.apache.org/releases/latest/javadoc\[APIs\] at all — for example, when using the
Spring Cache Abstraction for caching or the Spring Data Repository
abstraction for DAO development. There are many more examples.





For certain use cases, users may require low level access to
fine-grained functionally. Spring Boot for VMware GemFire's
`org.springframework.geode:apache-geode-extensions` module and library
builds on VMware GemFire's APIs by including several extensions
with enhanced functionality to offer an experience familiar to Spring
users inside a Spring context.





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
<td class="content">Spring Data for VMware GemFire (SDG) also
https://docs.spring.io/spring-data/geode/docs/current/reference/html/#apis[includes] additional extensions to
VMware GemFire's APIs.</td>
</tr>
</tbody>
</table>





### `SimpleCacheResolver`



In some cases, it is necessary to acquire a reference to the cache
instance in your application components at runtime. For example, you
might want to create a temporary `Region` on the fly to aggregate data
for analysis.





Typically, you already know the type of cache your application is using,
since you must declare your application to be either a client
(`ClientCache`) in the
https://geode.apache.org/docs/guide/115/topologies_and_comm/cs_configuration/chapter_overview.html\[client/server
topology\], or a
https://geode.apache.org/docs/guide/115/topologies_and_comm/p2p_configuration/chapter_overview.html\[peer
member or node\] (`Cache`) in the cluster on startup. This is expressed
in configuration when creating the cache instance required to interact
with the VMware GemFire data management system. In most cases,
your application will be a client. SBDG makes this decision easy, since
it auto-configures a `ClientCache` instance, [by
default](#geode-clientcache-applications).





In a Spring context, the cache instance created by the framework is a
managed bean in the Spring container. You can inject a reference to the
[*Singleton*](https://en.wikipedia.org/wiki/Singleton_pattern) cache
bean into any other managed application component:







Example 1. Autowired Cache Reference using Dependency Injection (DI)









``` highlight
@Service
class CacheMonitoringService {

    @Autowired
    ClientCache clientCache;

    // use the clientCache object reference to monitor the cache as necessary

}
```











However, in cases where your application component or class is not
managed by Spring and you need a reference to the cache instance at
runtime, SBDG provides the abstract
`org.springframework.geode.cache.SimpleCacheResolver` class (see its
{spring-boot-data-geode-javadoc}/org/springframework/geode/cache/SimpleCacheResolver.html\[Javadoc\]).







Example 2. `SimpleCacheResolver` API









``` highlight
package org.springframework.geode.cache;

abstract class SimpleCacheResolver {

    <T extends GemFireCache> T require() { }

    <T extends GemFireCache> Optional<T> resolve() { }

    Optional<ClientCache> resolveClientCache() { }

    Optional<Cache> resolvePeerCache() { }

}
```











`SimpleCacheResolver` adheres to [SOLID OO
Principles](https://en.wikipedia.org/wiki/SOLID). This class is abstract
and extensible so that you can change the algorithm used to resolve
client or peer cache instances as well as mock its methods in unit
tests.





Additionally, each method is precise. For example,
`resolveClientCache()` resolves a reference to a cache only if the cache
instance is a “client.” If a cache exists but is a “peer” cache
instance, `resolveClientCache()` returns `Optional.EMPTY`. The behavior
of `resolvePeerCache()` is similar.





`require()` returns a non-`Optional` reference to a cache instance and
throws an `IllegalStateException` if a cache is not present.







### `CacheUtils`



Under the hood, `SimpleCacheResolver` delegates some of its functions to
the
{spring-boot-data-geode-javadoc}/org/springframework/geode/util/CacheUtils.html\[`CacheUtils`\]
abstract utility class, which provides additional, convenient
capabilities when you use a cache.





While there are utility methods to determine whether a cache instance
(that is, a `GemFireCache`) or Region is a client or a peer, one of the
more useful functions is to extract all the values from a Region.





To extract all the values stored in a Region, call
`CacheUtils.collectValues(:Region<?, T>)`. This method returns a
`Collection<T>` that contains all the values stored in the given
`Region`. The method is smart and knows how to handle the `Region`
appropriately regardless of whether the `Region` is a client or a peer.
This distinction is important, since client `PROXY` Regions store no
values.





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
<td class="content">Caution is advised when you get all values from a
Region. While getting filtered reference values from a
non-transactional, reference data only [<code>REPLICATE</code>] Region
is quite useful, getting all values from a transactional,
[<code>PARTITION</code>] Region can prove quite detrimental, especially
in production. Getting all values from a Region can be useful during
testing.</td>
</tr>
</tbody>
</table>







### `MembershipListenerAdapter` and `MembershipEvent`



Another useful API hidden by VMware GemFire is the membership
events and listener interface. This API is especially useful on the
server side when your Spring Boot application serves as a peer member of
an VMware GemFire distributed system.





When a peer member is disconnected from the distributed system, perhaps
due to a network failure, the member is forcibly removed from the
cluster. This node immediately enters a reconnecting state, trying to
establish a connection back to the cluster. Once reconnected, the peer
member must rebuild all cache objects (`Cache`, `Region` instances,
`Index` instances, `DiskStore` instances, and so on). All previous cache
objects are now invalid, and their references are stale.





In a Spring context, this is particularly problematic since most
VMware GemFire objects are *Singleton* beans declared in and
managed by the Spring container. Those beans may be injected and used in
other framework and application components. For instance, `Region`
instances are injected into SDG’s `GemfireTemplate`, Spring Data
Repositories and possibly application-specific data access objects
([DAOs](https://en.wikipedia.org/wiki/Data_access_object)).





If references to those cache objects become stale on a forced disconnect
event, there is no way to auto-wire fresh object references into the
dependent application or framework components when the peer member is
reconnected, unless the Spring `ApplicationContext` is “refreshed”. In
fact, there is no way to even know that this event has occurred, since
the VMware GemFire `MembershipListener` API and corresponding
events are “internal”.





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
<td class="content">The Spring team explored the idea of creating
proxies for all types of cache objects (<code>Cache</code>,
<code>Region</code>, <code>Index</code>, <code>DiskStore</code>,
<code>AsyncEventQueue</code>, <code>GatewayReceiver</code>,
<code>GatewaySender</code>, and others) used by Spring. The proxies
would know how to obtain a fresh reference on a reconnect event.
However, this turns out to be more problematic than it is worth. It is
easier to “refresh” the Spring <code>ApplicationContext</code>, although
doing so is no less expensive. Neither way is ideal. See <a
href="https://jira.spring.io/browse/SGF-921">SGF-921</a> and <a
href="https://jira.spring.io/browse/SGF-227">SGF-227</a> for further
details.</td>
</tr>
</tbody>
</table>





In the case where membership events are useful to the Spring Boot
application, SBDG provides the following
{spring-boot-data-geode-javadoc}/org/springframework/geode/distributed/event/package-frame.html\[API\]:





- {spring-boot-data-geode-javadoc}/org/springframework/geode/distributed/event/MembershipListenerAdapter.html\[`MembershipListenerAdapter`\]

- {spring-boot-data-geode-javadoc}/org/springframework/geode/distributed/event/MembershipEvent.html\[`MembershipEvent`\]





The abstract `MembershipListenerAdapter` class implements
VMware GemFire's
`org.apache.geode.distributed.internal.MembershipListener` interface to
simplify the event handler method signatures by using an appropriate
`MembershipEvent` type to encapsulate the actors in the event.





The abstract `MembershipEvent` class is further subclassed to represent
specific membership event types that occur within the
VMware GemFire system:





- {spring-boot-data-geode-javadoc}/org/springframework/geode/distributed/event/support/MemberDepartedEvent.html\[`MemberDepartedEvent`\]

- {spring-boot-data-geode-javadoc}/org/springframework/geode/distributed/event/support/MemberJoinedEvent.html\[`MemberJoinedEvent`\]

- {spring-boot-data-geode-javadoc}/org/springframework/geode/distributed/event/support/MemberSuspectEvent.html\[`MemberSuspectEvent`\]

- {spring-boot-data-geode-javadoc}/org/springframework/geode/distributed/event/support/QuorumLostEvent.html\[`QuorumLostEvent`\]





The API is depicted in the following UML diagram:







![membership api uml](./images/membership-api-uml.png)







The membership event type is further categorized with an appropriate
enumerated value,
{spring-boot-data-geode-javadoc}/org/springframework/geode/distributed/event/MembershipEvent.Type.html\[`MembershipEvent.Type`\],
as a property of the `MembershipEvent` itself (see
{spring-boot-data-geode-javadoc}/org/springframework/geode/distributed/event/MembershipEvent.html#getType--\[`getType()`\]).





The type hierarchy is useful in `instanceof` expressions, while the
`Enum` is useful in `switch` statements.





You can see one particular implementation of the
`MembershipListenerAdapter` with the
{spring-boot-data-geode-javadoc}/org/springframework/geode/distributed/event/ApplicationContextMembershipListener.html\[`ApplicationContextMembershipListener`\]
class, which does exactly as we described earlier, handling
forced-disconnect/auto-reconnect membership events inside a Spring
container in order to refresh the Spring `ApplicationContext`.







### PDX



VMware GemFire's PDX serialization framework is yet another API
that falls short of a complete stack.





For instance, there is no easy or direct way to serialize an object as
PDX bytes. It is also not possible to modify an existing `PdxInstance`
by adding or removing fields, since doing so would require a new PDX
type. In this case, you must create a new `PdxInstance` and copy from an
existing `PdxInstance`. Unfortunately, the VMware GemFire API
offers no help in this regard. It is also not possible to use PDX in a
client, local-only mode without a server, since the PDX type registry is
only available and managed on servers in a cluster.





#### `PdxInstanceBuilder`



In such cases, SBDG conveniently provides the
{spring-boot-data-geode-javadoc}/org/springframework/geode/pdx/PdxInstanceBuilder.html\[`PdxInstanceBuilder`\]
class, appropriately named after the [Builder software design
pattern](https://en.wikipedia.org/wiki/Builder_pattern). The
`PdxInstanceBuilder` also offers a fluent API for constructing
`PdxInstances`:







Example 3. `PdxInstanceBuilder` API









``` highlight
class PdxInstanceBuilder {

    PdxInstanceFactory copy(PdxInstance pdx);

    Factory from(Object target);

}
```











For example, you could serialize an application domain object as PDX
bytes with the following code:







Example 4. Serializing an Object to PDX









``` highlight
@Component
class CustomerSerializer {

    PdxInstance serialize(Customer customer) {

        return PdxInstanceBuilder.create()
            .from(customer)
            .create();
    }
}
```











You could then modify the `PdxInstance` by copying from the original:







Example 5. Copy `PdxInstance`









``` highlight
@Component
class CustomerDecorator {

    @Autowired
    CustomerSerializer serializer;

    PdxIntance decorate(Customer customer) {

        PdxInstance pdxCustomer = serializer.serialize(customer);

        return PdxInstanceBuilder.create()
            .copy(pdxCustomer)
            .writeBoolean("vip", isImportant(customer))
            .create();
    }
}
```













#### `PdxInstanceWrapper`



SBDG also provides the
{spring-boot-data-geode-javadoc}/org/springframework/geode/pdx/PdxInstanceWrapper.html\[`PdxInstanceWrapper`\]
class to wrap an existing `PdxInstance` in order to provide more control
during the conversion from PDX to JSON and from JSON back into a POJO.
Specifically, the wrapper gives you more control over the configuration
of Jackson’s `ObjectMapper`.





The `ObjectMapper` constructed by VMware GemFire's own
`PdxInstance` implementation (`PdxInstanceImpl`) is not configurable,
nor was it configured correctly. Unfortunately, since `PdxInstance` is
not extensible, the `getObject()` method fails when converting the JSON
generated from PDX back into a POJO for any practical application domain
model type.





The following example wraps an existing `PdxInstance`:







Example 6. Wrapping an existing `PdxInstance`









``` highlight
PdxInstanceWrapper wrapper = PdxInstanceWrapper.from(pdxInstance);
```











For all operations on `PdxInstance` except `getObject()`, the wrapper
delegates to the underlying `PdxInstance` method implementation called
by the user.





In addition to the decorated `getObject()` method, the
`PdxInstanceWrapper` provides a thorough implementation of the
`toString()` method. The state of the `PdxInstance` is output in a
JSON-like `String`.





Finally, the `PdxInstanceWrapper` class adds a `getIdentifier()` method.
Rather than put the burden on the user to have to iterate the field
names of the `PdxInstance` to determine whether a field is the identity
field and then call `getField(name)` with the field name to get the ID
(value) — assuming an identity field was marked in the first place — the
`PdxInstanceWrapper` class provides the `getIdentifier()` method to
return the ID of the `PdxInstance` directly.





The `getIdentifier()` method is smart in that it first iterates the
fields of the `PdxInstance`, asking each field if it is the identity
field. If no field was marked as the identity field, the algorithm
searches for a field named `id`. If no field with the name `id` exists,
the algorithm searches for a metadata field called `@identifier`, which
refers to the field that is the identity field of the `PdxInstance`.





The `@identifier` metadata field is useful in cases where the
`PdxInstance` originated from JSON and the application domain object
uses a natural identifier, rather than a surrogate ID, such as
`Book.isbn`.





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
<td class="content">VMware GemFire's <code>JSONFormatter</code>
class is not capable of marking the identity field of a
<code>PdxInstance</code> originating from JSON.</td>
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
Warning
</td>
<td class="content">It is not currently possible to implement the
<code>PdxInstance</code> interface and store instances of this type as a
value in a Region. VMware GemFire assumes all
<code>PdxInstance</code> objects are an implementation created by
VMware GemFire itself (that is, <code>PdxInstanceImpl</code>),
which has a tight coupling to the PDX type registry. An
<code>Exception</code> is thrown if you try to store instances of your
own <code>PdxInstance</code> implementation.</td>
</tr>
</tbody>
</table>







#### `ObjectPdxInstanceAdapter`



In rare cases, you may need to treat an `Object` as a `PdxInstance`,
depending on the context without incurring the overhead of serializing
an `Object` to PDX. For such cases, SBDG offers the
`ObjectPdxInstanceAdapter` class.





This might be true when calling a method with a parameter expecting an
argument of, or returning an instance of, type `PdxInstance`,
particularly when VMware GemFire's `read-serialized` PDX
configuration property is set to `true` and only an object is available
in the current context.





Under the hood, SBDG’s `ObjectPdxInstanceAdapter` class uses Spring’s
https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/beans/BeanWrapper.html\[`BeanWrapper`\]
class along with Java’s introspection and reflection functionality to
adapt the given `Object` and access it with the full
https://geode.apache.org/releases/latest/javadoc/org/apache/geode/pdx/PdxInstance.html\[`PdxInstance`\]
API. This includes the use of the
https://geode.apache.org/releases/latest/javadoc/org/apache/geode/pdx/WritablePdxInstance.html\[`WritablePdxInstance`\]
API, obtained from
https://geode.apache.org/releases/latest/javadoc/org/apache/geode/pdx/PdxInstance.html#createWriter--\[`PdxInstance.createWriter()`\],
to modify the underlying `Object` as well.





Like the `PdxInstanceWrapper` class, `ObjectPdxInstanceAdapter` contains
special logic to resolve the identity field and ID of the `PdxInstance`,
including consideration for Spring Data’s
https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/annotation/Id.html\[`@Id`\]
mapping annotation, which can be introspected in this case, given that
the underlying `Object` backing the `PdxInstance` is a POJO.





The `ObjectPdxInstanceAdapter.getObject()` method returns the wrapped
`Object` used to construct the `ObjectPdxInstanceAdapter` and is,
therefore, automatically deserializable, as determined by the
https://geode.apache.org/releases/latest/javadoc/org/apache/geode/pdx/PdxInstance.html#isDeserializable--\[`PdxInstance.isDeseriable()`\]
method, which always returns `true`.





You can adapt any `Object` as a `PdxInstance`:







Example 7. Adapt an `Object` as a `PdxInstance`









``` highlight
class OfflineObjectToPdxInstanceConverter {

    @NonNull PdxInstance convert(@NonNull Object target) {
        return ObjectPdxInstanceAdapter.from(target);
    }
}
```











Once the [Adapter](https://en.wikipedia.org/wiki/Adapter_pattern) is
created, you can use it to access data on the underlying `Object`.





Consider the following example of a `Customer` class:







Example 8. `Customer` class









``` highlight
@Region("Customers")
class Customer {

    @Id
    private Long id;

    String name;

    // constructors, getters and setters omitted

}
```











Then you can access an instance of `Customer` by using the `PdxInstance`
API:







Example 9. Accessing an `Object` using the `PdxInstance` API









``` highlight
class ObjectPdxInstanceAdapterTest {

    @Test
    public void getAndSetObjectProperties() {

        Customer jonDoe = new Customer(1L, "Jon Doe");

        PdxInstance adapter = ObjectPdxInstanceAdapter.from(jonDoe);

        assertThat(jonDoe.getName()).isEqualTo("Jon Doe");
        assertThat(adapter.getField("name")).isEqualTo("Jon Doe");

        adapter.createWriter().setField("name", "Jane Doe");

        assertThat(adapter.getField("name")).isEqualTo("Jane Doe");
        assertThat(jonDoe.getName()).isEqualTo("Jane Doe");
    }
}
```















### Security



For testing purposes, SBDG provides a test implementation of
VMware GemFire's
https://geode.apache.org/releases/latest/javadoc/org/apache/geode/security/SecurityManager.html\[`SecurityManager`\]
interface, which expects the password to match the username
(case-sensitive) when authenticating.





By default, all operations are authorized.





To match the expectations of SBDG’s `TestSecurityManager`, SBDG
additionally provides a test implementation of VMware GemFire's
https://geode.apache.org/releases/latest/javadoc/org/apache/geode/security/AuthInitialize.html\[`AuthInitialize`\]
interface, which supplies matching credentials for both the username and
password.











<div id="footer">

<div id="footer-text">

Last updated 2022-10-10 12:14:46 -0700




