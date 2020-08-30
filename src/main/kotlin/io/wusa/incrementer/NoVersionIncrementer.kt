package io.wusa.incrementer

import io.wusa.Info
import io.wusa.Version
import org.gradle.api.Transformer

class NoVersionIncrementer : Transformer<Version, Info> {
    override fun transform(info: Info): Version {
        return info.version
    }
}
