package io.wusa

import org.gradle.internal.impldep.org.eclipse.jgit.api.Git
import org.gradle.internal.impldep.org.eclipse.jgit.lib.Repository
import java.io.File

abstract class FunctionalBaseTest {

    fun initializeGitWithBranch(repository: Repository, directory: File, tag: String = "0.1.0", branch: String = "develop") {
        val gitIgnore = File(directory, ".gitignore")
        gitIgnore.writeText(".gradle")
        Git(repository).add().addFilepattern(".").call()
        val commit = Git(repository).commit().setMessage("").call()
        Git(repository).checkout().setCreateBranch(true).setName(branch).call()
        Git(repository).tag().setName(tag).setObjectId(commit).call()
    }

    fun initializeGitWithoutBranchAnnotated(repository: Repository, directory: File, tag: String = "0.1.0") {
        val gitIgnore = File(directory, ".gitignore")
        gitIgnore.writeText(".gradle")
        Git(repository).add().addFilepattern(".").call()
        val commit = Git(repository).commit().setMessage("").call()
        Git(repository).tag().setName(tag).setMessage(tag).setAnnotated(true).setObjectId(commit).call()
    }

    fun initializeGitWithoutBranchLightweight(repository: Repository, directory: File, tag: String = "0.1.0") {
        val gitIgnore = File(directory, ".gitignore")
        gitIgnore.writeText(".gradle")
        Git(repository).add().addFilepattern(".").call()
        val commit = Git(repository).commit().setMessage("").call()
        Git(repository).tag().setName(tag).setObjectId(commit).setAnnotated(false).call()
    }

    fun initializeGitWithoutBranchAndWithoutTag(repository: Repository, directory: File) {
        val gitIgnore = File(directory, ".gitignore")
        gitIgnore.writeText(".gradle")
        Git(repository).add().addFilepattern(".").call()
        Git(repository).commit().setMessage("").call()
    }
}
