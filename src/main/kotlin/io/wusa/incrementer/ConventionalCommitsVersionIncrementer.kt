package io.wusa.incrementer

import io.wusa.GitService
import io.wusa.Version
import io.wusa.extension.SemverGitPluginExtension
import org.gradle.api.Transformer
import org.koin.java.KoinJavaComponent.inject

object ConventionalCommitsVersionIncrementer : Transformer<Version, Version> {
    private val gitService: GitService by inject(GitService::class.java)
    private val semverGitPluginExtension by inject(SemverGitPluginExtension::class.java)

    override fun transform(version: Version): Version {
        val listOfCommits = gitService.getCommitsSinceLastTag(semverGitPluginExtension.tagPrefix, semverGitPluginExtension.tagType)
        var major = 0
        var minor = 0
        var patch = 0
        listOfCommits.forEach {
            if (it.contains("""^[0-9a-f]{7} BREAKING CHANGE""".toRegex())) {
                major += 1
            }
            if (it.contains("""^[0-9a-f]{7} feat""".toRegex())) {
                minor += 1
            }
            if (it.contains("""^[0-9a-f]{7} fix""".toRegex())) {
                patch += 1
            }
        }
        if (patch > 0) {
            version.patch += 1
        }
        if (minor > 0) {
            version.patch = 0
            version.minor += 1
        }
        if (major > 0) {
            version.patch = 0
            version.minor = 0
            version.major += 1
        }
        return version
    }
}
