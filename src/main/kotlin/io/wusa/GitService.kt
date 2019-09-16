package io.wusa

import io.wusa.exception.GitException
import io.wusa.exception.NoAnnotatedTagFoundException
import org.gradle.api.Project

class GitService {
    companion object {
        fun currentBranch(project: Project): String {
            return try {
                val branches = GitCommandRunner.execute(projectDir, arrayOf("branch", "--all", "--verbose", "--no-abbrev", "--contains"))
                return """(remotes)*/*(origin)*/*([a-z_-]*/?[a-z_-]+)\s+[0-9a-z]{40}""".toRegex().find(branches)!!.groupValues[3]
            } catch (ex: GitException) {
                ""
            } catch (ex: KotlinNullPointerException) {
                ""
            }
        }

        fun currentCommit(project: Project, isShort: Boolean): String {
            return if (isShort) {
                try {
                    GitCommandRunner.execute(project.projectDir, arrayOf("rev-parse", "--short", "HEAD"))
                } catch (ex: GitException) {
                    ""
                }
            } else {
                try {
                    GitCommandRunner.execute(project.projectDir, arrayOf("rev-parse", "HEAD"))
                } catch (ex: GitException) {
                    ""
                }
            }
        }

        fun currentTag(project: Project): String {
            return try {
                GitCommandRunner.execute(project.projectDir, arrayOf("describe", "--exact-match"))
            } catch (ex: GitException) {
                "none"
            }
        }

        fun lastTag(project: Project): String {
            return try {
                GitCommandRunner.execute(project.projectDir, arrayOf("describe", "--dirty", "--abbrev=7"))
            } catch (ex: GitException) {
                "none"
            }
        }

        fun isDirty(project: Project): Boolean {
            return try {
                GitCommandRunner.execute(project.projectDir, arrayOf("diff", "--stat")) != ""
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
    }
}