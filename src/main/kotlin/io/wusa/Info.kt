package io.wusa

import java.io.File

class Info (private var nextVersion: String, private var snapshotSuffix: String, private var dirtyMarker: String, private var gitDescribeArgs: String, private var projectDir: File) {

    val branch: Branch
        get() = Branch(projectDir)

    val commit: String
        get() = GitService.currentCommitChecksum(projectDir)

    val tag: String
        get() = GitService.currentTag(projectDir, gitDescribeArgs)

    val lastTag: String
        get() = GitService.lastTag(projectDir, gitDescribeArgs)

    val dirty: Boolean
        get() = GitService.isDirty(projectDir)

    val version: String
        get() = GitService.describe(nextVersion, gitDescribeArgs, projectDir).format(snapshotSuffix, dirtyMarker)

}