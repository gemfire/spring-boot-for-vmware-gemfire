/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.geode.locator;

import java.util.Scanner;

import org.apache.geode.distributed.Locator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.gemfire.GemFireProperties;
import org.springframework.data.gemfire.config.annotation.LocatorApplication;
import org.springframework.data.gemfire.config.annotation.LocatorConfigurer;

/**
 * Spring Boot, Apache Geode {@link Locator} application.
 *
 * @author John Blum
 * @see org.apache.geode.distributed.Locator
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Profile
 * @see org.springframework.data.gemfire.GemFireProperties
 * @see org.springframework.data.gemfire.config.annotation.LocatorApplication
 * @see org.springframework.data.gemfire.config.annotation.LocatorConfigurer
 * @since 1.3.0
 */
@SpringBootApplication
@LocatorApplication(name = "SpringBootApacheGeodeLocatorApplication", port = 0)
@SuppressWarnings("unused")
public class SpringBootApacheGeodeLocatorApplication {

	public static void main(String[] args) {

		ConfigurableApplicationContext applicationContext =
			SpringApplication.run(SpringBootApacheGeodeLocatorApplication.class, args);

		block(applicationContext.getEnvironment());
	}

	@Bean
	@Profile("locator-configurer")
	LocatorConfigurer clusterLocatorConfigurer(
		@Value("${spring.data.gemfire.locators:localhost[10334]}") String locators) {

		return (beanName, bean) -> bean.getGemFireProperties()
			.setProperty(GemFireProperties.LOCATORS.toString(), locators);
	}

	private static void block(Environment environment) {

		if (environment.getProperty("spring.boot.data.geode.locator.prompt-for-shutdown", Boolean.class, false)) {
			System.err.println("Press <ENTER> to exit.");
			new Scanner(System.in).nextLine();
		}
	}
}
