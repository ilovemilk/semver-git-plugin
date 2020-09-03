package io.wusa

import io.wusa.exception.*

class GitService(private val gitCommandRunner: GitCommandRunner) {

    @Throws(NoCurrentBranchFoundException::class)
    fun currentBranch(): String {
        return try {
            val branches = getCurrentBranch()
            filterCurrentBranch(branches)
        } catch (ex: GitException) {
            throw NoCurrentBranchFoundException(ex)
        } catch (ex: KotlinNullPointerException) {
            throw NoCurrentBranchFoundException(ex)
        }
    }

    @Throws(NoCurrentCommitFoundException::class)
    fun currentCommit(isShort: Boolean): String {
        return if (isShort) {
            getCurrentShortCommit()
        } else {
            getCurrentCommit()
        }
    }

    @Throws(NoCurrentTagFoundException::class)
    fun currentTag(tagPrefix: String = "", tagType: TagType = TagType.ANNOTATED): String {
        var cmdArgs = arrayOf("describe", "--exact-match", "--match", "$tagPrefix*")
        if (tagType == TagType.LIGHTWEIGHT) {
            cmdArgs = arrayOf("describe", "--tags", "--exact-match", "--match", "$tagPrefix*")
        }
        return try {
            gitCommandRunner.execute(cmdArgs)
        } catch (ex: GitException) {
            throw NoCurrentTagFoundException(ex)
        }
    }

    @Throws(NoLastTagFoundException::class)
    fun lastTag(tagPrefix: String = "", tagType: TagType = TagType.ANNOTATED): String {
        var cmdArgs = arrayOf("describe", "--dirty", "--abbrev=7", "--match", "$tagPrefix*")
        if (tagType == TagType.LIGHTWEIGHT) {
            cmdArgs = arrayOf("describe", "--tags", "--dirty", "--abbrev=7", "--match", "$tagPrefix*")
        }
        return try {
            gitCommandRunner.execute(cmdArgs)
        } catch (ex: GitException) {
            throw NoLastTagFoundException(ex)
        }
    }

    fun isDirty(): Boolean {
        return try {
            isGitDifferent()
        } catch (ex: GitException) {
            false
        }
    }

    fun count(): Int {
        return try {
            gitCommandRunner.execute(arrayOf("rev-list", "--count", "HEAD")).toInt()
        } catch (ex: GitException) {
            0
        }
    }

    fun getCommitsSinceLastTag(): List<String> {
        return try {
            gitCommandRunner.execute(arrayOf("log", "--oneline", "\$(git describe --tags --abbrev=0 @^)..@")).lines()
        } catch (ex: GitException) {
            emptyList()
        }
    }

    private fun isGitDifferent() =
            gitCommandRunner.execute(arrayOf("diff", "--stat")) != ""

    private fun getCurrentCommit(): String {
        return try {
            gitCommandRunner.execute(arrayOf("rev-parse", "HEAD"))
        } catch (ex: GitException) {
            throw NoCurrentCommitFoundException(ex)
        }
    }

    private fun getCurrentShortCommit(): String {
        return try {
            gitCommandRunner.execute(arrayOf("rev-parse", "--short", "HEAD"))
        } catch (ex: GitException) {
            throw NoCurrentCommitFoundException(ex)
        }
    }

    private fun filterCurrentBranch(branches: String) =
            """(\*)? +(.*?) +(.*?)?""".toRegex().find(branches)!!.groupValues[2]

    private fun getCurrentBranch(): String {
        val branchName = gitCommandRunner.execute(arrayOf("branch", "--show-current"))
        return gitCommandRunner.execute(arrayOf("branch", branchName, "--verbose", "--no-abbrev", "--contains"))
    }

    private fun getAllBranches(): String {
        return gitCommandRunner.execute(arrayOf("branch", "--all", "--verbose", "--no-abbrev", "--contains"))
    }
}
