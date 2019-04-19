package io.wusa

class VersionService {
    companion object {

        fun parseVersion(describe: String): Version {
            val versionFactory: VersionFactory = SemanticVersionFactory()
            return versionFactory.createFromString(describe)
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