/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

CREATE TABLE IF NOT EXISTS customers (
	id BIGINT PRIMARY KEY,
  	name VARCHAR(256) NOT NULL
);
