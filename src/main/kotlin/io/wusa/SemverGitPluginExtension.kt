package io.wusa

import java.io.File

open class SemverGitPluginExtension(private var projectDir: File) {
    var nextVersion: String = DEFAULT_NEXT_VERSION

    var snapshotSuffix: String = DEFAULT_SNAPSHOT_SUFFIX

    var dirtyMarker: String = DEFAULT_DIRTY_MARKER

    var initialVersion: String = DEFAULT_INITIAL_VERSION

    val info: Info
        get() = Info(nextVersion, snapshotSuffix, dirtyMarker, initialVersion, projectDir)

    companion object {
        const val DEFAULT_NEXT_VERSION = "minor"
        const val DEFAULT_SNAPSHOT_SUFFIX = "SNAPSHOT"
        const val DEFAULT_DIRTY_MARKER = "-dirty"
        const val DEFAULT_INITIAL_VERSION = "0.1.0"
    }
}