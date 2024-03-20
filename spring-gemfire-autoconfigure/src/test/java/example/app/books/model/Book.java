/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.books.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.gemfire.mapping.annotation.Region;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * The {@link Book} class is an Abstract Data Type (ADT) modeling a book.
 *
 * @author John Blum
 * @see lombok
 * @see org.springframework.data.annotation.Id
 * @see org.springframework.data.gemfire.mapping.annotation.Region
 * @see example.app.books.model.Author
 * @see example.app.books.model.ISBN
 * @since 1.0.0
 */
@Data
@Region("Books")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(staticName = "newBook")
public class Book {

	private Author author;

	@Id
	private ISBN isbn;

	private LocalDate publishedDate;

	@NonNull
	private String title;

	@Transient
	public boolean isNew() {
		return getIsbn() == null;
	}

	public Book identifiedBy(ISBN isbn) {
		setIsbn(isbn);
		return this;
	}
}
