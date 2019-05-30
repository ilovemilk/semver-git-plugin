package io.wusa

import groovy.lang.Closure
import groovy.lang.GString
import io.wusa.RegexResolver.Companion.findMatchingRegex
import io.wusa.extension.SemverGitPluginExtension
import org.gradle.api.Project

data class Version(var major: Int, var minor: Int, var patch: Int, var prerelease: String, var build: String, var suffix: Suffix?, var project: Project) {
    override fun toString(): String {
        val semverGitPluginExtension: SemverGitPluginExtension = project.extensions.getByType(SemverGitPluginExtension::class.java)

        if (semverGitPluginExtension.info.count == 0) {
            // there is no commit yet
            return "$major.$minor.$patch-${semverGitPluginExtension.snapshotSuffix}"
        }

        if (suffix == null) {
            // we are on a tag
            var version = "$major.$minor.$patch"
            if (prerelease != "") {
                version += "-$prerelease"
            }
            if (build != "") {
                version += "+$build"
            }
            return version
        }

        // we have commits but no tag
        val regexFormatterPair = findMatchingRegex(semverGitPluginExtension.branches, semverGitPluginExtension.info.branch.name)
        var formattedVersion = format(SemverGitPluginExtension.DEFAULT_FORMATTER)
        formattedVersion = appendDirtyMarker(formattedVersion, suffix, semverGitPluginExtension.dirtyMarker)
        regexFormatterPair?.let {
            formattedVersion = format(regexFormatterPair.formatter)
            formattedVersion = appendDirtyMarker(formattedVersion, suffix, semverGitPluginExtension.dirtyMarker)
        }
        return appendSuffix(formattedVersion, suffix, semverGitPluginExtension.snapshotSuffix)
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

    private fun format(formatter: Any): String {
        if (formatter is Closure<*>) {
            return (formatter as Closure<GString>).call().toString()
        }
        return (formatter as () -> String).invoke()
    }
}