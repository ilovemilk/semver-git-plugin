package io.wusa

import io.wusa.exception.GitException
import org.gradle.api.Project
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GitCommandRunner(val project: Project) {
    fun execute(args: Array<String>): String {
        val process = startGitProcess(args, project.projectDir)
        waitForGitProcess(process)
        if (processFinishedWithoutErrors(process)) return readProcessOutput(process)

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
