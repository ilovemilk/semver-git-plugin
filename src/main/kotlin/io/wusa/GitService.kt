package io.wusa

import io.wusa.exception.GitException
import java.io.File

class GitService {
    companion object {
        fun describe(initialVersion: String, nextVersion: String, projectDir: File, snapshotSuffix: String = SemverGitPluginExtension.DEFAULT_SNAPSHOT_SUFFIX, dirtyMarker: String = SemverGitPluginExtension.DEFAULT_DIRTY_MARKER): Version {
            val versionFactory: VersionFactory = SemanticVersionFactory(snapshotSuffix, dirtyMarker)
            return try {
                val describe = GitCommandRunner.execute(projectDir, arrayOf("describe", "--exact-match"))
                versionFactory.createFromString(describe)
            } catch (ex: GitException) {
                try {
                    val describe = GitCommandRunner.execute(projectDir, arrayOf("describe", "--dirty", "--abbrev=7"))
                    versionFactory.createFromString(describe).bump(nextVersion)
                } catch (ex: GitException) {
                    val sha = currentCommit(projectDir, true)
                    val isDirty = isDirty(projectDir)
                    val count = count(projectDir)
                    val version = versionFactory.createFromString(initialVersion)
                    version.suffix  = Suffix(count, sha, isDirty)
                    return version
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

        private fun count(projectDir: File): Int {
            return try {
                GitCommandRunner.execute(projectDir, arrayOf("rev-list", "--count", "HEAD")).toInt()
            } catch (ex: GitException) {
                0
            }
        }
    }
}