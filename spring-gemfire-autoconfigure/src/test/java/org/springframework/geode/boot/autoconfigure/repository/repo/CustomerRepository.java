/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.repository.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.geode.boot.autoconfigure.repository.model.Customer;

/**
 * The {@link CustomerRepository} interface defines a Spring Data {@link CrudRepository} for performing basic CRUD
 * and simple query data access operations on {@link Customer} objects stored in Apache Geode.
 *
 * @author John Blum
 * @see org.springframework.data.repository.CrudRepository
 * @see org.springframework.geode.boot.autoconfigure.repository.model.Customer
 * @since 1.0.0
 */
public interface CustomerRepository extends CrudRepository<Customer, Long> {

	Customer findByName(String name);

}
