/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

CREATE TABLE IF NOT EXISTS golfers (
  	name VARCHAR(256) PRIMARY KEY,
  	hole NUMERIC(10),
  	score NUMERIC(10)
);
