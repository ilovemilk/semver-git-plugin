package io.wusa

import io.wusa.exception.NoCurrentBranchFoundException
import org.koin.java.KoinJavaComponent.inject

class Branch(private val gitService: GitService) {
    val group: String
        get() = this.name.split("/")[0]

    val name: String
        get() {
            return try {
                gitService.currentBranch()
            } catch (ex: NoCurrentBranchFoundException) {
                ""
            }
        }

    val id: String
        get() = this.name.replace("/", "-")
}