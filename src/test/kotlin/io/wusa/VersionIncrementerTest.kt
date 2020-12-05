package io.wusa

import io.wusa.incrementer.*
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin

class VersionIncrementerTest {
    @Test
    fun `no increment`() {
        Assertions.assertEquals(NoVersionIncrementer.transform(Version(0, 0, 0, "", "", null)), Version(0, 0, 0, "", "", null))
    }

    @Test
    fun `patch increment`() {
        Assertions.assertEquals(PatchVersionIncrementer.transform(Version(0, 0, 0, "", "", null)), Version(0, 0, 1, "", "", null))
    }

    @Test
    fun `minor increment`() {
        Assertions.assertEquals(MinorVersionIncrementer.transform(Version(0, 0, 0, "", "", null)), Version(0, 1, 0, "", "", null))
    }

    @Test
    fun `major increment`() {
        Assertions.assertEquals(MajorVersionIncrementer.transform(Version(0, 0, 0, "", "", null)), Version(1, 0, 0, "", "", null))
    }
}
