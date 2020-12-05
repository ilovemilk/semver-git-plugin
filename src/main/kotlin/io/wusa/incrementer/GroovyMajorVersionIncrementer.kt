package io.wusa.incrementer

import io.wusa.Version

object GroovyMajorVersionIncrementer {
    @JvmStatic
    fun transform(version: Version) : Version {
        return MajorVersionIncrementer.transform(version)
    }
}