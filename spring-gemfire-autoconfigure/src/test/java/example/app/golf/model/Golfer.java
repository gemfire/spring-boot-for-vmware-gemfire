/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.golf.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * An Abstract Data Type (ADT) that models a person who golfs.
 *
 * @author John Blum
 * @since 1.3.0
 */
@Region("Golfers")
@Getter
@EqualsAndHashCode(of = "name")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(staticName = "newGolfer")
public class Golfer {

	@Id @NonNull
	private Long id;

	@NonNull
	private String name;

	private Integer handicap;

	public Golfer withHandicap(int handicap) {
		this.handicap = handicap;
		return this;
	}

	@Override
	public String toString() {
		return String.format("%s:%d", getName(), getHandicap());
	}
}
