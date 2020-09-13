package io.wusa.incrementer

import io.wusa.Version
import org.gradle.api.Transformer

object PatchVersionIncrementer : Transformer<Version, Version> {
    override fun transform(version: Version): Version {
        version.patch += 1
        return version
    }
}
