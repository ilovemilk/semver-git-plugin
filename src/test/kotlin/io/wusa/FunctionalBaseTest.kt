package io.wusa

import org.gradle.internal.impldep.org.eclipse.jgit.api.Git
import java.io.File

abstract class FunctionalBaseTest {

    fun initializeGitWithBranch(directory: File, tag: String = "0.1.0", branch: String = "develop"): Git {
        val git = Git.init().setDirectory(directory).call()
        val commit = git.commit().setMessage("").call()
        git.checkout().setCreateBranch(true).setName(branch).call()
        git.tag().setName(tag).setObjectId(commit).call()
        return git
    }

    fun initializeGitWithoutBranchAnnotated(directory: File, tag: String = "0.1.0"): Git {
        val git = Git.init().setDirectory(directory).call()
        val commit = git.commit().setMessage("").call()
        git.tag().setName(tag).setMessage(tag).setAnnotated(true).setObjectId(commit).call()
        return git
    }

    fun initializeGitWithoutBranchLightweight(directory: File, tag: String = "0.1.0"): Git {
        val git = Git.init().setDirectory(directory).call()
        val commit = git.commit().setMessage("").call()
        git.tag().setName(tag).setObjectId(commit).setAnnotated(false).call()
        return git
    }

    fun initializeGitWithoutBranchAndWithoutTag(directory: File): Git {
        val git = Git.init().setDirectory(directory).call()
        git.commit().setMessage("").call()
        return git
    }
}
