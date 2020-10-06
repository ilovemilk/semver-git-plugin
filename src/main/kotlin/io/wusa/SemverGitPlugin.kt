package io.wusa

import io.wusa.extension.SemverGitPluginExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.WriteProperties
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module

class SemverGitPlugin : Plugin<Project> {

    companion object {
        init {
            startKoin {}
        }
    }

    override fun apply(project: Project) {
        val semverGitPluginExtension = project.extensions.create("semver", SemverGitPluginExtension::class.java, project)

        val modules = module {
            single(override = true) { project }
            single(override = true) { semverGitPluginExtension }
            single(override = true) { GitCommandRunner(get()) }
            single(override = true) { GitService(get()) }
            single(override = true) { VersionService(get(), get()) }
        }

        loadKoinModules(modules)

        project.tasks.register("createVersionProperties", WriteProperties::class.java) {
            it.group = "Help"
            it.description = "Create a properties file with all version information."
            it.property("branch.name", semverGitPluginExtension.info.branch.name)
            it.property("branch.group", semverGitPluginExtension.info.branch.group)
            it.property("branch.id", semverGitPluginExtension.info.branch.id)
            it.property("commit", semverGitPluginExtension.info.commit)
            it.property("short.commit", semverGitPluginExtension.info.shortCommit)
            it.property("tag", semverGitPluginExtension.info.tag)
            it.property("last.tag", semverGitPluginExtension.info.lastTag)
            it.property("dirty", semverGitPluginExtension.info.dirty.toString())
            it.property("version", semverGitPluginExtension.info.toString())
            it.property("version.major", semverGitPluginExtension.info.version.major.toString())
            it.property("version.minor", semverGitPluginExtension.info.version.minor.toString())
            it.property("version.patch", semverGitPluginExtension.info.version.patch.toString())
            it.property("version.prerelease", if (semverGitPluginExtension.info.version.prerelease.isEmpty()) "none" else semverGitPluginExtension.info.version.prerelease)
            it.property("version.build", if (semverGitPluginExtension.info.version.build.isEmpty()) "none" else semverGitPluginExtension.info.version.build)
            it.outputFile = project.buildDir.resolve("generated/version.properties")
            it.doLast {
                println("Version properties file successfully created.")
            }
        }

        project.tasks.register("showVersion") {
            it.group = "Help"
            it.description = "Show the project version"
            it.doLast { println("Version: " + semverGitPluginExtension.info) }
        }

        project.tasks.register("showInfo") {
            it.group = "Help"
            it.description = "Show the git info"
            it.doLast {
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
}
