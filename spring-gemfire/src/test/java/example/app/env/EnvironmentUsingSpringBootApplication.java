/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.env;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * {@link SpringBootApplication} allowing users to review Spring's property resolution precedence.
 *
 * @author John Blum
 * @see org.springframework.boot.ApplicationRunner
 * @see org.springframework.boot.SpringApplication
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.core.env.Environment
 * @since 1.4.0
 */
@SuppressWarnings("unused")
@SpringBootApplication(exclude = CassandraDataAutoConfiguration.class)
public class EnvironmentUsingSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnvironmentUsingSpringBootApplication.class, args);
	}

	@Value("${example.app.property:FROM-CODE}")
	private String testProperty;

	@Bean
	ApplicationRunner environmentRunner(Environment environment) {

		return args -> {
			//System.err.printf("PROPERTY is [%s]%n", environment.getProperty("example.app.property"));
			System.err.printf("PROPERTY is [%s]%n", this.testProperty);
		};
	}
}
