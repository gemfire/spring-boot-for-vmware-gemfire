/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.gradle.sagan;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class SaganDeleteReleaseTask extends DefaultTask {

	@Input
	private String gitHubAccessToken;
	@Input
	private String version;
	@Input
	private String projectName;

	@TaskAction
	public void saganCreateRelease() {
		SaganApi sagan = new SaganApi(this.gitHubAccessToken);
		sagan.deleteReleaseForProject(this.version, this.projectName);
	}

	public String getGitHubAccessToken() {
		return gitHubAccessToken;
	}

	public void setGitHubAccessToken(String gitHubAccessToken) {
		this.gitHubAccessToken = gitHubAccessToken;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

}
