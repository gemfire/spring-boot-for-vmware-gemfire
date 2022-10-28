---
title: Spring Boot Actuator
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

Spring Boot for VMware GemFire (SBDG) adds
[Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready.html)
support and dedicated `HealthIndicators` for VMware GemFire.
Equally, the provided `HealthIndicators` even work with Tanzu Cache
(which is backed by VMware GemFire) when you push your Spring
Boot applications using VMware GemFire to VMware Tanzu
Application Service (TAS) platform.

Spring Boot `HealthIndicators` provide details about the runtime
operation and behavior of your VMware GemFire-based Spring Boot
applications. For instance, by querying the right `HealthIndicator`
endpoint, you can get the current hit/miss count for your
`Region.get(key)` data access operations.

In addition to vital health information, SBDG provides basic,
pre-runtime configuration metadata about the VMware GemFire
components that are monitored by Spring Boot Actuator. This makes it
easier to see how the application was configured all in one place,
rather than in properties files, Spring configuration, XML, and so on.

The provided Spring Boot `HealthIndicators` fall into three categories:

- Base `HealthIndicators` that apply to all VMware GemFire,
  Spring Boot applications, regardless of cache type, such as `Regions`,
  `Indexes`, and `DiskStores`.

- Peer `Cache`-based `HealthIndicators` that apply only to peer `Cache`
  applications, such as `AsyncEventQueues`, `CacheServers`,
  `GatewayReceivers`, and `GatewaySenders`.

- `ClientCache`-based `HealthIndicators` that apply only to
  `ClientCache` applications, such as `ContinuousQuery` and connection
  `Pools`.

The following sections give a brief overview of all the available Spring
Boot `HealthIndicators` provided for VMware GemFire.

See the corresponding sample <a
href="guides/boot-actuator.html">guide</a> and
 https://github.com/spring-projects/spring-boot-data-geode/tree/1.7.4/spring-geode-samples/intro/getting-started/boot/actuator[code] to see Spring Boot Actuator for
VMware GemFire in action.


### Base HealthIndicators

This section covers Spring Boot `HealthIndicators` that apply to both
VMware GemFire peer `Cache` and `ClientCache`, Spring Boot
applications. That is, these `HealthIndicators` are not specific to the
cache type.

In VMware GemFire, the cache instance is either a peer `Cache`
instance (which makes your Spring Boot application part of a
VMware GemFire cluster) or, more commonly, a `ClientCache`
instance (which talks to an existing cluster). Your Spring Boot
application can only be one cache type or the other and can only have a
single instance of that cache type.

#### GeodeCacheHealthIndicator

`GeodeCacheHealthIndicator` provides essential details about the
(single) cache instance (client or peer) and the underlying
`DistributedSystem`, the `DistributedMember` and configuration details
of the `ResourceManager`.

When your Spring Boot application creates an instance of a peer
[`Cache`](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/Cache.html), the
[`DistributedMember`](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/distributed/DistributedMember.html)
object represents your application as a peer member or node of the
[`DistributedSystem`](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/distributed/DistributedSystem.html).
The distributed system (that is, the cluster) is formed from a
collection of connected peers, to which your application also has
[access](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/GemFireCache.html#getDistributedSystem) — indirectly,
through the cache instance.

This is no different for a `ClientCache` even though the client is
technically not part of the peer/server cluster. However, it still
creates instances of the `DistributedSystem` and `DistributedMember`
objects, respectively.

Each object has the following configuration metadata and health details:

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 1. Cache Details</caption>
<colgroup>
<col style="width: 23%" />
<col style="width: 76%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-center valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.name</p></td>
<td class="tableblock halign-left valign-top"><p>Name of the member in
the distributed system.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.cache.closed</p></td>
<td class="tableblock halign-left valign-top"><p>Determines whether the
cache has been closed.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.cancel-in-progress</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether
cancellation of operations is in progress.</p></td>
</tr>
</tbody>
</table>

Table 1. Cache Details

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 2. DistributedMember Details</caption>
<colgroup>
<col style="width: 23%" />
<col style="width: 76%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-center valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.distributed-member.id</p></td>
<td
class="tableblock halign-left valign-top"><p><code>DistributedMember</code>
identifier (used in logs internally).</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.distributed-member.name</p></td>
<td class="tableblock halign-left valign-top"><p>Name of the member in
the distributed system.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.distributed-members.groups</p></td>
<td class="tableblock halign-left valign-top"><p>Configured groups to
which the member belongs.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.distributed-members.host</p></td>
<td class="tableblock halign-left valign-top"><p>Name of the machine on
which the member is running.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.distributed-members.process-id</p></td>
<td class="tableblock halign-left valign-top"><p>Identifier of the JVM
process (PID).</p></td>
</tr>
</tbody>
</table>

Table 2. DistributedMember Details

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 3. DistributedSystem Details</caption>
<colgroup>
<col style="width: 23%" />
<col style="width: 76%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-center valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.distributed-system.connected</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether the
member is currently connected to the cluster.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.distributed-system.member-count</p></td>
<td class="tableblock halign-left valign-top"><p>Total number of members
in the cluster (1 for clients).</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.distributed-system.reconnecting</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether the
member is in a reconnecting state, which happens when a network
partition occurs and the member gets disconnected from the
cluster.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.distributed-system.properties-location</p></td>
<td class="tableblock halign-left valign-top"><p>Location of the
https://geode.apache.org/docs/guide/115/topics/gemfire_properties.html[standard
configuration properties].</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.distributed-system.security-properties-location</p></td>
<td class="tableblock halign-left valign-top"><p>Location of the
https://geode.apache.org/docs/guide/115/topics/gemfire_properties.html[security
configuration properties].</p></td>
</tr>
</tbody>
</table>

Table 3. DistributedSystem Details

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 4. ResourceManager Details</caption>
<colgroup>
<col style="width: 23%" />
<col style="width: 76%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-center valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.resource-manager.critical-heap-percentage</p></td>
<td class="tableblock halign-left valign-top"><p>Percentage of heap at
which the cache is in danger of becoming inoperable.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.resource-manager.critical-off-heap-percentage</p></td>
<td class="tableblock halign-left valign-top"><p>Percentage of off-heap
at which the cache is in danger of becoming inoperable.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.resource-manager.eviction-heap-percentage</p></td>
<td class="tableblock halign-left valign-top"><p>Percentage of heap at
which eviction begins on Regions configured with a heap LRU eviction
policy.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.resource-manager.eviction-off-heap-percentage</p></td>
<td class="tableblock halign-left valign-top"><p>Percentage of off-heap
at which eviction begins on Regions configured with a heap LRU eviction
policy.</p></td>
</tr>
</tbody>
</table>

Table 4. ResourceManager Details

#### GeodeRegionsHealthIndicator

`GeodeRegionsHealthIndicator` provides details about all the configured
and known `Regions` in the cache. If the cache is a client, details
include all `LOCAL`, `PROXY`, and `CACHING_PROXY` `Regions`. If the
cache is a peer then details include all `LOCAL`, `PARTITION`, and
`REPLICATE` `Region` instances.

The following table describes the essential details and basic
performance metrics:

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 5. Region Details</caption>
<colgroup>
<col style="width: 23%" />
<col style="width: 76%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-center valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.regions.&lt;name&gt;.cloning-enabled</p></td>
<td class="tableblock halign-left valign-top"><p>Whether Region values
are cloned on read (for example, <code>cloning-enabled</code> is
<code>true</code> when cache transactions are used to prevent in-place
modifications).</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.cache.regions.&lt;name&gt;.data-policy</p></td>
<td class="tableblock halign-left valign-top"><p>Policy used to manage
data in the Region (<code>PARTITION</code>, <code>REPLICATE</code>, and
others).</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.regions.&lt;name&gt;.initial-capacity</p></td>
<td class="tableblock halign-left valign-top"><p>Initial number of
entries that can be held by a Region before it needs to be
resized.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.cache.regions.&lt;name&gt;.load-factor</p></td>
<td class="tableblock halign-left valign-top"><p>Load factor used to
determine when to resize the Region when it nears capacity.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.regions.&lt;name&gt;.key-constraint</p></td>
<td class="tableblock halign-left valign-top"><p>Type constraint for
Region keys.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.cache.regions.&lt;name&gt;.off-heap</p></td>
<td class="tableblock halign-left valign-top"><p>Determines whether this
Region stores values in off-heap memory (NOTE: Keys are always kept on
the JVM heap).</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.regions.&lt;name&gt;.pool-name</p></td>
<td class="tableblock halign-left valign-top"><p>If this Region is a
client Region, this property determines the configured connection
<code>Pool</code>. (NOTE: Regions can have and use dedicated
<code>Pools</code> for their data access operations.)</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.cache.regions.&lt;name&gt;.pool-name</p></td>
<td class="tableblock halign-left valign-top"><p>Determines the
<code>Scope</code> of the Region, which plays a factor in the Region’s
consistency-level, as it pertains to acknowledgements for
writes.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.regions.&lt;name&gt;.value-constraint</p></td>
<td class="tableblock halign-left valign-top"><p>Type constraint for
Region values.</p></td>
</tr>
</tbody>
</table>

Table 5. Region Details

The following details also apply when the Region is a peer `Cache`
`PARTITION` Region:

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 6. Partition Region Details</caption>
<colgroup>
<col style="width: 23%" />
<col style="width: 76%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-center valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.regions.&lt;name&gt;.partition.collocated-with</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether this
Region is collocated with another <code>PARTITION</code> Region, which
is necessary when performing equi-joins queries (NOTE: distributed joins
are not supported).</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.cache.regions.&lt;name&gt;.partition.local-max-memory</p></td>
<td class="tableblock halign-left valign-top"><p>Total amount of heap
memory allowed to be used by this Region on this node.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.regions.&lt;name&gt;.partition.redundant-copies</p></td>
<td class="tableblock halign-left valign-top"><p>Number of replicas for
this <code>PARTITION</code> Region, which is useful in high availability
(HA) use cases.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.cache.regions.&lt;name&gt;.partition.total-max-memory</p></td>
<td class="tableblock halign-left valign-top"><p>Total amount of heap
memory allowed to be used by this Region across all nodes in the cluster
hosting this Region.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.regions.&lt;name&gt;.partition.total-number-of-buckets</p></td>
<td class="tableblock halign-left valign-top"><p>Total number of buckets
(shards) into which this Region is divided (defaults to 113).</p></td>
</tr>
</tbody>
</table>

Table 6. Partition Region Details

Finally, when statistics are enabled (for example, when you use
`@EnableStatistics` — (see
[doc](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config-statistics)
for more details), the following metadata is available:

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 7. Region Statistic Details</caption>
<colgroup>
<col style="width: 23%" />
<col style="width: 76%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-center valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.regions.&lt;name&gt;.statistics.hit-count</p></td>
<td class="tableblock halign-left valign-top"><p>Number of hits for a
region entry.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.cache.regions.&lt;name&gt;.statistics.hit-ratio</p></td>
<td class="tableblock halign-left valign-top"><p>Ratio of hits to the
number of <code>Region.get(key)</code> calls.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.regions.&lt;name&gt;.statistics.last-accessed-time</p></td>
<td class="tableblock halign-left valign-top"><p>For an entry, indicates
the last time it was accessed with
<code>Region.get(key)</code>.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.cache.regions.&lt;name&gt;.statistics.last-modified-time</p></td>
<td class="tableblock halign-left valign-top"><p>For an entry, indicates
the time when a Region’s entry value was last modified.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.regions.&lt;name&gt;.statistics.miss-count</p></td>
<td class="tableblock halign-left valign-top"><p>Returns the number of
times that a <code>Region.get</code> was performed and no value was
found locally.</p></td>
</tr>
</tbody>
</table>

Table 7. Region Statistic Details

#### GeodeIndexesHealthIndicator

`GeodeIndexesHealthIndicator` provides details about the configured
Region `Indexes` used by OQL query data access operations.

The following details are covered:

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 8. Index Details</caption>
<colgroup>
<col style="width: 23%" />
<col style="width: 76%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-center valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.index.&lt;name&gt;.from-clause</p></td>
<td class="tableblock halign-left valign-top"><p>Region from which data
is selected.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.index.&lt;name&gt;.indexed-expression</p></td>
<td class="tableblock halign-left valign-top"><p>Region value fields and
properties used in the Index expression.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.index.&lt;name&gt;.projection-attributes</p></td>
<td class="tableblock halign-left valign-top">For <code>Map</code>
<code>Indexes</code>, returns either
or the specific Map keys that were indexed. For all other
<code>Indexes</code>, returns .</td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.index.&lt;name&gt;.region</p></td>
<td class="tableblock halign-left valign-top"><p>Region to which the
Index is applied.</p></td>
</tr>
</tbody>
</table>

Table 8. Index Details

Additionally, when statistics are enabled (for example, when you use
`@EnableStatistics` — see
[Configuring Statistics](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config-statistics)
for more details), the following metadata is available:

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 9. Index Statistic Details</caption>
<colgroup>
<col style="width: 23%" />
<col style="width: 76%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-center valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.index.&lt;name&gt;.statistics.number-of-bucket-indexes</p></td>
<td class="tableblock halign-left valign-top"><p>Number of bucket
Indexes created in a PARTITION Region.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.index.&lt;name&gt;.statistics.number-of-keys</p></td>
<td class="tableblock halign-left valign-top"><p>Number of keys in this
Index.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.index.&lt;name&gt;.statistics.number-of-map-indexed-keys</p></td>
<td class="tableblock halign-left valign-top"><p>Number of keys in this
Index at the highest level.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.index.&lt;name&gt;.statistics.number-of-values</p></td>
<td class="tableblock halign-left valign-top"><p>Number of values in
this Index.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.index.&lt;name&gt;.statistics.number-of-updates</p></td>
<td class="tableblock halign-left valign-top"><p>Number of times this
Index has been updated.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.index.&lt;name&gt;.statistics.read-lock-count</p></td>
<td class="tableblock halign-left valign-top"><p>Number of read locks
taken on this Index.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.index.&lt;name&gt;.statistics.total-update-time</p></td>
<td class="tableblock halign-left valign-top"><p>Total amount of time
(ns) spent updating this Index.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.index.&lt;name&gt;.statistics.total-uses</p></td>
<td class="tableblock halign-left valign-top"><p>Total number of times
this Index has been accessed by an OQL query.</p></td>
</tr>
</tbody>
</table>

Table 9. Index Statistic Details

#### GeodeDiskStoresHealthIndicator

The `GeodeDiskStoresHealthIndicator` provides details about the
configured `DiskStores` in the system or application. Remember,
`DiskStores` are used to overflow and persist data to disk, including
type metadata tracked by PDX when the values in the Regions have been
serialized with PDX and the Regions are persistent.

Most of the tracked health information pertains to configuration:

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 10. DiskStore Details</caption>
<colgroup>
<col style="width: 23%" />
<col style="width: 76%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-center valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.disk-store.&lt;name&gt;.allow-force-compaction</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether
manual compaction of the DiskStore is allowed.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.disk-store.&lt;name&gt;.auto-compact</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether
compaction occurs automatically.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.disk-store.&lt;name&gt;.compaction-threshold</p></td>
<td class="tableblock halign-left valign-top"><p>Percentage at which the
oplog becomes compactible.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.disk-store.&lt;name&gt;.disk-directories</p></td>
<td class="tableblock halign-left valign-top"><p>Location of the oplog
disk files.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.disk-store.&lt;name&gt;.disk-directory-sizes</p></td>
<td class="tableblock halign-left valign-top"><p>Configured and allowed
sizes (MB) for the disk directory that stores the disk files.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.disk-store.&lt;name&gt;.disk-usage-critical-percentage</p></td>
<td class="tableblock halign-left valign-top"><p>Critical threshold of
disk usage proportional to the total disk volume.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.disk-store.&lt;name&gt;.disk-usage-warning-percentage</p></td>
<td class="tableblock halign-left valign-top"><p>Warning threshold of
disk usage proportional to the total disk volume.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.disk-store.&lt;name&gt;.max-oplog-size</p></td>
<td class="tableblock halign-left valign-top"><p>Maximum size (MB)
allowed for a single oplog file.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.disk-store.&lt;name&gt;.queue-size</p></td>
<td class="tableblock halign-left valign-top"><p>Size of the queue used
to batch writes that are flushed to disk.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.disk-store.&lt;name&gt;.time-interval</p></td>
<td class="tableblock halign-left valign-top"><p>Time to wait (ms)
before writes are flushed to disk from the queue if the size limit has
not be reached.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.disk-store.&lt;name&gt;.uuid</p></td>
<td class="tableblock halign-left valign-top"><p>Universally unique
identifier for the DiskStore across a distributed system.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.disk-store.&lt;name&gt;.write-buffer-size</p></td>
<td class="tableblock halign-left valign-top"><p>Size the of write
buffer the DiskStore uses to write data to disk.</p></td>
</tr>
</tbody>
</table>

Table 10. DiskStore Details

### `ClientCache` `HealthIndicators`

The `ClientCache`-based `HealthIndicators` provide additional details
specifically for Spring Boot, cache client applications. These
`HealthIndicators` are available only when the Spring Boot application
creates a `ClientCache` instance (that is, the application is a cache
client), which is the default.

#### GeodeContinuousQueriesHealthIndicator

`GeodeContinuousQueriesHealthIndicator` provides details about
registered client Continuous Queries (CQs). CQs let client applications
receive automatic notification about events that satisfy some criteria.
That criteria can be easily expressed by using the predicate of an OQL
query (for example, `SELECT * FROM /Customers c WHERE c.age > 21`). When
data is inserted or updated and the data matches the criteria specified
in the OQL query predicate (data of interests), an event is sent to the
registered client.

The following details are covered for CQs by name:

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 11. Continuous Query (CQ) Details</caption>
<colgroup>
<col style="width: 23%" />
<col style="width: 76%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-center valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.continuous-query.&lt;name&gt;.oql-query-string</p></td>
<td class="tableblock halign-left valign-top"><p>OQL query constituting
the CQ.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.continuous-query.&lt;name&gt;.closed</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether the
CQ has been closed.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.continuous-query.&lt;name&gt;.closing</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether the
CQ is in the process of closing.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.continuous-query.&lt;name&gt;.durable</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether the
CQ events are remembered between client sessions.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.continuous-query.&lt;name&gt;.running</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether the
CQ is currently running.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.continuous-query.&lt;name&gt;.stopped</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether the
CQ has been stopped.</p></td>
</tr>
</tbody>
</table>

Table 11. Continuous Query (CQ) Details

In addition, the following CQ query and statistical data is covered:

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 12. Continuous Query (CQ), Query Details</caption>
<colgroup>
<col style="width: 23%" />
<col style="width: 76%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-center valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.continuous-query.&lt;name&gt;.query.number-of-executions</p></td>
<td class="tableblock halign-left valign-top"><p>Total number of times
the query has been executed.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.continuous-query.&lt;name&gt;.query.total-execution-time</p></td>
<td class="tableblock halign-left valign-top"><p>Total amount of time
(ns) spent executing the query.</p></td>
</tr>
</tbody>
</table>

Table 12. Continuous Query (CQ), Query Details

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 13. Continuous Query(CQ), Statistic Details</caption>
<colgroup>
<col style="width: 23%" />
<col style="width: 76%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-center valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.continuous-query.&lt;name&gt;.statistics.number-of-deletes</p></td>
<td class="tableblock halign-left valign-top"><p>Number of delete events
qualified by this CQ.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.continuous-query.&lt;name&gt;.statistics.number-of-events</p></td>
<td class="tableblock halign-left valign-top"><p>Total number of events
qualified by this CQ.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.continuous-query.&lt;name&gt;.statistics.number-of-inserts</p></td>
<td class="tableblock halign-left valign-top"><p>Number of insert events
qualified by this CQ.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.continuous-query.&lt;name&gt;.statistics.number-of-updates</p></td>
<td class="tableblock halign-left valign-top"><p>Number of update events
qualified by this CQ.</p></td>
</tr>
</tbody>
</table>

Table 13. Continuous Query(CQ), Statistic Details

The VMware GemFire Continuous Query system is also tracked with
the following additional details on the client:

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 14. Continuous Query (CQ), Additional Statistic
Details</caption>
<colgroup>
<col style="width: 23%" />
<col style="width: 76%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-center valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.continuous-query.count</p></td>
<td class="tableblock halign-left valign-top"><p>Total count of
CQs.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.continuous-query.number-of-active</p></td>
<td class="tableblock halign-left valign-top"><p>Number of currently
active CQs (if available).</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.continuous-query.number-of-closed</p></td>
<td class="tableblock halign-left valign-top"><p>Total number of closed
CQs (if available).</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.continuous-query.number-of-created</p></td>
<td class="tableblock halign-left valign-top"><p>Total number of created
CQs (if available).</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.continuous-query.number-of-stopped</p></td>
<td class="tableblock halign-left valign-top"><p>Number of currently
stopped CQs (if available).</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.continuous-query.number-on-client</p></td>
<td class="tableblock halign-left valign-top"><p>Number of CQs that are
currently active or stopped (if available).</p></td>
</tr>
</tbody>
</table>

Table 14. Continuous Query (CQ), Additional Statistic Details

#### GeodePoolsHealthIndicator

`GeodePoolsHealthIndicator` provides details about all the configured
client connection `Pools`. This `HealthIndicator` primarily provides
configuration metadata for all the configured `Pools`.

The following details are covered:

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 15. Pool Details</caption>
<colgroup>
<col style="width: 23%" />
<col style="width: 76%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-center valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.pool.count</p></td>
<td class="tableblock halign-left valign-top"><p>Total number of client
connection pools.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.destroyed</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether the
pool has been destroyed.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.free-connection-timeout</p></td>
<td class="tableblock halign-left valign-top"><p>Configured amount of
time to wait for a free connection from the Pool.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.idle-timeout</p></td>
<td class="tableblock halign-left valign-top"><p>The amount of time to
wait before closing unused, idle connections, not exceeding the
configured number of minimum required connections.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.load-conditioning-interval</p></td>
<td class="tableblock halign-left valign-top"><p>How frequently the Pool
checks to see whether a connection to a given server should be moved to
a different server to improve the load balance.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.locators</p></td>
<td class="tableblock halign-left valign-top"><p>List of configured
Locators.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.max-connections</p></td>
<td class="tableblock halign-left valign-top"><p>Maximum number of
connections obtainable from the Pool.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.min-connections</p></td>
<td class="tableblock halign-left valign-top"><p>Minimum number of
connections contained by the Pool.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.multi-user-authentication</p></td>
<td class="tableblock halign-left valign-top"><p>Determines whether the
Pool can be used by multiple authenticated users.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.online-locators</p></td>
<td class="tableblock halign-left valign-top"><p>Returns a list of
living Locators.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.pending-event-count</p></td>
<td class="tableblock halign-left valign-top"><p>Approximate number of
pending subscription events maintained at the server for this durable
client Pool at the time it (re)connected to the server.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.ping-interval</p></td>
<td class="tableblock halign-left valign-top"><p>How often to ping the
servers to verify they are still alive.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.pr-single-hop-enabled</p></td>
<td class="tableblock halign-left valign-top"><p>Whether the client
acquires a direct connection to the server.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.read-timeout</p></td>
<td class="tableblock halign-left valign-top"><p>Number of milliseconds
to wait for a response from a server before timing out the operation and
trying another server (if any are available).</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.retry-attempts</p></td>
<td class="tableblock halign-left valign-top"><p>Number of times to
retry a request after a timeout or an exception.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.server-group</p></td>
<td class="tableblock halign-left valign-top"><p>All servers must belong
to the same group, and this value sets the name of that group.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.servers</p></td>
<td class="tableblock halign-left valign-top"><p>List of configured
servers.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.socket-buffer-size</p></td>
<td class="tableblock halign-left valign-top"><p>Socket buffer size for
each connection made in this pool.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.statistic-interval</p></td>
<td class="tableblock halign-left valign-top"><p>How often to send
client statistics to the server.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.subscription-ack-interval</p></td>
<td class="tableblock halign-left valign-top"><p>Interval in
milliseconds to wait before sending acknowledgements to the cache server
for events received from the server subscriptions.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.subscription-enabled</p></td>
<td class="tableblock halign-left valign-top"><p>Enabled
server-to-client subscriptions.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.subscription-message-tracking-timeout</p></td>
<td class="tableblock halign-left valign-top"><p>Time-to-Live (TTL)
period (ms) for subscription events the client has received from the
server.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.subscription-redundancy</p></td>
<td class="tableblock halign-left valign-top"><p>Redundancy level for
this Pool’s server-to-client subscriptions, which is used to ensure
clients do not miss potentially important events.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.pool.&lt;name&gt;.thread-local-connections</p></td>
<td class="tableblock halign-left valign-top"><p>Thread local connection
policy for this Pool.</p></td>
</tr>
</tbody>
</table>

Table 15. Pool Details

### Peer Cache HealthIndicators

The peer `Cache`-based `HealthIndicators` provide additional details
specifically for Spring Boot peer cache member applications. These
`HealthIndicators` are available only when the Spring Boot application
creates a peer `Cache` instance.

<p class="note><strong>Note:</strong>
The default cache instance created by Spring Boot
for VMware GemFire is a <code>ClientCache</code> instance.
</p>

To control what type of cache instance is created,
such as a “peer”, you can explicitly declare either the
<code>@PeerCacheApplication</code> or, alternatively, the
<code>@CacheServerApplication</code> annotation on your
<code>@SpringBootApplication</code>-annotated class.


#### GeodeCacheServersHealthIndicator

The `GeodeCacheServersHealthIndicator` provides details about the
configured VMware GemFire `CacheServer` instances. `CacheServer`
instances are required to enable clients to connect to the servers in
the cluster.

This `HealthIndicator` captures basic configuration metadata and the
runtime behavior and characteristics of the configured `CacheServer`
instances:

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 16. CacheServer Details</caption>
<colgroup>
<col style="width: 23%" />
<col style="width: 76%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-center valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.server.count</p></td>
<td class="tableblock halign-left valign-top"><p>Total number of
configured <code>CacheServer</code> instances on this peer
member.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.cache.server.&lt;index&gt;.bind-address</p></td>
<td class="tableblock halign-left valign-top"><p>IP address of the NIC
to which the <code>CacheServer</code> <code>ServerSocket</code> is bound
(useful when the system contains multiple NICs).</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.server.&lt;index&gt;.hostname-for-clients</p></td>
<td class="tableblock halign-left valign-top"><p>Name of the host used
by clients to connect to the <code>CacheServer</code> (useful with
DNS).</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.cache.server.&lt;index&gt;.load-poll-interval</p></td>
<td class="tableblock halign-left valign-top"><p>How often (ms) to query
the load probe on the <code>CacheServer</code>.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.server.&lt;index&gt;.max-connections</p></td>
<td class="tableblock halign-left valign-top"><p>Maximum number of
connections allowed to this <code>CacheServer</code>.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.cache.server.&lt;index&gt;.max-message-count</p></td>
<td class="tableblock halign-left valign-top"><p>Maximum number of
messages that can be put in a client queue.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.server.&lt;index&gt;.max-threads</p></td>
<td class="tableblock halign-left valign-top"><p>Maximum number of
threads allowed in this <code>CacheServer</code> to service client
requests.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.cache.server.&lt;index&gt;.max-time-between-pings</p></td>
<td class="tableblock halign-left valign-top"><p>Maximum time between
client pings.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.server.&lt;index&gt;.message-time-to-live</p></td>
<td class="tableblock halign-left valign-top"><p>Time (seconds) in which
the client queue expires.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.cache.server.&lt;index&gt;.port</p></td>
<td class="tableblock halign-left valign-top"><p>Network port to which
the CacheServer <code>ServerSocket</code> is bound and on which it
listens for client connections.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.server.&lt;index&gt;.running</p></td>
<td class="tableblock halign-left valign-top"><p>Determines whether this
<code>CacheServer</code> is currently running and accepting client
connections.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.cache.server.&lt;index&gt;.socket-buffer-size</p></td>
<td class="tableblock halign-left valign-top"><p>Configured buffer size
of the socket connection used by this CacheServer.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.server.&lt;index&gt;.tcp-no-delay</p></td>
<td class="tableblock halign-left valign-top"><p>Configures the TCP/IP
<code>TCP_NO_DELAY</code> setting on outgoing sockets.</p></td>
</tr>
</tbody>
</table>

Table 16. CacheServer Details

In addition to the configuration settings shown in the preceding table,
the `ServerLoadProbe` of the `CacheServer` tracks additional details
about the runtime characteristics of the `CacheServer`:

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 17. CacheServer Metrics and Load Details</caption>
<colgroup>
<col style="width: 23%" />
<col style="width: 76%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-center valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.server.&lt;index&gt;.load.connection-load</p></td>
<td class="tableblock halign-left valign-top"><p>Load on the server due
to client-to-server connections.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.cache.server.&lt;index&gt;.load.load-per-connection</p></td>
<td class="tableblock halign-left valign-top"><p>Estimate of how much
load each new connection adds to this server.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.server.&lt;index&gt;.load.subscription-connection-load</p></td>
<td class="tableblock halign-left valign-top"><p>Load on the server due
to subscription connections.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.cache.server.&lt;index&gt;.load.load-per-subscription-connection</p></td>
<td class="tableblock halign-left valign-top"><p>Estimate of how much
load each new subscriber adds to this server.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.server.&lt;index&gt;.metrics.client-count</p></td>
<td class="tableblock halign-left valign-top"><p>Number of connected
clients.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.cache.server.&lt;index&gt;.metrics.max-connection-count</p></td>
<td class="tableblock halign-left valign-top"><p>Maximum number of
connections made to this <code>CacheServer</code>.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.cache.server.&lt;index&gt;.metrics.open-connection-count</p></td>
<td class="tableblock halign-left valign-top"><p>Number of open
connections to this <code>CacheServer</code>.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.cache.server.&lt;index&gt;.metrics.subscription-connection-count</p></td>
<td class="tableblock halign-left valign-top"><p>Number of subscription
connections to this <code>CacheServer</code>.</p></td>
</tr>
</tbody>
</table>

Table 17. CacheServer Metrics and Load Details

#### GeodeAsyncEventQueuesHealthIndicator

`GeodeAsyncEventQueuesHealthIndicator` provides details about the
configured `AsyncEventQueues`. AEQs can be attached to Regions to
configure asynchronous write-behind behavior.

This `HealthIndicator` captures configuration metadata and runtime
characteristics for all AEQs:

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 18. AsyncEventQueue Details</caption>
<colgroup>
<col style="width: 23%" />
<col style="width: 76%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-center valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.async-event-queue.count</p></td>
<td class="tableblock halign-left valign-top"><p>Total number of
configured AEQs.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.async-event-queue.&lt;id&gt;.batch-conflation-enabled</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether batch
events are conflated when sent.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.async-event-queue.&lt;id&gt;.batch-size</p></td>
<td class="tableblock halign-left valign-top"><p>Size of the batch that
gets delivered over this AEQ.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.async-event-queue.&lt;id&gt;.batch-time-interval</p></td>
<td class="tableblock halign-left valign-top"><p>Maximum time interval
that can elapse before a batch is sent.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.async-event-queue.&lt;id&gt;.disk-store-name</p></td>
<td class="tableblock halign-left valign-top"><p>Name of the disk store
used to overflow and persist events.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.async-event-queue.&lt;id&gt;.disk-synchronous</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether disk
writes are synchronous or asynchronous.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.async-event-queue.&lt;id&gt;.dispatcher-threads</p></td>
<td class="tableblock halign-left valign-top"><p>Number of threads used
to dispatch events.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.async-event-queue.&lt;id&gt;.forward-expiration-destroy</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether
expiration destroy operations are forwarded to
<code>AsyncEventListener</code>.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.async-event-queue.&lt;id&gt;.max-queue-memory</p></td>
<td class="tableblock halign-left valign-top"><p>Maximum memory used
before data needs to be overflowed to disk.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.async-event-queue.&lt;id&gt;.order-policy</p></td>
<td class="tableblock halign-left valign-top"><p>Order policy followed
while dispatching the events to
<code>AsyncEventListeners</code>.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.async-event-queue.&lt;id&gt;.parallel</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether this
queue is parallel (higher throughput) or serial.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.async-event-queue.&lt;id&gt;.persistent</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether this
queue stores events to disk.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.async-event-queue.&lt;id&gt;.primary</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether this
queue is primary or secondary.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.async-event-queue.&lt;id&gt;.size</p></td>
<td class="tableblock halign-left valign-top"><p>Number of entries in
this queue.</p></td>
</tr>
</tbody>
</table>

Table 18. AsyncEventQueue Details

#### GeodeGatewayReceiversHealthIndicator

`GeodeGatewayReceiversHealthIndicator` provides details about the
configured (WAN) `GatewayReceivers`, which are capable of receiving
events from remote clusters when using VMware GemFire's
[multi-site, WAN topology](https://geode.apache.org/docs/guide/115/topologies_and_comm/multi_site_configuration/chapter_overview.html).

This `HealthIndicator` captures configuration metadata along with the
running state for each `GatewayReceiver`:

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 19. GatewayReceiver Details</caption>
<colgroup>
<col style="width: 23%" />
<col style="width: 76%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-center valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-receiver.count</p></td>
<td class="tableblock halign-left valign-top"><p>Total number of
configured <code>GatewayReceiver</code> instances.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-receiver.&lt;index&gt;.bind-address</p></td>
<td class="tableblock halign-left valign-top"><p>IP address of the NIC
to which the <code>GatewayReceiver</code> <code>ServerSocket</code> is
bound (useful when the system contains multiple NICs).</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-receiver.&lt;index&gt;.end-port</p></td>
<td class="tableblock halign-left valign-top"><p>End value of the port
range from which the port of the <code>GatewayReceiver</code> is
chosen.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-receiver.&lt;index&gt;.host</p></td>
<td class="tableblock halign-left valign-top"><p>IP address or hostname
that Locators tell clients (that is, <code>GatewaySender</code>
instances) on which this <code>GatewayReceiver</code> listens.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-receiver.&lt;index&gt;.max-time-between-pings</p></td>
<td class="tableblock halign-left valign-top"><p>Maximum amount of time
between client pings.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-receiver.&lt;index&gt;.port</p></td>
<td class="tableblock halign-left valign-top"><p>Port on which this
<code>GatewayReceiver</code> listens for clients (that is,
<code>GatewaySender</code> instances).</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-receiver.&lt;index&gt;.running</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether this
<code>GatewayReceiver</code> is running and accepting client connections
(from <code>GatewaySender</code> instances).</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-receiver.&lt;index&gt;.socket-buffer-size</p></td>
<td class="tableblock halign-left valign-top"><p>Configured buffer size
for the socket connections used by this
<code>GatewayReceiver</code>.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-receiver.&lt;index&gt;.start-port</p></td>
<td class="tableblock halign-left valign-top"><p>Start value of the port
range from which the port of the <code>GatewayReceiver</code> is
chosen.</p></td>
</tr>
</tbody>
</table>

Table 19. GatewayReceiver Details

#### GeodeGatewaySendersHealthIndicator

The `GeodeGatewaySendersHealthIndicator` provides details about the
configured `GatewaySenders`. `GatewaySender` instances are attached to
Regions in order to send Region events to remote clusters in
VMware GemFire's
[multi-site, WAN topology](https://geode.apache.org/docs/guide/115/topologies_and_comm/multi_site_configuration/chapter_overview.html).

This `HealthIndicator` captures essential configuration metadata and
runtime characteristics for each `GatewaySender`:

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 20. GatewaySender Details</caption>
<colgroup>
<col style="width: 23%" />
<col style="width: 76%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-center valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-sender.count</p></td>
<td class="tableblock halign-left valign-top"><p>Total number of
configured <code>GatewaySender</code> instances.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-sender.&lt;id&gt;.alert-threshold</p></td>
<td class="tableblock halign-left valign-top"><p>Alert threshold (ms)
for entries in this <code>GatewaySender</code> instances queue.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-sender.&lt;id&gt;.batch-conflation-enabled</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether batch
events are conflated when sent.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-sender.&lt;id&gt;.batch-size</p></td>
<td class="tableblock halign-left valign-top"><p>Size of the batches
sent.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-sender.&lt;id&gt;.batch-time-interval</p></td>
<td class="tableblock halign-left valign-top"><p>Maximum time interval
that can elapse before a batch is sent.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-sender.&lt;id&gt;.disk-store-name</p></td>
<td class="tableblock halign-left valign-top"><p>Name of the
<code>DiskStore</code> used to overflow and persist queued
events.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-sender.&lt;id&gt;.disk-synchronous</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether disk
writes are synchronous or asynchronous.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-sender.&lt;id&gt;.dispatcher-threads</p></td>
<td class="tableblock halign-left valign-top"><p>Number of threads used
to dispatch events.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-sender.&lt;id&gt;.max-parallelism-for-replicated-region</p></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-sender.&lt;id&gt;.max-queue-memory</p></td>
<td class="tableblock halign-left valign-top"><p>Maximum amount of
memory (MB) usable for this <code>GatewaySender</code> instance’s
queue.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-sender.&lt;id&gt;.order-policy</p></td>
<td class="tableblock halign-left valign-top"><p>Order policy followed
while dispatching the events to <code>GatewayReceiver</code>
instances.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-sender.&lt;id&gt;.parallel</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether this
<code>GatewaySender</code> is parallel (higher throughput) or
serial.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-sender.&lt;id&gt;.paused</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether this
<code>GatewaySender</code> is paused.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-sender.&lt;id&gt;.persistent</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether this
<code>GatewaySender</code> persists queue events to disk.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-sender.&lt;id&gt;.remote-distributed-system-id</p></td>
<td class="tableblock halign-left valign-top"><p>Identifier for the
remote distributed system.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-sender.&lt;id&gt;.running</p></td>
<td class="tableblock halign-left valign-top"><p>Indicates whether this
<code>GatewaySender</code> is currently running.</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-sender.&lt;id&gt;.socket-buffer-size</p></td>
<td class="tableblock halign-left valign-top"><p>Configured buffer size
for the socket connections between this <code>GatewaySender</code> and
the receiving <code>GatewayReceiver</code>.</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-center valign-top"><p>geode.gateway-sender.&lt;id&gt;.socket-read-timeout</p></td>
<td class="tableblock halign-left valign-top"><p>Amount of time (ms)
that a socket read between this sending <code>GatewaySender</code> and
the receiving <code>GatewayReceiver</code> blocks.</p></td>
</tr>
</tbody>
</table>

Table 20. GatewaySender Details

