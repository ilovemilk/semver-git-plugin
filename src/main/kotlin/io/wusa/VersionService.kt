package io.wusa

import java.lang.IllegalArgumentException

class VersionService {
    companion object {

        /*fun parseVersion(describe: String): Version {
            val regex = """^([0-9]+)\.([0-9]+)\.([0-9]+)(?:(?:-([0-9]+))+(?:-g([0-9a-f]+))+(-dirty)?)?$""".toRegex()
            return regex.matchEntire(describe)
                    ?.destructured
                    ?.let { (major, minor, patch, count, sha, dirty) ->
                        when (dirty.isEmpty() && count.isEmpty() && sha.isEmpty()) {
                            true -> Version(major.toInt(), minor.toInt(), patch.toInt(), "", "", null)
                            false -> Version(major.toInt(), minor.toInt(), patch.toInt(), "", "", Suffix(count.toInt(), sha, dirty.isNotEmpty()))
                        }
                    }
                    ?: throw IllegalArgumentException("Bad input '$describe'")
        }*/

        fun parseVersion(describe: String): Version {
            try {
                return parseWithoutPrereleaseAndBuild(describe)
            } catch (ex: IllegalArgumentException) {

            }
            try {
                return parseWithPrereleaseAndBuild(describe)
            } catch (ex: IllegalArgumentException) {

            }
            throw IllegalArgumentException("Bad input '$describe'")
        }

        //TODO: Make version factory from string
        private fun parseWithoutPrereleaseAndBuild(describe: String): Version {
            val regexWithoutPrereleaseAndBuild = """(?<=^[Vv]|^)(?<major>0|[1-9]\d*)\.(?<minor>0|[1-9]\d*)\.(?<patch>0|[1-9]\d*)(?:-(?<count>[0-9]*)-g(?<sha>[0-9a-f]{0,7})(?<dirty>-dirty)?)?$""".toRegex()

            return regexWithoutPrereleaseAndBuild.matchEntire(describe)
                    ?.destructured
                    ?.let { (major, minor, patch, count, sha, dirty) ->
                        when (dirty.isEmpty() && count.isEmpty() && sha.isEmpty()) {
                            true -> Version(major.toInt(), minor.toInt(), patch.toInt(), "", "", null)
                            false -> Version(major.toInt(), minor.toInt(), patch.toInt(), "", "", Suffix(count.toInt(), sha, dirty.isNotEmpty()))
                        }
                    }
                    ?: throw IllegalArgumentException("Bad input '$describe'")
        }

        private fun parseWithPrereleaseAndBuild(describe: String): Version {
            val regexWithPrereleaseAndBuild = """(?<=^[Vv]|^)(?<major>0|[1-9]\d*)\.(?<minor>0|[1-9]\d*)\.(?<patch>0|[1-9]\d*)(?:-(?<prerelease>(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?<build>\+[a-zA-Z0-9][a-zA-Z0-9\.-]+)?$""".toRegex()

            return regexWithPrereleaseAndBuild.matchEntire(describe)
                    ?.destructured
                    ?.let { (major, minor, patch, prerelase, build) ->
                        Version(major.toInt(), minor.toInt(), patch.toInt(), prerelase, build, null)
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