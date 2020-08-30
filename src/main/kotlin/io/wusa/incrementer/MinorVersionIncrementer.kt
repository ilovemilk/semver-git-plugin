package io.wusa.incrementer

import io.wusa.Info
import io.wusa.Version
import org.gradle.api.Transformer

class MinorVersionIncrementer : Transformer<Version, Info> {
    override fun transform(info: Info): Version {
        info.version.minor += 1
        info.version.patch = 0
        return info.version
    }
}
