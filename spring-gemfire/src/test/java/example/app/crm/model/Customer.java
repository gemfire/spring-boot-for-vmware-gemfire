/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.crm.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.springframework.data.cassandra.core.mapping.Indexed;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.gemfire.mapping.annotation.Region;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The {@link Customer} class is an Abstract Data Type (ADT) modeling a customer.
 *
 * @author John Blum
 * @see jakarta.persistence.Entity
 * @see jakarta.persistence.Table
 * @see org.springframework.data.cassandra.core.mapping.Indexed
 * @see org.springframework.data.cassandra.core.mapping.PrimaryKey
 * @see org.springframework.data.cassandra.core.mapping.Table
 * @see org.springframework.data.gemfire.mapping.annotation.Region
 * @since 1.1.0
 */
@Data
@Entity
@Region("Customers")
@Table(name = "Customers")
@org.springframework.data.cassandra.core.mapping.Table("Customers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "newCustomer")
public class Customer {

	@PrimaryKey
	@jakarta.persistence.Id
	private Long id;

	@Indexed
	private String name;

}
