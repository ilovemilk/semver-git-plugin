package io.wusa

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SemverGitTest {
    @Test
    fun testMyLanguage() {
        assertEquals("Kotlin", SemverGit().kotlinLanguage().name)
        assertEquals(10, SemverGit().kotlinLanguage().hotness)
    }
}