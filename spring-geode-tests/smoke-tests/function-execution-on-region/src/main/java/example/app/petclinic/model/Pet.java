/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.petclinic.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Abstract Data Type (ADT) modeling a pet.
 *
 * @author John Blum
 * @see org.springframework.data.annotation.Id
 * @see org.springframework.data.gemfire.mapping.annotation.Region
 * @since 1.2.1
 */
@Region("Pets")
@ToString(of = "name")
@EqualsAndHashCode(of = "name")
@RequiredArgsConstructor(staticName = "newPet")
@SuppressWarnings("unused")
public class Pet {

	@Getter
	private LocalDateTime vaccinationDateTime;

	@Id @NonNull @Getter
	private String name;

	@Getter
	private Type petType;

	public Pet as(Type petType) {
		this.petType = petType;
		return this;
	}

	public void vaccinate() {
		this.vaccinationDateTime = LocalDateTime.now();
	}

	public enum Type {
		CAT,
		DOG,
		RABBIT,
	}
}
