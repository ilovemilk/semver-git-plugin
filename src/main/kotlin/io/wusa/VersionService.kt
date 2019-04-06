package io.wusa

import java.lang.IllegalArgumentException

class VersionService {
    companion object {

        fun parseVersion(describe: String): Version {
            val regex = """^([0-9]+)\.([0-9]+)\.([0-9]+)(?:(?:-([0-9]+))+(?:-g([0-9a-f]+))+(-dirty)?)?$""".toRegex()
            return regex.matchEntire(describe)
                    ?.destructured
                    ?.let { (major, minor, patch, count, sha, dirty) ->
                        when (dirty.isEmpty() && count.isEmpty() && sha.isEmpty()) {
                            true -> Version(major.toInt(), minor.toInt(), patch.toInt(), null)
                            false -> Version(major.toInt(), minor.toInt(), patch.toInt(), Suffix(count.toInt(), sha, dirty.isNotEmpty()))
                        }
                    }
                    ?: throw IllegalArgumentException("Bad input '$describe'")
        }

        fun bumpVersion(version: Version, nextVersion: String): Version {
            when (nextVersion) {
                "major" -> {
                    version.major += 1
                    version.minor = 0
                    version.patch = 0
                    return version
                }
                "minor" -> {
                    version.minor += 1
                    version.patch = 0
                    return version
                }
                "patch" -> {
                    version.patch += 1
                    return version
                }
                "none" -> {
                    return version
                }
                else -> {
                    return version
                }
            }
        }
    }
}