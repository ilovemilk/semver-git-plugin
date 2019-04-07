package io.wusa

import java.io.File

class Info (private var nextVersion: String, private var snapshotSuffix: String, private var dirtyMarker: String, private var gitDescribeArgs: String, private var projectDir: File) {

    val branch: Branch
        get() = Branch(projectDir)

    val commit: String
        get() = "test"

    val tag: String
        get() = "test"

    val lastTag: String
        get() = "test"

    val dirty: String
        get() = "test"

    val version: String
        get() = GitService.describe(nextVersion, gitDescribeArgs, projectDir).format(snapshotSuffix, dirtyMarker)

}