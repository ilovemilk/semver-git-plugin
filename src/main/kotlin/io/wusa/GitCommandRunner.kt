package io.wusa

import io.wusa.exception.GitException
import java.io.File
import java.util.concurrent.ExecutionException

class GitCommandRunner {
    companion object {
        fun execute(projectDir: File, args: Array<String>): String {
            val process = ProcessBuilder("git", *args)
                    .directory(projectDir)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start()
            process.waitFor()
            if (process.exitValue() == 0) {
                return process.inputStream.bufferedReader().use { it.readText() }.trim()
            }
            throw GitException("Executing git command failed with " + process.exitValue())
        }
    }
}