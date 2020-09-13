package io.wusa

import io.wusa.extension.SemverGitPluginExtension
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class SemverGitPluginExtensionTest {

    private lateinit var project: Project

    @BeforeEach
    internal fun setUp() {
        project = ProjectBuilder.builder().build()
    }

    @Test
    fun `defaults`() {
        val extension = SemverGitPluginExtension(project)
        assertEquals(extension.dirtyMarker, "dirty")
        assertEquals(extension.info, Info(extension))
    }
}