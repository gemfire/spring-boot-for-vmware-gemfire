/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.crm.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import example.app.crm.model.Customer;

/**
 * {@link CustomerService} is a Spring {@link Service @Service} class servicing {@link Customer Customers}.
 *
 * @author John Blum
 * @see org.springframework.cache.annotation.Cacheable
 * @see org.springframework.stereotype.Service
 * @see example.app.crm.model.Customer
 * @since 1.2.0
 */
@Service
public class CustomerService {

	@Cacheable("CustomersByName")
	public Customer findByName(String name) {
		return Customer.newCustomer(System.currentTimeMillis(), name);
	}
}
