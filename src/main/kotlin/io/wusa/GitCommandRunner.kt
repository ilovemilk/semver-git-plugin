package io.wusa

import io.wusa.exception.GitException
import java.io.File
import java.util.concurrent.TimeUnit

class GitCommandRunner {
    companion object {
        fun execute(projectDir: File, args: Array<String>): String {
            val process = startGitProcess(args, projectDir)
            val output = readProcessOutput(process)
            waitForGitProcess(process)
            if (processFinishedWithoutErrors(process)) return output

            throw GitException("Executing git command failed with " + process.exitValue())
        }

        private fun readProcessOutput(process: Process): String {
            return process.inputStream.bufferedReader().use { it.readText() }.trim()
        }

        private fun processFinishedWithoutErrors(process: Process): Boolean {
            if (process.exitValue() == 0) {
                return true
            }
            return false
        }

        private fun waitForGitProcess(process: Process) {
            if (!process.waitFor(10, TimeUnit.SECONDS)) {
                process.destroy()
                throw RuntimeException("Execution timed out: $this")
            }
        }

        private fun startGitProcess(args: Array<String>, projectDir: File): Process {
            return ProcessBuilder("git", *args)
                    .directory(projectDir)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE)
                    .start()
        }
    }
}
