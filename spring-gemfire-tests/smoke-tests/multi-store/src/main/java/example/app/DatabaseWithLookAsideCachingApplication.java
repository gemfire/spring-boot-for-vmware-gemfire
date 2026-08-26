/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app;

import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.config.annotation.EnableCachingDefinedRegions;

import example.app.model.Contact;

/**
 * The {@link DatabaseWithLookAsideCachingApplication} class is a Spring Boot application testing Spring Data's
 * multi-store support along with using Apache Geode as a caching provider in Spring's Cache Abstraction.
 *
 * @author John Blum
 * @see org.springframework.boot.SpringApplication
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.autoconfigure.domain.EntityScan
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions
 * @see example.app.model.Contact
 * @since 1.2.0
 */
@SpringBootApplication
@SuppressWarnings("unused")
public class DatabaseWithLookAsideCachingApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatabaseWithLookAsideCachingApplication.class, args);
	}

	@Configuration
	@EntityScan(basePackageClasses = Contact.class)
	@EnableCachingDefinedRegions(clientRegionShortcut = ClientRegionShortcut.LOCAL)
	static class ApplicationConfiguration { }

}
