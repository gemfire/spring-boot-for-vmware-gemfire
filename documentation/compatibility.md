---
title: Compatibility and Versions
---

Spring Boot for VMware GemFire provides the convenience of Spring Boot's convention over configuration approach by using auto-configuration with Spring Framework's powerful abstractions and highly consistent programming model to simplify the development of VMware GemFire applications.

## Compatibility

<table>
   <tr>
      <th style="text-align: left">Spring Boot for VMware GemFire Artifact</th>
      <th style="text-align: left">Versions</th>
      <th style="text-align: left">Compatible GemFire Versions</th>
      <th style="text-align: left">Compatible Spring Boot Version</th>
   </tr>
   <tr>
      <td>spring-boot-2.6-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>9.15.x</td>
      <td>2.6.x</td>
   </tr>
      <td>spring-boot-2.7-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>9.15.x</td>
      <td>2.7.x</td>
   <tr>
      <td>spring-boot-3.0-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>9.15.x</td>
      <td>3.0.x</td>
   </tr>
   <tr>
      <td>spring-boot-2.7-gemfire-10.0</td>
      <td>1.0.0</td>
      <td>10.0.x</td>
      <td>2.7.x</td>
   </tr>
   <tr>
      <td>spring-boot-3.0-gemfire-10.0</td>
      <td>1.0.0</td>
      <td>10.0.x</td>
      <td>3.0.x</td>
   </tr>
</table>


## Modules
Your application may require more than one module if, for example, you may need (HTTP) Session state management, or you may need to enable Spring Boot Actuator endpoints for [vmware-gemfire-name].

You can declare and use any one of the [spring-boot-gemfire-name] modules (in addition to the Spring Boot for GemFire dependency).  

### Spring Boot Actuator
<table>
   <tr>
      <th style="text-align: left">Module</th>
      <th style="text-align: left">Versions</th>
      <th style="text-align: left">Compatible Spring Boot for GemFire Artifact</th>
   </tr>
   <tr>
      <td>spring-boot-actuator-2.6-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>spring-boot-2.6-gemfire-9.15</td>
   </tr>
      <td>spring-boot-actuator-2.7-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>spring-boot-2.7-gemfire-9.15</td>
   <tr>
      <td>spring-boot-actuator-3.0-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>spring-boot-3.0-gemfire-9.15</td>
   </tr>
   <tr>
      <td>spring-boot-actuator-2.7-gemfire-10.0</td>
      <td>1.0.0</td>
      <td>spring-boot-2.7-gemfire-10.0</td>
   </tr>
   <tr>
      <td>spring-boot-actuator-3.0-gemfire-10.0</td>
      <td>1.0.0</td>
      <td>spring-boot-3.0-gemfire-10.0</td>
   </tr>
</table>

### Spring Boot Logging
<table>
   <tr>
      <th style="text-align: left">Module</th>
      <th style="text-align: left">Versions</th>
      <th style="text-align: left">Compatible Spring Boot for GemFire Artifact</th>
   </tr>
   <tr>
      <td>spring-boot-logging-2.6-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>spring-boot-2.6-gemfire-9.15</td>
   </tr>
      <td>spring-boot-logging-2.7-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>spring-boot-2.7-gemfire-9.15</td>
   <tr>
      <td>spring-boot-logging-3.0-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>spring-boot-3.0-gemfire-9.15</td>
   </tr>
   <tr>
      <td>spring-boot-logging-2.7-gemfire-10.0</td>
      <td>1.0.0</td>
      <td>spring-boot-2.7-gemfire-10.0</td>
   </tr>
   <tr>
      <td>spring-boot-logging-3.0-gemfire-10.0</td>
      <td>1.0.0</td>
      <td>spring-boot-3.0-gemfire-10.0</td>
   </tr>
</table>

### Spring Session 
<table>
   <tr>
      <th style="text-align: left">Module</th>
      <th style="text-align: left">Versions</th>
      <th style="text-align: left">Compatible Spring Boot for GemFire Artifact</th>
   </tr>
   <tr>
      <td>spring-boot-session-2.6-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>spring-boot-2.6-gemfire-9.15</td>
   </tr>
      <td>spring-boot-session-2.7-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>spring-boot-2.7-gemfire-9.15</td>
   <tr>
      <td>spring-boot-session-3.0-gemfire-9.15</td>
      <td>1.0.0, 1.1.1</td>
      <td>spring-boot-3.0-gemfire-9.15</td>
   </tr>
   <tr>
      <td>spring-boot-session-2.7-gemfire-10.0</td>
      <td>1.0.0</td>
      <td>spring-boot-2.7-gemfire-10.0</td>
   </tr>
   <tr>
      <td>spring-boot-session-3.0-gemfire-10.0</td>
      <td>1.0.0</td>
      <td>spring-boot-3.0-gemfire-10.0</td>
   </tr>
</table>