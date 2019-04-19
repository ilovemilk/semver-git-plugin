package io.wusa

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class SemverGitPluginExtensionTest {

    @Test
    fun `defaults`() {
        val testProjectDirectory = createTempDir()

        val extension = SemverGitPluginExtension(testProjectDirectory)
        assertEquals(extension.nextVersion, "minor")
        assertEquals(extension.snapshotSuffix, "SNAPSHOT")
        assertEquals(extension.dirtyMarker, "-dirty")
    }
}