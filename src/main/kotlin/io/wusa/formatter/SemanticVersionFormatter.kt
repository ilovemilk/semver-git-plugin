package io.wusa.formatter

import io.wusa.Info
import io.wusa.RegexResolver
import io.wusa.Suffix
import io.wusa.extension.Branches
import io.wusa.extension.SemverGitPluginExtension
import org.gradle.api.Transformer

class SemanticVersionFormatter {
    companion object {
        fun format(info: Info, branches: Branches, dirtyMarker: String): String {
            if (!hasFirstCommit(info)) return appendSuffix(buildVersionString(info), branches, info)

            if (hasTag(info)) {
                return formatVersionWithTag(info)
            }

            val formattedVersion = formatVersionWithoutTag(branches, info, dirtyMarker)
            if (!hasTag(info)) {
                return appendSuffix(formattedVersion, branches, info)
            }
            return formattedVersion
        }

        private fun formatVersionWithTag(info: Info): String {
            var versionString = buildVersionString(info)
            if (hasVersionPrerelease(info)) {
                versionString = appendPrerelease(versionString, info)
            }
            if (hasVersionBuildInformation(info)) {
                versionString = appendBuildInformation(versionString, info)
            }
            return versionString
        }

        private fun appendBuildInformation(versionString: String, info: Info): String {
            var versionString1 = versionString
            versionString1 += "+${info.version.build}"
            return versionString1
        }

        private fun appendPrerelease(versionString: String, info: Info): String {
            var versionString1 = versionString
            versionString1 += "-${info.version.prerelease}"
            return versionString1
        }

        private fun buildVersionString(info: Info) = "${info.version.major}.${info.version.minor}.${info.version.patch}"

        private fun formatVersionWithoutTag(branches: Branches, info: Info, dirtyMarker: String): String {
            val regexFormatterPair = RegexResolver.findMatchingRegex(branches, info.branch.name)
            var formattedVersion = transform(SemverGitPluginExtension.DEFAULT_FORMATTER, info)
            formattedVersion = appendDirtyMarker(formattedVersion, info.version.suffix, dirtyMarker)
            regexFormatterPair?.let {
                formattedVersion = transform(regexFormatterPair.formatter, info)
                formattedVersion = appendDirtyMarker(formattedVersion, info.version.suffix, dirtyMarker)
            }
            return formattedVersion
        }

        private fun hasTag(info: Info) = info.version.suffix == null

        private fun hasVersionBuildInformation(info: Info) = info.version.build != ""

        private fun hasVersionPrerelease(info: Info) = info.version.prerelease != ""

        private fun appendSuffix(version: String, branches: Branches, info: Info): String {
            val regexFormatterPair = RegexResolver.findMatchingRegex(branches, info.branch.name)
            regexFormatterPair?.let {
                if (regexFormatterPair.snapshotSuffix != "") {
                    return "$version-${regexFormatterPair.snapshotSuffix}"
                }
                return version
            }
            return "$version-${SemverGitPluginExtension.DEFAULT_SNAPSHOT_SUFFIX}"
        }

        private fun hasFirstCommit(info: Info): Boolean {
            if (info.count == 0) {
                return false
            }
            return true
        }

        private fun appendDirtyMarker(version: String, suffix: Suffix?, dirtyMarker: String): String {
            if (suffix != null && suffix.dirty) {
                if (dirtyMarker != "") {
                    return "$version-$dirtyMarker"
                }
                return version
            }
            return version
        }

        private fun transform(transformer: Transformer<Any, Info>, info: Info): String {
            return transformer.transform(info).toString()
        }
    }
}
