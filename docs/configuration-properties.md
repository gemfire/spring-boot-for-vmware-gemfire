---
Title: Configuration Metadata Reference
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

The following reference sections cover documented and well-known
properties recognized and processed by Spring Data for
VMware GemFire (SDG) and Spring Session for VMware GemFire
(SSDG).

These properties may be used in Spring Boot `application.properties` or
as JVM System properties, to configure different aspects of or enable
individual features of VMware GemFire in a Spring application. When
combined with the power of Spring Boot, they give you the ability to
quickly create an application that uses VMware GemFire.

### Spring Data Based Properties

The following properties all have a `spring.data.gemfire.*` prefix. For
example, to set the cache `copy-on-read` property, use
`spring.data.gemfire.cache.copy-on-read` in Spring Boot
`application.properties`.

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 1. <code>spring.data.gemfire.*</code>
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>name</code></p></td>
<td class="tableblock halign-left valign-top"><p>Name of the
VMware GemFire server.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>SpringBasedCacheClientApplication</code></p></td>
<td
class="tableblock halign-left valign-top"><p><a href="https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#name--"><code>ClientCacheApplication.name</code></a></td></tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>locators</code></p></td>
<td class="tableblock halign-left valign-top"><p>Comma-delimited list of
Locator endpoints formatted as:
<code>locator1[port1],...​,locatorN[portN]</code>.</p></td>
<td class="tableblock halign-left valign-top"><p>[]</p></td>
<td
class="tableblock halign-left valign-top"><p><a href="https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/PeerCacheApplication.html#locators--"><code>PeerCacheApplication.locators</code></a></p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>use-bean-factory-locator</code></p></td>
<td class="tableblock halign-left valign-top"><p>Enable the SDG
<code>BeanFactoryLocator</code> when mixing Spring config with
VMware GemFire native config (such as <code>cache.xml</code>) and
you wish to configure VMware GemFire objects declared in
<code>cache.xml</code> with Spring.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
<td
class="tableblock halign-left valign-top"><p><a href="https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#useBeanFactoryLocator--"><code>ClientCacheApplication.useBeanFactoryLocator</code></a></p></td>
</tr>
</tbody>
</table>

Table 1. `spring.data.gemfire.*` properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 2. <code>spring.data.gemfire.*</code>
<em>GemFireCache</em> properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.copy-on-read</code></p></td>
<td class="tableblock halign-left valign-top"><p>Configure whether a
copy of an object returned from <code>Region.get(key)</code> is
made.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#copyOnRead--[<code>ClientCacheApplication.copyOnRead</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.critical-heap-percentage</code></p></td>
<td class="tableblock halign-left valign-top"><p>Percentage of heap at
or above which the cache is considered in danger of becoming
inoperable.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#criticalHeapPercentage--[<code>ClientCacheApplication.criticalHeapPercentage</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.critical-off-heap-percentage</code></p></td>
<td class="tableblock halign-left valign-top"><p>Percentage of off-heap
at or above which the cache is considered in danger of becoming
inoperable.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#criticalOffHeapPercentage--[<code>ClientCacheApplication.criticalOffHeapPercentage</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.enable-auto-region-lookup</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether to lookup
Regions configured in VMware GemFire native configuration and
declare them as Spring beans.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableAutoRegionLookup.html#enabled--[<code>EnableAutoRegionLookup.enable</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.eviction-heap-percentage</code></p></td>
<td class="tableblock halign-left valign-top"><p>Percentage of heap at
or above which the eviction should begin on Regions configured for
HeapLRU eviction.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#evictionHeapPercentage--[<code>ClientCacheApplication.evictionHeapPercentage</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.eviction-off-heap-percentage</code></p></td>
<td class="tableblock halign-left valign-top"><p>Percentage of off-heap
at or above which the eviction should begin on Regions configured for
HeapLRU eviction.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#evictionOffHeapPercentage--[<code>ClientCacheApplication.evictionOffHeapPercentage</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.log-level</code></p></td>
<td class="tableblock halign-left valign-top"><p>Configure the log-level
of a VMware GemFire cache.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>config</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#logLevel--[<code>ClientCacheApplication.logLevel</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.name</code></p></td>
<td class="tableblock halign-left valign-top"><p>Alias for
<code>spring.data.gemfire.name</code>.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>SpringBasedCacheClientApplication</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#name--[<code>ClientCacheApplication.name</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.compression.bean-name</code></p></td>
<td class="tableblock halign-left valign-top"><p>Name of a Spring bean
that implements
<code>org.apache.geode.compression.Compressor</code>.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableCompression.html#compressorBeanName--[<code>EnableCompression.compressorBeanName</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.compression.region-names</code></p></td>
<td class="tableblock halign-left valign-top"><p>Comma-delimited list of
Region names for which compression is configured.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>[]</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableCompression.html#RegionNames--[EnableCompression.RegionNames]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p>cache.off-heap.memory-size</p></td>
<td class="tableblock halign-left valign-top"><p>Determines the size of
off-heap memory used by VMware GemFire in megabytes (m) or
gigabytes (g) — for example, <code>120g</code></p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableOffHeap.html#memorySize--[<code>EnableOffHeap.memorySize</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.off-heap.region-names</code></p></td>
<td class="tableblock halign-left valign-top"><p>Comma-delimited list of
Region names for which off-heap is configured.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>[]</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableOffHeap.html#RegionNames--[<code>EnableOffHeap.RegionNames</code>]</p></td>
</tr>
</tbody>
</table>

Table 2. `spring.data.gemfire.*` *GemFireCache* properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 3. <code>spring.data.gemfire.*</code>
<em>ClientCache</em> properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.client.durable-client-id</code></p></td>
<td class="tableblock halign-left valign-top"><p>Used only for clients
in a client/server installation. If set, this indicates that the client
is durable and identifies the client. The ID is used by servers to
reestablish any messaging that was interrupted by client
downtime.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#durableClientId--[<code>ClientCacheApplication.durableClientId</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.client.durable-client-timeout</code></p></td>
<td class="tableblock halign-left valign-top"><p>Used only for clients
in a client/server installation. Number of seconds this client can
remain disconnected from its server and have the server continue to
accumulate durable events for it.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>300</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#durableClientTimeout--[<code>ClientCacheApplication.durableClientTimeout</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.client.keep-alive</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether the server
should keep the durable client’s queues alive for the timeout
period.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#keepAlive--[<code>ClientCacheApplication.keepAlive</code>]</p></td>
</tr>
</tbody>
</table>

Table 3. `spring.data.gemfire.*` *ClientCache* properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 4. <code>spring.data.gemfire.*</code> peer <em>Cache</em>
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.peer.enable-auto-reconnect</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether a member (a
Locator or Server) try to reconnect and reinitialize the cache after it
has been forced out of the cluster by a network partition event or has
otherwise been shunned by other members.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/PeerCacheApplication.html#enableAutoReconnect--[<code>PeerCacheApplication.enableAutoReconnect</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.peer.lock-lease</code></p></td>
<td class="tableblock halign-left valign-top"><p>The length, in seconds,
of distributed lock leases obtained by this cache.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>120</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/PeerCacheApplication.html#lockLease--[<code>PeerCacheApplication.lockLease</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.peer.lock-timeout</code></p></td>
<td class="tableblock halign-left valign-top"><p>The number of seconds a
cache operation waits to obtain a distributed lock lease.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>60</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/PeerCacheApplication.html#lockTimeout--[<code>PeerCacheApplication.lockTimeout</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.peer.message-sync-interval</code></p></td>
<td class="tableblock halign-left valign-top"><p>The frequency (in
seconds) at which a message is sent by the primary cache-server to all
the secondary cache-server nodes to remove the events that have already
been dispatched from the queue.</p></td>
<td class="tableblock halign-left valign-top"><p><code>1</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/PeerCacheApplication.html#messageSyncInterval--[<code>PeerCacheApplication.messageSyncInterval</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.peer.search-timeout</code></p></td>
<td class="tableblock halign-left valign-top"><p>The number of seconds a
cache get operation can spend searching for a value.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>300</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/PeerCacheApplication.html#searchTimeout--[<code>PeerCacheApplication.searchTimeout</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.peer.use-cluster-configuration</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether this cache
member node pulls its configuration metadata from the cluster-based
cluster configuration service.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/PeerCacheApplication.html#useClusterConfiguration--[<code>PeerCacheApplication.useClusterConfiguration</code>]</p></td>
</tr>
</tbody>
</table>

Table 4. `spring.data.gemfire.*` peer *Cache* properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 5. <code>spring.data.gemfire.*</code>
<em>CacheServer</em> properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.auto-startup</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether the
<code>CacheServer</code> should be started automatically at
runtime.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>true</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#autoStartup--[<code>CacheServerApplication.autoStartup</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.bind-address</code></p></td>
<td class="tableblock halign-left valign-top"><p>The IP address or
hostname on which this cache server listens.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#bindAddress--[<code>CacheServerApplication.bindAddress</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.hostname-for-clients</code></p></td>
<td class="tableblock halign-left valign-top"><p>The IP address or
hostname that server locators tell to clients to indicate the IP address
on which the cache server listens.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#hostnameForClients--[<code>CacheServerApplication.hostNameForClients</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.load-poll-interval</code></p></td>
<td class="tableblock halign-left valign-top"><p>The frequency in
milliseconds at which to poll the load probe on this cache
server.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>5000</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#loadPollInterval--[<code>CacheServerApplication.loadPollInterval</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.max-connections</code></p></td>
<td class="tableblock halign-left valign-top"><p>The maximum client
connections.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>800</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#maxConnections--[<code>CacheServerApplication.maxConnections</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.max-message-count</code></p></td>
<td class="tableblock halign-left valign-top"><p>The maximum number of
messages that can be in a client queue.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>230000</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#maxMessageCount--[<code>CacheServerApplication.maxMessageCount</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.max-threads</code></p></td>
<td class="tableblock halign-left valign-top"><p>The maximum number of
threads allowed in this cache server to service client
requests.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#maxThreads--[<code>CacheServerApplication.maxThreads</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.max-time-between-pings</code></p></td>
<td class="tableblock halign-left valign-top"><p>The maximum amount of
time between client pings.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>60000</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#maxTimeBetweenPings--[<code>CacheServerApplication.maxTimeBetweenPings</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.message-time-to-live</code></p></td>
<td class="tableblock halign-left valign-top"><p>The time (in seconds)
after which a message in the client queue expires.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>180</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#messageTimeToLive--[<code>CacheServerApplication.messageTimeToLive</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.port</code></p></td>
<td class="tableblock halign-left valign-top"><p>The port on which this
cache server listens for clients.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>40404</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#port--[<code>CacheServerApplication.port</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.socket-buffer-size</code></p></td>
<td class="tableblock halign-left valign-top"><p>The buffer size of the
socket connection to this <code>CacheServer</code>.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>32768</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#socketBufferSize--[<code>CacheServerApplication.socketBufferSize</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.subscription-capacity</code></p></td>
<td class="tableblock halign-left valign-top"><p>The capacity of the
client queue.</p></td>
<td class="tableblock halign-left valign-top"><p><code>1</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#subscriptionCapacity--[<code>CacheServerApplication.subscriptionCapacity</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.subscription-disk-store-name</code></p></td>
<td class="tableblock halign-left valign-top"><p>The name of the disk
store for client subscription queue overflow.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#subscriptionDiskStoreName--[<code>CacheServerApplication.subscriptionDiskStoreName</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.subscription-eviction-policy</code></p></td>
<td class="tableblock halign-left valign-top"><p>The eviction policy
that is executed when the capacity of the client subscription queue is
reached.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>none</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#subscriptionEvictionPolicy--[<code>CacheServerApplication.subscriptionEvictionPolicy</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.tcp-no-delay</code></p></td>
<td class="tableblock halign-left valign-top"><p>The outgoing socket
connection tcp-no-delay setting.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>true</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html#tcpNoDelay--[<code>CacheServerApplication.tcpNoDelay</code>]</p></td>
</tr>
</tbody>
</table>

Table 5. `spring.data.gemfire.*` *CacheServer* properties

`CacheServer` properties can be further targeted at specific
`CacheServer` instances by using an optional bean name of the
`CacheServer` bean defined in the Spring `ApplicationContext`. Consider
the following example:

``` highlight
spring.data.gemfire.cache.server.[<cacheServerBeanName>].bind-address=...
```

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 6. <code>spring.data.gemfire.*</code> Cluster
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cluster.Region.type</code></p></td>
<td class="tableblock halign-left valign-top"><p>Specifies the data
management policy used when creating Regions on the servers in the
cluster.</p></td>
<td
class="tableblock halign-left valign-top"><p>https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/RegionShortcut.html#PARTITION[<code>RegionShortcut.PARTITION</code>]</p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableClusterConfiguration.html#serverRegionShortcut--[<code>EnableClusterConfiguration.serverRegionShortcut</code>]</p></td>
</tr>
</tbody>
</table>

Table 6. `spring.data.gemfire.*` Cluster properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 7. <code>spring.data.gemfire.*</code> <em>DiskStore</em>
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>disk.store.allow-force-compaction</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether to allow
<code>DiskStore.forceCompaction()</code> to be called on Regions that
use a disk store.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html#allowForceCompaction--[<code>EnableDiskStore.allowForceCompaction</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>disk.store.auto-compact</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether to cause the
disk files to be automatically compacted.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>true</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html#autoCompact--[<code>EnableDiskStore.autoCompact</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>disk.store.compaction-threshold</code></p></td>
<td class="tableblock halign-left valign-top"><p>The threshold at which
an oplog becomes compactible.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>50</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html#compactionThreshold--[<code>EnableDiskStore.compactionThreshold</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>disk.store.directory.location</code></p></td>
<td class="tableblock halign-left valign-top"><p>The system directory
where the <code>DiskStore</code> (oplog) files are stored.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>[]</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html#diskDirectories--[<code>EnableDiskStore.diskDirectories.location</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>disk.store.directory.size</code></p></td>
<td class="tableblock halign-left valign-top"><p>The amount of disk
space allowed to store disk store (oplog) files.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>21474883647</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html#diskDirectories--[<code>EnableDiskStore.diskDirectories.size</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>disk.store.disk-usage-critical-percentage</code></p></td>
<td class="tableblock halign-left valign-top"><p>The critical threshold
for disk usage as a percentage of the total disk volume.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>99.0</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html#diskUsageCriticalPercentage--[<code>EnableDiskStore.diskUsageCriticalPercentage</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>disk.store.disk-usage-warning-percentage</code></p></td>
<td class="tableblock halign-left valign-top"><p>The warning threshold
for disk usage as a percentage of the total disk volume.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>90.0</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html#diskUsageWarningPercentage--[<code>EnableDiskStore.diskUsageWarningPercentage</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>disk.store.max-oplog-size</code></p></td>
<td class="tableblock halign-left valign-top"><p>The maximum size (in
megabytes) a single oplog (operation log) can be.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>1024</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html#maxOplogSize--[<code>EnableDiskStore.maxOplogSize</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>disk.store.queue-size</code></p></td>
<td class="tableblock halign-left valign-top"><p>The maximum number of
operations that can be asynchronously queued.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html#queueSize--[<code>EnableDiskStore.queueSize</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>disk.store.time-interval</code></p></td>
<td class="tableblock halign-left valign-top"><p>The number of
milliseconds that can elapse before data written asynchronously is
flushed to disk.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>1000</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html#timeInterval--[<code>EnableDiskStore.timeInterval</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>disk.store.write-buffer-size</code></p></td>
<td class="tableblock halign-left valign-top"><p>Configures the write
buffer size in bytes.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>32768</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html#writeBufferSize--[<code>EnableDiskStore.writeBufferSize</code>]</p></td>
</tr>
</tbody>
</table>

Table 7. `spring.data.gemfire.*` *DiskStore* properties

`DiskStore` properties can be further targeted at specific `DiskStore`
instances by setting the
[`DiskStore.name`](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/DiskStore.html#getName)
property.

For example, you can specify directory location of the files for a
specific, named `DiskStore` by using:

``` highlight
spring.data.gemfire.disk.store.Example.directory.location=/path/to/geode/disk-stores/Example/
```

The directory location and size of the `DiskStore` files can be further
divided into multiple locations and size using array syntax:

``` highlight
spring.data.gemfire.disk.store.Example.directory[0].location=/path/to/geode/disk-stores/Example/one
spring.data.gemfire.disk.store.Example.directory[0].size=4096000
spring.data.gemfire.disk.store.Example.directory[1].location=/path/to/geode/disk-stores/Example/two
spring.data.gemfire.disk.store.Example.directory[1].size=8192000
```

Both the name and array index are optional, and you can use any
combination of name and array index. Without a name, the properties
apply to all `DiskStore` instances. Without array indexes, all named
`DiskStore` files are stored in the specified location and limited to
the defined size.

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 8. <code>spring.data.gemfire.*</code> Entity
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>entities.base-packages</code></p></td>
<td class="tableblock halign-left valign-top"><p>Comma-delimited list of
package names indicating the start points for the entity scan.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableEntityDefinedRegions.html#basePackages--[<code>EnableEntityDefinedRegions.basePackages</code>]</p></td>
</tr>
</tbody>
</table>

Table 8. `spring.data.gemfire.*` Entity properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 9. <code>spring.data.gemfire.*</code> Locator
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>locator.host</code></p></td>
<td class="tableblock halign-left valign-top"><p>The IP address or
hostname of the system NIC to which the embedded Locator is bound to
listen for connections.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableLocator.html#host--[<code>EnableLocator.host</code>]</p></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>locator.port</p></td>
<td class="tableblock halign-left valign-top"><p>The network port to
which the embedded Locator will listen for connections.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>10334</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableLocator.html#port--[<code>EnableLocator.port</code>]</p></td>
</tr>
</tbody>
</table>

Table 9. `spring.data.gemfire.*` Locator properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 10. <code>spring.data.gemfire.*</code> Logging
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>logging.level</code></p></td>
<td class="tableblock halign-left valign-top"><p>The log level of an
VMware GemFire cache. Alias for
'spring.data.gemfire.cache.log-level'.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>config</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableLogging.html#logLevel--[<code>EnableLogging.logLevel</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>logging.log-disk-space-limit</code></p></td>
<td class="tableblock halign-left valign-top"><p>The amount of disk
space allowed to store log files.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableLogging.html#logDiskSpaceLimit--[<code>EnableLogging.logDiskSpaceLimit</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>logging.log-file</code></p></td>
<td class="tableblock halign-left valign-top"><p>The pathname of the log
file used to log messages.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableLogging.html#logFile--[<code>EnableLogging.logFile</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>logging.log-file-size</code></p></td>
<td class="tableblock halign-left valign-top"><p>The maximum size of a
log file before the log file is rolled.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableLogging.html#logFileSizeLimit--[<code>EnableLogging.logFileSize</code>]</p></td>
</tr>
</tbody>
</table>

Table 10. `spring.data.gemfire.*` Logging properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 11. <code>spring.data.gemfire.*</code> Management
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>management.use-http</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether to use the HTTP
protocol to communicate with a VMware GemFire Manager.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableClusterConfiguration.html#useHttp--[<code>EnableClusterConfiguration.useHttp</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>management.http.host</code></p></td>
<td class="tableblock halign-left valign-top"><p>The IP address or
hostname of the VMware GemFire Manager that runs the HTTP
service.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableClusterConfiguration.html#host--[<code>EnableClusterConfiguration.host</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>management.http.port</code></p></td>
<td class="tableblock halign-left valign-top"><p>The port used by the
VMware GemFire Manager’s HTTP service to listen for
connections.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>7070</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableClusterConfiguration.html#port--[<code>EnableClusterConfiguration.port</code>]</p></td>
</tr>
</tbody>
</table>

Table 11. `spring.data.gemfire.*` Management properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 12. <code>spring.data.gemfire.*</code> Manager
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>manager.access-file</code></p></td>
<td class="tableblock halign-left valign-top"><p>The access control list
(ACL) file used by the Manager to restrict access to the JMX MBeans by
the clients.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableManager.html#accessFile--[<code>EnableManager.accessFile</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p>manager.bind-address</p></td>
<td class="tableblock halign-left valign-top"><p>The IP address or
hostname of the system NIC used by the Manager to bind and listen for
JMX client connections.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableManager.html#bindAddress--[<code>EnableManager.bindAddress</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>manager.hostname-for-clients</code></p></td>
<td class="tableblock halign-left valign-top"><p>The hostname given to
JMX clients to ask the Locator for the location of the Manager.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableManager.html#hostnameForClients--[<code>EnableManager.hostNameForClients</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>manager.password-file</code></p></td>
<td class="tableblock halign-left valign-top"><p>By default, the JMX
Manager lets clients without credentials connect. If this property is
set to the name of a file, only clients that connect with credentials
that match an entry in this file are allowed.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableManager.html#passwordFile--[<code>EnableManager.passwordFile</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>manager.port</code></p></td>
<td class="tableblock halign-left valign-top"><p>The port used by the
Manager to listen for JMX client connections.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>1099</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableManager.html#port--[<code>EnableManager.port</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>manager.start</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether to start the
Manager service at runtime.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableManager.html#start--[<code>EnableManager.start</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>manager.update-rate</code></p></td>
<td class="tableblock halign-left valign-top"><p>The rate, in
milliseconds, at which this member pushes updates to any JMX
Managers.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>2000</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableManager.html#updateRate--[<code>EnableManager.updateRate</code>]</p></td>
</tr>
</tbody>
</table>

Table 12. `spring.data.gemfire.*` Manager properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 13. <code>spring.data.gemfire.*</code> PDX
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pdx.disk-store-name</code></p></td>
<td class="tableblock halign-left valign-top"><p>The name of the
<code>DiskStore</code> used to store PDX type metadata to disk when PDX
is persistent.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePdx.html#diskStoreName--[<code>EnablePdx.diskStoreName</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pdx.ignore-unread-fields</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether PDX ignores
fields that were unread during deserialization.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePdx.html#ignoreUnreadFields--[<code>EnablePdx.ignoreUnreadFields</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pdx.persistent</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether PDX persists
type metadata to disk.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePdx.html#persistent--[<code>EnablePdx.persistent</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pdx.read-serialized</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether a Region entry
is returned as a <code>PdxInstance</code> or deserialized back into
object form on read.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePdx.html#readSerialized--[<code>EnablePdx.readSerialized</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pdx.serialize-bean-name</code></p></td>
<td class="tableblock halign-left valign-top"><p>The name of a custom
Spring bean that implements
<code>org.apache.geode.pdx.PdxSerializer</code>.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePdx.html#serializerBeanName--[<code>EnablePdx.serializerBeanName</code>]</p></td>
</tr>
</tbody>
</table>

Table 13. `spring.data.gemfire.*` PDX properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 14. <code>spring.data.gemfire.*</code> Pool
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pool.free-connection-timeout</code></p></td>
<td class="tableblock halign-left valign-top"><p>The timeout used to
acquire a free connection from a Pool.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>10000</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#freeConnectionTimeout--[<code>EnablePool.freeConnectionTimeout</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pool.idle-timeout</code></p></td>
<td class="tableblock halign-left valign-top"><p>The amount of time a
connection can be idle before expiring (and closing) the
connection.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>5000</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#idleTimeout--[<code>EnablePool.idleTimeout</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pool.load-conditioning-interval</code></p></td>
<td class="tableblock halign-left valign-top"><p>The interval for how
frequently the Pool checks to see if a connection to a given server
should be moved to a different server to improve the load
balance.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>300000</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#loadConditioningInterval--[<code>EnablePool.loadConditioningInterval</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pool.locators</code></p></td>
<td class="tableblock halign-left valign-top"><p>Comma-delimited list of
locator endpoints in the format of
<code>locator1[port1],...​,locatorN[portN]</code></p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#locators--[<code>EnablePool.locators</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pool.max-connections</code></p></td>
<td class="tableblock halign-left valign-top"><p>The maximum number of
client to server connections that a Pool will create.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#maxConnections--[EnablePool.maxConnections]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pool.min-connections</code></p></td>
<td class="tableblock halign-left valign-top"><p>The minimum number of
client to server connections that a Pool maintains.</p></td>
<td class="tableblock halign-left valign-top"><p><code>1</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#minConnections--[<code>EnablePool.minConnections</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pool.multi-user-authentication</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether the created
Pool can be used by multiple authenticated users.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#multiUserAuthentication--[<code>EnablePool.multiUserAuthentication</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pool.ping-interval</code></p></td>
<td class="tableblock halign-left valign-top"><p>How often to ping
servers to verify that they are still alive.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>10000</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#pingInterval--[<code>EnablePool.pingInterval</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pool.pr-single-hop-enabled</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether to perform
single-hop data access operations between the client and servers. When
<code>true</code>, the client is aware of the location of partitions on
servers that host Regions with
<code>DataPolicy.PARTITION</code>.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>true</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#prSingleHopEnabled--[<code>EnablePool.prSingleHopEnabled</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pool.read-timeout</code></p></td>
<td class="tableblock halign-left valign-top"><p>The number of
milliseconds to wait for a response from a server before timing out the
operation and trying another server (if any are available).</p></td>
<td
class="tableblock halign-left valign-top"><p><code>10000</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#readTimeout--[<code>EnablePool.readTimeout</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pool.ready-for-events</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether to signal the
server that the client is prepared and ready to receive events.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html#readyForEvents--[<code>ClientCacheApplication.readyForEvents</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pool.retry-attempts</code></p></td>
<td class="tableblock halign-left valign-top"><p>The number of times to
retry a request after timeout/exception.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#retryAttempts--[<code>EnablePool.retryAttempts</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pool.server-group</code></p></td>
<td class="tableblock halign-left valign-top"><p>The group that all
servers to which a Pool connects must belong.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#serverGroup--[<code>EnablePool.serverGroup</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pool.servers</code></p></td>
<td class="tableblock halign-left valign-top"><p>Comma-delimited list of
<code>CacheServer</code> endpoints in the format of
<code>server1[port1],...​,serverN[portN]</code></p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#servers--[<code>EnablePool.servers</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pool.socket-buffer-size</code></p></td>
<td class="tableblock halign-left valign-top"><p>The socket buffer size
for each connection made in all Pools.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>32768</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#socketBufferSize--[<code>EnablePool.socketBufferSize</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pool.statistic-interval</code></p></td>
<td class="tableblock halign-left valign-top"><p>How often to send
client statistics to the server.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#statisticInterval--[<code>EnablePool.statisticInterval</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p>pool.subscription-ack-interval</p></td>
<td class="tableblock halign-left valign-top"><p>The interval in
milliseconds to wait before sending acknowledgements to the
<code>CacheServer</code> for events received from the server
subscriptions.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>100</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#subscriptionAckInterval--[<code>EnablePool.subscriptionAckInterval</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pool.subscription-enabled</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether the created
Pool has server-to-client subscriptions enabled.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#subscriptionEnabled--[<code>EnablePool.subscriptionEnabled</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pool.subscription-message-tracking-timeout</code></p></td>
<td class="tableblock halign-left valign-top"><p>The
<code>messageTrackingTimeout</code> attribute, which is the time-to-live
period, in milliseconds, for subscription events the client has received
from the server.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>900000</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#subscriptionMessageTrackingTimeout--[<code>EnablePool.subscriptionMessageTrackingTimeout</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>pool.subscription-redundancy</code></p></td>
<td class="tableblock halign-left valign-top"><p>The redundancy level
for all Pools server-to-client subscriptions.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#subscriptionRedundancy--[<code>EnablePool.subsriptionRedundancy</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>pool.thread-local-connections</code></p></td>
<td class="tableblock halign-left valign-top"><p>The thread local
connections policy for all Pools.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnablePool.html#threadLocalConnections--[<code>EnablePool.threadLocalConnections</code>]</p></td>
</tr>
</tbody>
</table>

Table 14. `spring.data.gemfire.*` Pool properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 15. <code>spring.data.gemfire.*</code> Security
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.username</code></p></td>
<td class="tableblock halign-left valign-top"><p>The name of the user
used to authenticate with the servers.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSecurity.html#securityUsername--[<code>EnableSecurity.securityUsername</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.password</code></p></td>
<td class="tableblock halign-left valign-top"><p>The user password used
to authenticate with the servers.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSecurity.html#securityPassword--[<code>EnableSecurity.securityPassword</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.properties-file</code></p></td>
<td class="tableblock halign-left valign-top"><p>The system pathname to
a properties file that contains security credentials.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableAuth.html#securityPropertiesFile--[<code>EnableAuth.propertiesFile</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.client.accessor</code></p></td>
<td class="tableblock halign-left valign-top"><p>X</p></td>
<td class="tableblock halign-left valign-top"><p>X</p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableAuth.html#clientAccessor--[<code>EnableAuth.clientAccessor</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.client.accessor-post-processor</code></p></td>
<td class="tableblock halign-left valign-top"><p>The callback that
should be invoked in the post-operation phase, which is when the
operation has completed on the server but before the result is sent to
the client.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableAuth.html#clientAccessorPostProcessor--[<code>EnableAuth.clientAccessorPostProcessor</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.client.authentication-initializer</code></p></td>
<td class="tableblock halign-left valign-top"><p>Static creation method
that returns an <code>AuthInitialize</code> object, which obtains
credentials for peers in a cluster.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSecurity.html#clientAuthenticationInitializer--[<code>EnableSecurity.clientAuthentiationInitializer</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.client.authenticator</code></p></td>
<td class="tableblock halign-left valign-top"><p>Static creation method
that returns an <code>Authenticator</code> object used by a cluster
member (Locator or Server) to verify the credentials of a connecting
client.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableAuth.html#clientAuthenticator--[<code>EnableAuth.clientAuthenticator</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.client.diffie-hellman-algorithm</code></p></td>
<td class="tableblock halign-left valign-top"><p>Used for
authentication. For secure transmission of sensitive credentials (such
as passwords), you can encrypt the credentials by using the
Diffie-Hellman key-exchange algorithm. You can do so by setting the
<code>security-client-dhalgo</code> system property on the clients to
the name of a valid, symmetric key cipher supported by the JDK.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableAuth.html#clientDiffieHellmanAlgorithm--[<code>EnableAuth.clientDiffieHellmanAlgorithm</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.log.file</code></p></td>
<td class="tableblock halign-left valign-top"><p>The pathname to a log
file used for security log messages.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableAuth.html#securityLogFile--[<code>EnableAuth.securityLogFile</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.log.level</code></p></td>
<td class="tableblock halign-left valign-top"><p>The log level for
security log messages.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableAuth.html#securityLogLevel--[<code>EnableAuth.securityLogLevel</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.manager.class-name</code></p></td>
<td class="tableblock halign-left valign-top"><p>The name of a class
that implements
<code>org.apache.geode.security.SecurityManager</code>.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSecurity.html#securityManagerClassName--[<code>EnableSecurity.securityManagerClassName</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.peer.authentication-initializer</code></p></td>
<td class="tableblock halign-left valign-top"><p>Static creation method
that returns an <code>AuthInitialize</code> object, which obtains
credentials for peers in a cluster.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSecurity.html#peerAuthenticationInitializer--[<code>EnableSecurity.peerAuthenticationInitializer</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.peer.authenticator</code></p></td>
<td class="tableblock halign-left valign-top"><p>Static creation method
that returns an <code>Authenticator</code> object, which is used by a
peer to verify the credentials of a connecting node.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableAuth.html#peerAuthenticator--[<code>EnableAuth.peerAuthenticator</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p>security.peer.verify-member-timeout</p></td>
<td class="tableblock halign-left valign-top"><p>The timeout in
milliseconds used by a peer to verify membership of an unknown
authenticated peer requesting a secure connection.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableAuth.html#peerVerifyMemberTimeout--[<code>EnableAuth.peerVerifyMemberTimeout</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.post-processor.class-name</code></p></td>
<td class="tableblock halign-left valign-top"><p>The name of a class
that implements the <code>org.apache.geode.security.PostProcessor</code>
interface that can be used to change the returned results of Region get
operations.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSecurity.html#securityPostProcessorClassName--[<code>EnableSecurity.securityPostProcessorClassName</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.shiro.ini-resource-path</code></p></td>
<td class="tableblock halign-left valign-top"><p>The VMware GemFire
System property that refers to the location of an Apache Shiro INI file
that configures the Apache Shiro Security Framework in order to secure
VMware GemFire.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSecurity.html#shiroIniResourcePath--[<code>EnableSecurity.shiroIniResourcePath</code>]</p></td>
</tr>
</tbody>
</table>

Table 15. `spring.data.gemfire.*` Security properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 16. <code>spring.data.gemfire.*</code> SSL
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.certificate.alias.cluster</code></p></td>
<td class="tableblock halign-left valign-top"><p>The alias to the stored
SSL certificate used by the cluster to secure communications.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#componentCertificateAliases--[<code>EnableSsl.componentCertificateAliases</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.certificate.alias.default-alias</code></p></td>
<td class="tableblock halign-left valign-top"><p>The default alias to
the stored SSL certificate used to secure communications across the
entire VMware GemFire system.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#defaultCertificateAlias--[<code>EnableSsl.defaultCertificateAlias</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.certificate.alias.gateway</code></p></td>
<td class="tableblock halign-left valign-top"><p>The alias to the stored
SSL certificate used by the WAN Gateway Senders/Receivers to secure
communications.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#componentCertificateAliases--[<code>EnableSsl.componentCertificateAliases</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.certificate.alias.jmx</code></p></td>
<td class="tableblock halign-left valign-top"><p>The alias to the stored
SSL certificate used by the Manager’s JMX-based JVM MBeanServer and JMX
clients to secure communications.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#componentCertificateAliases--[<code>EnableSsl.componentCertificateAliases</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.certificate.alias.locator</code></p></td>
<td class="tableblock halign-left valign-top"><p>The alias to the stored
SSL certificate used by the Locator to secure communications.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#componentCertificateAliases--[<code>EnableSsl.componentCertificateAliases</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.certificate.alias.server</code></p></td>
<td class="tableblock halign-left valign-top"><p>The alias to the stored
SSL certificate used by clients and servers to secure
communications.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#componentCertificateAliases--[<code>EnableSsl.componentCertificateAliases</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.certificate.alias.web</code></p></td>
<td class="tableblock halign-left valign-top"><p>The alias to the stored
SSL certificate used by the embedded HTTP server to secure
communications (HTTPS).</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#componentCertificateAliases--[<code>EnableSsl.componentCertificateAliases</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.ciphers</code></p></td>
<td class="tableblock halign-left valign-top"><p>Comma-separated list of
SSL ciphers or <code>any</code>.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#ciphers--[<code>EnableSsl.ciphers</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.components</code></p></td>
<td class="tableblock halign-left valign-top"><p>Comma-delimited list of
VMware GemFire components (for example, WAN) to be configured for
SSL communication.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#components--[<code>EnableSsl.components</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.keystore</code></p></td>
<td class="tableblock halign-left valign-top"><p>The system pathname to
the Java KeyStore file storing certificates for SSL.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#keystore--[<code>EnableSsl.keystore</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.keystore.password</code></p></td>
<td class="tableblock halign-left valign-top"><p>The password used to
access the Java KeyStore file.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#keystorePassword--[<code>EnableSsl.keystorePassword</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.keystore.type</code></p></td>
<td class="tableblock halign-left valign-top"><p>The password used to
access the Java KeyStore file (for example, JKS).</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#keystoreType--[<code>EnableSsl.keystoreType</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.protocols</code></p></td>
<td class="tableblock halign-left valign-top"><p>Comma-separated list of
SSL protocols or <code>any</code>.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#protocols--[<code>EnableSsl.protocols</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.require-authentication</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether two-way
authentication is required.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#requireAuthentication--[<code>EnableSsl.requireAuthentication</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.truststore</code></p></td>
<td class="tableblock halign-left valign-top"><p>The system pathname to
the trust store (Java KeyStore file) that stores certificates for
SSL.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#truststore--[<code>EnableSsl.truststore</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.truststore.password</code></p></td>
<td class="tableblock halign-left valign-top"><p>The password used to
access the trust store (Java KeyStore file).</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#truststorePassword--[<code>EnableSsl.truststorePassword</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.truststore.type</code></p></td>
<td class="tableblock halign-left valign-top"><p>The password used to
access the trust store (Java KeyStore file — for example, JKS).</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#truststoreType--[<code>EnableSsl.truststoreType</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>security.ssl.web-require-authentication</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether two-way HTTP
authentication is required.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableSsl.html#webRequireAuthentication--[<code>EnableSsl.webRequireAuthentication</code>]</p></td>
</tr>
</tbody>
</table>

Table 16. `spring.data.gemfire.*` SSL properties

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 17. <code>spring.data.gemfire.*</code> Service
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>service.http.bind-address</code></p></td>
<td class="tableblock halign-left valign-top"><p>The IP address or
hostname of the system NIC used by the embedded HTTP server to bind and
listen for HTTP(S) connections.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableHttpService.html#bindAddress--[<code>EnableHttpService.bindAddress</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>service.http.port</code></p></td>
<td class="tableblock halign-left valign-top"><p>The port used by the
embedded HTTP server to listen for HTTP(S) connections.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>7070</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableHttpService.html#port--[<code>EnableHttpService.port</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>service.http.ssl-require-authentication</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether two-way HTTP
authentication is required.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableHttpService.html#sslRequireAuthentication--[<code>EnableHttpService.sslRequireAuthentication</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>service.http.dev-rest-api-start</code></p></td>
<td class="tableblock halign-left valign-top"><p>Whether to start the
Developer REST API web service. A full installation of
VMware GemFire is required, and you must set the
<code>$GEODE</code> environment variable.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>false</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableHttpService.html#startDeveloperRestApi--[<code>EnableHttpService.startDeveloperRestApi</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>service.memcached.port</code></p></td>
<td class="tableblock halign-left valign-top"><p>The port of the
embedded Memcached server (service).</p></td>
<td
class="tableblock halign-left valign-top"><p><code>11211</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableMemcachedServer.html#port--[<code>EnableMemcachedServer.port</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>service.memcached.protocol</code></p></td>
<td class="tableblock halign-left valign-top"><p>The protocol used by
the embedded Memcached server (service).</p></td>
<td
class="tableblock halign-left valign-top"><p><code>ASCII</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableMemcachedServer.html#protocol--[<code>EnableMemcachedServer.protocol</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>service.redis.bind-address</code></p></td>
<td class="tableblock halign-left valign-top"><p>The IP address or
hostname of the system NIC used by the embedded Redis server to bind and
listen for connections.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableRedisServer.html#bindAddress--[<code>EnableRedis.bindAddress</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>service.redis.port</code></p></td>
<td class="tableblock halign-left valign-top"><p>The port used by the
embedded Redis server to listen for connections.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>6479</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/config/annotation/EnableRedisServer.html#port--[<code>EnableRedisServer.port</code>]</p></td>
</tr>
</tbody>
</table>

Table 17. `spring.data.gemfire.*` Service properties

### Spring Session Based Properties

The following properties all have a `spring.session.data.gemfire.*`
prefix. For example, to set the session Region name, set
`spring.session.data.gemfire.session.region.name` in Spring Boot
`application.properties`.

<table class="tableblock frame-all grid-all" style="width: 90%;">
<caption>Table 18. <code>spring.session.data.gemfire.*</code>
properties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Name</th>
<th class="tableblock halign-left valign-top">Description</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">From</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.client.pool.name</code></p></td>
<td class="tableblock halign-left valign-top"><p>Name of the pool used
to send data access operations between the client and servers.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>gemfirePool</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/autorepo/docs/spring-session-data-geode-build/2.7.1/api/org/springframework/session/data/gemfire/config/annotation/web/http/EnableGemFireHttpSession.html#poolName--[<code>EnableGemFireHttpSession.poolName</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>cache.client.Region.shortcut</code></p></td>
<td class="tableblock halign-left valign-top"><p>The
<code>DataPolicy</code> used by the client Region to manage (HTTP)
session state.</p></td>
<td
class="tableblock halign-left valign-top"><p>https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/client/ClientRegionShortcut.html#PROXY[<code>ClientRegionShortcut.PROXY</code>]</p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/autorepo/docs/spring-session-data-geode-build/2.7.1/api/org/springframework/session/data/gemfire/config/annotation/web/http/EnableGemFireHttpSession.html#clientRegionShortcut--[<code>EnableGemFireHttpSession.clientRegionShortcut</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>cache.server.Region.shortcut</code></p></td>
<td class="tableblock halign-left valign-top"><p>The
<code>DataPolicy</code> used by the server Region to manage (HTTP)
session state.</p></td>
<td
class="tableblock halign-left valign-top"><p>https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/RegionShortcut.html#PARTITION[<code>RegionShortcut.PARTITION</code>]</p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/autorepo/docs/spring-session-data-geode-build/2.7.1/api/org/springframework/session/data/gemfire/config/annotation/web/http/EnableGemFireHttpSession.html#serverRegionShortcut--[<code>EnableGemFireHttpSession.serverRegionShortcut</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>session.attributes.indexable</code></p></td>
<td class="tableblock halign-left valign-top"><p>The names of session
attributes for which an Index is created.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>[]</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/autorepo/docs/spring-session-data-geode-build/2.7.1/api/org/springframework/session/data/gemfire/config/annotation/web/http/EnableGemFireHttpSession.html#indexableSessionAttributes--[<code>EnableGemFireHttpSession.indexableSessionAttributes</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>session.expiration.max-inactive-interval-seconds</code></p></td>
<td class="tableblock halign-left valign-top"><p>Configures the number
of seconds in which a session can remain inactive before it
expires.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>1800</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/autorepo/docs/spring-session-data-geode-build/2.7.1/api/org/springframework/session/data/gemfire/config/annotation/web/http/EnableGemFireHttpSession.html#maxInactiveIntervalSeconds--[<code>EnableGemFireHttpSession.maxInactiveIntervalSeconds</code>]</p></td>
</tr>
<tr class="even">
<td
class="tableblock halign-left valign-top"><p><code>session.Region.name</code></p></td>
<td class="tableblock halign-left valign-top"><p>The name of the
(client/server) Region used to manage (HTTP) session state.</p></td>
<td
class="tableblock halign-left valign-top"><p><code>ClusteredSpringSessions</code></p></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/autorepo/docs/spring-session-data-geode-build/2.7.1/api/org/springframework/session/data/gemfire/config/annotation/web/http/EnableGemFireHttpSession.html#RegionName--[<code>EnableGemFireHttpSession.RegionName</code>]</p></td>
</tr>
<tr class="odd">
<td
class="tableblock halign-left valign-top"><p><code>session.serializer.bean-name</code></p></td>
<td class="tableblock halign-left valign-top"><p>The name of a Spring
bean that implements
<code>org.springframework.session.data.gemfire.serialization.SessionSerializer</code>.</p></td>
<td class="tableblock halign-left valign-top"></td>
<td
class="tableblock halign-left valign-top"><p>https://docs.spring.io/autorepo/docs/spring-session-data-geode-build/2.7.1/api/org/springframework/session/data/gemfire/config/annotation/web/http/EnableGemFireHttpSession.html#sessionSerializerBeanName--[<code>EnableGemFireHttpSession.sessionSerializerBeanName</code>]</p></td>
</tr>
</tbody>
</table>

Table 18. `spring.session.data.gemfire.*` properties

### VMware GemFire Properties

While we do not recommend using VMware GemFire properties directly
in your Spring applications, SBDG does not prevent you from doing so.
See the
[complete reference to the VMware GemFire specific properties](https://geode.apache.org/docs/guide/115/reference/topics/gemfire_properties.html).

<p class="warning"><strong>Warning:</strong>
VMware GemFire is very strict about the
properties that may be specified in a <code>gemfire.properties</code>
file. You cannot mix Spring properties with <code>gemfire.*</code>
properties in a VMware GemFire <code>gemfire.properties</code>
file.
</p>
