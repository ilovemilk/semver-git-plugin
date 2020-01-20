package io.wusa

import io.wusa.exception.NoValidSemverTagFoundException

interface IVersionFactory {

    @Throws(NoValidSemverTagFoundException::class)
    fun createFromString(describe: String): Version
}
