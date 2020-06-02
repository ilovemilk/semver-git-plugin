package io.wusa.incrementer

import io.wusa.Version
import org.gradle.api.Project

class PatchVersionIncrementer: IIncrementer {
    override fun increment(version: Version, project: Project): Version {
        version.patch += 1
        return version
    }
}
