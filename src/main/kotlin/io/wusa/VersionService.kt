package io.wusa

import io.wusa.exception.NoCurrentCommitFoundException
import io.wusa.exception.NoCurrentTagFoundException
import io.wusa.exception.NoLastTagFoundException
import io.wusa.extension.SemverGitPluginExtension
import io.wusa.incrementer.VersionIncrementer
import org.gradle.api.GradleException
import org.gradle.api.Project

class VersionService(private var project: Project) {
    private val semverGitPluginExtension: SemverGitPluginExtension = project.extensions.getByType(SemverGitPluginExtension::class.java)

    fun getVersion(): Version {
        val versionFactory: IVersionFactory = SemanticVersionFactory()

        try {
            return versionFactory.createFromString(GitService.currentTag(project))
        } catch (ex: IllegalArgumentException) {
            throw GradleException("The current tag is not a semantic version.")
        } catch (ex: NoCurrentTagFoundException) {
            try {
                val version = versionFactory.createFromString(GitService.lastTag(project))
                val regexIncrementerPair = RegexResolver.findMatchingRegex(semverGitPluginExtension.branches, semverGitPluginExtension.info.branch.name)
                regexIncrementerPair?.let {
                    return VersionIncrementer.getVersionIncrementerByName(regexIncrementerPair.incrementer).increment(version)
                } ?: run {
                    return VersionIncrementer.getVersionIncrementerByName(SemverGitPluginExtension.DEFAULT_INCREMENTER).increment(version)
                }
            } catch (ex: IllegalArgumentException) {
                throw GradleException("The last tag is not a semantic version.")
            } catch (ex: NoLastTagFoundException) {
                return try {
                    val sha = GitService.currentCommit(project, true)
                    val isDirty = GitService.isDirty(project)
                    val count = GitService.count(project)
                    val version = versionFactory.createFromString(semverGitPluginExtension.initialVersion)
                    version.suffix = Suffix(count, sha, isDirty)
                    version
                } catch (ex: NoCurrentCommitFoundException) {
                     Version(0, 1, 0, "", "", null)
                }
            }
        }
    }
}