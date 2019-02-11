package io.wusa

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException

class SemverGitPluginTest {
    @Test
    fun testParseVersion() {
        assertEquals(SemverGitPlugin().parseVersion("1.1.1"), Version(1, 1, 1, null))
        assertEquals(SemverGitPlugin().parseVersion("1.0.0-dirty-5-g5242341"), Version(1, 0, 0, Suffix(5, "5242341", true)))
        assertEquals(SemverGitPlugin().parseVersion("1.0.0-5-g5242341"), Version(1, 0, 0, Suffix(5, "5242341", false)))
        assertThrows(IllegalArgumentException::class.java) {
            SemverGitPlugin().parseVersion("")
        }
        assertThrows(IllegalArgumentException::class.java) {
            SemverGitPlugin().parseVersion("111")
        }
        assertThrows(IllegalArgumentException::class.java) {
            SemverGitPlugin().parseVersion("v1.0.0")
        }
    }

    @Test
    fun testBumpVersionMajor() {
        val version = Version(1, 1, 1, null)
        val nextVersion = "major"
        assertEquals(SemverGitPlugin().bumpVersion(version, nextVersion), Version(2, 0, 0, null))
        assertEquals(SemverGitPlugin().bumpVersion(version, nextVersion), Version(3, 0, 0, null))
    }

    @Test
    fun testBumpVersionMinor() {
        val version = Version(1, 1, 1, null)
        val nextVersion = "minor"
        assertEquals(SemverGitPlugin().bumpVersion(version, nextVersion), Version(1, 2, 0, null))
        assertEquals(SemverGitPlugin().bumpVersion(version, nextVersion), Version(1, 3, 0, null))
    }

    @Test
    fun testBumpVersionPatch() {
        val version = Version(1, 1, 1, null)
        val nextVersion = "patch"
        assertEquals(SemverGitPlugin().bumpVersion(version, nextVersion), Version(1, 1, 2, null))
        assertEquals(SemverGitPlugin().bumpVersion(version, nextVersion), Version(1, 1, 3, null))
    }

    @Test
    fun testBumpVersionDefault() {
        val version = Version(1, 1, 1, null)
        val nextVersion = ""
        assertEquals(SemverGitPlugin().bumpVersion(version, nextVersion), Version(1, 1, 1, null))
    }

    @Test
    fun testVersionFormat() {
        assertEquals(Version(1, 1, 1, null).format("", ""), "1.1.1")
        assertEquals(Version(1, 1, 1, Suffix(0, "123", true)).format("<count>-<sha>", ""), "1.1.1-0-123")
        assertEquals(Version(1, 1, 1, Suffix(0, "123", false)).format("<count>-<sha>", ""), "1.1.1-0-123")
        assertEquals(Version(1, 1, 1, Suffix(0, "123", true)).format("<count>-<sha><dirty>", "-dirty"), "1.1.1-0-123-dirty")
        assertEquals(Version(1, 1, 1, Suffix(0, "123", false)).format("<count>-<sha><dirty>", "-dirty"), "1.1.1-0-123")
        assertEquals(Version(1, 1, 1, Suffix(0, "123", true)).format("<count>.g<sha><dirty>-SNAPSHOT", "-dirty"), "1.1.1-0.g123-dirty-SNAPSHOT")
        assertEquals(Version(1, 1, 1, Suffix(0, "123", false)).format("<count>.g<sha><dirty>-SNAPSHOT", "-dirty"), "1.1.1-0.g123-SNAPSHOT")
    }
}