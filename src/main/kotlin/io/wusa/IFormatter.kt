package io.wusa

interface IFormatter<T> {
    fun format(objectToFormat: T, suffix: String, dirtyMarker: String): String
}