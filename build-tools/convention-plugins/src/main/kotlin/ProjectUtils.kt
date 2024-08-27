import java.lang.RuntimeException

/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

object ProjectUtils {
  fun getGemFireBaseVersion(gemfireVersion:String): String {
    return getBaseVersion(gemfireVersion)
  }

  fun getBaseVersion(version: String): String {
    val split = version.split(".")
    if (split.size < 2) {
      throw RuntimeException("version is malformed")
    }
    return "${split[0]}.${split[1]}"
  }
}
