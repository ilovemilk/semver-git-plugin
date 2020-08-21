package io.wusa

import io.wusa.exception.*
import org.gradle.api.Project

class GitService {
    companion object {

        @Throws(NoCurrentBranchFoundException::class)
        fun currentBranch(project: Project): String {
            return try {
                val branches = getCurrentBranch(project)
                filterCurrentBranch(branches)
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
            var cmdArgs = arrayOf("describe", "--dirty", "--abbrev=7", "--match", "$tagPrefix*")
            if (tagType == TagType.LIGHTWEIGHT){
                cmdArgs = arrayOf("describe", "--tags", "--dirty", "--abbrev=7", "--match", "$tagPrefix*")
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

        fun getCommitsSinceLastTag(project: Project): List<String> {
            return try {
                GitCommandRunner.execute(project.projectDir, arrayOf("log", "--oneline", "\$(git describe --tags --abbrev=0 @^)..@")).lines()
            } catch (ex: GitException) {
                emptyList()
            }
        }

        private fun isGitDifferent(project: Project) =
                GitCommandRunner.execute(project.projectDir, arrayOf("diff", "--stat")) != ""

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

        private fun filterCurrentBranch(branches: String) =
                """(\*)? +(.*?) +(.*?)?""".toRegex().find(branches)!!.groupValues[2]

        private fun getCurrentBranch(project: Project): String {
            val branchName = GitCommandRunner.execute(project.projectDir, arrayOf("branch", "--show-current"))
            return GitCommandRunner.execute(project.projectDir, arrayOf("branch", branchName, "--verbose", "--no-abbrev", "--contains"))
        }

        private fun getAllBranches(project: Project): String {
            return GitCommandRunner.execute(project.projectDir, arrayOf("branch", "--all", "--verbose", "--no-abbrev", "--contains"))
        }
    }
}
