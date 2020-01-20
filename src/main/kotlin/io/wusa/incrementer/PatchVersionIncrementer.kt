package io.wusa.incrementer

import io.wusa.Version

class PatchVersionIncrementer: IIncrementer {
    override fun increment(version: Version): Version {
        version.patch += 1
        return version
    }
}