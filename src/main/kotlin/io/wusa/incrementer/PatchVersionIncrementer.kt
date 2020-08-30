package io.wusa.incrementer

import io.wusa.Info
import io.wusa.Version
import org.gradle.api.Transformer

class PatchVersionIncrementer : Transformer<Version, Info> {
    override fun transform(info: Info): Version {
        info.version.patch += 1
        return info.version
    }
}
