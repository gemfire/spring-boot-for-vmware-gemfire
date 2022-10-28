# Deactivating Auto-configuration

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

If you would like to deactivate the auto-configuration of any feature
provided by Spring Boot for VMware GemFire, you can specify the
auto-configuration class in the `exclude` attribute of the
`@SpringBootApplication` annotation:

Example 9. Deactivate Auto-configuration of PDX


``` highlight
@SpringBootApplication(exclude = PdxSerializationAutoConfiguration.class)
public class MySpringBootApplication {

  public static void main(String[] args) {
    SpringApplication.run(MySpringBootApplication.class, args);
  }
}
```

You can deactivate more than one auto-configuration class at a time by
specifying each class in the `exclude` attribute using array syntax:

Example 10. Deactivate Auto-configuration of PDX & SSL


``` highlight
@SpringBootApplication(exclude = { PdxSerializationAutoConfiguration.class, SslAutoConfiguration.class })
public class MySpringBootApplication {

  public static void main(String[] args) {
    SpringApplication.run(MySpringBootApplication.class, args);
  }
}
```

#### <a id='auto-configuration-deactivate-classes'></a>Complete Set of Auto-configuration Classes

The current set of auto-configuration classes in Spring Boot for
VMware GemFire includes:

- `CacheNameAutoConfiguration`

- `CachingProviderAutoConfiguration`

- `ClientCacheAutoConfiguration`

- `ClientSecurityAutoConfiguration`

- `ContinuousQueryAutoConfiguration`

- `FunctionExecutionAutoConfiguration`

- `GemFirePropertiesAutoConfiguration`

- `LoggingAutoConfiguration`

- `PdxSerializationAutoConfiguration`

- `PeerSecurityAutoConfiguration`

- `RegionTemplateAutoConfiguration`

- `RepositoriesAutoConfiguration`

- `SpringSessionAutoConfiguration`

- `SpringSessionPropertiesAutoConfiguration`

- `SslAutoConfiguration`


