/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.gradle.sagan;

import io.spring.gradle.convention.Utils;
import org.gradle.api.*;

public class SaganPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		project.getTasks().register("saganCreateRelease", SaganCreateReleaseTask.class, new Action<SaganCreateReleaseTask>() {
			@Override
			public void execute(SaganCreateReleaseTask saganCreateVersion) {
				saganCreateVersion.setGroup("Release");
				saganCreateVersion.setDescription("Creates a new version for the specified project on spring.io");
				saganCreateVersion.setVersion((String) project.findProperty("nextVersion"));
				saganCreateVersion.setProjectName(Utils.getProjectName(project));
				saganCreateVersion.setGitHubAccessToken((String) project.findProperty("gitHubAccessToken"));
			}
		});
		project.getTasks().register("saganDeleteRelease", SaganDeleteReleaseTask.class, new Action<SaganDeleteReleaseTask>() {
			@Override
			public void execute(SaganDeleteReleaseTask saganDeleteVersion) {
				saganDeleteVersion.setGroup("Release");
				saganDeleteVersion.setDescription("Delete a version for the specified project on spring.io");
				saganDeleteVersion.setVersion((String) project.findProperty("previousVersion"));
				saganDeleteVersion.setProjectName(Utils.getProjectName(project));
				saganDeleteVersion.setGitHubAccessToken((String) project.findProperty("gitHubAccessToken"));
			}
		});
	}

}
