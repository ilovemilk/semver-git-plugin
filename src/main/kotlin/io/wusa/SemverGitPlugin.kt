package io.wusa

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

data class Version(var major: Int, var minor: Int, var patch: Int, var suffix: String)

class SemverGitPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val nextVersion = "minor"
        val snapshotSuffix = "SNAPSHOT"
        val dirtyMarker = "-dirty"
        val gitDescribeArgs = "--match *[0-9].[0-9]*.[0-9]*"

        project.version = getGitVersion(nextVersion, snapshotSuffix, dirtyMarker, gitDescribeArgs, project.projectDir)
        project.task("showVersion") {
            it.group = "Help"
            it.description = "Show the project version"
        }
        project.tasks.getByName("showVersion").doLast {
            println("Version: " + project.version)
        }
    }

    private fun checkVersion(version: String): Version {
        return parseVersion(version)
    }

    private fun parseVersion(version: String): Version {
        val regex = "/^([0-9]+).([0-9]+).([0-9]+)(-([a-zA-Z0-9.-]+))?$/".toRegex()
        val matchResults = regex.find(version)
        val (major, minor, patch, suffix) = matchResults!!.destructured
        return Version(major.toInt(), minor.toInt(), patch.toInt(), suffix)
    }

    private fun getNextVersion(version: String, nextVersion: String, snapshotSuffix: String): Version {
        when(nextVersion) {
            "major" -> {
                val v = parseVersion(version)
                if (v.suffix == null) {
                    v.major += 1
                    v.minor = 0
                    v.patch = 0
                }
                v.suffix = snapshotSuffix
                return v
            }
            "minor" -> {
                val v = parseVersion(version)
                if (v.suffix == null) {
                    v.minor += 1
                    v.patch = 0
                }
                v.suffix = snapshotSuffix
                return v
            }
            "patch" -> {
                val v = parseVersion(version)
                if (v.suffix == null) {
                    v.patch += 1
                }
                v.suffix = snapshotSuffix
                return v
            }
            else -> {
                return checkVersion(nextVersion)
            }
        }
    }

    private fun getGitVersion(nextVersion: String, snapshotSuffix: String, dirtyMarker: String, gitArgs: String, projectDir: File): Version {
        var process = ProcessBuilder("git describe --exact-match " + gitArgs)
                .directory(projectDir)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
        process.waitFor()
        if (process.exitValue() == 0) {
            val output = process.inputStream.bufferedReader().use { it.readText() }
            return checkVersion(output)
        }
        process = ProcessBuilder("git describe --dirty --abbrev=7 " + gitArgs)
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
            val versionRegex = "/-[0-9]+-g[0-9a-f]+\$/".toRegex()
            val (version) = versionRegex.find(describe)!!.destructured
            val suffixRegex = "/-([0-9]+)-g([0-9a-f]+)\$/".toRegex()
            val (count, sha) = suffixRegex.find(describe)!!.destructured
            var suffix = snapshotSuffix
            suffix = suffix.replace("<count>", count)
            suffix = suffix.replace("<sha>", sha)
            if (dirty)
                suffix = suffix.replace("<dirty>", dirtyMarker);
            else
                suffix = suffix.replace("<dirty>", dirtyMarker);
            return getNextVersion(version, nextVersion, suffix)
        }
        return getNextVersion("0.0.0", nextVersion, "SNAPSHOT")
    }
}