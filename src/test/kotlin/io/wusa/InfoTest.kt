package io.wusa

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.jupiter.api.*
import java.lang.IllegalArgumentException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InfoTest {

    @BeforeEach
    internal fun setUp() {
        mockkObject(GitService)
    }

    @AfterEach
    internal fun cleanUp() {
        unmockkObject(GitService)
    }

    @Test
    fun `get version of non-semver tag`() {
        val info = Info("none", "", "", "0.1.0", createTempDir())
        every { GitService.describe(initialVersion = any(), nextVersion = any(), projectDir = any()) } throws IllegalArgumentException("error")
        Assertions.assertEquals("The current or last tag is not a semantic version.", info.version)
    }

    @Test
    fun `get version`() {
        val info = Info("none", "", "", "0.1.0", createTempDir())
        every { GitService.describe(initialVersion = any(), nextVersion = any(), projectDir = any()) } returns Version(1, 0, 0, "", "", null)
        Assertions.assertEquals("1.0.0", info.version)
    }

    @Test
    fun `get dirty`() {
        val info = Info("none", "", "", "0.1.0", createTempDir())
        every { GitService.isDirty(projectDir = any()) } returns true
        Assertions.assertEquals(true, info.dirty)
    }

    @Test
    fun `get last tag`() {
        val info = Info("none", "", "", "0.1.0", createTempDir())
        every { GitService.lastTag(projectDir = any()) } returns "0.1.0"
        Assertions.assertEquals("0.1.0", info.lastTag)
    }

    @Test
    fun `get current tag`() {
        val info = Info("none", "", "", "0.1.0", createTempDir())
        every { GitService.currentTag(projectDir = any()) } returns "0.1.0"
        Assertions.assertEquals("0.1.0", info.tag)
    }

    @Test
    fun `get short commit`() {
        val info = Info("none", "", "", "0.1.0", createTempDir())
        every { GitService.currentCommit(projectDir = any(), isShort = true) } returns "1234567"
        Assertions.assertEquals("1234567", info.shortCommit)
    }

    @Test
    fun `get commit`() {
        val info = Info("none", "", "", "0.1.0", createTempDir())
        every { GitService.currentCommit(projectDir = any(), isShort = false) } returns "123456789"
        Assertions.assertEquals("123456789", info.commit)
    }

    @Test
    fun `get branch`() {
        val projectDir = createTempDir()
        val info = Info("none", "", "", "0.1.0", projectDir)
        Assertions.assertEquals(Branch(projectDir), info.branch)
    }
}