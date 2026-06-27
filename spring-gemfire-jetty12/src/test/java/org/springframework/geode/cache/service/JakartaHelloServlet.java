/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.cache.service;

import java.io.IOException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Minimal {@code jakarta.servlet}-based servlet used by
 * {@link Jetty12HttpServiceDeploymentIntegrationTests} to verify that a WAR whose classes
 * depend on {@code jakarta.servlet} (Jakarta EE 10) deploys correctly under Jetty's EE10
 * environment.
 */
public class JakartaHelloServlet extends HttpServlet {

	public static final String RESPONSE = "Hello from jakarta.servlet EE10";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain;charset=UTF-8");
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getWriter().write(RESPONSE);
	}
}
