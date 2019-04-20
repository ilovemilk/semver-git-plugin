package io.wusa

import org.gradle.api.Plugin
import org.gradle.api.Project

class SemverGitPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val semverGitPluginExtension = project.extensions.create("semver", SemverGitPluginExtension::class.java, project.projectDir)

        project.task("showVersion") {
            it.group = "Help"
            it.description = "Show the project version"
        }
        project.tasks.getByName("showVersion").doLast {
            println("Version: " + semverGitPluginExtension.info.version)
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
        }
    }
}