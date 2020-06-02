package io.wusa.incrementer

import io.wusa.Version
import org.gradle.api.Project


interface IIncrementer {

    fun increment(version: Version, project: Project): Version
}
