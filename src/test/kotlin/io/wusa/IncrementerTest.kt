package io.wusa

import io.wusa.incrementer.MajorVersionIncrementer
import io.wusa.incrementer.MinorVersionIncrementer
import io.wusa.incrementer.NoVersionIncrementer
import io.wusa.incrementer.PatchVersionIncrementer
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class IncrementerTest {

    @Test
    fun `bump major version`() {
        val version = MajorVersionIncrementer.transform(Version(1, 1, 1, "", "", null))
        Assertions.assertEquals(version, Version(2, 0, 0, "", "", null))
    }

    @Test
    fun `bump minor version`() {
        val version = MinorVersionIncrementer.transform(Version(1, 1, 1, "", "", null))
        Assertions.assertEquals(version, Version(1, 2, 0, "", "", null))
    }

    @Test
    fun `bump patch version`() {
        val version = PatchVersionIncrementer.transform(Version(1, 1, 1, "", "", null))
        Assertions.assertEquals(version, Version(1, 1, 2, "", "", null))
    }

    @Test
    fun `don't bump version`() {
        val version = NoVersionIncrementer.transform(Version(1, 1, 1, "", "", null))
        Assertions.assertEquals(version, Version(1, 1, 1, "", "", null))
    }
}
