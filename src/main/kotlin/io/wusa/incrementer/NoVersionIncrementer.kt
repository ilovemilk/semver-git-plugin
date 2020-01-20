package io.wusa.incrementer

import io.wusa.Version

class NoVersionIncrementer: IIncrementer {
    override fun increment(version: Version): Version {
        return version
    }
}