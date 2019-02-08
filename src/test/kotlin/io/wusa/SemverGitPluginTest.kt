package io.wusa

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SemverGitPluginTest {
    @Test
    fun testMyLanguage() {
        assertEquals("Kotlin", SemverGitPlugin().kotlinLanguage().name)
        assertEquals(10, SemverGitPlugin().kotlinLanguage().hotness)
    }
}