/*************************************************************************
 * Copyright (c) 2021, The Eclipse Foundation and others.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which accompanies this
 * distribution, and is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *************************************************************************/
package org.eclipse.dash.licenses.review;

import org.gitlab4j.api.Constants.IssueState;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.IssuesApi;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.IssueFilter;

public class GitLabConnection {
	private GitLabApi gitLabApi;
	private String path;

	public GitLabConnection(GitLabApi gitLabApi, String path) {
		this.gitLabApi = gitLabApi;
		this.path = path;
	}

	public Issue findIssue(GitLabReview review) throws GitLabApiException {
		String title = review.getTitle();
		IssueFilter filter = new IssueFilter().withState(IssueState.OPENED);
		return getIssuesApi()
				.getIssuesStream(path, filter)
				.filter(issue -> issue.getTitle().equals(title))
				.findAny()
				.orElse(null);
	}

	public Issue createIssue(GitLabReview review) throws GitLabApiException {
		return getIssuesApi()
				.createIssue(path, review.getTitle(), review.getDescription(), false, null, null, review.getLabels(),
						null, null, null, null);
	}

	public String getUserId() {
		try {
			return gitLabApi.getUserApi().getCurrentUser().getUsername();
		} catch (GitLabApiException e) {
			throw new RuntimeException(e);
		}
	}

	private IssuesApi getIssuesApi() {
		return gitLabApi.getIssuesApi();
	}
}
