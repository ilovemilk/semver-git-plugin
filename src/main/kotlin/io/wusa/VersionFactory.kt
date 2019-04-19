package io.wusa

interface VersionFactory {

    fun createFromString(describe: String): Version
}