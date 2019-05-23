package io.wusa

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class VersionTest {
    @Test
    fun `bump major version`() {
        val version = Version(1, 1, 1, "", "", null)
        val nextVersion = "major"
        Assertions.assertEquals(version.bump(nextVersion), Version(2, 0, 0, "", "", null))
        Assertions.assertEquals(version.bump(nextVersion), Version(3, 0, 0, "", "", null))
    }

    @Test
    fun `bump minor version`() {
        val version = Version(1, 1, 1, "", "", null)
        val nextVersion = "minor"
        Assertions.assertEquals(version.bump(nextVersion), Version(1, 2, 0, "", "", null))
        Assertions.assertEquals(version.bump(nextVersion), Version(1, 3, 0, "", "", null))
    }

    @Test
    fun `bump patch version`() {
        val version = Version(1, 1, 1, "", "", null)
        val nextVersion = "patch"
        Assertions.assertEquals(version.bump(nextVersion), Version(1, 1, 2, "", "", null))
        Assertions.assertEquals(version.bump(nextVersion), Version(1, 1, 3, "", "", null))
    }

    @Test
    fun testBumpVersionDefault() {
        val version = Version(1, 1, 1, "", "", null)
        val nextVersion = ""
        Assertions.assertEquals(version.bump(nextVersion), Version(1, 1, 1, "", "", null))
    }

    @Test
    fun `don't bump version`() {
        val version = Version(1, 1, 1, "", "", null)
        val nextVersion = "none"
        Assertions.assertEquals(version.bump(nextVersion), Version(1, 1, 1, "", "",null))
    }
}