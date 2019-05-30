package io.wusa

import io.wusa.extension.SemverGitPluginExtension
import io.wusa.incrementer.VersionIncrementer
import org.gradle.api.GradleException
import org.gradle.api.Project
import java.lang.IllegalArgumentException

class VersionService(private var project: Project) {
    private val semverGitPluginExtension: SemverGitPluginExtension = project.extensions.getByType(SemverGitPluginExtension::class.java)

    fun getVersion(): Version {
        val versionFactory: IVersionFactory = SemanticVersionFactory()
        var versionString = GitService.currentTag(project)
        if (versionString == "none") {
            versionString = GitService.lastTag(project)
            if (versionString == "none") {
                val sha = GitService.currentCommit(project, true)
                val isDirty = GitService.isDirty(project)
                val count = GitService.count(project)
                val version = versionFactory.createFromString(semverGitPluginExtension.initialVersion, project)
                version.suffix = Suffix(count, sha, isDirty)
                return version
            } else {
                try {
                    val version = versionFactory.createFromString(GitService.lastTag(project), project)
                    val regexIncrementerPair = RegexResolver.findMatchingRegex(semverGitPluginExtension.branches, semverGitPluginExtension.info.branch.name)
                    regexIncrementerPair?.let {
                        return VersionIncrementer.getVersionIncrementerByName(regexIncrementerPair.incrementer).increment(version)
                    } ?: run {
                        return VersionIncrementer.getVersionIncrementerByName(SemverGitPluginExtension.DEFAULT_INCREMENTER).increment(version)
                    }
                } catch (ex: IllegalArgumentException) {
                    throw GradleException("The last tag is not a semantic version.")
                }
            }
        } else {
            try {
                return versionFactory.createFromString(GitService.currentTag(project), project)
            } catch (ex: IllegalArgumentException) {
                throw GradleException("The current tag is not a semantic version.")
            }
        }
    }
}