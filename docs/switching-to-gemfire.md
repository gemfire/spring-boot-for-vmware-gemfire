# Switching from Apache Geode to VMware GemFire or VMware GemFire for TAS


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

Spring Boot for Apache Geode (SBDG) stopped providing support for
VMware GemFire after SBDG 1.3. SBDG 1.3 was the last version to
support both Apache Geode and VMware GemFire. If you need
support for Apache Geode in Spring Boot, then you will need to
downgrade to SBDG 1.3.

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
<td class="content">This section is now deprecated. Spring Boot for
Apache Geode (SBDG) no longer provides the
<code>spring-gemfire-starter</code> or related starter modules. As of
SBDG 1.4, SBDG is based on VMware GemFire 9.10. Standalone GemFire
bits based on VMware GemFire are no longer being released by
VMware, Inc. after GemFire 9.10. GemFire 9.10 was based on
Apache Geode 1.12, and SBDG can no longer properly support
standalone GemFire bits (version <= 9.10).</td>
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
<td class="content">What was Pivotal GemFire has now been rebranded as
https://pivotal.io/pivotal-gemfire[VMware GemFire] and what was Pivotal
Cloud Cache (PCC) running on Pivotal CloudFoundry (PCF) has been
rebranded as [VMware GemFire for TAS](https://pivotal.io/pivotal-cloud-cache) and
[Tanzu Application Service (TAS)](https://pivotal.io/platform),
respectively.</td>
</tr>
</tbody>
</table>

