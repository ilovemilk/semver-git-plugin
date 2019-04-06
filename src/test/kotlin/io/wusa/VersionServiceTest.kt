package io.wusa

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException

class VersionServiceTest {
    @Test
    fun testParseVersion() {
        Assertions.assertEquals(VersionService.parseVersion("1.1.1"), Version(1, 1, 1, null))
        Assertions.assertEquals(VersionService.parseVersion("1.0.0-5-g5242341-dirty"), Version(1, 0, 0, Suffix(5, "5242341", true)))
        Assertions.assertEquals(VersionService.parseVersion("1.0.0-5-g5242341"), Version(1, 0, 0, Suffix(5, "5242341", false)))
        Assertions.assertEquals(VersionService.parseVersion("5000.1.1000000-5-g5242341-dirty"), Version(5000, 1, 1000000, Suffix(5, "5242341", true)))
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            VersionService.parseVersion("1.1.1 ")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            VersionService.parseVersion("")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            VersionService.parseVersion("111")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            VersionService.parseVersion("v1.0.0")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            VersionService.parseVersion("a.b.c")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            VersionService.parseVersion("1.0.0--dirty-5-g5242341")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            VersionService.parseVersion("1.0.0-dirty--5-g5242341")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            VersionService.parseVersion("1.0.0-dirty-5--g5242341")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            VersionService.parseVersion("1.0.0--g5242341")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            VersionService.parseVersion("1.0.0-")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            VersionService.parseVersion("1.0.0-g")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            VersionService.parseVersion("1.0.0--g")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            VersionService.parseVersion("1.0.0-g123-5")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            VersionService.parseVersion("1.0.0-5-dirty")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            VersionService.parseVersion("1.0.0-5")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            VersionService.parseVersion("1.0.0-5-g")
        }
    }

    @Test
    fun testBumpVersionMajor() {
        val version = Version(1, 1, 1, null)
        val nextVersion = "major"
        Assertions.assertEquals(VersionService.bumpVersion(version, nextVersion), Version(2, 0, 0, null))
        Assertions.assertEquals(VersionService.bumpVersion(version, nextVersion), Version(3, 0, 0, null))
    }

    @Test
    fun testBumpVersionMinor() {
        val version = Version(1, 1, 1, null)
        val nextVersion = "minor"
        Assertions.assertEquals(VersionService.bumpVersion(version, nextVersion), Version(1, 2, 0, null))
        Assertions.assertEquals(VersionService.bumpVersion(version, nextVersion), Version(1, 3, 0, null))
    }

    @Test
    fun testBumpVersionPatch() {
        val version = Version(1, 1, 1, null)
        val nextVersion = "patch"
        Assertions.assertEquals(VersionService.bumpVersion(version, nextVersion), Version(1, 1, 2, null))
        Assertions.assertEquals(VersionService.bumpVersion(version, nextVersion), Version(1, 1, 3, null))
    }

    @Test
    fun testBumpVersionDefault() {
        val version = Version(1, 1, 1, null)
        val nextVersion = ""
        Assertions.assertEquals(VersionService.bumpVersion(version, nextVersion), Version(1, 1, 1, null))
    }

    @Test
    fun testVersionFormat() {
        Assertions.assertEquals(Version(1, 1, 1, null).format("", ""), "1.1.1")
        Assertions.assertEquals(Version(1, 1, 1, Suffix(0, "123", true)).format("<count>-<sha>", ""), "1.1.1-0-123")
        Assertions.assertEquals(Version(1, 1, 1, Suffix(0, "123", false)).format("<count>-<sha>", ""), "1.1.1-0-123")
        Assertions.assertEquals(Version(1, 1, 1, Suffix(0, "123", true)).format("<count>-<sha><dirty>", "-dirty"), "1.1.1-0-123-dirty")
        Assertions.assertEquals(Version(1, 1, 1, Suffix(0, "123", false)).format("<count>-<sha><dirty>", "-dirty"), "1.1.1-0-123")
        Assertions.assertEquals(Version(1, 1, 1, Suffix(0, "123", true)).format("<count>.g<sha><dirty>-SNAPSHOT", "-dirty"), "1.1.1-0.g123-dirty-SNAPSHOT")
        Assertions.assertEquals(Version(1, 1, 1, Suffix(0, "123", false)).format("<count>.g<sha><dirty>-SNAPSHOT", "-dirty"), "1.1.1-0.g123-SNAPSHOT")
    }
}