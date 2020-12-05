package io.wusa.incrementer

import io.wusa.Version

object GroovyMinorVersionIncrementer {
    @JvmStatic
    fun transform(version: Version) : Version {
        return MinorVersionIncrementer.transform(version)
    }
}