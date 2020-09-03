package io.wusa.incrementer

import io.wusa.Version
import org.gradle.api.Transformer

object MinorVersionIncrementer : Transformer<Version, Version> {
    override fun transform(version: Version): Version {
        version.minor += 1
        version.patch = 0
        return version
    }
}
