/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.pos.model;

import java.math.BigDecimal;

import org.springframework.util.Assert;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * The {@link LineItem} class models a {@link Product} purchase on a {@link PurchaseOrder}.
 *
 * @author John Blum
 * @since 1.3.0
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(staticName = "newLineItem")
@SuppressWarnings("unused")
public class LineItem {

	@NonNull
	private Product product;

	private Integer quantity = 1;

	public String getDescription() {
		return getProduct().getName();
	}

	public BigDecimal getTotal() {
		return getUnitPrice().multiply(BigDecimal.valueOf(getQuantity()));
	}

	public BigDecimal getUnitPrice() {
		return getProduct().getPrice();
	}

	public LineItem withQuantity(int quantity) {
		Assert.isTrue(quantity > 0, "Quantity must be greater than equal to 1");
		this.quantity = quantity;
		return this;
	}

	@Override
	public String toString() {
		return String.format("Purchasing [%d] of Product [%s]", getQuantity(), getProduct());
	}
}
