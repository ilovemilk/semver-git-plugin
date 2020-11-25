package io.wusa.formatter

import io.wusa.Info
import io.wusa.RegexResolver
import io.wusa.extension.Branches
import io.wusa.extension.SemverGitPluginExtension
import org.gradle.api.Transformer

class SemanticVersionFormatter {
    companion object {
        fun format(info: Info, branches: Branches, snapshotSuffix: String, dirtyMarker: String): String {
            if (!hasFirstCommit(info)) return appendSuffix(buildVersionString(info), snapshotSuffix)

            if (hasTag(info) && !isDirty(info)) {
                return formatVersionWithTag(info)
            }

            val formattedVersion = formatVersion(branches, info, dirtyMarker)
            if (!hasTag(info) || hasTag(info) && isDirty(info)) {
                return appendSuffix(formattedVersion, snapshotSuffix)
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

        private fun formatVersion(branches: Branches, info: Info, dirtyMarker: String): String {
            val regexFormatterPair = RegexResolver.findMatchingRegex(branches, info.branch.name)
            var formattedVersion = transform(SemverGitPluginExtension.DEFAULT_FORMATTER, info)
            if (isDirty(info)) {
                formattedVersion = appendDirtyMarker(formattedVersion, dirtyMarker)
            }
            regexFormatterPair?.let {
                formattedVersion = transform(regexFormatterPair.formatter, info)
                if (isDirty(info)) {
                    formattedVersion = appendDirtyMarker(formattedVersion, dirtyMarker)
                }
            }
            return formattedVersion
        }

        private fun hasTag(info: Info) = info.version.suffix == null

        private fun isDirty(info: Info) = info.dirty

        private fun hasVersionBuildInformation(info: Info) = info.version.build != ""

        private fun hasVersionPrerelease(info: Info) = info.version.prerelease != ""

        private fun appendSuffix(version: String, snapshotSuffix: String): String {
            if (snapshotSuffix != "") {
                return "$version-$snapshotSuffix"
            }
            return version
        }

        private fun hasFirstCommit(info: Info): Boolean {
            if (info.count == 0) {
                return false
            }
            return true
        }

        private fun appendDirtyMarker(version: String, dirtyMarker: String): String {
            if (dirtyMarker != "") {
                return "$version-$dirtyMarker"
            }
            return version
        }

        private fun transform(transformer: Transformer<Any, Info>, info: Info): String {
            return transformer.transform(info).toString()
        }
    }
}
