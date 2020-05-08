package io.wusa

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.wusa.exception.NoValidSemverTagFoundException
import io.wusa.extension.SemverGitPluginExtension
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InfoTest {

    private lateinit var project: Project
    private lateinit var semverGitPluginExtension: SemverGitPluginExtension

    @BeforeEach
    internal fun setUp() {
        project = ProjectBuilder.builder().build()
        project.plugins.apply(SemverGitPlugin::class.java)
        semverGitPluginExtension = project.extensions.getByType(SemverGitPluginExtension::class.java)
        mockkObject(GitService)
    }

    @AfterEach
    internal fun tearDown() {
        unmockkObject(GitService)
        semverGitPluginExtension.tagPrefix = ""
    }

    @Test
    fun `get dirty`() {
        val info = Info(project)
        every { GitService.isDirty(project = any()) } returns true
        Assertions.assertEquals(true, info.dirty)
    }

    @Test
    fun `get last tag`() {
        val info = Info(project)
        every { GitService.lastTag(project = any(), tagPrefix = any()) } returns "0.1.0"
        Assertions.assertEquals("0.1.0", info.lastTag)
    }

    @Test
    fun `get current tag`() {
        val info = Info(project)
        every { GitService.currentTag(project = any()) } returns "0.1.0"
        Assertions.assertEquals("0.1.0", info.tag)
    }

    @Test
    fun `get last tag with prefix`() {
        semverGitPluginExtension.tagPrefix = "prj_"
        val info = Info(project)
        every { GitService.lastTag(project = any(), tagPrefix = any()) } returns "prj_0.1.0"
        Assertions.assertEquals("prj_0.1.0", info.lastTag)
    }

    @Test
    fun `get current tag with prefix`() {
        semverGitPluginExtension.tagPrefix = "prj_"
        val info = Info(project)
        every { GitService.currentTag(project = any()) } returns "prj_0.1.0"
        Assertions.assertEquals("prj_0.1.0", info.tag)
    }

    @Test
    fun `get short commit`() {
        val info = Info(project)
        every { GitService.currentCommit(project = any(), isShort = true) } returns "1234567"
        Assertions.assertEquals("1234567", info.shortCommit)
    }

    @Test
    fun `get commit`() {
        val info = Info(project)
        every { GitService.currentCommit(project = any(), isShort = false) } returns "123456789"
        Assertions.assertEquals("123456789", info.commit)
    }

    @Test
    fun `get branch`() {
        val project = project
        val info = Info(project)
        Assertions.assertEquals(Branch(project), info.branch)
    }

    @Test
    fun `get version`() {
        val project = project
        val info = Info(project)
        every { GitService.currentTag(project = any()) } returns "0.1.0"
        Assertions.assertEquals("Version(major=0, minor=1, patch=0, prerelease=, build=, suffix=null)", info.version.toString())
    }

    @Test
    fun `current version is tagged with tag prefix`() {
        semverGitPluginExtension.tagPrefix = "prj_"
        val info = Info(project)
        every { GitService.currentTag(project = any()) } returns "prj_0.1.0"
        Assertions.assertEquals("Version(major=0, minor=1, patch=0, prerelease=, build=, suffix=null)", info.version.toString())
    }

    @Test
    fun `current version has no tag with tag prefix`() {
        semverGitPluginExtension.tagPrefix = "prj_"
        val info = Info(project)
        every { GitService.lastTag(project = any(), tagPrefix = any()) } returns "prj_0.1.0"
        Assertions.assertEquals("Version(major=0, minor=2, patch=0, prerelease=, build=, suffix=null)", info.version.toString())
    }

    @Test
    fun `current version has not tag with tag prefix`() {
        val info = Info(project)
        every { GitService.currentTag(project = any()) } returns "prj_0.1.0"
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java ) {
            info.version
        }
    }

    @Test
    fun `last version has not tag with tag prefix`() {
        val info = Info(project)
        every { GitService.lastTag(project = any(), tagPrefix = any()) } returns "prj_0.1.0"
        Assertions.assertThrows(GradleException::class.java ) {
            info.version
        }
    }
}
