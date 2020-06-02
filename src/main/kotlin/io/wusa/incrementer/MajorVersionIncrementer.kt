package io.wusa.incrementer

import io.wusa.Version
import org.gradle.api.Project

class MajorVersionIncrementer: IIncrementer {
    override fun increment(version: Version, project: Project): Version {
        version.major += 1
        version.minor = 0
        version.patch = 0
        return version
    }
}
