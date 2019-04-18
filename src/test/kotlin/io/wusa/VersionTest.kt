package io.wusa

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class VersionTest {
    @Test
    fun `version formatting`() {
        Assertions.assertEquals(Version(1, 1, 1, "", "", null).format("", ""), "1.1.1")
        Assertions.assertEquals(Version(1, 1, 1, "beta", "exp", null).format("", ""), "1.1.1-beta+exp")
        Assertions.assertEquals(Version(1, 1, 1, "beta", "exp", Suffix(0, "123", true)).format("<count>-<sha>", ""), "1.1.1-beta+exp-0-123")
        Assertions.assertEquals(Version(1, 1, 1, "beta", "exp", Suffix(0, "123", false)).format("<count>-<sha>", ""), "1.1.1-beta+exp-0-123")
        Assertions.assertEquals(Version(1, 1, 1, "beta", "exp", Suffix(0, "123", true)).format("<count>-<sha><dirty>", "-dirty"), "1.1.1-beta+exp-0-123-dirty")
        Assertions.assertEquals(Version(1, 1, 1, "beta", "exp", Suffix(0, "123", false)).format("<count>-<sha><dirty>", "-dirty"), "1.1.1-beta+exp-0-123")
        Assertions.assertEquals(Version(1, 1, 1, "beta", "exp", Suffix(0, "123", true)).format("<count>.g<sha><dirty>-SNAPSHOT", "-dirty"), "1.1.1-beta+exp-0.g123-dirty-SNAPSHOT")
        Assertions.assertEquals(Version(1, 1, 1, "beta", "exp", Suffix(0, "123", false)).format("<count>.g<sha><dirty>-SNAPSHOT", "-dirty"), "1.1.1-beta+exp-0.g123-SNAPSHOT")
    }
}