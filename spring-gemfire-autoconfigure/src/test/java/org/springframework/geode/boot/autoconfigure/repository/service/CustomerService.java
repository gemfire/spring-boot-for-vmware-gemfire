/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.repository.service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.geode.boot.autoconfigure.repository.model.Customer;
import org.springframework.geode.boot.autoconfigure.repository.repo.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * The {@link CustomerService} class is an application service for managing {@link Customer Customers}.
 *
 * @author John Blum
 * @see org.springframework.geode.boot.autoconfigure.repository.model.Customer
 * @see org.springframework.geode.boot.autoconfigure.repository.repo.CustomerRepository
 * @see org.springframework.stereotype.Service
 * @since 1.0.0
 */
@Service
public class CustomerService {

	private final CustomerRepository customerRepository;

	private final AtomicLong identifierSequence = new AtomicLong(0L);

	public CustomerService(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	public CustomerRepository getCustomerRepository() {

		Assert.state(this.customerRepository != null,
			"CustomerRepository was not properly configured");

		return this.customerRepository;
	}

	public Optional<Customer> findBy(String name) {
		return Optional.ofNullable(getCustomerRepository().findByName(name));
	}

	protected Long nextId() {
		return identifierSequence.incrementAndGet();
	}

	public Customer save(Customer customer) {

		Assert.state(customer != null, "Customer is required");

		if (customer.isNew()) {
			customer = customer.identifiedBy(nextId());
		}

		return getCustomerRepository().save(customer);
	}
}
