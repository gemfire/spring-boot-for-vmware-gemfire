/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.petclinic;

import static org.assertj.core.api.Assertions.assertThat;
import com.vmware.gemfire.testcontainers.GemFireCluster;
import example.app.petclinic.function.PetServiceFunctionExecutions;
import example.app.petclinic.model.Pet;
import example.app.petclinic.repo.PetRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.shiro.util.CollectionUtils;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.function.config.EnableGemfireFunctionExecutions;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.utility.MountableFile;

/**
 * Smoke Tests asserting the proper injection and execution of an Apache Geode {@link Function} using Spring Data
 * for Apache Geode {@link Function} annotation support in a Spring (Boot) context.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.execute.Function
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.context.annotation.AnnotationConfigApplicationContext
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Profile
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.test.context.ActiveProfiles
 * @see org.springframework.test.context.junit4.SpringRunner
 * @see PetServiceFunctionExecutions
 * @see example.app.petclinic.model.Pet
 * @see example.app.petclinic.repo.PetRepository
 * @since 1.2.1
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@SuppressWarnings("unused")
public class PetClinicApplicationSmokeTests {

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void runGemFireServer() {
		String dockerImage = System.getProperty("spring.test.gemfire.docker.image");
		gemFireCluster = new GemFireCluster(dockerImage,1,1)
				.withPreStart(GemFireCluster.ALL_GLOB, container -> container.copyFileToContainer(MountableFile.forHostPath(System.getProperty("TEST_JAR_PATH")), "/testJar.jar"))
				.withGfsh(false, "deploy --jar=/testJar.jar", "create region --name=Pets --type=REPLICATE")
				.withClasspath(GemFireCluster.ALL_GLOB, System.getProperty("TEST_JAR_PATH"))
				.withPdx("example\\.app\\.petclinic\\.model\\.Pet", false);
		gemFireCluster.acceptLicense().start();

		System.setProperty("spring.data.gemfire.pool.locators", "localhost[" + gemFireCluster.getLocatorPort() + "]");
	}

	private final Pet castle = Pet.newPet("Castle").as(Pet.Type.CAT);
	private final Pet cocoa = Pet.newPet("Cocoa").as(Pet.Type.CAT);
	private final Pet maha = Pet.newPet("Maha").as(Pet.Type.DOG);
	private final Pet mittens = Pet.newPet("Mittens").as(Pet.Type.CAT);

	private final Set<Pet> pets = CollectionUtils.asSet(castle, cocoa, maha, mittens);

	@Autowired
	private PetRepository petRepository;

	@Autowired
	private PetServiceFunctionExecutions petServiceFunctions;

	@Before
	public void setup() {

		assertThat(this.petRepository.count()).isEqualTo(0);

		this.pets.forEach(pet -> assertThat(pet.getVaccinationDateTime()).isNull());
		this.petRepository.saveAll(this.pets);

		assertThat(this.petRepository.count()).isEqualTo(this.pets.size());
	}

	@Test
	public void administerPetVaccinationsIsSuccessful() {

		LocalDateTime beforeVaccinations = LocalDateTime.now(ZoneOffset.UTC);

		this.petServiceFunctions.administerPetVaccinations();

		Awaitility.await().untilAsserted(() -> {
			LocalDateTime afterVaccinations = LocalDateTime.now(ZoneOffset.UTC);
			this.petRepository.findAll().forEach(pet -> {
				assertThat(pet.getVaccinationDateTime())
						.describedAs("Vaccinations [%s] for Pet [%s] was not correct", pet.getVaccinationDateTime(), pet)
						.isAfterOrEqualTo(beforeVaccinations);

				assertThat(pet.getVaccinationDateTime()).isBeforeOrEqualTo(afterVaccinations);
			});
		});
	}

	@EnableEntityDefinedRegions(basePackageClasses = Pet.class)
	@EnableGemfireFunctionExecutions
	@SpringBootApplication(scanBasePackageClasses = PetClinicApplicationSmokeTests.class)
	static class GeodeClientTestConfiguration { }

	public static class PetServiceFunctions implements Function {

		@Override
		public String getId() {
			return "AdministerPetVaccinations";
		}

		@Override
		public void execute(FunctionContext functionContext) {
			Optional.ofNullable(functionContext)
					.filter(RegionFunctionContext.class::isInstance)
					.map(RegionFunctionContext.class::cast)
					.map(RegionFunctionContext::getDataSet)
					.map(Region::values)
					.ifPresent(pets -> pets.forEach(pet -> {

						Pet resolvedPet = (Pet) pet;

						resolvedPet.vaccinate();

						((RegionFunctionContext<?>) functionContext).getDataSet().put(resolvedPet.getName(), resolvedPet);
					}));
		}

		@Override
		public boolean hasResult() {
			return false;
		}

		@Override
		public boolean isHA() {
			return false;
		}

		@Override
		public boolean optimizeForWrite() {
			return true;
		}
	}
}
