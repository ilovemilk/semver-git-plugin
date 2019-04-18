package io.wusa

data class Version(var major: Int, var minor: Int, var patch: Int, var prerelease: String, var build: String, var suffix: Suffix?) {
    fun format(suffixFormat: String, dirtyMarker: String): String {
        var version = "$major.$minor.$patch"
        if (prerelease != "") {
            version += "-$prerelease"
        }
        if (build != "") {
            version += "+$build"
        }
        if (suffix != null) {
            version+= "-${suffix!!.format(suffixFormat, dirtyMarker)}"
        }
        return version
    }
}