package io.wusa

import io.mockk.every
import io.mockk.mockkObject
import io.wusa.exception.GitException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GitServiceTest {
    @Test
    fun `git is dirty`() {
        mockkObject(GitCommandRunner)
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "modified"
        Assertions.assertEquals(true, GitService.isDirty(createTempDir()))
    }

    @Test
    fun `git is not dirty`() {
        mockkObject(GitCommandRunner)
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns ""
        Assertions.assertEquals(false, GitService.isDirty(createTempDir()))
    }

    @Test
    fun `get last tag`() {
        mockkObject(GitCommandRunner)
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "modified"
        Assertions.assertEquals("modified", GitService.lastTag(createTempDir(), ""))
    }

    @Test
    fun `no last tag`() {
        mockkObject(GitCommandRunner)
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns ""
        Assertions.assertEquals("", GitService.lastTag(createTempDir(), ""))
    }

    @Test
    fun `get current tag`() {
        mockkObject(GitCommandRunner)
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "modified"
        Assertions.assertEquals("modified", GitService.currentTag(createTempDir(), ""))
    }

    @Test
    fun `no current tag`() {
        mockkObject(GitCommandRunner)
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns ""
        Assertions.assertEquals("", GitService.currentTag(createTempDir(), ""))
    }

    @Test
    fun `get commit sha`() {
        mockkObject(GitCommandRunner)
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "5f68d6b1ba57fd183e2c0e6cb968c4353907fa17"
        Assertions.assertEquals("5f68d6b1ba57fd183e2c0e6cb968c4353907fa17", GitService.currentCommit(createTempDir(), false))
    }

    @Test
    fun `no current commit sha`() {
        mockkObject(GitCommandRunner)
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns ""
        Assertions.assertEquals("", GitService.currentCommit(createTempDir(), false))
    }

    @Test
    fun `get short commit sha`() {
        mockkObject(GitCommandRunner)
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "916776"
        Assertions.assertEquals("916776", GitService.currentCommit(createTempDir(), true))
    }

    @Test
    fun `no current short commit sha`() {
        mockkObject(GitCommandRunner)
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns ""
        Assertions.assertEquals("", GitService.currentCommit(createTempDir(), true))
    }

    @Test
    fun `get current branch`() {
        mockkObject(GitCommandRunner)
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "modified"
        Assertions.assertEquals("modified", GitService.currentBranch(createTempDir()))
    }

    @Test
    fun `no current branch`() {
        mockkObject(GitCommandRunner)
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns ""
        Assertions.assertEquals("", GitService.currentBranch(createTempDir()))
    }

    @Test
    fun `get describe for tagged commit`() {
        mockkObject(GitCommandRunner)
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "1.0.0"
        Assertions.assertEquals(Version(1, 0, 0, "", "", null), GitService.describe("none", "", createTempDir()))
    }

    @Test
    fun `get describe for not tagged commit`() {
        mockkObject(GitCommandRunner)
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns "1.0.0-0-g1234567-dirty"
        Assertions.assertEquals(Version(1, 0, 0, "", "", Suffix(0, "1234567", true)), GitService.describe("none", "", createTempDir()))
    }

    @Test
    fun `get describe without tags`() {
        mockkObject(GitCommandRunner)
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } throws GitException("error")
        Assertions.assertEquals(Version(0, 0, 0, "", "", null), GitService.describe("none", "", createTempDir()))
    }
}