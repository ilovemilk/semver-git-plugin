package io.wusa

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.wusa.exception.GitException
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GitServiceTest {
    @BeforeEach
    internal fun setUp() {
        mockkObject(GitCommandRunner)
    }

    @AfterEach
    internal fun cleanUp() {
        unmockkObject(GitCommandRunner)
    }

    @Test
    fun `git is dirty`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "modified"
        Assertions.assertEquals(true, GitService.isDirty(createTempDir()))
    }

    @Test
    fun `git is not dirty`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } throws GitException("error")
        Assertions.assertEquals(false, GitService.isDirty(createTempDir()))
    }

    @Test
    fun `get last tag`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "modified"
        Assertions.assertEquals("modified", GitService.lastTag(createTempDir(), ""))
    }

    @Test
    fun `no last tag`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } throws GitException("error")
        Assertions.assertEquals("none", GitService.lastTag(createTempDir(), ""))
    }

    @Test
    fun `get current tag`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "modified"
        Assertions.assertEquals("modified", GitService.currentTag(createTempDir(), ""))
    }

    @Test
    fun `no current tag`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } throws GitException("error")
        Assertions.assertEquals("none", GitService.currentTag(createTempDir(), ""))
    }

    @Test
    fun `get commit sha`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "5f68d6b1ba57fd183e2c0e6cb968c4353907fa17"
        Assertions.assertEquals("5f68d6b1ba57fd183e2c0e6cb968c4353907fa17", GitService.currentCommit(createTempDir(), false))
    }

    @Test
    fun `no current commit sha`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } throws GitException("error")
        Assertions.assertEquals("", GitService.currentCommit(createTempDir(), false))
    }

    @Test
    fun `get short commit sha`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "916776"
        Assertions.assertEquals("916776", GitService.currentCommit(createTempDir(), true))
    }

    @Test
    fun `no current short commit sha`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } throws GitException("error")
        Assertions.assertEquals("", GitService.currentCommit(createTempDir(), true))
    }

    @Test
    fun `get current branch`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "modified"
        Assertions.assertEquals("modified", GitService.currentBranch(createTempDir()))
    }

    @Test
    fun `no current branch`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } throws GitException("error")
        Assertions.assertEquals("", GitService.currentBranch(createTempDir()))
    }

    @Test
    fun `get describe for tagged commit`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "1.0.0"
        Assertions.assertEquals(Version(1, 0, 0, "", "", null), GitService.describe("none", "", createTempDir()))
    }

    @Test
    fun `get describe for not tagged commit`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "1.0.0-0-g1234567-dirty"
        Assertions.assertEquals(Version(1, 0, 0, "", "", Suffix(0, "1234567", true)), GitService.describe("none", "", createTempDir()))
    }

    @Test
    fun `get describe without tags`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } throws GitException("error")
        Assertions.assertEquals(Version(0, 0, 0, "", "", null), GitService.describe("none", "", createTempDir()))
    }
}