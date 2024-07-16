/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.cluster.aware;

import static com.vmware.gemfire.testcontainers.GemFireCluster.ALL_GLOB;
import static org.assertj.core.api.Assertions.assertThat;

import example.app.books.model.Book;
import example.app.books.model.ISBN;

import java.io.IOException;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.apache.geode.cache.DataPolicy;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.junit.AfterClass;
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
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.config.support.RestTemplateConfigurer;
import org.springframework.geode.boot.autoconfigure.security.TestSecurityManager;
import org.springframework.geode.config.annotation.ClusterAwareConfiguration;
import org.springframework.geode.config.annotation.EnableClusterAware;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.NonNull;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.utility.MountableFile;

import com.vmware.gemfire.testcontainers.GemFireCluster;

/**
 * Integration Tests testing the {@link EnableClusterAware} annotation configuration when the Apache Geode cluster
 * (server(s)) are secure (i.e. when both Authentication and TLS/SSL are enabled).
 *
 * @author John Blum
 * @see java.security.KeyStore
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.boot.ApplicationRunner
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.builder.SpringApplicationBuilder
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Profile
 * @see org.springframework.core.env.Environment
 * @see org.springframework.core.env.Profiles
 * @see org.springframework.data.gemfire.GemfireTemplate
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.geode.config.annotation.ClusterAwareConfiguration
 * @see org.springframework.geode.config.annotation.EnableClusterAware
 * @see org.springframework.geode.security.TestSecurityManager
 * @see org.springframework.test.annotation.DirtiesContext
 * @see org.springframework.test.context.ActiveProfiles
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.4.1
 */
@ActiveProfiles({ "cluster-aware-with-secure-client", "ssl" })
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SecureClusterAwareConfigurationIntegrationTests.TestGeodeClientConfiguration.class,
		properties = { "spring.data.gemfire.management.require-https=true", "spring.data.gemfire.security.username=test",
				"spring.data.gemfire.security.password=test" },
		webEnvironment = SpringBootTest.WebEnvironment.NONE)
@SuppressWarnings("unused")
public class SecureClusterAwareConfigurationIntegrationTests {

	private static final String SPRING_DATA_GEMFIRE_CACHE_CLIENT_REGION_SHORTCUT_PROPERTY = "spring.data.gemfire.cache.client.region.shortcut";

	@BeforeClass
	public static void startGeodeServer() {
		String dockerImage = System.getProperty("spring.test.gemfire.docker.image");

		GemFireCluster gemFireCluster = new GemFireCluster(dockerImage, 1, 1);
		gemFireCluster.withPdx("example\\.app\\.books\\.model\\..*", true);

		gemFireCluster.withClasspath(GemFireCluster.ALL_GLOB, System.getProperty("TEST_JAR_PATH"));
		gemFireCluster.withPreStart(GemFireCluster.ALL_GLOB, container -> container.copyFileToContainer(MountableFile.forClasspathResource("test-trusted.keystore"),
        "/test-trusted.keystore"));

		gemFireCluster.withGemFireProperty(GemFireCluster.ALL_GLOB, "ssl-enabled-components", "web,locator,server")
				.withGemFireProperty(GemFireCluster.ALL_GLOB, "ssl-keystore", "/test-trusted.keystore")
				.withGemFireProperty(GemFireCluster.ALL_GLOB, "ssl-truststore", "/test-trusted.keystore")
				.withGemFireProperty(GemFireCluster.ALL_GLOB, "ssl-keystore-password", "s3cr3t")
				.withGemFireProperty(GemFireCluster.ALL_GLOB, "ssl-truststore-password", "s3cr3t");

		gemFireCluster.withGemFireProperty(ALL_GLOB, "security-manager", TestSecurityManager.class.getName())
				.withGemFireProperty(ALL_GLOB, "security-username", "cluster")
				.withGemFireProperty(ALL_GLOB, "security-password", "cluster");

		gemFireCluster.acceptLicense().start();
		gemFireCluster.gfsh(true, "create region --name=Books --type=REPLICATE");

		System.setProperty("spring.data.gemfire.pool.locators", "localhost[" + gemFireCluster.getLocatorPort() + "]");
		System.setProperty("spring.data.gemfire.management.http.port",
				String.valueOf(gemFireCluster.getHttpPorts().get(0)));
	}

	@BeforeClass
	@AfterClass
	public static void resetClusterAwareCondition() {
		ClusterAwareConfiguration.ClusterAwareCondition.reset();
	}

	@Autowired
	@Qualifier("booksTemplate") private GemfireTemplate booksTemplate;

	@Before
	public void assertBooksClientRegionIsProxy() {

		assertThat(System.getProperties()).doesNotContainKeys(SPRING_DATA_GEMFIRE_CACHE_CLIENT_REGION_SHORTCUT_PROPERTY);

		assertThat(this.booksTemplate).isNotNull();
		assertThat(this.booksTemplate.getRegion()).isNotNull();
		assertThat(this.booksTemplate.getRegion().getName()).isEqualTo("Books");
		assertThat(this.booksTemplate.getRegion().getAttributes()).isNotNull();
		assertThat(this.booksTemplate.getRegion().getAttributes().getDataPolicy()).isEqualTo(DataPolicy.EMPTY);
	}

	@Test
	public void clientServerConfigurationAndConfigurationIsSuccessful() {

		Book book = Book.newBook("Book of Job").identifiedBy(ISBN.autoGenerated());

		this.booksTemplate.put(book.getIsbn(), book);

		Book returnedBook = this.booksTemplate.get(book.getIsbn());

		assertThat(returnedBook).isNotNull();
		assertThat(returnedBook).isEqualTo(book);
		assertThat(returnedBook).isNotSameAs(book);
	}

	@SpringBootApplication
	@EnableClusterAware
	@EnableEntityDefinedRegions(basePackageClasses = Book.class)
	@Profile(("cluster-aware-with-secure-client"))
	static class TestGeodeClientConfiguration {

		static final String DEFAULT_TRUSTSTORE_PASSWORD = "unknown";
		static final String SSL_PROFILE = "ssl";
		static final String TEST_TRUSTED_KEYSTORE_FILENAME = "test-trusted.keystore";

		@Bean
		RestTemplateConfigurer secureClientHttpRequestConfigurer(Environment environment) {

			return restTemplate -> {

				if (areProfilesActive(environment, SSL_PROFILE)) {
					try {

						char[] trustStorePassword = environment
								.getProperty("spring.data.gemfire.security.ssl.truststore.password", DEFAULT_TRUSTSTORE_PASSWORD)
								.toCharArray();

						KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

						keyStore.load(new ClassPathResource(TEST_TRUSTED_KEYSTORE_FILENAME).getInputStream(), trustStorePassword);

						SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(keyStore, TrustAllStrategy.INSTANCE).build();

						SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
								.setHostnameVerifier(new NoopHostnameVerifier()).setSslContext(sslContext).build();

						HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
								.setSSLSocketFactory(sslSocketFactory).build();

						HttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();

						restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
					} catch (Exception cause) {
						throw new RuntimeException(cause);
					}
				}
			};
		}

		private boolean areProfilesActive(@NonNull Environment environment, @NonNull String... profiles) {
			return environment.acceptsProfiles(Profiles.of(profiles));
		}
	}
}
