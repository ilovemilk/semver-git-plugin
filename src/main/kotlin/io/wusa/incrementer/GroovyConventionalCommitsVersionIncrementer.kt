package io.wusa.incrementer

import io.wusa.Version

object GroovyConventionalCommitsVersionIncrementer {
    @JvmStatic
    fun transform(version: Version) : Version {
        return ConventionalCommitsVersionIncrementer.transform(version)
    }
}