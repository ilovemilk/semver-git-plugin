package io.wusa

import io.wusa.exception.NoValidSemverTagFoundException
import org.gradle.api.Project
import org.slf4j.LoggerFactory

class SemanticVersionFactory() : IVersionFactory {
    private val LOG = LoggerFactory.getLogger(SemanticVersionFactory::class.java)

    private val suffixRegex = """(?:-(?<count>[0-9]+)(?:-g(?<sha>[0-9a-f]{1,7}))(?<dirty>-dirty)?)$""".toRegex()
    private val versionRegex = """^[vV]?(?<major>0|[1-9]\d*)\.(?<minor>0|[1-9]\d*)\.(?<patch>0|[1-9]\d*)(?:-(?<prerelease>(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\+(?<build>[a-zA-Z0-9][a-zA-Z0-9\.-]+)?)?$""".toRegex()

    @Throws(NoValidSemverTagFoundException::class)
    override fun createFromString(describe: String): Version {
        try {
            val suffix = parseSuffix(suffixRegex, describe)
            val version = removeSuffixFromDescribe(describe, suffix, suffixRegex)
            val parsedVersion = parseVersion(version, versionRegex)

            parsedVersion.suffix = suffix
            return parsedVersion
        } catch (ex: IllegalArgumentException) {
            throw NoValidSemverTagFoundException("The last tag is not a semantic version: $describe.")
        }
    }

    private fun removeSuffixFromDescribe(describe: String, suffix: Suffix?, suffixRegex: Regex): String {
        var version = describe
        if (suffix != null) {
            version = suffixRegex.replace(describe, "")
        }
        return version
    }

    private fun parseSuffix(suffixRegex: Regex, describe: String): Suffix? {
        return suffixRegex.find(describe)
                ?.destructured
                ?.let { (count, sha, dirty) ->
                    Suffix(count.toInt(), sha, dirty.isNotEmpty())
                }
    }

    @Throws(IllegalArgumentException::class)
    private fun parseVersion(version: String, versionRegex: Regex): Version {
        return versionRegex.matchEntire(version)
                ?.destructured
                ?.let { (major, minor, patch, prerelease, build) ->
                    Version(major.toInt(), minor.toInt(), patch.toInt(), prerelease, build, null)
                }
                ?: throw IllegalArgumentException("Bad input '$version'")
    }
}
