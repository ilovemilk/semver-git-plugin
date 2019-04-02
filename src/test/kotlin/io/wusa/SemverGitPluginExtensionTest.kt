package io.wusa

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SemverGitPluginExtensionTest {

    @Test
    fun testExtensionDefaults() {
        val extension = SemverGitPluginExtension()
        assertEquals(extension.nextVersion, "minor")
        assertEquals(extension.snapshotSuffix, "SNAPSHOT")
        assertEquals(extension.dirtyMarker, "-dirty")
        assertEquals(extension.gitDescribeArgs, "--match [0-9]*.[0-9]*.[0-9]*")
    }
}