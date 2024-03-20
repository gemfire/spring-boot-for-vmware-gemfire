/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.cache.inline;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;

import example.app.crm.model.Customer;
import example.app.crm.repo.CustomerRepository;

/**
 * Abstract base class encapsulating functionality and test cases common to all Inline Caching Integration Tests.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.GemfireTemplate
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see example.app.crm.model.Customer
 * @see example.app.crm.repo.CustomerRepository
 * @since 1.1.0
 */
@SuppressWarnings("unused")
public abstract class AbstractInlineCachingWithExternalDataSourceIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private GemfireTemplate customersTemplate;

	@Before
	public void setup() {

		assertThat(this.customerRepository).isNotNull();
		assertThat(this.customerRepository.count()).isEqualTo(1);
		assertThat(this.customerRepository.existsById(16L)).isTrue();

		Customer pieDoe = this.customerRepository.findByName("Pie Doe");

		assertThat(pieDoe).isNotNull();
		assertThat(pieDoe.getId()).isEqualTo(16L);
		assertThat(pieDoe.getName()).isEqualTo("Pie Doe");
	}

	@Test
	public void cacheLoadsFromExternalDataSource() {

		assertThat(this.customersTemplate.containsKey(16L)).isFalse();

		Customer pieDoe = this.customersTemplate.get(16L);

		assertThat(pieDoe).isNotNull();
		assertThat(pieDoe.getId()).isEqualTo(16L);
		assertThat(pieDoe.getName()).isEqualTo("Pie Doe");

		assertThat(this.customersTemplate.containsKey(16L)).isTrue();
	}

	@Test
	public void cacheWritesToExternalDataSource() {

		Customer jonDoe = Customer.newCustomer(2L, "Jon Doe");

		assertThat(this.customerRepository.existsById(jonDoe.getId())).isFalse();
		assertThat(this.customerRepository.findByName(jonDoe.getName())).isNull();
		assertThat(this.customersTemplate.containsKey(jonDoe.getId())).isFalse();

		this.customersTemplate.put(jonDoe.getId(), jonDoe);

		assertThat(this.customersTemplate.containsKey(jonDoe.getId())).isTrue();
		assertThat(this.customerRepository.existsById(jonDoe.getId())).isTrue();

		Customer jonDoeLoaded = this.customerRepository.findByName(jonDoe.getName());

		assertThat(jonDoeLoaded).isNotNull();
		assertThat(jonDoeLoaded).isEqualTo(jonDoe);
	}
}
