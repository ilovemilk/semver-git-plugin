package io.wusa.incrementer

import io.wusa.Version

object GroovyNoVersionIncrementer {
    @JvmStatic
    fun transform(version: Version) : Version {
        return NoVersionIncrementer.transform(version)
    }
}