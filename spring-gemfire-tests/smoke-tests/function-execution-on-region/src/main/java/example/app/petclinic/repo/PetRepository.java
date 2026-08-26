/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.petclinic.repo;

import org.springframework.data.repository.CrudRepository;

import example.app.petclinic.model.Pet;

/**
 * Data Access Object (DAO) containing basic CRUD and simple Query data access operations on {@link Pet} objects.
 *
 * @author John Blum
 * @see org.springframework.data.repository.CrudRepository
 * @see example.app.petclinic.model.Pet
 * @since 1.2.1
 */
public interface PetRepository extends CrudRepository<Pet, String> {

}
