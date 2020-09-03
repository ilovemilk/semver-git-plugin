package io.wusa.incrementer

import io.wusa.Version
import org.gradle.api.Transformer

object NoVersionIncrementer : Transformer<Version, Version> {
    override fun transform(version: Version): Version {
        return version
    }
}
