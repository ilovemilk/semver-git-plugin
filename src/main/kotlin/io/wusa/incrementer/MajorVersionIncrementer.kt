package io.wusa.incrementer

import io.wusa.Version

class MajorVersionIncrementer: IIncrementer {
    override fun increment(version: Version): Version {
        version.major += 1
        version.minor = 0
        version.patch = 0
        return version
    }
}