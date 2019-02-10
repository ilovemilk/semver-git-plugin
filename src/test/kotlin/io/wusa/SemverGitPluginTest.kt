package io.wusa

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SemverGitPluginTest {
    @Test
    fun testParseVersion() {
        assertEquals(SemverGitPlugin().parseVersion("1.1.1"), Version(1, 1, 1, ""))
        assertEquals(SemverGitPlugin().parseVersion("1.0.0-5-g5242341"), Version(1, 0, 0, "-5-g5242341"))
    }

    @Test
    fun testBumpVersionMajor() {
        val version = Version(1, 1, 1, "")
        val nextVersion = "major"
        assertEquals(SemverGitPlugin().bumpVersion(version, nextVersion, "SNAPSHOT"), Version(2, 0, 0, "SNAPSHOT"))
        assertEquals(SemverGitPlugin().bumpVersion(version, nextVersion, ""), Version(2, 0, 0, ""))
    }

    @Test
    fun testBumpVersionMinor() {
        val version = Version(1, 1, 1, "")
        val nextVersion = "minor"
        assertEquals(SemverGitPlugin().bumpVersion(version, nextVersion, "SNAPSHOT"), Version(1, 2, 0, "SNAPSHOT"))
        assertEquals(SemverGitPlugin().bumpVersion(version, nextVersion, ""), Version(1, 2, 0, ""))
    }

    @Test
    fun testBumpVersionPatch() {
        val version = Version(1, 1, 1, "")
        val nextVersion = "patch"
        assertEquals(SemverGitPlugin().bumpVersion(version, nextVersion, "SNAPSHOT"), Version(1, 1, 2, "SNAPSHOT"))
        assertEquals(SemverGitPlugin().bumpVersion(version, nextVersion, ""), Version(1, 1, 2, ""))
    }
}