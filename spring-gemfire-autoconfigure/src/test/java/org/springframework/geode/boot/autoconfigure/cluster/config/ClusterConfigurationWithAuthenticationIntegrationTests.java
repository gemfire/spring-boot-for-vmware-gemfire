/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.cluster.config;

import static com.vmware.gemfire.testcontainers.GemFireCluster.ALL_GLOB;
import static org.assertj.core.api.Assertions.assertThat;

import example.app.books.model.Book;
import example.app.books.model.ISBN;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.geode.cache.DataPolicy;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.config.annotation.EnableClusterConfiguration;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.geode.boot.autoconfigure.security.TestSecurityManager;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.vmware.gemfire.testcontainers.GemFireCluster;

/**
 * Integration Tests testing the SDG {@link EnableClusterConfiguration} annotation functionality when the Apache Geode
 * server is configured with Security (Authentication).
 *
 * @author John Blum
 * @see java.net.URI
 * @see org.junit.Test
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.boot.ApplicationRunner
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.builder.SpringApplicationBuilder
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Profile
 * @see org.springframework.data.gemfire.GemfireTemplate
 * @see org.springframework.data.gemfire.config.annotation.EnableClusterConfiguration
 * @see org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.geode.security.TestSecurityManager
 * @see org.springframework.test.annotation.DirtiesContext
 * @see org.springframework.test.context.ActiveProfiles
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.0.0
 */
@ActiveProfiles("cluster-configuration-with-auth-client")
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ClusterConfigurationWithAuthenticationIntegrationTests.GeodeClientConfiguration.class,
		webEnvironment = SpringBootTest.WebEnvironment.NONE,
		properties = { "spring.data.gemfire.security.username=test", "spring.data.gemfire.security.password=test" })
@SuppressWarnings("unused")
public class ClusterConfigurationWithAuthenticationIntegrationTests {

	private static final AtomicBoolean REDIRECTING_CLIENT_HTTP_REQUEST_INTERCEPTOR_INVOKED = new AtomicBoolean(false);

	@BeforeClass
	public static void startGemFireServer() throws IOException {
		String dockerImage = System.getProperty("spring.test.gemfire.docker.image");

		GemFireCluster gemFireCluster = new GemFireCluster(dockerImage, 1, 1);
		gemFireCluster.withPdx("example\\.app\\.books\\.model\\..*", true)
				.withGemFireProperty(GemFireCluster.ALL_GLOB, "security-manager", TestSecurityManager.class.getName())
				.withClasspath(GemFireCluster.ALL_GLOB, System.getProperty("TEST_JAR_PATH"))
				.withGemFireProperty(ALL_GLOB, "security-username", "cluster")
				.withGemFireProperty(ALL_GLOB, "security-password", "cluster");

		gemFireCluster.acceptLicense().start();

		System.setProperty("spring.data.gemfire.pool.locators", "localhost[" + gemFireCluster.getLocatorPort() + "]");
		System.setProperty("spring.data.gemfire.pool.locator.port", String.valueOf(gemFireCluster.getLocatorPort()));
		System.setProperty("spring.data.gemfire.management.http.port",
				String.valueOf(gemFireCluster.getHttpPorts().get(0)));
	}

	@Autowired
	@Qualifier("booksTemplate") private GemfireTemplate booksTemplate;

	@Before
	public void setup() {

		assertThat(this.booksTemplate).isNotNull();
		assertThat(this.booksTemplate.getRegion()).isNotNull();
		assertThat(this.booksTemplate.getRegion().getName()).isEqualTo("Books");
		assertThat(this.booksTemplate.getRegion().getAttributes()).isNotNull();
		assertThat(this.booksTemplate.getRegion().getAttributes().getDataPolicy()).isEqualTo(DataPolicy.EMPTY);
	}

	@After
	public void tearDown() {
		assertThat(REDIRECTING_CLIENT_HTTP_REQUEST_INTERCEPTOR_INVOKED.get()).isFalse();
	}

	@Test
	public void clusterConfigurationAndRegionDataAccessOperationsAreSuccessful() {

		Book expectedSeriesOfUnfortunateEvents = Book.newBook("A Series of Unfortunate Events")
				.identifiedBy(ISBN.autoGenerated());

		this.booksTemplate.put(expectedSeriesOfUnfortunateEvents.getIsbn(), expectedSeriesOfUnfortunateEvents);

		Book actualSeriesOfUnfortunateEvents = this.booksTemplate.get(expectedSeriesOfUnfortunateEvents.getIsbn());

		assertThat(actualSeriesOfUnfortunateEvents).isNotNull();
		assertThat(actualSeriesOfUnfortunateEvents).isEqualTo(expectedSeriesOfUnfortunateEvents);
		assertThat(actualSeriesOfUnfortunateEvents).isNotSameAs(expectedSeriesOfUnfortunateEvents);
	}

	@SpringBootApplication
	@Profile("cluster-configuration-with-auth-client")
	@EnableClusterConfiguration(useHttp = true, requireHttps = false)
	@EnableEntityDefinedRegions(basePackageClasses = Book.class)
	static class GeodeClientConfiguration {

		// NOTE: This ClientHttpRequestInterceptor bean should no longer be picked up by SDG's Cluster Configuration
		// infrastructure as of SD Moore-SR1
		@Bean
		ClientHttpRequestInterceptor testRedirectingClientHttpRequestInterceptor() {

			return (request, body, execution) -> {

				REDIRECTING_CLIENT_HTTP_REQUEST_INTERCEPTOR_INVOKED.set(true);

				String urlPattern = "%1$s://%2$s:%3$d%4$s";

				URI originalUri = request.getURI();
				URI redirectedUri = URI.create(String.format(urlPattern, originalUri.getScheme(), "nonExistingHost",
						originalUri.getPort(), originalUri.getPath()));

				HttpMethod httpMethod = request.getMethod();

				httpMethod = httpMethod != null ? httpMethod : HttpMethod.GET;

				ClientHttpRequest newRequest = new SimpleClientHttpRequestFactory().createRequest(redirectedUri, httpMethod);

				return execution.execute(newRequest, body);
			};
		}
	}
}
