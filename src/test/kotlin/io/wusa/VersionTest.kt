package io.wusa

import io.mockk.mockkObject
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class VersionTest {

    private lateinit var project: Project

    @BeforeEach
    internal fun setUp() {
        project = ProjectBuilder.builder().build()
        mockkObject(GitService)
    }

    @Test
    fun `bump major version`() {
        val version = Version(1, 1, 1, "", "", null, project)
        val nextVersion = "major"
        Assertions.assertEquals(version.bump(nextVersion), Version(2, 0, 0, "", "", null, project))
        Assertions.assertEquals(version.bump(nextVersion), Version(3, 0, 0, "", "", null, project))
    }

    @Test
    fun `bump minor version`() {
        val version = Version(1, 1, 1, "", "", null, project)
        val nextVersion = "minor"
        Assertions.assertEquals(version.bump(nextVersion), Version(1, 2, 0, "", "", null, project))
        Assertions.assertEquals(version.bump(nextVersion), Version(1, 3, 0, "", "", null, project))
    }

    @Test
    fun `bump patch version`() {
        val version = Version(1, 1, 1, "", "", null, project)
        val nextVersion = "patch"
        Assertions.assertEquals(version.bump(nextVersion), Version(1, 1, 2, "", "", null, project))
        Assertions.assertEquals(version.bump(nextVersion), Version(1, 1, 3, "", "", null, project))
    }

    @Test
    fun testBumpVersionDefault() {
        val version = Version(1, 1, 1, "", "", null, project)
        val nextVersion = ""
        Assertions.assertEquals(version.bump(nextVersion), Version(1, 1, 1, "", "", null, project))
    }

    @Test
    fun `don't bump version`() {
        val version = Version(1, 1, 1, "", "", null, project)
        val nextVersion = "none"
        Assertions.assertEquals(version.bump(nextVersion), Version(1, 1, 1, "", "",null, project))
    }
}