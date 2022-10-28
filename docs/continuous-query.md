---
Title: Continuous Query
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


Some applications must process a stream of events as they happen and
intelligently react in (near) real-time to the countless changes in the
data over time. Those applications need frameworks that can make
processing a stream of events as they happen as easy as possible.





Spring Boot for VMware GemFire does just that, without users
having to perform any complex setup or configure any necessary
infrastructure components to enable such functionality. Developers can
define the criteria for the data of interest and implement a handler
(listener) to process the stream of events as they occur.





https://geode.apache.org/docs/guide/115/developing/continuous_querying/chapter_overview.html\[Continuous
Query (CQ)\] lets you easily define your criteria for the data you need.
With CQ, you can express the criteria that match the data you need by
specifying a query predicate. VMware GemFire implements the
https://geode.apache.org/docs/guide/115/developing/querying_basics/query_basics.html\[Object
Query Language (OQL)\] for defining and executing queries. OQL resembles
SQL and supports projections, query predicates, ordering, and
aggregates. Also, when used in CQs, they execute continuously, firing
events when the data changes in such ways as to match the criteria
expressed in the query predicate.





Spring Boot for VMware GemFire combines the ease of identifying
the data you need by using an OQL query statement with implementing the
listener callback (handler) in one easy step.





For example, suppose you want to perform some follow-up action when a
customer’s financial loan application is either approved or denied.





First, the application model for our `EligibilityDecision` class might
look something like the following:







Example 1. EligibilityDecision class









``` highlight
@Region("EligibilityDecisions")
class EligibilityDecision {

    private final Person person;

    private Status status = Status.UNDETERMINED;

    private final Timespan timespan;

    enum Status {

        APPROVED,
        DENIED,
        UNDETERMINED,

    }
}
```











Then we can implement and declare our CQ event handler methods to be
notified when an eligibility decision is either `APPROVED` or `DENIED`:











``` highlight
@Component
class EligibilityDecisionPostProcessor {

    @ContinuousQuery(name = "ApprovedDecisionsHandler",
        query = "SELECT decisions.*
                 FROM /EligibilityDecisions decisions
                 WHERE decisions.getStatus().name().equalsIgnoreCase('APPROVED')")
    public void processApprovedDecisions(CqEvent event) {
        // ...
    }

    @ContinuousQuery(name = "DeniedDecisionsHandler",
        query = "SELECT decisions.*
                 FROM /EligibilityDecisions decisions
                 WHERE decisions.getStatus().name().equalsIgnoreCase('DENIED')")
    public void processDeniedDecisions(CqEvent event) {
        // ...
    }
}
```











Thus, when eligibility is processed and a decision has been made, either
approved or denied, our application gets notified, and as an application
developer, you are free to code your handler and respond to the event
any way you like. Also, because our Continuous Query (CQ) handler class
is a component (or a bean in the Spring `ApplicationContext`) you can
auto-wire any other beans necessary to carry out the application’s
intended function.





This is not unlike Spring’s
https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#jms-annotated\[annotation-driven
listener endpoints\], which are used in (JMS) message listeners and
handlers, except in Spring Boot for VMware GemFire, you need not
do anything special to enable this functionality. You can declare the
`@ContinuousQuery` annotation on any POJO method and go to work on other
things.









<div id="footer">

<div id="footer-text">

Last updated 2022-10-10 12:13:35 -0700




