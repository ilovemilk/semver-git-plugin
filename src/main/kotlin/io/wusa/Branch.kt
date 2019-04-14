package io.wusa

import java.io.File

class Branch(private var projectDir: File) {

    fun test() {
        this.name.
    }

    val group: String
        get() = this.name.split("/")[0]

    val name: String
        get() = GitService.currentBranch(projectDir)

    val id: String
        get() = this.name.replace("/", "-")
}