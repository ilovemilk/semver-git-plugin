package io.wusa.incrementer

import io.wusa.Info
import io.wusa.Version
import org.gradle.api.Transformer

class MajorVersionIncrementer : Transformer<Version, Info> {
    override fun transform(info: Info): Version {
        info.version.major += 1
        info.version.minor = 0
        info.version.patch = 0
        return info.version
    }
}
