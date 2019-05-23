package io.wusa

class DefaultVersionFormatter : IFormatter<Version> {
    override fun format(objectToFormat: Version, suffix: String, dirtyMarker: String): String {
        var formattedVersion = "${objectToFormat.major}.${objectToFormat.minor}.${objectToFormat.patch}"
        if (objectToFormat.prerelease != "") {
            formattedVersion += "-${objectToFormat.prerelease}"
        }
        if (objectToFormat.build != "") {
            formattedVersion += "+${objectToFormat.build}"
        }
        if (objectToFormat.suffix != null) {
            DefaultSuffixFormatter().format(objectToFormat.suffix!!, suffix, dirtyMarker)
            if (DefaultSuffixFormatter().format(objectToFormat.suffix!!, suffix, dirtyMarker) != "")
                formattedVersion += "-${DefaultSuffixFormatter().format(objectToFormat.suffix!!, suffix, dirtyMarker)}"
        }
        return formattedVersion
    }
}