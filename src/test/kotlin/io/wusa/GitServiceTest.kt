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
    fun `get current branch master`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "* master 5824168c73ba0618c1b6e384fbd7d61c5e8b8bc3"
        Assertions.assertEquals("master", GitService.currentBranch(createTempDir()))
    }

    @Test
    fun `get current branch feature-test`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "* feature/test 5824168c73ba0618c1b6e384fbd7d61c5e8b8bc3"
        Assertions.assertEquals("feature/test", GitService.currentBranch(createTempDir()))
    }

    @Test
    fun `get current branch feature-test with origin`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns
                "* feature/test                cd55642b18ef34d976eda337d2b7abd296b37c8f remove code quality\n" +
                "  remotes/origin/feature/test cd55642b18ef34d976eda337d2b7abd296b37c8f remove code quality"
        Assertions.assertEquals("feature/test", GitService.currentBranch(createTempDir()))
    }

    @Test
    fun `get current branch with null pointer exception`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } throws KotlinNullPointerException()
        Assertions.assertEquals("", GitService.currentBranch(createTempDir()))
    }

    @Test
    fun `no current branch`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } throws GitException("error")
        Assertions.assertEquals("", GitService.currentBranch(project))
    }
}