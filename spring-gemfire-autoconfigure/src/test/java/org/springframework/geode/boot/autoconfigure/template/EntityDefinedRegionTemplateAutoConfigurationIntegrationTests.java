/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.template;

import static org.assertj.core.api.Assertions.assertThat;
import example.app.books.model.Author;
import example.app.books.model.Book;
import example.app.books.model.ISBN;
import jakarta.annotation.Resource;
import java.util.stream.Collectors;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.geode.boot.autoconfigure.RegionTemplateAutoConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link RegionTemplateAutoConfiguration} using SDG's {@link EnableEntityDefinedRegions}
 * annotation to define {@link Region Regions} and associated Templates.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.data.gemfire.GemfireTemplate
 * @see org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.geode.boot.autoconfigure.RegionTemplateAutoConfiguration
 * @see org.springframework.test.annotation.DirtiesContext
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.0.0
 */
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@SuppressWarnings("unused")
public class EntityDefinedRegionTemplateAutoConfigurationIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	public ClientCache gemfireCache;

	@Autowired
	@Qualifier("authorsTemplate")
	private GemfireTemplate authorsTemplate;

	@Autowired
	@Qualifier("booksTemplate")
	private GemfireTemplate booksTemplate;

	@Resource(name = "Authors")
	private Region<Long, Author> authors;

	@Resource(name = "Books")
	private Region<ISBN, Book> books;

	@Before
	public void setup() {

		assertThat(this.gemfireCache).isNotNull();

		assertThat(this.gemfireCache.rootRegions().stream()
			.map(Region::getName)
			.sorted()
			.collect(Collectors.toList())).containsExactly("Authors", "Books");

		assertThat(this.authors).isNotNull();
		assertThat(this.authors.getName()).isEqualTo("Authors");
		assertThat(this.books).isNotNull();
		assertThat(this.books.getName()).isEqualTo("Books");
	}

	@Test
	public void authorsRegionTemplateIsPresent() {

		assertThat(this.authorsTemplate).isNotNull();
		assertThat(this.authorsTemplate.getRegion()).isEqualTo(this.authors);
	}

	@Test
	public void booksRegionTemplateIsPresent() {

		assertThat(this.booksTemplate).isNotNull();
		assertThat(this.booksTemplate.getRegion()).isEqualTo(this.books);
	}

	@SpringBootApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = Book.class, clientRegionShortcut = ClientRegionShortcut.LOCAL)
	static class TestConfiguration {

		@Bean("TestBean")
		Object testBean(@Qualifier("booksTemplate") GemfireTemplate booksTemplate) {
			return "TEST";
		}
	}
}
