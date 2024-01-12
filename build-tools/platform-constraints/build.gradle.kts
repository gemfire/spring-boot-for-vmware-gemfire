plugins {
  id("java-platform")
}

group = "com.vmware.gemfire.spring.boot"

javaPlatform.allowDependencies()

dependencies {
  api(platform(bom.spring.framework.bom))
  api(platform(bom.spring.boot.dependencies.bom))
  api(platform(bom.spring.security.bom))
  api(platform(bom.testcontainers.dependencies.bom))
}
