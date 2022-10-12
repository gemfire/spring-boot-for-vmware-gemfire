/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.gradle.sagan;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class SaganCreateReleaseTask extends DefaultTask {

	@Input
	private String gitHubAccessToken;
	@Input
	private String version;
	@Input
	private String apiDocUrl;
	@Input
	private String referenceDocUrl;
	@Input
	private String projectName;

	@TaskAction
	public void saganCreateRelease() {
		SaganApi sagan = new SaganApi(this.gitHubAccessToken);
		Release release = new Release();
		release.setVersion(this.version);
		release.setApiDocUrl(this.apiDocUrl);
		release.setReferenceDocUrl(this.referenceDocUrl);
		sagan.createReleaseForProject(release, this.projectName);
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

	public String getApiDocUrl() {
		return apiDocUrl;
	}

	public void setApiDocUrl(String apiDocUrl) {
		this.apiDocUrl = apiDocUrl;
	}

	public String getReferenceDocUrl() {
		return referenceDocUrl;
	}

	public void setReferenceDocUrl(String referenceDocUrl) {
		this.referenceDocUrl = referenceDocUrl;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

}
