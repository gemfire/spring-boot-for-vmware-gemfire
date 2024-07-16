/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.petclinic.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;

/**
 * Abstract Data Type (ADT) modeling a pet.
 *
 * @author John Blum
 * @see org.springframework.data.annotation.Id
 * @see org.springframework.data.gemfire.mapping.annotation.Region
 * @since 1.2.1
 */
@Getter
@NoArgsConstructor
@Region("Pets")
@ToString(of = "name")
@EqualsAndHashCode(of = "name")
@RequiredArgsConstructor(staticName = "newPet")
@SuppressWarnings("unused")
public class Pet {

	private LocalDateTime vaccinationDateTime;

	@Id @NonNull
  private String name;

	private Type petType;

	public Pet as(Type petType) {
		this.petType = petType;
		return this;
	}

	public void vaccinate() {
		this.vaccinationDateTime = LocalDateTime.now(ZoneOffset.UTC);
	}

	public enum Type {
		CAT,
		DOG,
		RABBIT,
	}
}
