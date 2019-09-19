package io.wusa

import io.mockk.mockkObject
import io.wusa.incrementer.MajorVersionIncrementer
import io.wusa.incrementer.MinorVersionIncrementer
import io.wusa.incrementer.NoVersionIncrementer
import io.wusa.incrementer.PatchVersionIncrementer
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class IncrementerTest {

    private lateinit var project: Project

    @BeforeEach
    internal fun setUp() {
        project = ProjectBuilder.builder().build()
        project.plugins.apply(SemverGitPlugin::class.java)
        mockkObject(GitService)
    }

    @Test
    fun `bump major version`() {
        val version = MajorVersionIncrementer().increment(Version(1, 1, 1, "", "", null))
        Assertions.assertEquals(version, Version(2, 0, 0, "", "", null))
    }

    @Test
    fun `bump minor version`() {
        val version = MinorVersionIncrementer().increment(Version(1, 1, 1, "", "", null))
        Assertions.assertEquals(version, Version(1, 2, 0, "", "", null))
    }

    @Test
    fun `bump patch version`() {
        val version = PatchVersionIncrementer().increment(Version(1, 1, 1, "", "", null))
        Assertions.assertEquals(version, Version(1, 1, 2, "", "", null))
    }

    @Test
    fun `don't bump version`() {
        val version = NoVersionIncrementer().increment(Version(1, 1, 1, "", "", null))
        Assertions.assertEquals(version, Version(1, 1, 1, "", "", null))
    }
}