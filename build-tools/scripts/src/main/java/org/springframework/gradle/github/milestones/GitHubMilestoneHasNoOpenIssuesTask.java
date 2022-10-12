/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.gradle.github.milestones;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

public class GitHubMilestoneHasNoOpenIssuesTask extends DefaultTask {
	@Input
	private RepositoryRef repository = new RepositoryRef();

	@Input
	private String milestoneTitle;

	@Input @Optional
	private String gitHubAccessToken;

	private GitHubMilestoneApi milestones = new GitHubMilestoneApi();

	@TaskAction
	public void checkHasNoOpenIssues() {
		long milestoneNumber = this.milestones.findMilestoneNumberByTitle(this.repository, this.milestoneTitle);
		boolean isOpenIssues = this.milestones.isOpenIssuesForMilestoneNumber(this.repository, milestoneNumber);
		if (isOpenIssues) {
			throw new IllegalStateException("The repository " + this.repository + " has open issues for milestone with the title " + this.milestoneTitle + " and number " + milestoneNumber);
		}
		System.out.println("The repository " + this.repository + " has no open issues for milestone with the title " + this.milestoneTitle + " and number " + milestoneNumber);
	}

	public RepositoryRef getRepository() {
		return repository;
	}

	public void repository(Action<RepositoryRef> repository) {
		repository.execute(this.repository);
	}

	public void setRepository(RepositoryRef repository) {
		this.repository = repository;
	}

	public String getMilestoneTitle() {
		return milestoneTitle;
	}

	public void setMilestoneTitle(String milestoneTitle) {
		this.milestoneTitle = milestoneTitle;
	}

	public String getGitHubAccessToken() {
		return gitHubAccessToken;
	}

	public void setGitHubAccessToken(String gitHubAccessToken) {
		this.gitHubAccessToken = gitHubAccessToken;
		this.milestones = new GitHubMilestoneApi(gitHubAccessToken);
	}
}
