/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.books.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * The {@link Author} class is an Abstract Data Type (ADT) modeling a book author.
 *
 * @author John Blum
 * @see lombok
 * @see org.springframework.data.annotation.Id
 * @see org.springframework.data.gemfire.mapping.annotation.Region
 * @since 1.0.0
 */
@Data
@Region("Authors")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(staticName = "newAuthor")
@SuppressWarnings("unused")
public class Author {

	@Id
	private Long id;

	@NonNull
	private String name;

	public boolean isNew() {
		return getId() == null;
	}

	public Author identifiedBy(Long id) {
		setId(id);
		return this;
	}
}
