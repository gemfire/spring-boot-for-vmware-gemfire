/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.hamcrest;

import java.util.regex.Pattern;

import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * A Hamcrest {@link Matcher} using a Java {@link Pattern} with a {@link String Regular Expression}
 * to match a {@link String} argument.
 *
 * @author John Blum
 * @see java.util.regex.Pattern
 * @see org.hamcrest.BaseMatcher
 * @see org.hamcrest.Matcher
 * @since 1.4.0
 */
public class RegexMatcher extends BaseMatcher<String> {

	private final Pattern pattern;

	public static RegexMatcher from(@NonNull String regex) {
		return new RegexMatcher(regex);
	}

	public RegexMatcher(@NonNull String regex) {

		Assert.hasText(regex, () -> String.format("Regular Expression [%s] is required", regex));

		this.pattern = Pattern.compile(regex);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText(String.format("Matches text to the Regular Expression (Pattern) [%s]",
			this.pattern.toString()));
	}

	@Override
	public boolean matches(Object actual) {
		return this.pattern.matcher(String.valueOf(actual)).matches();
	}
}
