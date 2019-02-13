package io.wusa

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.lang.IllegalArgumentException

data class Version(var major: Int, var minor: Int, var patch: Int, var suffix: Suffix?) {
    fun format(suffixFormat: String, dirtyMarker: String): String {
        if (suffix != null) {
            return "$major.$minor.$patch-${suffix!!.format(suffixFormat, dirtyMarker)}"
        } else {
            return "$major.$minor.$patch"
        }
    }
}

data class Suffix(var count: Int, var sha: String, var dirty: Boolean) {
    fun format(format: String, dirtyMarker: String): String {
        var formattedSuffix = format
        formattedSuffix = formattedSuffix.replace("<count>", count.toString())
        formattedSuffix = formattedSuffix.replace("<sha>", sha)
        formattedSuffix = if (dirty)
            formattedSuffix.replace("<dirty>", dirtyMarker)
        else
            formattedSuffix.replace("<dirty>", "")
        return formattedSuffix
    }
}

class SemverGitPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val semverGitPluginExtension = project.extensions.create("semver", SemverGitPluginExtension::class.java)

        project.afterEvaluate {
            val version = parseGitDescribe(semverGitPluginExtension.nextVersion, semverGitPluginExtension.gitDescribeArgs, project.projectDir)
            project.version = version.format(semverGitPluginExtension.snapshotSuffix, semverGitPluginExtension.dirtyMarker)
        }

        project.task("showVersion") {
            it.group = "Help"
            it.description = "Show the project version"
        }
        project.tasks.getByName("showVersion").doLast {
            println("Version: " + project.version)
        }
    }

    fun parseVersion(describe: String): Version {
        val regex = """^([0-9]+)\.([0-9]+)\.([0-9]+)(-dirty)?(?:-([0-9]+))?(?:-g([0-9a-f]+))?$""".toRegex()
        return regex.matchEntire(describe)
                ?.destructured
                ?.let { (major, minor, patch, dirty, count, sha) ->
                    when (dirty.isEmpty() && count.isEmpty() && sha.isEmpty()) {
                        true -> Version(major.toInt(), minor.toInt(), patch.toInt(), null)
                        false -> Version(major.toInt(), minor.toInt(), patch.toInt(), Suffix(count.toInt(), sha, dirty.isNotEmpty()))
                    }
                }
                ?: throw IllegalArgumentException("Bad input '$describe'")
    }

    fun bumpVersion(version: Version, nextVersion: String): Version {
        when (nextVersion) {
            "major" -> {
                version.major += 1
                version.minor = 0
                version.patch = 0
                return version
            }
            "minor" -> {
                version.minor += 1
                version.patch = 0
                return version
            }
            "patch" -> {
                version.patch += 1
                return version
            }
            else -> {
                return version
            }
        }
    }

    fun parseGitDescribe(nextVersion: String, gitArgs: String, projectDir: File): Version {
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
            var describe = process.inputStream.bufferedReader().use { it.readText() }.trim()
            val version = parseVersion(describe)

            return bumpVersion(version, nextVersion)
        }
        return bumpVersion(Version(0, 0, 0, null), nextVersion)
    }
}