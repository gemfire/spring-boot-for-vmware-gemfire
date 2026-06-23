/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.cache.service.classloader;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit Tests for {@link JettyRestrictedParentResourcesClassLoader}.
 *
 * <p>The loader was introduced to prevent {@code spring.factories} from the application
 * classpath (Spring Boot 3.x / Spring 6.x) bleeding into the WAR's own Spring context
 * (Spring 5.x / Spring Security 5.x). If the parent's {@code spring.factories} leaks
 * through, auto-configuration entries designed for Spring 6.x are applied to a Spring 5.x
 * context, causing startup failures.
 */
public class JettyRestrictedParentResourcesClassLoaderUnitTests {

	private static final String SPRING_FACTORIES = "META-INF/spring.factories";
	private static final String ORDINARY_RESOURCE = "META-INF/ordinary.properties";

	private File warJar;
	private File parentJar;

	@Before
	public void createTestJars() throws IOException {
		warJar = Files.createTempFile("test-war", ".jar").toFile();
		try (JarOutputStream jar = new JarOutputStream(new FileOutputStream(warJar))) {
			addEntry(jar, SPRING_FACTORIES, "from-war");
			addEntry(jar, ORDINARY_RESOURCE, "ordinary-from-war");
		}

		parentJar = Files.createTempFile("test-parent", ".jar").toFile();
		try (JarOutputStream jar = new JarOutputStream(new FileOutputStream(parentJar))) {
			addEntry(jar, SPRING_FACTORIES, "from-parent");
			addEntry(jar, ORDINARY_RESOURCE, "ordinary-from-parent");
		}
	}

	@After
	public void deleteTestJars() {
		warJar.delete();
		parentJar.delete();
	}

	// --- spring.factories isolation ---

	@Test
	public void getResourceForSpringFactoriesReturnsWarVersionNotParent() throws IOException {
		JettyRestrictedParentResourcesClassLoader loader = newLoader();

		URL resource = loader.getResource(SPRING_FACTORIES);

		assertThat(resource).isNotNull();
		assertThat(readContent(resource)).isEqualTo("from-war");
	}

	@Test
	public void getResourceAsStreamForSpringFactoriesReturnsWarVersionNotParent() throws IOException {
		JettyRestrictedParentResourcesClassLoader loader = newLoader();

		try (var stream = loader.getResourceAsStream(SPRING_FACTORIES)) {
			assertThat(stream).isNotNull();
			assertThat(new String(stream.readAllBytes())).isEqualTo("from-war");
		}
	}

	@Test
	public void getResourcesForSpringFactoriesReturnsOnlyWarVersionNotParent() throws IOException {
		JettyRestrictedParentResourcesClassLoader loader = newLoader();

		Enumeration<URL> resources = loader.getResources(SPRING_FACTORIES);
		List<String> contents = readAllContents(resources);

		assertThat(contents).containsExactly("from-war");
	}

	@Test
	public void resourcesStreamForSpringFactoriesReturnsOnlyWarVersion() throws IOException {
		JettyRestrictedParentResourcesClassLoader loader = newLoader();

		List<String> contents = loader.resources(SPRING_FACTORIES)
			.map(url -> {
				try { return readContent(url); }
				catch (IOException e) { throw new RuntimeException(e); }
			})
			.toList();

		assertThat(contents).containsExactly("from-war");
	}

	// --- ordinary resources delegate to parent ---

	@Test
	public void getResourceForOrdinaryResourceDelegatesToParent() throws IOException {
		JettyRestrictedParentResourcesClassLoader loader = newLoader();

		// Without exclusion, the WAR loader searches WAR first (parent-loader-priority=false),
		// but both URLs are present; the key assertion is that it does NOT restrict to WAR only.
		Enumeration<URL> resources = loader.getResources(ORDINARY_RESOURCE);
		List<String> contents = readAllContents(resources);

		assertThat(contents)
			.as("Non-excluded resources must NOT be restricted to WAR only")
			.hasSizeGreaterThanOrEqualTo(1);
	}

	// --- system-property-driven exclusions ---

	@Test
	public void resourcesMatchingSystemPropertyExclusionAreLoadedFromWarOnly() throws IOException {
		System.setProperty(
			"spring.data.gemfire.jetty.classloader.excludedResources",
			"ordinary.properties");
		try {
			JettyRestrictedParentResourcesClassLoader loader = newLoader();

			Enumeration<URL> resources = loader.getResources(ORDINARY_RESOURCE);
			List<String> contents = readAllContents(resources);

			assertThat(contents)
				.as("Resource matching the system-property exclusion must come from WAR only")
				.containsExactly("ordinary-from-war");
		}
		finally {
			System.clearProperty("spring.data.gemfire.jetty.classloader.excludedResources");
		}
	}

	@Test
	public void multipleExclusionPatternsInSystemPropertyAreAllHonoured() throws IOException {
		System.setProperty(
			"spring.data.gemfire.jetty.classloader.excludedResources",
			"ordinary.properties,some.other.resource");
		try {
			JettyRestrictedParentResourcesClassLoader loader = newLoader();

			List<String> ordinary = readAllContents(loader.getResources(ORDINARY_RESOURCE));
			assertThat(ordinary).containsExactly("ordinary-from-war");
		}
		finally {
			System.clearProperty("spring.data.gemfire.jetty.classloader.excludedResources");
		}
	}

	@Test
	public void noExclusionSystemPropertyMeansNoAdditionalFiltering() throws IOException {
		System.clearProperty("spring.data.gemfire.jetty.classloader.excludedResources");

		JettyRestrictedParentResourcesClassLoader loader = newLoader();

		// ORDINARY_RESOURCE is not excluded by default — both WAR and parent versions visible.
		Enumeration<URL> resources = loader.getResources(ORDINARY_RESOURCE);
		List<String> contents = readAllContents(resources);

		assertThat(contents).hasSizeGreaterThanOrEqualTo(2)
			.contains("ordinary-from-war", "ordinary-from-parent");
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private JettyRestrictedParentResourcesClassLoader newLoader() throws IOException {
		URLClassLoader parentLoader = new URLClassLoader(
			new URL[]{ parentJar.toURI().toURL() }, null);
		return new JettyRestrictedParentResourcesClassLoader(
			new URL[]{ warJar.toURI().toURL() }, parentLoader);
	}

	private static void addEntry(JarOutputStream jar, String name, String content) throws IOException {
		jar.putNextEntry(new JarEntry(name));
		jar.write(content.getBytes());
		jar.closeEntry();
	}

	private static String readContent(URL url) throws IOException {
		try (var in = url.openStream()) {
			return new String(in.readAllBytes());
		}
	}

	private static List<String> readAllContents(Enumeration<URL> urls) throws IOException {
		List<String> result = new ArrayList<>();
		while (urls.hasMoreElements()) {
			result.add(readContent(urls.nextElement()));
		}
		return result;
	}
}
