package io.wusa

import org.gradle.api.GradleException
import org.gradle.api.Project

data class Info(private var nextVersion: String, private var initialVersion: String, private var project: Project) {

    val branch: Branch
        get() = Branch(project)

    val commit: String
        get() = GitService.currentCommit(project, false)

    val shortCommit: String
        get() = GitService.currentCommit(project, true)

    val tag: String
        get() = GitService.currentTag(project)

    val lastTag: String
        get() = GitService.lastTag(project)

    val dirty: Boolean
        get() = GitService.isDirty(project)

    val count: Int
        get() = GitService.count(project)

    val version: Version
        get() {
            return try {
                GitService.describe(initialVersion, nextVersion, project)
            } catch (ex: IllegalArgumentException) {
                throw GradleException("The current or last tag is not a semantic version.")
            }
        }
}