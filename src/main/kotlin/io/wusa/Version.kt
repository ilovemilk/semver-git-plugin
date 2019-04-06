package io.wusa

data class Version(var major: Int, var minor: Int, var patch: Int, var suffix: Suffix?) {
    fun format(suffixFormat: String, dirtyMarker: String): String {
        return if (suffix != null) {
            "$major.$minor.$patch-${suffix!!.format(suffixFormat, dirtyMarker)}"
        } else {
            "$major.$minor.$patch"
        }
    }
}