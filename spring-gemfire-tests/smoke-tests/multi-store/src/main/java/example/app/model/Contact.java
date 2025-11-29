/*
 * Copyright 2023-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.springframework.data.annotation.Id;
import org.springframework.lang.NonNull;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Abstract Data Type (ADT) modeling contact information for a person.
 *
 * @author John Blum
 * @see jakarta.persistence.Entity
 * @see org.springframework.data.annotation.Id
 * @since 1.2.0
 */
@Data
@Entity
@Table(name = "Contacts")
@ToString(of = "name")
@EqualsAndHashCode(of = "name")
@SuppressWarnings("unused")
public class Contact {

	@jakarta.persistence.Id @Id @NonNull
	private String name;

	@Column(name = "email_address")
	private String emailAddress;

	@Column(name = "phone_number")
	private String phoneNumber;

	public Contact() {
	}

	public Contact(@NonNull String name) {
		this.name = name;
	}

	public Contact withEmailAddress(String emailAddress) {
		setEmailAddress(emailAddress);
		return this;
	}

	@NonNull
	public String getName() {
		return name;
	}

	public void setName(@NonNull String name) {
		this.name = name;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Contact withPhoneNumber(String phoneNumber) {
		setPhoneNumber(phoneNumber);
		return this;
	}
}
