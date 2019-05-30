package io.wusa

import io.wusa.RegexResolver.Companion.findMatchingRegex
import io.wusa.extension.SemverGitPluginExtension
import io.wusa.incrementer.VersionIncrementer
import org.gradle.api.GradleException
import org.gradle.api.Project

data class Info(private var initialVersion: String, private var project: Project) {
    private val semverGitPluginExtension: SemverGitPluginExtension = project.extensions.getByType(SemverGitPluginExtension::class.java)

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
                val regexIncrementerPair = findMatchingRegex(semverGitPluginExtension.branches, semverGitPluginExtension.info.branch.name)
                regexIncrementerPair?.let {
                    VersionIncrementer.getVersionIncrementerByName(regexIncrementerPair.incrementer).increment(GitService.describe(initialVersion, project))
                } ?: run {
                    VersionIncrementer.getVersionIncrementerByName(SemverGitPluginExtension.DEFAULT_INCREMENTER).increment(GitService.describe(initialVersion, project))
                }
            } catch (ex: IllegalArgumentException) {
                throw GradleException("The current or last tag is not a semantic version.")
            }
        }
}