package io.wusa

import io.wusa.exception.NoCurrentTagFoundException
import io.wusa.exception.NoLastTagFoundException
import io.wusa.extension.SemverGitPluginExtension
import io.wusa.formatter.SemanticVersionFormatter
import org.gradle.api.Project
import org.koin.java.KoinJavaComponent.inject

data class Info(var semverGitPluginExtension: SemverGitPluginExtension) {
    private val gitService: GitService by inject(GitService::class.java)
    private val versionService: VersionService by inject(VersionService::class.java)

    val branch: Branch
        get() = Branch(gitService)

    val commit: String
        get() {
            return try {
                gitService.currentCommit(false)
            } catch (ex: NoCurrentTagFoundException) {
                "none"
            }
        }

    val shortCommit: String
        get() {
            return try {
                gitService.currentCommit(true)
            } catch (ex: NoCurrentTagFoundException) {
                "none"
            }
        }

    val tag: String
        get() {
            return try {
                gitService.currentTag(tagType = semverGitPluginExtension.tagType)
            } catch (ex: NoCurrentTagFoundException) {
                "none"
            }
        }

    val lastTag: String
        get() {
            return try {
                gitService.lastTag(tagType = semverGitPluginExtension.tagType)
            } catch (ex: NoLastTagFoundException) {
                "none"
            }
        }

    val dirty: Boolean
        get() = gitService.isDirty()

    val count: Int
        get() = gitService.count()

    val version: Version
        get() = versionService.getVersion()

    override fun toString(): String {
        return SemanticVersionFormatter.format(this, semverGitPluginExtension.branches, semverGitPluginExtension.snapshotSuffix)
    }
}
