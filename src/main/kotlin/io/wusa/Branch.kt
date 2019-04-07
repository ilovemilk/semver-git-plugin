package io.wusa

import java.io.File

class Branch(private var projectDir: File) {

    val group: String
        get() = "test"

    val name: String
        get() = GitService.currentBranch(projectDir)
}