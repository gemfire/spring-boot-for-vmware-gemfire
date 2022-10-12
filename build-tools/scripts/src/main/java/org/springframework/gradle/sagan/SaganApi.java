/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.gradle.sagan;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.Base64;

/**
 * Implements necessary calls to the Sagan API See https://spring.io/restdocs/index.html
 */
public class SaganApi {
	private String baseUrl = "https://spring.io/api";

	private OkHttpClient client;
	private Gson gson = new Gson();

	public SaganApi(String gitHubToken) {
		this.client = new OkHttpClient.Builder()
				.addInterceptor(new BasicInterceptor("not-used", gitHubToken))
				.build();
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public void createReleaseForProject(Release release, String projectName) {
		String url = this.baseUrl + "/projects/" + projectName + "/releases";
		String releaseJsonString = gson.toJson(release);
		RequestBody body = RequestBody.create(MediaType.parse("application/json"), releaseJsonString);
		Request request = new Request.Builder()
			.url(url)
			.post(body)
			.build();
		try {
			Response response = this.client.newCall(request).execute();
			if (!response.isSuccessful()) {
				throw new RuntimeException("Could not create release " + release + ". Got response " + response);
			}
		} catch (IOException fail) {
			throw new RuntimeException("Could not create release " + release, fail);
		}
	}

	public void deleteReleaseForProject(String release, String projectName) {
		String url = this.baseUrl + "/projects/" + projectName + "/releases/" + release;
		Request request = new Request.Builder()
				.url(url)
				.delete()
				.build();
		try {
			Response response = this.client.newCall(request).execute();
			if (!response.isSuccessful()) {
				throw new RuntimeException("Could not delete release " + release + ". Got response " + response);
			}
		} catch (IOException fail) {
			throw new RuntimeException("Could not delete release " + release, fail);
		}
	}

	private static class BasicInterceptor implements Interceptor {

		private final String token;

		public BasicInterceptor(String username, String token) {
			this.token = Base64.getEncoder().encodeToString((username + ":" + token).getBytes());
		}

		@Override
		public okhttp3.Response intercept(Chain chain) throws IOException {
			Request request = chain.request().newBuilder()
					.addHeader("Authorization", "Basic " + this.token).build();
			return chain.proceed(request);
		}
	}
}
