package io.wusa.extension

import io.wusa.Info
import io.wusa.TagType
import io.wusa.incrementer.VersionIncrementer
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Transformer

open class SemverGitPluginExtension(private var project: Project) {
    val branches: Branches = Branches(project)
    fun branches(configure: Action<in Branches>) = configure.execute(branches)

    var dirtyMarker: String = DEFAULT_DIRTY_MARKER

    var initialVersion: String = DEFAULT_INITIAL_VERSION

    var snapshotSuffix: String = DEFAULT_SNAPSHOT_SUFFIX

    var tagPrefix: String = DEFAULT_TAG_PREFIX

    var tagType: TagType = DEFAULT_TAG_TYPE

    val info: Info
        get() = Info(project)

    companion object {
        const val DEFAULT_SNAPSHOT_SUFFIX = "SNAPSHOT"
        const val DEFAULT_DIRTY_MARKER = "dirty"
        const val DEFAULT_INITIAL_VERSION = "0.1.0"
        val DEFAULT_INCREMENTER = VersionIncrementer.MINOR_INCREMENTER
        const val DEFAULT_TAG_PREFIX = ""
        val DEFAULT_TAG_TYPE = TagType.ANNOTATED
        val DEFAULT_FORMATTER: Transformer<Any, Info> = Transformer { info: Info -> "${info.version.major}.${info.version.minor}.${info.version.patch}+build.${info.count}.sha.${info.shortCommit}" }
    }
}
