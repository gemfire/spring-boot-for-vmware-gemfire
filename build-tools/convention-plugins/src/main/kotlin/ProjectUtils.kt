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
