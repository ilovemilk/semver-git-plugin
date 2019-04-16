package io.wusa

import java.io.File

class GitService {
    companion object {
        fun describe(nextVersion: String, gitArgs: String, projectDir: File): Version {
            val splitGitArgs = gitArgs.split(" ").toTypedArray()
            var process = ProcessBuilder("git", "describe", "--exact-match", *splitGitArgs)
                    .directory(projectDir)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start()
            process.waitFor()
            if (process.exitValue() == 0) {
                val describe = process.inputStream.bufferedReader().use { it.readText() }.trim()
                return VersionService.parseVersion(describe)
            }
            process = ProcessBuilder("git", "describe", "--dirty", "--abbrev=7", *splitGitArgs)
                    .directory(projectDir)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start()
            process.waitFor()
            if (process.exitValue() == 0) {
                val describe = process.inputStream.bufferedReader().use { it.readText() }.trim()
                val version = VersionService.parseVersion(describe)

                return VersionService.bumpVersion(version, nextVersion)
            }
            return VersionService.bumpVersion(Version(0, 0, 0, null), nextVersion)
        }

        fun currentBranch(projectDir: File): String {
            val process = ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD")
                    .directory(projectDir)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start()
            process.waitFor()
            if (process.exitValue() == 0) {
                return process.inputStream.bufferedReader().use { it.readText() }.trim()
            }
            return ""
        }

        fun currentCommit(projectDir: File, isShort: Boolean): String {
            if (isShort) {
                val process = ProcessBuilder("git", "rev-parse", "--short", "HEAD")
                        .directory(projectDir)
                        .redirectError(ProcessBuilder.Redirect.INHERIT)
                        .start()
                process.waitFor()
                if (process.exitValue() == 0) {
                    return process.inputStream.bufferedReader().use { it.readText() }.trim()
                }
                return ""
            } else {
                val process = ProcessBuilder("git", "rev-parse", "HEAD")
                        .directory(projectDir)
                        .redirectError(ProcessBuilder.Redirect.INHERIT)
                        .start()
                process.waitFor()
                if (process.exitValue() == 0) {
                    return process.inputStream.bufferedReader().use { it.readText() }.trim()
                }
                return ""
            }
        }

        fun currentTag(projectDir: File, gitArgs: String): String {
            val splitGitArgs = gitArgs.split(" ").toTypedArray()
            val process = ProcessBuilder("git", "describe", "--tags", "--exact-match", *splitGitArgs)
                    .directory(projectDir)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start()
            process.waitFor()
            if (process.exitValue() == 0) {
                return process.inputStream.bufferedReader().use { it.readText() }.trim()
            }
            return "none"
        }

        fun lastTag(projectDir: File, gitArgs: String): String {
            val splitGitArgs = gitArgs.split(" ").toTypedArray()
            val process = ProcessBuilder("git", "describe", "--tags", "--abbrev=0", *splitGitArgs)
                    .directory(projectDir)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start()
            process.waitFor()
            if (process.exitValue() == 0) {
                return process.inputStream.bufferedReader().use { it.readText() }.trim()
            }
            return "none"
        }

        fun isDirty(projectDir: File): Boolean {
            val process = ProcessBuilder("git", "diff", "--stat")
                    .directory(projectDir)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start()
            process.waitFor()
            if (process.exitValue() == 0) {
                return process.inputStream.bufferedReader().use { it.readText() }.trim() != ""
            }
            return false
        }

    }
}