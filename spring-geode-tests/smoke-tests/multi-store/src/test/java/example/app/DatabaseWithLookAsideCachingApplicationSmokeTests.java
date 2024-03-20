/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.junit4.SpringRunner;

import example.app.model.Contact;
import example.app.repo.ContactRepository;
import example.app.service.ContactsService;

/**
 * Smoke Tests for {@link DatabaseWithLookAsideCachingApplication}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.2.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@SuppressWarnings("unused")
public class DatabaseWithLookAsideCachingApplicationSmokeTests extends IntegrationTestsSupport {

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private ContactsService contactsService;

	@Before
	public void setup() {

		assertThat(this.contactRepository).isNotNull();
		assertThat(this.contactRepository.findById("Fro Doe").isPresent()).isTrue();
		assertThat(this.contactsService).isNotNull();
		assertThat(this.contactsService.isCacheMiss()).isInstanceOf(Boolean.class);
	}

	private void assertContact(Contact actual, Contact expected) {
		assertContact(actual, expected.getName(), expected.getEmailAddress(), expected.getPhoneNumber());
	}

	private void assertContact(Contact contact, String name, String emailAddress, String phoneNumber) {

		assertThat(contact).isNotNull();
		assertThat(contact.getName()).isEqualTo(name);
		assertThat(contact.getEmailAddress()).isEqualTo(emailAddress);
		assertThat(contact.getPhoneNumber()).isEqualTo(phoneNumber);
	}

	@Test
	public void loadsExistingContactFromDatabaseThenGetsFromCache() {

		assertThat(this.contactsService.isCacheMiss()).isFalse();

		Contact froDoe = this.contactsService.findByName("Fro Doe");

		assertContact(froDoe, "Fro Doe", "frodoe@home.com", "503-555-1234");
		assertThat(this.contactsService.isCacheMiss()).isTrue();

		Contact froDoeReloaded = this.contactsService.findByName(froDoe.getName());

		assertContact(froDoeReloaded, froDoe);
		assertThat((this.contactsService.isCacheMiss())).isFalse();
	}

	@Test
	public void savesNewContactToDatabaseAndCachesContact() {

		assertThat(this.contactsService.isCacheMiss()).isFalse();

		Contact pieDoe = Contact.newContact("Pie Doe")
			.withEmailAddress("pieDoe@work.com")
			.withPhoneNumber("503-555-4321");

		pieDoe = this.contactsService.save(pieDoe);

		assertThat(this.contactsService.isCacheMiss()).isFalse();

		Contact pieDoeLoaded = this.contactsService.findByName(pieDoe.getName());

		assertContact(pieDoeLoaded, pieDoe);
		assertThat(this.contactsService.isCacheMiss()).isFalse();
	}

	@Test
	public void savesNewContactToDatabaseUsingRepositoryThenLoadsContactFromDatabaseAndCachesContact() {

		assertThat(this.contactsService.isCacheMiss()).isFalse();

		Contact giDoe = Contact.newContact("Gi Doe")
			.withEmailAddress("giDoe@thefarm.org")
			.withPhoneNumber("971-555-8899");

		giDoe = this.contactRepository.save(giDoe);

		Contact giDoeLoaded = this.contactsService.findByName(giDoe.getName());

		assertContact(giDoeLoaded, giDoe);
		assertThat(this.contactsService.isCacheMiss()).isTrue();

		Contact giDoeReloaded = this.contactsService.findByName(giDoe.getName());

		assertContact(giDoeReloaded, giDoe);
		assertThat(this.contactsService.isCacheMiss()).isFalse();
	}
}
