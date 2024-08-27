/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.library.service;

import java.util.Collections;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import example.app.books.model.Author;
import example.app.books.model.Book;

/**
 * The {@link LibraryService} class models a Library.
 *
 * @author John Blum
 * @see org.springframework.cache.annotation.Cacheable
 * @see org.springframework.stereotype.Service
 * @see example.app.books.model.Author
 * @see example.app.books.model.Book
 * @since 1.0.0
 */
@Service
@SuppressWarnings("unused")
public class LibraryService {

	@Cacheable("BooksByAuthor")
	public List<Book> findBooksByAuthor(Author author) {
		return Collections.emptyList();
	}

	@Cacheable("BooksByYear")
	public List<Book> findBooksByYear(int year) {
		return Collections.emptyList();
	}
}
