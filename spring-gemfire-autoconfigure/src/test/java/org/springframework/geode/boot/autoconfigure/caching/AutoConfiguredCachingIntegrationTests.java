/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.caching;

import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.gemfire.config.annotation.EnableCachingDefinedRegions;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.data.gemfire.util.RegionUtils;
import org.springframework.test.context.junit4.SpringRunner;

import example.app.books.NonBeanType;
import example.app.books.model.Book;
import example.app.books.service.support.CachingBookService;

/**
 * Integration Tests testing the auto-configuration of Spring's Cache Abstraction with Apache Geode
 * as the caching provider.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.data.gemfire.config.annotation.EnableCachingDefinedRegions
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.geode.boot.autoconfigure.CachingProviderAutoConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@SuppressWarnings("unused")
public class AutoConfiguredCachingIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private CachingBookService bookService;

	@Resource(name = "CachedBooks")
	private Region<String, Book> cachedBooks;

	private void assertBook(Book book, String title) {

		assertThat(book).isNotNull();
		assertThat(book.isNew()).isFalse();
		assertThat(book.getTitle()).isEqualTo(title);
	}

	@Test
	public void bookServiceWasConfiguredCorrectly() {

		assertThat(this.bookService).isNotNull();
		assertThat(this.bookService.isCacheMiss()).isFalse();
	}

	@Test
	public void cachedBooksRegionWasConfiguredCorrectly() {

		assertThat(this.cachedBooks).isNotNull();
		assertThat(this.cachedBooks.getName()).isEqualTo("CachedBooks");
		assertThat(this.cachedBooks.getFullPath()).isEqualTo(RegionUtils.toRegionPath("CachedBooks"));
		assertThat(this.cachedBooks).isEmpty();
	}

	@Test
	public void geodeAsTheCachingProviderWasAutoConfiguredCorrectly() {

		assertThat(this.cachedBooks).isEmpty();

		Book bookOne = this.bookService.findByTitle("Star Wars 3 - Revenge of the Sith");

		assertBook(bookOne, "Star Wars 3 - Revenge of the Sith");
		assertThat(this.bookService.isCacheMiss()).isTrue();
		assertThat(this.cachedBooks).hasSize(1);
		assertThat(this.cachedBooks.get(bookOne.getTitle())).isEqualTo(bookOne);

		Book bookOneAgain = this.bookService.findByTitle(bookOne.getTitle());

		assertThat(bookOneAgain).isEqualTo(bookOne);
		assertThat(this.bookService.isCacheMiss()).isFalse();
		assertThat(this.cachedBooks).hasSize(1);
		assertThat(this.cachedBooks.get(bookOne.getTitle())).isEqualTo(bookOne);

		Book bookTwo = this.bookService.findByTitle("Star Wars 6 - Return of the Jedi");

		assertBook(bookTwo, "Star Wars 6 - Return of the Jedi");
		assertThat(this.bookService.isCacheMiss()).isTrue();
		assertThat(this.cachedBooks).hasSize(2);
		assertThat(this.cachedBooks.get(bookOne.getTitle())).isEqualTo(bookOne);
		assertThat(this.cachedBooks.get(bookTwo.getTitle())).isEqualTo(bookTwo);
	}

	@SpringBootApplication(scanBasePackageClasses = NonBeanType.class)
	@EnableCachingDefinedRegions(clientRegionShortcut = ClientRegionShortcut.LOCAL)
	@EnableGemFireMockObjects
	static class TestConfiguration {  }

}
