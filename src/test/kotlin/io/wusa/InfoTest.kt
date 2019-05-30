package io.wusa

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.*
import java.lang.IllegalArgumentException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InfoTest {

    private lateinit var project: Project

    @BeforeEach
    internal fun setUp() {
        project = ProjectBuilder.builder().build()
        project.plugins.apply(SemverGitPlugin::class.java)
        mockkObject(GitService)
    }

    @AfterEach
    internal fun tearDown() {
        unmockkObject(GitService)
    }

    @Test
    fun `get dirty`() {
        val info = Info("0.1.0", project)
        every { GitService.isDirty(project = any()) } returns true
        Assertions.assertEquals(true, info.dirty)
    }

    @Test
    fun `get last tag`() {
        val info = Info("0.1.0", project)
        every { GitService.lastTag(project = any()) } returns "0.1.0"
        Assertions.assertEquals("0.1.0", info.lastTag)
    }

    @Test
    fun `get current tag`() {
        val info = Info("0.1.0", project)
        every { GitService.currentTag(project = any()) } returns "0.1.0"
        Assertions.assertEquals("0.1.0", info.tag)
    }

    @Test
    fun `get short commit`() {
        val info = Info("0.1.0", project)
        every { GitService.currentCommit(project = any(), isShort = true) } returns "1234567"
        Assertions.assertEquals("1234567", info.shortCommit)
    }

    @Test
    fun `get commit`() {
        val info = Info("0.1.0", project)
        every { GitService.currentCommit(project = any(), isShort = false) } returns "123456789"
        Assertions.assertEquals("123456789", info.commit)
    }

    @Test
    fun `get branch`() {
        val project = project
        val info = Info("0.1.0", project)
        Assertions.assertEquals(Branch(project), info.branch)
    }
}