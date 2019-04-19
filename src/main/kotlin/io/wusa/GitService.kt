package io.wusa

import io.wusa.exception.GitException
import java.io.File

class GitService {
    companion object {
        fun describe(nextVersion: String, projectDir: File): Version {
            return try {
                val describe = GitCommandRunner.execute(projectDir, arrayOf("describe", "--exact-match"))
                val versionFactory: VersionFactory = SemanticVersionFactory()
                versionFactory.createFromString(describe)
            } catch (ex: GitException) {
                try {
                    val describe = GitCommandRunner.execute(projectDir, arrayOf("describe", "--dirty", "--abbrev=7"))
                    val versionFactory: VersionFactory = SemanticVersionFactory()
                    versionFactory.createFromString(describe).bump(nextVersion)
                } catch (ex: GitException) {
                    return Version(0, 0, 0, "", "", null).bump(nextVersion)
                }
            }
        }

        fun currentBranch(projectDir: File): String {
            return try {
                GitCommandRunner.execute(projectDir, arrayOf("rev-parse", "--abbrev-ref", "HEAD"))
            } catch (ex: GitException) {
                ""
            }
        }

        fun currentCommit(projectDir: File, isShort: Boolean): String {
            return if (isShort) {
                try {
                    GitCommandRunner.execute(projectDir, arrayOf("rev-parse", "--short", "HEAD"))
                } catch (ex: GitException) {
                    ""
                }
            } else {
                try {
                    GitCommandRunner.execute(projectDir, arrayOf("rev-parse", "HEAD"))
                } catch (ex: GitException) {
                    ""
                }
            }
        }

        fun currentTag(projectDir: File): String {
            return try {
                GitCommandRunner.execute(projectDir, arrayOf("describe", "--tags", "--exact-match"))
            } catch (ex: GitException) {
                "none"
            }
        }

        fun lastTag(projectDir: File): String {
            return try {
                GitCommandRunner.execute(projectDir, arrayOf("describe", "--tags", "--abbrev=0"))
            } catch (ex: GitException) {
                "none"
            }
        }

        fun isDirty(projectDir: File): Boolean {
            return try {
                GitCommandRunner.execute(projectDir, arrayOf("diff", "--stat")) != ""
            } catch (ex: GitException) {
                false
            }
        }
    }
}