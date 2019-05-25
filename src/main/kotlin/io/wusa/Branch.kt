package io.wusa

import org.gradle.api.Project

data class Branch(private var project: Project) {
    val group: String
        get() = this.name.split("/")[0]

    val name: String
        get() = GitService.currentBranch(project)

    val id: String
        get() = this.name.replace("/", "-")
}