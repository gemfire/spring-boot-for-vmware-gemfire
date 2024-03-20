/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.service;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import example.app.model.Contact;
import example.app.repo.ContactRepository;

/**
 * Spring {@link Service} class for servicing {@link Contact Contacts}.
 *
 * @author John Blum
 * @see org.springframework.cache.annotation.Cacheable
 * @see org.springframework.cache.annotation.CachePut
 * @see org.springframework.stereotype.Service
 * @see example.app.model.Contact
 * @see example.app.repo.ContactRepository
 * @since 1.2.0
 */
@Service
public class ContactsService {

	private final AtomicBoolean cacheMiss = new AtomicBoolean(false);

	private final ContactRepository contactRepository;

	public ContactsService(ContactRepository contactRepository) {

		Assert.notNull(contactRepository, "ContactRepository is required");

		this.contactRepository = contactRepository;
	}

	public boolean isCacheMiss() {
		return this.cacheMiss.getAndSet(false);
	}

	@Cacheable(cacheNames = "ContactsByName")
	public Contact findByName(String name) {

		this.cacheMiss.set(true);

		return this.contactRepository.findById(name).orElseThrow(() ->
			newIllegalStateException("No Contact with name [%s] was found", name));
	}

	@CachePut(cacheNames = "ContactsByName", key="#contact.name")
	public Contact save(Contact contact) {
		return this.contactRepository.save(contact);
	}
}
