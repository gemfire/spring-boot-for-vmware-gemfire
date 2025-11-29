/*
 * Copyright 2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import org.gradle.kotlin.dsl.create

/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

pluginManagement {
  repositories.gradlePluginPortal()
  repositories.google()
  repositories.maven {
    url = uri("https://repo.spring.io/plugins-release")
  }
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
  repositories.mavenCentral()
  versionCatalogs {
    create("libs") {
      from(files("../../gradle/publish.versions.toml"))
    }
  }
}
