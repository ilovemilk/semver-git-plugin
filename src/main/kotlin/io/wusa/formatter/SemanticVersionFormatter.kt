package io.wusa.formatter

import io.wusa.Info
import io.wusa.RegexResolver
import io.wusa.Suffix
import io.wusa.Version
import io.wusa.extension.Branches
import io.wusa.extension.SemverGitPluginExtension
import org.gradle.api.Project
import org.gradle.api.Transformer

class SemanticVersionFormatter {
    companion object {
        fun format(info: Info, branches: Branches, snapshotSuffix: String, dirtyMarker: String): String {
            if (info.count == 0) {
                // there is no commit yet so add snapshotSuffix to version
                return "${info.version.major}.${info.version.minor}.${info.version.patch}-${snapshotSuffix}"
            }

            if (info.version.suffix == null) {
                // we are on a tag
                var versionString = "${info.version.major}.${info.version.minor}.${info.version.patch}"
                if (info.version.prerelease != "") {
                    versionString += "-${info.version.prerelease}"
                }
                if (info.version.build != "") {
                    versionString += "+${info.version.build}"
                }
                return versionString
            }

            // we have commits but no tag
            val regexFormatterPair = RegexResolver.findMatchingRegex(branches, info.branch.name)
            var formattedVersion = transform(SemverGitPluginExtension.DEFAULT_FORMATTER, info)
            formattedVersion = appendDirtyMarker(formattedVersion, info.version.suffix, dirtyMarker)
            regexFormatterPair?.let {
                formattedVersion = transform(regexFormatterPair.formatter, info)
                formattedVersion = appendDirtyMarker(formattedVersion, info.version.suffix, dirtyMarker)
            }
            return appendSuffix(formattedVersion, info.version.suffix, snapshotSuffix)
        }

        private fun appendSuffix(version: String, suffix: Suffix?, snapshotSuffix: String): String {
            if (suffix != null) {
                return "$version-$snapshotSuffix"
            }
            return version
        }

        private fun appendDirtyMarker(version: String, suffix: Suffix?, dirtyMarker: String): String {
            if (suffix != null && suffix.dirty) {
                return "$version-$dirtyMarker"
            }
            return version
        }

        private fun transform(transformer: Transformer<Any, Info>, info: Info): String {
            return transformer.transform(info).toString()
        }
    }
}