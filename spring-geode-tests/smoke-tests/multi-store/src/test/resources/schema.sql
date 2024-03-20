/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

CREATE TABLE IF NOT EXISTS contacts (
  	name VARCHAR(256) PRIMARY KEY,
  	email_address VARCHAR(256),
  	phone_number VARCHAR(256)
);
