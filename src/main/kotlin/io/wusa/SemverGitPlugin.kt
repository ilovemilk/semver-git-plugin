package io.wusa

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class SemverGitPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val semverGitPluginExtension = project.extensions.create("semver", SemverGitPluginExtension::class.java, project.projectDir)

        project.task("showVersion") {
            it.group = "Help"
            it.description = "Show the project version"
        }
        project.tasks.getByName("showVersion").doLast {
            println("Version: " + semverGitPluginExtension.version)
        }
    }
}