/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.books.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import example.app.books.model.Author;
import example.app.books.model.Book;
import example.app.books.model.ISBN;
import example.app.books.repo.BookRepository;

/**
 * The {@link BookService} class is an application {@link Service service} class for managing {@link Book Books}.
 *
 * @author John Blum
 * @see example.app.books.model.Author
 * @see example.app.books.model.Book
 * @see example.app.books.model.ISBN
 * @see example.app.books.repo.BookRepository
 * @see org.springframework.stereotype.Service
 * @since 1.0.0
 */
@Service
@SuppressWarnings("unused")
public class BookService {

	private final BookRepository bookRepository;

	public BookService(@Autowired(required = false) BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	protected BookRepository getBookRepository() {

		Assert.state(this.bookRepository != null, "BookRepository was not properly configured");

		return this.bookRepository;
	}

	public List<Book> findByAuthor(Author author) {
		return getBookRepository().findByAuthorOrderByAuthorNameAscTitleAsc(author);
	}

	public Book findByIsbn(ISBN isbn) {
		return getBookRepository().findByIsbn(isbn);
	}

	public Book findByTitle(String title) {
		return getBookRepository().findByTitle(title);
	}

	public Book stock(Book book) {

		if (book.isNew()) {
			book.identifiedBy(ISBN.autoGenerated());
		}

		return getBookRepository().save(book);
	}
}
