package io.wusa.incrementer

import io.wusa.Version


class MinorVersionIncrementer: IIncrementer {
    override fun increment(version: Version): Version {
        version.minor += 1
        version.patch = 0
        return version
    }
}