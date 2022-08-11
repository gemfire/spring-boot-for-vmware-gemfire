package com.vmware.gemfire.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class DependencyConstraintsPlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {
    new DependencyConstraints().apply(project)
  }
}
