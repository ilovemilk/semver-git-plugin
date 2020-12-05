package io.wusa

import io.wusa.exception.*

class GitService(private val gitCommandRunner: GitCommandRunner) {

    @Throws(NoCurrentBranchFoundException::class)
    fun currentBranch(): String {
        return try {
            getCurrentBranch()
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

    fun getCommitsSinceLastTag(tagPrefix: String = "", tagType: TagType = TagType.ANNOTATED): List<String> {
        var cmdArgs = arrayOf("describe", "--dirty", "--abbrev=0", "--match", "$tagPrefix*")
        if (tagType == TagType.LIGHTWEIGHT) {
            cmdArgs = arrayOf("describe", "--tags", "--dirty", "--abbrev=0", "--match", "$tagPrefix*")
        }
        return try {
            val lastTag = gitCommandRunner.execute(cmdArgs)
            gitCommandRunner.execute(arrayOf("log", "--pretty=format:%s %(trailers:separator=%x2c)", "$lastTag..@")).lines()
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

    private fun getCurrentBranch(): String {
        val head = gitCommandRunner.execute(arrayOf("log", "-n", "1", "--pretty=%d", "HEAD"))
        return """\(HEAD -> (.*?)[,|)]""".toRegex().find(head)!!.groupValues[1]
    }
}
