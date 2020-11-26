package io.wusa

import io.wusa.exception.*
import org.gradle.api.Project

class GitService {
    companion object {

        @Throws(NoCurrentBranchFoundException::class)
        fun currentBranch(project: Project): String {
            return try {
                getCurrentBranch(project)
            } catch (ex: GitException) {
                throw NoCurrentBranchFoundException(ex)
            } catch (ex: KotlinNullPointerException) {
                throw NoCurrentBranchFoundException(ex)
            }
        }

        @Throws(NoCurrentCommitFoundException::class)
        fun currentCommit(project: Project, isShort: Boolean): String {
            return if (isShort) {
                getCurrentShortCommit(project)
            } else {
                getCurrentCommit(project)
            }
        }

        @Throws(NoCurrentTagFoundException::class)
        fun currentTag(project: Project, tagPrefix : String = "", tagType : TagType = TagType.ANNOTATED): String {
            var cmdArgs = arrayOf("describe", "--exact-match", "--match", "$tagPrefix*")
            if (tagType == TagType.LIGHTWEIGHT){
                cmdArgs = arrayOf("describe", "--tags", "--exact-match", "--match", "$tagPrefix*")
            }
            return try {
                GitCommandRunner.execute(project.projectDir, cmdArgs)
            } catch (ex: GitException) {
                throw NoCurrentTagFoundException(ex)
            }
        }

        @Throws(NoLastTagFoundException::class)
        fun lastTag(project : Project, tagPrefix : String = "", tagType : TagType = TagType.ANNOTATED): String {
            var cmdArgs = arrayOf("describe", "--abbrev=7", "--match", "$tagPrefix*")
            if (tagType == TagType.LIGHTWEIGHT){
                cmdArgs = arrayOf("describe", "--tags", "--abbrev=7", "--match", "$tagPrefix*")
            }
            return try {
                GitCommandRunner.execute(project.projectDir, cmdArgs)
            } catch (ex: GitException) {
                throw NoLastTagFoundException(ex)
            }
        }

        fun isDirty(project: Project): Boolean {
            return try {
                isGitDifferent(project)
            } catch (ex: GitException) {
                false
            }
        }

        fun count(project: Project): Int {
            return try {
                GitCommandRunner.execute(project.projectDir, arrayOf("rev-list", "--count", "HEAD")).toInt()
            } catch (ex: GitException) {
                0
            }
        }

        fun getCommitsSinceLastTag(project: Project, tagPrefix : String = "", tagType : TagType = TagType.ANNOTATED): List<String> {
            var cmdArgs = arrayOf("describe", "--abbrev=0", "--match", "$tagPrefix*")
            if (tagType == TagType.LIGHTWEIGHT) {
                cmdArgs = arrayOf("describe", "--tags", "--abbrev=0", "--match", "$tagPrefix*")
            }
            return try {
                val lastTag = GitCommandRunner.execute(project.projectDir, cmdArgs)
                GitCommandRunner.execute(project.projectDir, arrayOf("log", "--pretty=format:%s %(trailers:separator=%x2c)", "$lastTag..@")).lines()
            } catch (ex: GitException) {
                emptyList()
            }
        }

        private fun isGitDifferent(project: Project): Boolean {
            return GitCommandRunner.execute(project.projectDir, arrayOf("status", "-s")).isNotBlank()
        }

        private fun getCurrentCommit(project: Project): String {
            return try {
                GitCommandRunner.execute(project.projectDir, arrayOf("rev-parse", "HEAD"))
            } catch (ex: GitException) {
                throw NoCurrentCommitFoundException(ex)
            }
        }

        private fun getCurrentShortCommit(project: Project): String {
            return try {
                GitCommandRunner.execute(project.projectDir, arrayOf("rev-parse", "--short", "HEAD"))
            } catch (ex: GitException) {
                throw NoCurrentCommitFoundException(ex)
            }
        }

        private fun getCurrentBranch(project: Project): String {
            val head = GitCommandRunner.execute(project.projectDir, arrayOf("log", "-n", "1", "--pretty=%d", "HEAD"))
            return """\(HEAD -> (.*?)[,|)]""".toRegex().find(head)!!.groupValues[1]
        }
    }
}
