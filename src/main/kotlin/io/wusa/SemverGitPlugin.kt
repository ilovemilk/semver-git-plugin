package io.wusa

import io.wusa.extension.SemverGitPluginExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class SemverGitPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        val semverGitPluginExtension = project.extensions.create("semver", SemverGitPluginExtension::class.java, project)

        project.task("showVersion") {
            it.group = "Help"
            it.description = "Show the project version"
        }
        project.tasks.getByName("showVersion").doLast {
            println("Version: " + semverGitPluginExtension.info)
        }

        project.task("showInfo") {
            it.group = "Help"
            it.description = "Show the git info"
        }
        project.tasks.getByName("showInfo").doLast {
            println("Branch name: " + semverGitPluginExtension.info.branch.name)
            println("Branch group: " + semverGitPluginExtension.info.branch.group)
            println("Branch id: " + semverGitPluginExtension.info.branch.id)
            println("Commit: " + semverGitPluginExtension.info.commit)
            println("Short commit: " + semverGitPluginExtension.info.shortCommit)
            println("Tag: " + semverGitPluginExtension.info.tag)
            println("Last tag: " + semverGitPluginExtension.info.lastTag)
            println("Dirty: " + semverGitPluginExtension.info.dirty)
            println("Version: " + semverGitPluginExtension.info)
            println("Version major: " + semverGitPluginExtension.info.version.major)
            println("Version minor: " + semverGitPluginExtension.info.version.minor)
            println("Version patch: " + semverGitPluginExtension.info.version.patch)
            println("Version pre release: " + if (semverGitPluginExtension.info.version.prerelease.isEmpty()) "none" else semverGitPluginExtension.info.version.prerelease)
            println("Version build: " + if (semverGitPluginExtension.info.version.build.isEmpty()) "none" else semverGitPluginExtension.info.version.build)
        }
    }
}