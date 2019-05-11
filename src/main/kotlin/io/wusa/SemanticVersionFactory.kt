package io.wusa

import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException

class SemanticVersionFactory(var suffixFormat: String = SemverGitPluginExtension.DEFAULT_SNAPSHOT_SUFFIX, var dirtyMarker: String = SemverGitPluginExtension.DEFAULT_DIRTY_MARKER) : VersionFactory {

    private val LOG = LoggerFactory.getLogger(SemanticVersionFactory::class.java)

    override fun createFromString(describe: String): Version {
        val suffixRegex = """(?:-(?<count>[0-9]+)(?:-g(?<sha>[0-9a-f]{1,7}))(?<dirty>-dirty)?)$""".toRegex()
        val suffix = suffixRegex.find(describe)
                ?.destructured
                ?.let { (count, sha, dirty) ->
                    Suffix(count.toInt(), sha, dirty.isNotEmpty())
                }

        var version = describe
        if (suffix != null) {
            version = suffixRegex.replace(describe, "")
        }

        val parsedVersion = parseVersion(version)
        parsedVersion.suffix = suffix
        return parsedVersion
    }

    private fun parseVersion(version: String): Version {
        val versionRegex = """^[vV]?(?<major>0|[1-9]\d*)\.(?<minor>0|[1-9]\d*)\.(?<patch>0|[1-9]\d*)(?:-(?<prerelease>(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\+(?<build>[a-zA-Z0-9][a-zA-Z0-9\.-]+)?)?$""".toRegex()

        return versionRegex.matchEntire(version)
                ?.destructured
                ?.let { (major, minor, patch, prerelase, build) ->
                    Version(major.toInt(), minor.toInt(), patch.toInt(), prerelase, build, null, suffixFormat, dirtyMarker)
                }
                ?: throw IllegalArgumentException("Bad input '$version'")
    }
}