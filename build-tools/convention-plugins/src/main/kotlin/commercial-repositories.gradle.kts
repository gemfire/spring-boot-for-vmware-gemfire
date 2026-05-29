/*
 * Copyright 2024-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import java.nio.file.Path

plugins {
  id("java-library")
  id("idea")
  id("eclipse")
}

repositories {
  addGemFireRepositories(providers)
}
