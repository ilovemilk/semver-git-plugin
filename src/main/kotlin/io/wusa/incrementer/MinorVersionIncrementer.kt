package io.wusa.incrementer

import io.wusa.Version
import org.gradle.api.Project


class MinorVersionIncrementer: IIncrementer {
    override fun increment(version: Version, project: Project): Version {
        version.minor += 1
        version.patch = 0
        return version
    }
}
