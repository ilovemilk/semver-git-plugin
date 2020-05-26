package io.wusa

import io.wusa.exception.NoIncrementerFoundException
import io.wusa.incrementer.VersionIncrementer
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class VersionIncrementerTest {

    private lateinit var project: Project

    @BeforeEach
    internal fun setUp() {
        project = ProjectBuilder.builder().build()
        project.plugins.apply(SemverGitPlugin::class.java)
    }

    @Test
    fun `no increment`() {
        Assertions.assertEquals(VersionIncrementer.getVersionIncrementerByName("NO_VERSION_INCREMENTER").increment(Version(0, 0, 0, "", "", null), project), Version(0, 0, 0, "", "", null))
    }

    @Test
    fun `patch increment`() {
        Assertions.assertEquals(VersionIncrementer.getVersionIncrementerByName("PATCH_INCREMENTER").increment(Version(0, 0, 0, "", "", null), project), Version(0, 0, 1, "", "", null))
    }

    @Test
    fun `minor increment`() {
        Assertions.assertEquals(VersionIncrementer.getVersionIncrementerByName("MINOR_INCREMENTER").increment(Version(0, 0, 0, "", "", null), project), Version(0, 1, 0, "", "", null))
    }

    @Test
    fun `major increment`() {
        Assertions.assertEquals(VersionIncrementer.getVersionIncrementerByName("MAJOR_INCREMENTER").increment(Version(0, 0, 0, "", "", null), project), Version(1, 0, 0, "", "", null))
    }

    @Test
    fun `wrong incrementer`() {
        Assertions.assertThrows(NoIncrementerFoundException::class.java) {
            VersionIncrementer.getVersionIncrementerByName("WRONG_INCREMENTER").increment(Version(0, 0, 0, "", "", null), project)
        }
    }
}
