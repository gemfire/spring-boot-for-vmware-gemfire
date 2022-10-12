/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.gradle.github.milestones;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class GitHubMilestonePlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		project.getTasks().register("gitHubCheckMilestoneHasNoOpenIssues", GitHubMilestoneHasNoOpenIssuesTask.class, new Action<GitHubMilestoneHasNoOpenIssuesTask>() {
			@Override
			public void execute(GitHubMilestoneHasNoOpenIssuesTask githubCheckMilestoneHasNoOpenIssues) {
				githubCheckMilestoneHasNoOpenIssues.setGroup("Release");
				githubCheckMilestoneHasNoOpenIssues.setDescription("Checks if there are any open issues for the specified repository and milestone");
				githubCheckMilestoneHasNoOpenIssues.setMilestoneTitle((String) project.findProperty("nextVersion"));
				if (project.hasProperty("githubAccessToken")) {
					githubCheckMilestoneHasNoOpenIssues.setGitHubAccessToken((String) project.findProperty("gitHubAccessToken"));
				}
			}
		});
	}
}
