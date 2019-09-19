package io.wusa

import io.wusa.exception.NoCurrentBranchFoundException
import org.gradle.api.Project

data class Branch(private var project: Project) {
    val group: String
        get() = this.name.split("/")[0]

    val name: String
        get() {
            return try {
                GitService.currentBranch(project)
            } catch (ex: NoCurrentBranchFoundException) {
                ""
            }
        }

    val id: String
        get() = this.name.replace("/", "-")
}