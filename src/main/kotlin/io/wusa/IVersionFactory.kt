package io.wusa

import org.gradle.api.Project

interface IVersionFactory {

    fun createFromString(describe: String, project: Project): Version
}