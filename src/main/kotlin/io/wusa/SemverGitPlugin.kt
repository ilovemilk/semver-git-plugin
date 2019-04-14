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
            println("[semver] branch name: " + semverGitPluginExtension.info.branch.name)
            println("[semver] branch group: " + semverGitPluginExtension.info.branch.group)
            println("[semver] branch id: " + semverGitPluginExtension.info.branch.id)
            println("[semver] commit: " + semverGitPluginExtension.info.commit)
            println("[semver] tag: " + semverGitPluginExtension.info.tag)
            println("[semver] last tag: " + semverGitPluginExtension.info.lastTag)
            println("[semver] dirty: " + semverGitPluginExtension.info.dirty)
        }
    }
}