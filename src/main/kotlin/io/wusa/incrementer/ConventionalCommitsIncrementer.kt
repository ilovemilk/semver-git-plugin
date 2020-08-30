package io.wusa.incrementer

import io.wusa.GitService
import io.wusa.Info
import io.wusa.Version
import org.gradle.api.Transformer

class ConventionalCommitsIncrementer: Transformer<Version, Info> {
    override fun transform(info: Info): Version {
        val listOfCommits = GitService.getCommitsSinceLastTag(info.project)
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
            info.version.patch += 1
        }
        if (minor > 0) {
            info.version.patch = 0
            info.version.minor += 1
        }
        if (major > 0) {
            info.version.patch = 0
            info.version.minor = 0
            info.version.major += 1
        }
        return info.version
    }
}
