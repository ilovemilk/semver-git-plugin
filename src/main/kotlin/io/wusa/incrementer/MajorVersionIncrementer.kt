package io.wusa.incrementer

import io.wusa.Version
import org.gradle.api.Transformer

object MajorVersionIncrementer : Transformer<Version, Version> {
    override fun transform(version: Version): Version {
        version.major += 1
        version.minor = 0
        version.patch = 0
        return version
    }
}
