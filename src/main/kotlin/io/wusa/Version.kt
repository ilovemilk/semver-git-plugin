package io.wusa

import groovy.lang.Closure
import groovy.lang.GString
import org.gradle.api.Project

data class Version(var major: Int, var minor: Int, var patch: Int, var prerelease: String, var build: String, var suffix: Suffix?, var project: Project) {
    override fun toString(): String {
        val semverGitPluginExtension: SemverGitPluginExtension = project.extensions.getByType(SemverGitPluginExtension::class.java)

        if (semverGitPluginExtension.info.count == 0) {
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

        val regexFormatterPair = semverGitPluginExtension.branchVersionFormatter.filterKeys {
            it.toRegex().matches(semverGitPluginExtension.info.branch.name)
        }
        if (regexFormatterPair.values.first() is Closure<*>) {
            val version = (regexFormatterPair.values.first() as Closure<GString>).call().toString()
            return appendSuffix(version, suffix, semverGitPluginExtension.snapshotSuffix)
        }
        val version = (regexFormatterPair.values.first() as () -> String).invoke()
        return appendSuffix(version, suffix, semverGitPluginExtension.snapshotSuffix)
    }

    private fun appendSuffix(version: String, suffix: Suffix?, snapshotSuffix: String): String {
        if (suffix != null) {
            return "$version-$snapshotSuffix"
        }
        return version
    }

    fun bump(nextVersion: String): Version {
        when (nextVersion) {
            "major" -> {
                this.major += 1
                this.minor = 0
                this.patch = 0
                return this
            }
            "minor" -> {
                this.minor += 1
                this.patch = 0
                return this
            }
            "patch" -> {
                this.patch += 1
                return this
            }
            "none" -> {
                return this
            }
            else -> {
                return this
            }
        }
    }
}