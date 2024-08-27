/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.crm.repo;

import org.springframework.data.repository.CrudRepository;

import example.app.crm.model.Customer;

/**
 * {@link CustomerRepository} is a Spring Data {@link CrudRepository} and Data Access Object (DAO) defining basic CRUD
 * and simple query data access operations on {@link Customer} objects.
 *
 * @author John Blum
 * @see java.lang.Long
 * @see org.springframework.data.repository.CrudRepository
 * @see example.app.crm.model.Customer
 * @since 1.1.0
 */
public interface CustomerRepository extends CrudRepository<Customer, Long> {

	Customer findByName(String name);

}
