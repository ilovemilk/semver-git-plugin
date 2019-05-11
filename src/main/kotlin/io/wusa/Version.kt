package io.wusa

data class Version(var major: Int, var minor: Int, var patch: Int, var prerelease: String, var build: String, var suffix: Suffix?, var suffixFormat: String = SemverGitPluginExtension.DEFAULT_SNAPSHOT_SUFFIX, var dirtyMarker: String = SemverGitPluginExtension.DEFAULT_DIRTY_MARKER) {
    override fun toString(): String {
        return format(suffixFormat, dirtyMarker)
    }

    fun format(suffixFormat: String, dirtyMarker: String): String {
        var version = "$major.$minor.$patch"
        if (prerelease != "") {
            version += "-$prerelease"
        }
        if (build != "") {
            version += "+$build"
        }
        if (suffix != null) {
            if (suffix!!.format(suffixFormat, dirtyMarker) != "")
                version+= "-${suffix!!.format(suffixFormat, dirtyMarker)}"
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