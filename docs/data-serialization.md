---
Title: Data Serialization with PDX
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


Anytime data is overflowed or persisted to disk, transferred between
clients and servers, transferred between peers in a cluster or between
different clusters in a multi-site WAN topology, all data stored in
VMware GemFire must be serializable.





To serialize objects in Java, object types must implement the
`java.io.Serializable` interface. However, if you have a large number of
application domain object types that currently do not implement
`java.io.Serializable`, refactoring hundreds or even thousands of class
types to implement `java.io.Serializable` would be a tedious task just
to store and manage those objects in VMware GemFire.





Additionally, it is not only your application domain object types you
necessarily need to consider. If you used third-party libraries in your
application domain model, any types referred to by your application
domain object types stored in VMware GemFire must also be
serializable. This type explosion may bleed into class types for which
you may have no control over.





Furthermore, Java serialization is not the most efficient format, given
that metadata about your types is stored with the data itself.
Therefore, even though Java serialized bytes are more descriptive, it
adds a great deal of overhead.





Then, along came serialization using VMware GemFire's
https://geode.apache.org/docs/guide/115/developing/data_serialization/gemfire_pdx_serialization.html\[PDX\]
format. PDX stands for Portable Data Exchange and achieves four goals:





- Separates type metadata from the data itself, streamlining the bytes
  during transfer. VMware GemFire maintains a type registry that
  stores type metadata about the objects serialized with PDX.

- Supports versioning as your application domain types evolve. It is
  common to have old and new versions of the same application deployed
  to production, running simultaneously, sharing data, and possibly
  using different versions of the same domain types. PDX lets fields be
  added or removed while still preserving interoperability between old
  and new application clients without loss of data.

- Enables objects stored as PDX to be queried without being
  de-serialized. Constant serialization and deserialization of data is a
  resource-intensive task that adds to the latency of each data request
  when redundancy is enabled. Since data is replicated across peers in
  the cluster to preserve High Availability (HA) and must be serialized
  to be transferred, keeping data serialized is more efficient when data
  is updated frequently, since it is likely the data will need to be
  transferred again in order to maintain consistency in the face of
  redundancy and availability.

- Enables interoperability between native language clients (such as C,
  C++ and C#) and Java language clients, with each being able to access
  the same data set regardless from where the data originated.





However, PDX does have limitations.





For instance, unlike Java serialization, PDX does not handle cyclic
dependencies. Therefore, you must be careful how you structure and
design your application domain object types.





Also, PDX cannot handle field type changes.





Furthermore, while VMware GemFire's general
https://geode.apache.org/docs/guide/115/developing/data_serialization/gemfire_data_serialization.html\[Data
Serialization\] handles
https://geode.apache.org/docs/guide/115/developing/delta_propagation/chapter_overview.html\[Deltas\],
this is not achievable without de-serializing the object, since it
involves a method invocation, which defeats one of the key benefits of
PDX: preserving format to avoid the cost of serialization and
deserialization.





However, we think the benefits of using PDX outweigh the limitations
and, therefore, have enabled PDX by default.





You need do nothing special. You can code your domain types and rest
assured that objects of those domain types are properly serialized when
overflowed and persisted to disk, transferred between clients and
servers, transferred between peers in a cluster, and even when data is
transferred over the network when you use VMware GemFire's
multi-site WAN topology.







Example 1. EligibilityDecision is automatically serialiable without
implementing Java Serializable.









``` highlight
@Region("EligibilityDecisions")
class EligibilityDecision {
    // ...
}
```











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
<td class="content">VMware GemFire does
https://geode.apache.org/docs/guide/115/developing/data_serialization/java_serialization.html[support]
the standard Java Serialization format.</td>
</tr>
</tbody>
</table>





### SDG `MappingPdxSerializer` vs. VMware GemFire's `ReflectionBasedAutoSerializer`



Under-the-hood, Spring Boot for VMware GemFire
https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config-pdx\[enables\]
and uses Spring Data for VMware GemFire's
https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/mapping/MappingPdxSerializer.html\[`MappingPdxSerializer`\]
to serialize your application domain objects with PDX.





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
<td class="content">See the SDG
https://docs.spring.io/spring-data/geode/docs/current/reference/html/#mapping.pdx-serializer[Reference Guide]
for more details on the <code>MappingPdxSerializer</code> class.</td>
</tr>
</tbody>
</table>





The `MappingPdxSerializer` class offers several advantages above and
beyond VMware GemFire's own
https://geode.apache.org/releases/latest/javadoc/org/apache/geode/pdx/ReflectionBasedAutoSerializer.html\[`ReflectionBasedAutoSerializer`\]
class.





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
<td class="content">See VMware GemFire's
https://geode.apache.org/docs/guide/115/developing/data_serialization/auto_serialization.html[User
Guide] for more details about the
<code>ReflectionBasedAutoSerializer</code>.</td>
</tr>
</tbody>
</table>





The SDG `MappingPdxSerializer` class offers the following benefits and
capabilities:





- PDX serialization is based on Spring Data’s powerful mapping
  infrastructure and metadata.

- Includes support for both `includes` and `excludes` with first-class
  https://docs.spring.io/spring-data/geode/docs/current/reference/html/#mapping.pdx-serializer.type-filtering\[type
  filtering\]. Additionally, you can implement type filters by using
  Java’s `java.util.function.Predicate` interface as opposed to the
  limited regex capabilities provided by VMware GemFire's
  `ReflectionBasedAutoSerializer` class. By default,
  `MappingPdxSerializer` excludes all types in the following packages:
  `java`, `org.apache.geode`, `org.springframework` and
  `com.gemstone.gemfire`.

- Handles
  https://docs.spring.io/spring-data/geode/docs/current/reference/html/#mapping.pdx-serializer.transient-properties\[transient
  object fields and properties\] when either Java’s `transient` keyword
  or Spring Data’s `@Transient` annotation is used.

- Handles
  https://docs.spring.io/spring-data/geode/docs/current/reference/html/#mapping.pdx-serializer.read-only-properties\[read-only
  object properties\].

- Automatically determines the identifier of your entities when you
  annotate the appropriate entity field or property with Spring Data’s
  https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/annotation/Id.html\[`@Id`\]
  annotation.

- Lets additional `o.a.g.pdx.PdxSerializers` be registered to
  https://docs.spring.io/spring-data/geode/docs/current/reference/html/#mapping.pdx-serializer.custom-serialization\[customize
  the serialization\] of nested entity/object field and property types.





The support for `includes` and `excludes` deserves special attention,
since the `MappingPdxSerializer` excludes all Java, Spring, and
VMware GemFire types, by default. However, what happens when you
need to serialize one of those types?





For example, suppose you need to serialize objects of type
`java.security.Principal`. Then you can override the excludes by
registering an `include` type filter:











``` highlight
package example.app;

import java.security.Principal;

@SpringBootApplication
@EnablePdx(serializerBeanName = "myCustomMappingPdxSerializer")
class SpringBootApacheGeodeClientCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApacheGeodeClientCacheApplication.class, args);
    }

    @Bean
    MappingPdxSerializer myCustomMappingPdxSerializer() {

        MappingPdxSerializer customMappingPdxSerializer =
            MappingPdxSerializer.newMappginPdxSerializer();

        customMappingPdxSerializer.setIncludeTypeFilters(
            type -> Principal.class.isAssignableFrom(type));

        return customMappingPdxSerializer;
    }
}
```











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
<td class="content">Normally, you need not explicitly declare SDG’s
<code>@EnablePdx</code> annotation to enable and configure PDX. However,
if you want to override auto-configuration, as we have demonstrated
above, you must do this.</td>
</tr>
</tbody>
</table>











<div id="footer">

<div id="footer-text">

Last updated 2022-10-10 12:14:04 -0700




