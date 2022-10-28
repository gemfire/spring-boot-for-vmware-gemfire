---
Title: Function Implementations & Executions
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



This chapter is about using VMware GemFire in a Spring context
for distributed computing use cases.





### Background



Distributed computing, particularly in conjunction with data access and
mutation operations, is a very effective and efficient use of clustered
computing resources. This is similar to
{wikipedia-docs}/MapReduce\[MapReduce\].





A naively conceived query returning potentially hundreds of thousands
(or even millions) of rows of data in a result set to the application
that queried and requested the data can be very costly, especially under
load. Therefore, it is typically more efficient to move the processing
and computations on the predicated data set to where the data resides,
perform the required computations, summarize the results, and then send
the reduced data set back to the client.





Additionally, when the computations are handled in parallel, across the
cluster of computing resources, the operation can be performed much more
quickly. This typically involves intelligently organizing the data using
various partitioning (a.k.a. sharding) strategies to uniformly balance
the data set across the cluster.





VMware GemFire addresses this very important application concern
in its
https://geode.apache.org/docs/guide/115/developing/function_exec/chapter_overview.html\[Function
execution\] framework.





Spring Data for VMware GemFire
https://docs.spring.io/spring-data/geode/docs/current/reference/html/#function-annotations\[builds\] on this
Function execution framework by letting developers
https://docs.spring.io/spring-data/geode/docs/current/reference/html/#function-implementation\[implement\] and
https://docs.spring.io/spring-data/geode/docs/current/reference/html/#function-execution\[execute\]
VMware GemFire functions with a simple POJO-based annotation
configuration model.





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
<td class="content">See
https://docs.spring.io/spring-data/geode/docs/current/reference/html/#_implementation_vs_execution[the section
about implementation versus execution] for the difference between
Function implementation and execution.</td>
</tr>
</tbody>
</table>





Taking this a step further, Spring Boot for VMware GemFire
auto-configures and enables both Function implementation and execution
out-of-the-box. Therefore, you can immediately begin writing Functions
and invoking them without having to worry about all the necessary
plumbing to begin with. You can rest assured that it works as expected.







### Applying Functions



Earlier, when we talked about [caching](#geode-caching-provider), we
described a `FinancialLoanApplicationService` class that could process
eligibility when someone (represented by a `Person` object) applied for
a financial loan.





This can be a very resource intensive and expensive operation, since it
might involve collecting credit and employment history, gathering
information on outstanding loans, and so on. We applied caching in order
to not have to recompute or redetermine eligibility every time a loan
office may want to review the decision with the customer.





But, what about the process of computing eligibility in the first place?





Currently, the application’s `FinancialLoanApplicationService` class
seems to be designed to fetch the data and perform the eligibility
determination in place. However, it might be far better to distribute
the processing and even determine eligibility for a larger group of
people all at once, especially when multiple, related people are
involved in a single decision, as is typically the case.





We can implement an `EligibilityDeterminationFunction` class by using
SDG:







Example 1. Function implementation









``` highlight
@Component
class EligibilityDeterminationFunction {

    @GemfireFunction(HA = true, hasResult = true, optimizeForWrite=true)
    public EligibilityDecision determineEligibility(FunctionContext functionContext, Person person, Timespan timespan) {
        // ...
    }
}
```











By using the SDG
https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/data/gemfire/function/annotation/GemfireFunction.html\[`@GemfireFunction`\]
annotation, we can implement our Function as a POJO method. SDG
appropriately handles registering this POJO method as a proper Function
with VMware GemFire.





If we now want to call this function from our Spring Boot `ClientCache`
application, we can define a function execution interface with a method
name that matches the function name and that targets the execution on
the `EligibilityDecisions` Region:







Example 2. Function execution









``` highlight
@OnRegion("EligibilityDecisions")
interface EligibilityDeterminationExecution {

  EligibilityDecision determineEligibility(Person person, Timespan timespan);

}
```











We can then inject an instance of the
`EligibilityDeterminationExecution` interface into our
`FinancialLoanApplicationService`, as we would any other object or
Spring bean:







Example 3. Function use









``` highlight
@Service
class FinancialLoanApplicationService {

    private final EligibilityDeterminationExecution execution;

    public LoanApplicationService(EligibilityDeterminationExecution execution) {
        this.execution = execution;
    }

    @Cacheable("EligibilityDecisions")
    EligibilityDecision processEligibility(Person person, Timespan timespan) {
        return this.execution.determineEligibility(person, timespan);
    }
}
```











As with caching, no additional configuration is required to enable and
find your application Function implementations and executions. You can
simply build and run. Spring Boot for VMware GemFire handles the
rest.





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
<td class="content">It is common to "implement" and register your
application Functions on the server and "execute" them from the
client.</td>
</tr>
</tbody>
</table>











<div id="footer">

<div id="footer-text">

Last updated 2022-10-10 12:13:25 -0700




