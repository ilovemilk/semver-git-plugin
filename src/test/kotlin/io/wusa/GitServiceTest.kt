package io.wusa

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.wusa.exception.GitException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.*
import java.lang.IllegalArgumentException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GitServiceTest {

    private lateinit var project: Project

    @BeforeEach
    internal fun setUp() {
        project = ProjectBuilder.builder().build()
        mockkObject(GitCommandRunner)
    }

    @AfterEach
    internal fun tearDown() {
        unmockkObject(GitCommandRunner)
    }

    @Test
    fun `git is dirty`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "modified"
        Assertions.assertEquals(true, GitService.isDirty(project))
    }

    @Test
    fun `git is not dirty`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } throws GitException("error")
        Assertions.assertEquals(false, GitService.isDirty(project))
    }

    @Test
    fun `get last tag`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "modified"
        Assertions.assertEquals("modified", GitService.lastTag(project))
    }

    @Test
    fun `no last tag`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } throws GitException("error")
        Assertions.assertEquals("none", GitService.lastTag(project))
    }

    @Test
    fun `get current tag`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "modified"
        Assertions.assertEquals("modified", GitService.currentTag(project))
    }

    @Test
    fun `no current tag`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } throws GitException("error")
        Assertions.assertEquals("none", GitService.currentTag(project))
    }

    @Test
    fun `get commit sha`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "5f68d6b1ba57fd183e2c0e6cb968c4353907fa17"
        Assertions.assertEquals("5f68d6b1ba57fd183e2c0e6cb968c4353907fa17", GitService.currentCommit(project, false))
    }

    @Test
    fun `no current commit sha`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } throws GitException("error")
        Assertions.assertEquals("", GitService.currentCommit(project, false))
    }

    @Test
    fun `get short commit sha`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "916776"
        Assertions.assertEquals("916776", GitService.currentCommit(project, true))
    }

    @Test
    fun `no current short commit sha`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } throws GitException("error")
        Assertions.assertEquals("", GitService.currentCommit(project, true))
    }

    @Test
    fun `get current branch`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "modified"
        Assertions.assertEquals("modified", GitService.currentBranch(project))
    }

    @Test
    fun `no current branch`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } throws GitException("error")
        Assertions.assertEquals("", GitService.currentBranch(project))
    }

    @Test
    fun `get describe for tagged semver commit`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "1.0.0"
        Assertions.assertEquals(Version(1, 0, 0, "", "", null, project), GitService.describe(project))
    }

    @Test
    fun `get describe for tagged non-semver commit`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "test-tag"
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            GitService.describe(project)
        }
    }

    @Test
    fun `get describe for not tagged commit`() {
        every { GitCommandRunner.execute(projectDir = any(), args = arrayOf("describe", "--dirty", "--abbrev=7")) } returns "1.0.0-0-g1234567-dirty"
        every { GitCommandRunner.execute(projectDir = any(), args = arrayOf("describe", "--exact-match")) } throws GitException("error")
        Assertions.assertEquals(Version(1, 0, 0, "", "", Suffix(0, "1234567", true), project), GitService.describe(project))
    }

    @Test
    fun `get describe without tags`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } throws GitException("error")
        Assertions.assertEquals(Version(0, 1, 0, "", "", Suffix(0, "", false),project), GitService.describe(project))
    }
}