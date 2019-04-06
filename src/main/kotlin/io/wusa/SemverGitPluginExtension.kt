package io.wusa

import java.io.File

open class SemverGitPluginExtension(private var projectDir: File) {
    var nextVersion: String = DEFAULT_NEXT_VERSION

    var snapshotSuffix: String = DEFAULT_SNAPSHOT_SUFFIX

    var dirtyMarker: String = DEFAULT_DIRTY_MARKER

    var gitDescribeArgs: String = DEFAULT_GIT_DESCRIBE_ARGS

    val version: String
        get() = GitService.describe(nextVersion, gitDescribeArgs, projectDir).format(snapshotSuffix, dirtyMarker)

    companion object {
        const val DEFAULT_NEXT_VERSION = "minor"
        const val DEFAULT_SNAPSHOT_SUFFIX = "SNAPSHOT"
        const val DEFAULT_DIRTY_MARKER = "-dirty"
        const val DEFAULT_GIT_DESCRIBE_ARGS = "--match *[0-9].[0-9]*.[0-9]*"
    }
}