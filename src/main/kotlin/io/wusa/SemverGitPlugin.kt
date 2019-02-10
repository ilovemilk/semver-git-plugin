package io.wusa

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

data class Version(var major: Int, var minor: Int, var patch: Int, var suffix: String) {
    override fun toString(): String {
        return when (suffix.isEmpty()) {
            true -> "$major.$minor.$patch"
            false -> "$major.$minor.$patch-$suffix"
        }
    }
}

class SemverGitPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val semverGitPluginExtension = project.extensions.create("semverGitPlugin", SemverGitPluginExtension::class.java)

        project.afterEvaluate {
            val version = getGitVersion(semverGitPluginExtension.nextVersion, semverGitPluginExtension.snapshotSuffix, semverGitPluginExtension.dirtyMarker, semverGitPluginExtension.gitDescribeArgs, project.projectDir)
            project.version = version.toString()
        }

        project.task("showVersion") {
            it.group = "Help"
            it.description = "Show the project version"
        }
        project.tasks.getByName("showVersion").doLast {
            println("Version: " + project.version)
        }
    }

    fun parseVersion(version: String): Version {
        val regex = """^([0-9]+)\.([0-9]+)\.([0-9]+)(-([a-zA-Z0-9.-]+))?$""".toRegex()
        val matchResult = regex.find(version)
        val (major, minor, patch, suffix) = matchResult!!.destructured
        return Version(major.toInt(), minor.toInt(), patch.toInt(), suffix)
    }

    fun bumpVersion(version: Version, nextVersion: String, snapshotSuffix: String): Version {
        when(nextVersion) {
            "major" -> {
                if (version.suffix.isEmpty()) {
                    version.major += 1
                    version.minor = 0
                    version.patch = 0
                }
                version.suffix = snapshotSuffix
                return version
            }
            "minor" -> {
                if (version.suffix.isEmpty()) {
                    version.minor += 1
                    version.patch = 0
                }
                version.suffix = snapshotSuffix
                return version
            }
            "patch" -> {
                if (version.suffix.isEmpty()) {
                    version.patch += 1
                }
                version.suffix = snapshotSuffix
                return version
            }
            else -> {
                return parseVersion(nextVersion)
            }
        }
    }

    fun getGitVersion(nextVersion: String, snapshotSuffix: String, dirtyMarker: String, gitArgs: String, projectDir: File): Version {
        val splittedGitArgs = gitArgs.split(" ").toTypedArray()
        var process = ProcessBuilder("git", "describe", "--exact-match", *splittedGitArgs)
                .directory(projectDir)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
        process.waitFor()
        if (process.exitValue() == 0) {
            val output = process.inputStream.bufferedReader().use { it.readText() }
            return parseVersion(output)
        }
        process = ProcessBuilder("git", "describe", "--dirty", "--abbrev=7", *splittedGitArgs)
                .directory(projectDir)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
        process.waitFor()
        if (process.exitValue() == 0) {
            val output = process.inputStream.bufferedReader().use { it.readText() }
            var describe = output.trim()
            val dirty = describe.endsWith("-dirty")
            if (dirty)
                describe = describe.substring(0, describe.length - 6)
            val versionRegex = """-[0-9]+-g[0-9a-f]+$""".toRegex()
            val version = describe.replace(versionRegex, "")
            val suffixRegex = """-([0-9]+)-g([0-9a-f]+)$""".toRegex()
            val (count, sha) = suffixRegex.find(describe)!!.destructured
            var suffix = snapshotSuffix
            suffix = suffix.replace("<count>", count)
            suffix = suffix.replace("<sha>", sha)
            if (dirty)
                suffix = suffix.replace("<dirty>", dirtyMarker)
            else
                suffix = suffix.replace("<dirty>", dirtyMarker)
            return bumpVersion(parseVersion(version), nextVersion, suffix)
        }
        return bumpVersion(Version(0, 0, 0, ""), nextVersion, "SNAPSHOT")
    }
}