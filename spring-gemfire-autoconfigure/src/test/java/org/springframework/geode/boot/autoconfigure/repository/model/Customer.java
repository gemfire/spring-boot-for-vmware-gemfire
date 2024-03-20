/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.repository.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * {@link Customer} class and Abstract Data Type (ADT) modeling a customer.
 *
 * @author John Blum
 * @see lombok
 * @see org.springframework.data.annotation.Id
 * @see org.springframework.data.gemfire.mapping.annotation.Region
 * @since 1.0.0
 */
@Data
@Region("Customers")
@RequiredArgsConstructor(staticName = "newCustomer")
public class Customer {

	@Id
	private Long id;

	@NonNull
	private String name;

	public boolean isNew() {
		return getId() == null;
	}

	public Customer identifiedBy(Long id) {
		setId(id);
		return this;
	}
}
