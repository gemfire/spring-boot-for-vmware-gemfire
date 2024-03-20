/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.cache.service.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.stream.Stream;

/**
 * A custom {@link ClassLoader} set in {@link org.eclipse.jetty.ee10.webapp.WebAppContext#setClassLoader(ClassLoader)}
 * in {@link org.springframework.geode.cache.service.Jetty12HttpService} to restrict what resources can be loaded from
 * the parent classloader to avoid Spring version conflicts between the web WAR and the application classpath.
 *
 * @author Patrick Johnson
 * @see org.springframework.geode.cache.service.Jetty12HttpService
 * @see org.eclipse.jetty.ee10.webapp.WebAppContext
 * @see URLClassLoader
 */
public class JettyRestrictedParentResourcesClassLoader extends URLClassLoader {

	private static final String EXCLUDED_RESOURCES_PROPERTY = "spring.data.gemfire.jetty.classloader.excludedResources";
	private static final String DISALLOWED = "spring.factories";

	/**
	* Use {@literal spring.data.gemfire.jetty.classloader.excludedResources} property in {@literal application.properties}.
	*/
	private final String[] excludedResources;

	private final URLClassLoader warOnlyClassLoader;
	public JettyRestrictedParentResourcesClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
		warOnlyClassLoader = new URLClassLoader(urls, null);
		String excludedResources = System.getProperty(EXCLUDED_RESOURCES_PROPERTY);
		this.excludedResources = excludedResources != null? excludedResources.split(","): new String[]{};
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		if(isExcluded(name)) {
			return warOnlyClassLoader.getResourceAsStream(name);
		}
		return super.getResourceAsStream(name);
	}
	@Override
	public URL getResource(String name) {
		if(isExcluded(name)) {
			return warOnlyClassLoader.getResource(name);
		}
		return super.getResource(name);
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		if(isExcluded(name)) {
			return warOnlyClassLoader.getResources(name);
		}
		return super.getResources(name);
	}

	@Override
	public Stream<URL> resources(String name) {
		if(isExcluded(name)) {
			return warOnlyClassLoader.resources(name);
		}
		return super.resources(name);
	}

	private boolean isExcluded(String resource) {
		return resource.contains(DISALLOWED) || Arrays.stream(excludedResources).anyMatch(resource::contains);
	}
}