package io.wusa

import org.gradle.api.Project

data class Version(var major: Int, var minor: Int, var patch: Int, var prerelease: String, var build: String, var suffix: Suffix?, var project: Project) {
    override fun toString(): String {
        val semverGitPluginExtension: SemverGitPluginExtension = project.extensions.getByType(SemverGitPluginExtension::class.java)
        return VersionFormatter.DEFAULT.format(this, semverGitPluginExtension.snapshotSuffix, semverGitPluginExtension.dirtyMarker)
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