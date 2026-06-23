/*
 * Copyright 2023-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.cache.service;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Minimal {@code javax.servlet}-based servlet used by
 * {@link Jetty12HttpServiceDeploymentIntegrationTests} to verify that a WAR whose classes
 * depend on {@code javax.servlet} (Java EE 8 / Spring 5.x) deploys correctly under
 * Jetty's EE8 environment without requiring a Jakarta EE bytecode migration.
 */
public class HelloServlet extends HttpServlet {

	public static final String RESPONSE = "Hello from javax.servlet EE8";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain;charset=UTF-8");
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getWriter().write(RESPONSE);
	}
}
