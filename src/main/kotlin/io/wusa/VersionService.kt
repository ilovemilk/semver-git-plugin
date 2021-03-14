package io.wusa

import io.wusa.exception.*
import io.wusa.extension.SemverGitPluginExtension
import org.gradle.api.GradleException

class VersionService(private val semverGitPluginExtension: SemverGitPluginExtension, private val gitService: GitService) {

    @Throws(GradleException::class)
    fun getVersion(): Version {
        val versionFactory: IVersionFactory = SemanticVersionFactory()

        return try {
            getCurrentVersion(versionFactory)
        } catch (ex: IllegalArgumentException) {
            throw GradleException("The current tag is not a semantic version.")
        } catch (ex: NoCurrentTagFoundException) {
            handleNoCurrentTagFound(versionFactory)
        } catch (ex: DirtyWorkingTreeException) {
            handleDirtyWorkingTree(versionFactory)
        }
    }

    @Throws(GradleException::class)
    private fun handleDirtyWorkingTree(versionFactory: IVersionFactory): Version {
        return try {
            val lastVersion = getLastVersion(versionFactory)
            incrementVersion(lastVersion)
        } catch (ex: NoValidSemverTagFoundException) {
            throw GradleException(ex.localizedMessage)
        } catch (ex: NoIncrementerFoundException) {
            throw GradleException(ex.localizedMessage)
        } catch (ex: NoLastTagFoundException) {
            buildInitialVersion(versionFactory)
        }
    }

    @Throws(GradleException::class)
    private fun handleNoCurrentTagFound(versionFactory: IVersionFactory): Version {
        return try {
            val lastVersion = getLastVersion(versionFactory)
            incrementVersion(lastVersion)
        } catch (ex: NoValidSemverTagFoundException) {
            throw GradleException(ex.localizedMessage)
        } catch (ex: NoIncrementerFoundException) {
            throw GradleException(ex.localizedMessage)
        } catch (ex: NoLastTagFoundException) {
            buildInitialVersion(versionFactory)
        }
    }

    private fun buildInitialVersion(versionFactory: IVersionFactory): Version {
        return try {
            buildInitialVersionForTag(versionFactory)
        } catch (ex: NoCurrentCommitFoundException) {
            buildInitialVersionWithNoTag()
        }
    }

    private fun buildInitialVersionWithNoTag() = Version(0, 1, 0, "", "", null)

    private fun buildInitialVersionForTag(versionFactory: IVersionFactory): Version {
        val sha = gitService.currentCommit(true)
        val isDirty = gitService.isDirty()
        val count = gitService.count()
        val version = versionFactory.createFromString(semverGitPluginExtension.initialVersion)
        version.suffix = Suffix(count, sha, isDirty)
        return version
    }

    private fun getLastVersion(versionFactory: IVersionFactory): Version {
        val tagPrefix = semverGitPluginExtension.tagPrefix
        val lastTag = gitService.lastTag(tagPrefix, tagType = semverGitPluginExtension.tagType)
        if (!lastTag.startsWith(tagPrefix)) {
            throw NoCurrentTagFoundException("$lastTag doesn't match $tagPrefix")
        }

        return versionFactory.createFromString(lastTag.substring(tagPrefix.length))
    }

    private fun getCurrentVersion(versionFactory: IVersionFactory): Version {
        val tagPrefix = semverGitPluginExtension.tagPrefix
        val curTag = gitService.currentTag(tagPrefix, tagType = semverGitPluginExtension.tagType)
        if (!curTag.startsWith(tagPrefix)) {
            throw NoCurrentTagFoundException("$curTag doesn't match $tagPrefix")
        }
        val isDirty = gitService.isDirty()
        if (isDirty) {
            throw DirtyWorkingTreeException("The current working tree is dirty.")
        }

        return versionFactory.createFromString(curTag.substring(tagPrefix.length))
    }

    private fun incrementVersion(version: Version): Version {
        val regexIncrementerPair = RegexResolver.findMatchingRegex(semverGitPluginExtension.branches, semverGitPluginExtension.info.branch.name)
        regexIncrementerPair?.let {
            return regexIncrementerPair.incrementer.transform(version)
        } ?: run {
            return SemverGitPluginExtension.DEFAULT_INCREMENTER.transform(version)
        }
    }
}
