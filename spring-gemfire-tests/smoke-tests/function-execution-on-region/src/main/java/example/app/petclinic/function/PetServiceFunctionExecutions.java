/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.petclinic.function;

import org.springframework.data.gemfire.function.annotation.FunctionId;
import org.springframework.data.gemfire.function.annotation.OnRegion;

import example.app.petclinic.model.Pet;

/**
 * Function interface declaring all {@link Pet} services administered by the Pet Clinic.
 *
 * @author John Blum
 * @see example.app.petclinic.model.Pet
 * @since 1.2.1
 */
@OnRegion(region = "Pets")
public interface PetServiceFunctionExecutions {

	@FunctionId(("AdministerPetVaccinations"))
	void administerPetVaccinations();

}
