package io.wusa

import org.gradle.api.GradleException
import java.io.File

data class Info(private var nextVersion: String, private var initialVersion: String, private var projectDir: File) {

    val branch: Branch
        get() = Branch(projectDir)

    val commit: String
        get() = GitService.currentCommit(projectDir, false)

    val shortCommit: String
        get() = GitService.currentCommit(projectDir, true)

    val tag: String
        get() = GitService.currentTag(projectDir)

    val lastTag: String
        get() = GitService.lastTag(projectDir)

    val dirty: Boolean
        get() = GitService.isDirty(projectDir)

    val version: Version
        get() {
            return try {
                GitService.describe(initialVersion, nextVersion, projectDir)
            } catch (ex: IllegalArgumentException) {
                throw GradleException("The current or last tag is not a semantic version.")
            }
        }
}