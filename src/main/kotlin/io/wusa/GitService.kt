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
    }
}