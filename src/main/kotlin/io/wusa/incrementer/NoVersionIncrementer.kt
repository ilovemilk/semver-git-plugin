package io.wusa.incrementer

import io.wusa.Version
import org.gradle.api.Project

class NoVersionIncrementer: IIncrementer {
    override fun increment(version: Version, project: Project): Version {
        return version
    }
}
