package io.wusa

import org.gradle.api.Project

open class SemverGitPluginExtension(private var project: Project) {
    var nextVersion: String = DEFAULT_NEXT_VERSION

    var dirtyMarker: String = DEFAULT_DIRTY_MARKER

    var initialVersion: String = DEFAULT_INITIAL_VERSION

    var snapshotSuffix: String = DEFAULT_SNAPSHOT_SUFFIX

    var versionFormatter: (version: Version) -> String = {"${info.version.major}.${info.version.minor}.${info.version.patch}"}

    val info: Info
        get() = Info(nextVersion, initialVersion, project)

    companion object {
        const val DEFAULT_NEXT_VERSION = "minor"
        const val DEFAULT_SNAPSHOT_SUFFIX = "SNAPSHOT"
        const val DEFAULT_DIRTY_MARKER = "-dirty"
        const val DEFAULT_INITIAL_VERSION = "0.1.0"
    }
}