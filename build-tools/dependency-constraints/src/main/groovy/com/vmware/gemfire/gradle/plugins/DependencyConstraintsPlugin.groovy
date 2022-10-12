/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.vmware.gemfire.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class DependencyConstraintsPlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {
    new DependencyConstraints().apply(project)
  }
}
