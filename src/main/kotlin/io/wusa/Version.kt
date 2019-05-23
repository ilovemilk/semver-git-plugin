package io.wusa

data class Version(var major: Int, var minor: Int, var patch: Int, var prerelease: String, var build: String, var suffix: Suffix?) {
    override fun toString(): String {
        VersionFormatter.DEFAULT.format(this, "")
        return super.toString()
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