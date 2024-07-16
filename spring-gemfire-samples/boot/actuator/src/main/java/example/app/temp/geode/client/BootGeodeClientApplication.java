/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright 2017-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package example.app.temp.geode.client;

import example.app.temp.event.BoilingTemperatureEvent;
import example.app.temp.event.FreezingTemperatureEvent;
import example.app.temp.event.TemperatureEvent;
import example.app.temp.service.TemperatureMonitor;
import java.util.Optional;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.config.annotation.EnableStatistics;
import org.springframework.data.gemfire.config.annotation.RegionConfigurer;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.geode.config.annotation.UseGroups;
import org.springframework.scheduling.annotation.EnableScheduling;

import example.app.temp.model.TemperatureReading;
import example.app.temp.repo.TemperatureReadingRepository;
import example.app.temp.service.TemperatureSensor;

// tag::class[]
@SpringBootApplication
@EnableEntityDefinedRegions(basePackageClasses = TemperatureReading.class)
@EnableGemfireRepositories(basePackageClasses = TemperatureReadingRepository.class)
@EnableScheduling
@EnableStatistics
@UseGroups("TemperatureSensors")
@SuppressWarnings("unused")
public class BootGeodeClientApplication {

	public static void main(String[] args) {

		new SpringApplicationBuilder(BootGeodeClientApplication.class)
				.web(WebApplicationType.SERVLET)
				.build()
				.run(args);
	}

	@Bean
	TemperatureSensor temperatureSensor(TemperatureReadingRepository repository) {
		return new TemperatureSensor(repository);
	}

	@Bean
	TemperatureMonitor temperatureMonitor(ApplicationEventPublisher applicationEventPublisher) {
		return new TemperatureMonitor(applicationEventPublisher);
	}

	@Bean
	RegionConfigurer temperatureReadingsConfigurer() {

		return new RegionConfigurer() {

			@Override
			public void configure(String beanName, ClientRegionFactoryBean<?, ?> regionBean) {

				Optional.ofNullable(beanName)
						.filter("TemperatureReadings"::equals)
						.ifPresent(it -> regionBean.setStatisticsEnabled(true));
			}
		};
	}

	@EventListener(classes = { BoilingTemperatureEvent.class, FreezingTemperatureEvent.class })
	public void temperatureEventHandler(TemperatureEvent temperatureEvent) {
		System.err.printf("%1$s TEMPERATURE READING [%2$s]%n",
				temperatureEvent instanceof BoilingTemperatureEvent ? "HOT" : "COLD",
				temperatureEvent.getTemperatureReading());
	}
}
// end::class[]
