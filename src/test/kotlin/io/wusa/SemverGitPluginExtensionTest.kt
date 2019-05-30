package io.wusa

import io.mockk.mockkObject
import io.wusa.extension.SemverGitPluginExtension
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SemverGitPluginExtensionTest {

    private lateinit var project: Project

    @BeforeEach
    internal fun setUp() {
        project = ProjectBuilder.builder().build()
        project.plugins.apply(SemverGitPlugin::class.java)
        mockkObject(GitService)
    }

    @Test
    fun `defaults`() {
        val extension = SemverGitPluginExtension(project)
        assertEquals(extension.dirtyMarker, "dirty")
        assertEquals(extension.info, Info("0.1.0", project))
    }
}