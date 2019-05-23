package io.wusa

class DefaultSuffixFormatter : IFormatter<Suffix> {
    override fun format(objectToFormat: Suffix, suffix: String, dirtyMarker: String): String {
        // only the initial commit without a commit can have a count of 0
        if (objectToFormat.count == 0)
            return ""
        var formattedSuffix = suffix
        formattedSuffix = formattedSuffix.replace("<count>", objectToFormat.count.toString())
        formattedSuffix = formattedSuffix.replace("<sha>", objectToFormat.sha)
        formattedSuffix = if (objectToFormat.dirty)
            formattedSuffix.replace("<dirty>", dirtyMarker)
        else
            formattedSuffix.replace("<dirty>", "")
        return formattedSuffix
    }
}