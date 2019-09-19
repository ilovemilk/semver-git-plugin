package io.wusa

interface IVersionFactory {

    fun createFromString(describe: String): Version
}