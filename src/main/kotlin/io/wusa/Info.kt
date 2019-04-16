package io.wusa

import java.io.File

data class Info (private var nextVersion: String, private var snapshotSuffix: String, private var dirtyMarker: String, private var gitDescribeArgs: String, private var projectDir: File) {

    val branch: Branch
        get() = Branch(projectDir)

    val commit: String
        get() = GitService.currentCommit(projectDir, false)

    val shortCommit: String
        get() = GitService.currentCommit(projectDir, true)

    val tag: String
        get() = GitService.currentTag(projectDir, gitDescribeArgs)

    val lastTag: String
        get() = GitService.lastTag(projectDir, gitDescribeArgs)

    val dirty: Boolean
        get() = GitService.isDirty(projectDir)

    val version: String
        get() = GitService.describe(nextVersion, gitDescribeArgs, projectDir).format(snapshotSuffix, dirtyMarker)

}