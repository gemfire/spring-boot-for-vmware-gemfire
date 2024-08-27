/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package example.app.books.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import example.app.books.model.Author;
import example.app.books.model.Book;
import example.app.books.model.ISBN;

/**
 * The {@link BookRepository} interface is a Spring Data {@link CrudRepository} defining basic CRUD
 * and simple query data access operations on {@link Book} objects to the backing data store.
 *
 * @author John Blum
 * @see example.app.books.model.Book
 * @see example.app.books.model.ISBN
 * @see org.springframework.data.repository.CrudRepository
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public interface BookRepository extends CrudRepository<Book, ISBN> {

	List<Book> findByAuthorOrderByAuthorNameAscTitleAsc(Author author);

	Book findByIsbn(ISBN isbn);

	Book findByTitle(String title);

}
