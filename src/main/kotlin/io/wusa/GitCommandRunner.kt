package io.wusa

import io.wusa.exception.GitException
import java.io.File
import java.util.concurrent.TimeUnit

class GitCommandRunner {
    companion object {
        fun execute(projectDir: File, args: Array<String>): String {
            val process = ProcessBuilder("git", *args)
                    .directory(projectDir)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE)
                    .start()
            if (!process.waitFor(10, TimeUnit.SECONDS)) {
                process.destroy()
                throw RuntimeException("Execution timed out: $this")
            }
            if (process.exitValue() == 0) {
                return process.inputStream.bufferedReader().use { it.readText() }.trim()
            }
            throw GitException("Executing git command failed with " + process.exitValue())
        }
    }
}