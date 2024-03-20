/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.repo;

import org.springframework.data.repository.CrudRepository;

import example.app.model.Contact;

/**
 * Spring Data {@link CrudRepository} and Data Access Object (DAO) for performing basic CRUD and simple Query
 * data access operations on a {@link Contact}.
 *
 * @author John Blum
 * @see org.springframework.data.repository.CrudRepository
 * @see example.app.model.Contact
 * @since 1.2.0
 */
//public interface ContactRepository extends JpaRepository<Contact, String> {
public interface ContactRepository extends CrudRepository<Contact, String> {

}
