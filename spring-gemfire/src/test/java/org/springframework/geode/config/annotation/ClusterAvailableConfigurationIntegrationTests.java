/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import com.vmware.gemfire.testcontainers.GemFireCluster;
import jakarta.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.admin.GemfireAdminOperations;
import org.springframework.data.gemfire.config.admin.remote.RestHttpGemfireAdminTemplate;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableCachingDefinedRegions;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.config.annotation.EnablePdx;
import org.springframework.test.context.junit4.SpringRunner;

import example.app.crm.model.Customer;
import example.app.crm.service.CustomerService;

/**
 * Integration Tests asserting the functionality and behavior of {@link EnableClusterAware}
 * and {@link ClusterAvailableConfiguration} when the Apache Geode cluster of servers is available.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.cache.annotation.Cacheable
 * @see org.springframework.context.annotation.AnnotationConfigApplicationContext
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.data.gemfire.client.ClientRegionFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.CacheServerApplication
 * @see org.springframework.data.gemfire.config.annotation.ClientCacheApplication
 * @see org.springframework.data.gemfire.config.annotation.EnableCachingDefinedRegions
 * @see org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions
 * @see org.springframework.geode.config.annotation.ClusterAvailableConfiguration
 * @see org.springframework.geode.config.annotation.EnableClusterAware
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.2.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = ClusterAvailableConfigurationIntegrationTests.GeodeClientApplication.class,
	properties = { "spring.data.gemfire.management.use-http=true" },
	webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@SuppressWarnings("unused")
public class ClusterAvailableConfigurationIntegrationTests {

	private static final String LOG_LEVEL = "error";
	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void runGemFireServer() throws IOException {
		String dockerImage = System.getProperty("spring.test.gemfire.docker.image");
		gemFireCluster = new GemFireCluster(dockerImage,1,1);
		gemFireCluster.acceptLicense();
		gemFireCluster.start();
		System.setProperty("spring.data.gemfire.pool.locators", "127.0.0.1["+gemFireCluster.getLocatorPort()+"]");
		System.setProperty("spring.data.gemfire.management.http.port", String.valueOf(gemFireCluster.getHttpPorts().get(0)));
	}

	@AfterClass
	public static void tearDown() {
		ClusterAwareConfiguration.ClusterAwareCondition.reset();
	}

	@Autowired
	private ClientCache clientCache;

	@Resource(name = "Customers")
	private Region<Long, Customer> customers;

	@Resource(name = "CustomersByName")
	private Region<String, Customer> customersByName;

	@Resource(name = "Example")
	private Region<Object, Object> example;

	@Before
	public void assertRegionConfiguration() {

		assertRegion(this.example, "Example", DataPolicy.EMPTY, "DEFAULT");
		assertRegion(this.customers, "Customers", DataPolicy.EMPTY, "DEFAULT");
		assertRegion(this.customersByName, "CustomersByName", DataPolicy.EMPTY, "DEFAULT");
	}

	@Before
	public void assertRegionConfigurationOnServers() {
		String regions = gemFireCluster.gfsh(false,"list regions");

		assertThat(regions).contains("CustomersByName");
		assertThat(regions).contains("Customers");
		assertThat(regions).contains("Example");
	}

	private void assertRegion(Region<?, ?> region, String name, DataPolicy dataPolicy, String poolName) {

		assertThat(region).isNotNull();
		assertThat(region.getName()).isEqualTo(name);
		assertThat(region.getAttributes()).isNotNull();
		assertThat(region.getAttributes().getDataPolicy()).isEqualTo(dataPolicy);
		assertThat(region.getAttributes().getPoolName()).isEqualTo(poolName);
	}

	@Test
	public void customersRegionDataAccessOperationsWork() {

		Customer jonDoe = Customer.newCustomer(1L, "Jon Doe");

		assertThat(this.customers.put(jonDoe.getId(), jonDoe)).isNull();
		assertThat(this.customers.get(jonDoe.getId())).isEqualTo(jonDoe);
	}

	@Test
	public void customersByNameRegionDataAccessOperationsWork() {

		Customer jonDoe = Customer.newCustomer(2L, "Jane Doe");

		assertThat(this.customersByName.put(jonDoe.getName(), jonDoe)).isNull();
		assertThat(this.customersByName.get(jonDoe.getName())).isEqualTo(jonDoe);
	}

	@Test
	public void exampleRegionDataAccessOperationsWork() {

		assertThat(this.example.put(1, "TEST")).isNull();
		assertThat(this.example.get(1)).isEqualTo("TEST");
	}

	@EnableClusterAware
	@EnableCachingDefinedRegions
	@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
	@EnablePdx
	@ClientCacheApplication(name = "ClusterAvailableConfigurationIntegrationTests", logLevel = LOG_LEVEL)
	static class GeodeClientApplication {

		@Bean("Example")
		ClientRegionFactoryBean<Object, Object> exampleRegion(GemFireCache cache) {

			ClientRegionFactoryBean<Object, Object> exampleRegion = new ClientRegionFactoryBean<>();

			exampleRegion.setCache(cache);
			exampleRegion.setShortcut(ClientRegionShortcut.PROXY);

			return exampleRegion;
		}

		@Bean
		CustomerService customerService() {
			return new CustomerService();
		}
	}
}
