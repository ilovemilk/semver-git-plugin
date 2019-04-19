package io.wusa

import io.mockk.every
import io.mockk.mockkObject
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
}