package io.wusa.incrementer

import io.wusa.Version

object GroovyPatchVersionIncrementer {
    @JvmStatic
    fun transform(version: Version) : Version {
        return PatchVersionIncrementer.transform(version)
    }
}