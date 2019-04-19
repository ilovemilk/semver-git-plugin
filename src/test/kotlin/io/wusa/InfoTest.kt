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
        val info = Info("none", "", "", createTempDir())
        every { GitService.describe(nextVersion = any(), projectDir = any()) } throws IllegalArgumentException("error")
        Assertions.assertEquals(info.version, "The current or last tag is not a semantic version.")
    }
}