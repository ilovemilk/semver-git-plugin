package io.wusa

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import io.wusa.exception.NoCurrentTagFoundException
import io.wusa.exception.NoValidSemverTagFoundException
import io.wusa.extension.SemverGitPluginExtension
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.*
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InfoTest {

    private lateinit var project: Project
    private lateinit var semverGitPluginExtension: SemverGitPluginExtension
    private lateinit var gitService: GitService
    private lateinit var versionService: VersionService
    private lateinit var gitCommandRunner: GitCommandRunner

    private var modules = module {
        single(override = true) { gitCommandRunner }
        single(override = true) { gitService }
        single(override = true) { versionService }
    }

    @BeforeEach
    internal fun setUp() {
        project = ProjectBuilder.builder().build()
        gitService = mockkClass(GitService::class)
        gitCommandRunner = mockkClass(GitCommandRunner::class)
        semverGitPluginExtension = SemverGitPluginExtension(project)
        versionService = VersionService(semverGitPluginExtension, gitService)
        startKoin {
            modules(modules)
        }
    }

    @AfterEach
    internal fun tearDown() {
        semverGitPluginExtension.tagPrefix = ""
        stopKoin()
    }

    @Test
    fun `get dirty`() {
        val info = Info(semverGitPluginExtension)
        every { gitService.isDirty() } returns true
        Assertions.assertEquals(true, info.dirty)
    }

    @Test
    fun `get last tag`() {
        val info = Info(semverGitPluginExtension)
        every { gitService.lastTag(tagPrefix = any()) } returns "0.1.0"
        Assertions.assertEquals("0.1.0", info.lastTag)
    }

    @Test
    fun `get current tag`() {
        val info = Info(semverGitPluginExtension)
        every { gitService.currentTag() } returns "0.1.0"
        Assertions.assertEquals("0.1.0", info.tag)
    }

    @Test
    fun `get last tag with prefix`() {
        semverGitPluginExtension.tagPrefix = "prj_"
        val info = Info(semverGitPluginExtension)
        every { gitService.lastTag(tagPrefix = any()) } returns "prj_0.1.0"
        Assertions.assertEquals("prj_0.1.0", info.lastTag)
    }

    @Test
    fun `get current tag with prefix`() {
        semverGitPluginExtension.tagPrefix = "prj_"
        val info = Info(semverGitPluginExtension)
        every { gitService.currentTag() } returns "prj_0.1.0"
        Assertions.assertEquals("prj_0.1.0", info.tag)
    }

    @Test
    fun `get short commit`() {
        val info = Info(semverGitPluginExtension)
        every { gitService.currentCommit(isShort = true) } returns "1234567"
        Assertions.assertEquals("1234567", info.shortCommit)
    }

    @Test
    fun `get commit`() {
        val info = Info(semverGitPluginExtension)
        every { gitService.currentCommit(isShort = false) } returns "123456789"
        Assertions.assertEquals("123456789", info.commit)
    }

    @Test
    fun `get branch`() {
        val info = Info(semverGitPluginExtension)
        every { gitService.currentBranch() } returns "test"
        Assertions.assertEquals(Branch(gitService).name, info.branch.name)
    }

    @Test
    fun `get version`() {
        val info = Info(semverGitPluginExtension)
        every { gitService.currentTag() } returns "0.1.0"
        every { gitService.isDirty() } returns false
        Assertions.assertEquals("Version(major=0, minor=1, patch=0, prerelease=, build=, suffix=null)", info.version.toString())
    }

    @Test
    fun `get version with lightweight tag`() {
        semverGitPluginExtension.tagType = TagType.LIGHTWEIGHT
        val info = Info(semverGitPluginExtension)
        every { gitService.currentTag(any(), tagType = TagType.LIGHTWEIGHT) } returns "0.1.0"
        every { gitService.isDirty() } returns false
        Assertions.assertEquals("Version(major=0, minor=1, patch=0, prerelease=, build=, suffix=null)", info.version.toString())
    }

    @Test
    fun `current version is tagged with tag prefix`() {
        semverGitPluginExtension.tagPrefix = "prj_"
        val info = Info(semverGitPluginExtension)
        every { gitService.currentTag(any(), any()) } returns "prj_0.1.0"
        every { gitService.isDirty() } returns false
        Assertions.assertEquals("Version(major=0, minor=1, patch=0, prerelease=, build=, suffix=null)", info.version.toString())
    }

    @Test
    fun `current version has no tag with tag prefix`() {
        semverGitPluginExtension.tagPrefix = "prj_"
        val info = Info(semverGitPluginExtension)
        every { gitService.currentBranch() } returns "master"
        every { gitService.lastTag(tagPrefix = any()) } returns "prj_0.1.0"
        every { gitService.currentTag(tagPrefix = any(), tagType = any()) } throws NoCurrentTagFoundException("Nothing found.")
        Assertions.assertEquals("Version(major=0, minor=2, patch=0, prerelease=, build=, suffix=null)", info.version.toString())
    }

    @Test
    fun `current version has not tag with tag prefix`() {
        val info = Info(semverGitPluginExtension)
        every { gitService.currentTag() } returns "prj_0.1.0"
        every { gitService.isDirty() } returns false
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java ) {
            info.version
        }
    }

    @Test
    fun `last version has not tag with tag prefix`() {
        val info = Info(semverGitPluginExtension)
        every { gitService.lastTag(tagPrefix = any()) } returns "prj_0.1.0"
        every { gitService.currentTag(tagPrefix = any(), tagType = any()) } throws NoCurrentTagFoundException("Nothing found.")
        Assertions.assertThrows(GradleException::class.java ) {
            info.version
        }
    }
}
