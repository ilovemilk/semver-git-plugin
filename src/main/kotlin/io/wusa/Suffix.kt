package io.wusa

data class Suffix(var count: Int, var sha: String, var dirty: Boolean) {
    fun format(format: String, dirtyMarker: String): String {
        var formattedSuffix = format
        formattedSuffix = formattedSuffix.replace("<count>", count.toString())
        formattedSuffix = formattedSuffix.replace("<sha>", sha)
        formattedSuffix = if (dirty)
            formattedSuffix.replace("<dirty>", dirtyMarker)
        else
            formattedSuffix.replace("<dirty>", "")
        return formattedSuffix
    }
}