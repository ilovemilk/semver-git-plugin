package io.wusa

import io.wusa.exception.NoCurrentTagFoundException
import io.wusa.exception.NoLastTagFoundException
import io.wusa.extension.SemverGitPluginExtension
import io.wusa.formatter.SemanticVersionFormatter
import org.gradle.api.Project

data class Info(private var project: Project) {
    private val semverGitPluginExtension: SemverGitPluginExtension = project.extensions.getByType(SemverGitPluginExtension::class.java)

    val branch: Branch
        get() = Branch(project)

    val commit: String
        get() {
            return try {
                GitService.currentCommit(project, false)
            } catch (ex: NoCurrentTagFoundException) {
                "none"
            }
        }

    val shortCommit: String
        get() {
            return try {
                GitService.currentCommit(project, true)
            } catch (ex: NoCurrentTagFoundException) {
                "none"
            }
        }

    val tag: String
        get() {
            return try {
                GitService.currentTag(project, tagType = semverGitPluginExtension.tagType)
            } catch (ex: NoCurrentTagFoundException) {
                "none"
            }
        }

    val lastTag: String
        get() {
            return try {
                GitService.lastTag(project, tagType = semverGitPluginExtension.tagType)
            } catch (ex: NoLastTagFoundException) {
                "none"
            }
        }

    val dirty: Boolean
        get() = GitService.isDirty(project)

    val count: Int
        get() = GitService.count(project)

    val version: Version
        get() = VersionService(project).getVersion()

    override fun toString(): String {
        val semverGitPluginExtension: SemverGitPluginExtension = project.extensions.getByType(SemverGitPluginExtension::class.java)

        return SemanticVersionFormatter.format(this, semverGitPluginExtension.branches, semverGitPluginExtension.snapshotSuffix, semverGitPluginExtension.dirtyMarker)
    }
}
