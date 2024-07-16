/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.topology.clientserver;

import static org.assertj.core.api.Assertions.assertThat;
import com.vmware.gemfire.testcontainers.GemFireCluster;
import jakarta.annotation.Resource;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.geode.cache.DataPolicy;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.mapping.annotation.Region;
import org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport;
import org.springframework.data.repository.CrudRepository;
import org.springframework.geode.config.annotation.ClusterAwareConfiguration;
import org.springframework.geode.config.annotation.EnableClusterAware;
import org.springframework.geode.pdx.MappingPdxSerializerIncludedTypesRegistrar;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing and asserting the interaction between an Apache Geode client &amp; server
 * in the client/server topology, bootstrapped, configured and initialized with Spring Boot for Apache Geode.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.context.annotation.Profile
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.test.context.ActiveProfiles
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.5.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
	properties = "spring.data.gemfire.management.use-http=false",
	webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@SuppressWarnings("unused")
public class SpringBootApacheGeodeClientServerIntegrationTests extends ForkingClientServerIntegrationTestsSupport {

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void runGemFireServer() {
		String dockerImage = System.getProperty("spring.test.gemfire.docker.image");
		gemFireCluster = new GemFireCluster(dockerImage,1,1)
				.withGfsh(false, "create region --name=Users --type=REPLICATE");
		gemFireCluster.acceptLicense().start();

		System.setProperty("spring.data.gemfire.pool.locators", "localhost[" + gemFireCluster.getLocatorPort() + "]");
	}

	@BeforeClass @AfterClass
	public static void resetClusterAwareCondition() {
		ClusterAwareConfiguration.ClusterAwareCondition.reset();
	}

	@Resource(name = "Users")
	private org.apache.geode.cache.Region<String, User> users;

	@Autowired
	private UserRepository userRepository;

	@Before
	public void assertRegionAndRepositoryConfiguration() {

		assertThat(this.users).isNotNull();
		assertThat(this.users.getAttributes()).isNotNull();
		assertThat(this.users.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.EMPTY);
		assertThat(this.userRepository).isNotNull();
		assertThat(this.userRepository.count()).isZero();
	}

	@Test
	public void saveAndFindUserIsSuccessful() {

		User jonDoe = User.as("jonDoe");

		assertThat(this.userRepository.save(jonDoe)).isNotNull();

		User jonDoeFoundById = this.userRepository.findById(jonDoe.getName()).orElse(null);

		assertThat(jonDoeFoundById).isEqualTo(jonDoe);
		assertThat(jonDoeFoundById).isNotSameAs(jonDoe);
	}

	@SpringBootApplication
	@EnableClusterAware
	@EnableEntityDefinedRegions(basePackageClasses = User.class)
	static class TestGeodeClientConfiguration {

		@Bean
		BeanPostProcessor mappingPdxSerializerIncludeTypeFilterRegistrar() {
			return MappingPdxSerializerIncludedTypesRegistrar.with(User.class);
		}
	}
}

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "as")
@Region("Users")
class User {

	@Id
	@lombok.NonNull
	private final String name;

}

interface UserRepository extends CrudRepository<User, String> { }
