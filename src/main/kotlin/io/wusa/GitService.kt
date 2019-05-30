package io.wusa

import io.wusa.exception.GitException
import io.wusa.exception.NoAnnotatedTagFoundException
import org.gradle.api.Project

class GitService {
    companion object {
        fun describe(project: Project): Version {
            val versionFactory: IVersionFactory = SemanticVersionFactory()
            return try {
                val describe = GitCommandRunner.execute(project.projectDir, arrayOf("describe", "--exact-match"))
                versionFactory.createFromString(describe, project)
            } catch (ex: GitException) {
                try {
                    val describe = GitCommandRunner.execute(project.projectDir, arrayOf("describe", "--dirty", "--abbrev=7"))
                    // annotated tag found use tagged version as current version
                    versionFactory.createFromString(describe, project)
                } catch (ex: GitException) {
                    // no annotated tag found will use initialVersion as current version
                    throw NoAnnotatedTagFoundException("No annotated tag found.")
                }
            }
        }

        fun currentBranch(project: Project): String {
            return try {
                GitCommandRunner.execute(project.projectDir, arrayOf("rev-parse", "--abbrev-ref", "HEAD"))
            } catch (ex: GitException) {
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