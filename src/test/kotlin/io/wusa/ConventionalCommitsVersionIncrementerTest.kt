package io.wusa

import io.mockk.every
import io.mockk.mockkClass
import io.wusa.extension.SemverGitPluginExtension
import io.wusa.incrementer.ConventionalCommitsVersionIncrementer
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConventionalCommitsVersionIncrementerTest {

    private lateinit var project: Project
    private lateinit var gitService: GitService
    private lateinit var semverGitPluginExtension: SemverGitPluginExtension

    private var modules = module {
        single { gitService }
        single { semverGitPluginExtension }
    }

    @BeforeAll
    internal fun setUp() {
        project = ProjectBuilder.builder().build()
        gitService = mockkClass(GitService::class)
        semverGitPluginExtension = mockkClass(SemverGitPluginExtension::class)
        every { semverGitPluginExtension.tagPrefix } returns ""
        every { semverGitPluginExtension.tagType } returns TagType.ANNOTATED
        startKoin {
            modules(modules)
        }
    }

    @AfterAll
    internal fun tearDown() {
        stopKoin()
    }

    @Test
    fun `patch should be increased by 1`() {
        every { gitService.getCommitsSinceLastTag() } returns listOf(
                "7356414 fix: update gradle plugin publish due to security bugs",
                "45f65f6 fix: update version an changelog",
                "67f03b1 feat: Merge pull request #18 from ilovemilk/feature/support-multi-module",
                "fba5872 fix: add default tagPrefix behaviour",
                "2d03c4b fix: Merge pull request #17 from jgindin/support-multi-module",
                "f96697f fix: Merge remote-tracking branch 'origin/feature/add-more-tests' into develop",
                "73fc8b4 fix: Add support for multi-module projects.",
                "74e3eb1 fix: add test for kebab-case with numbers",
                "63ca60f fix: add more tests")

        Assertions.assertEquals(ConventionalCommitsVersionIncrementer.transform(Version(0, 0, 0, "", "", null)), Version(0, 1, 0, "", "", null))
    }

    @Test
    fun `minor should be increased by 1`() {
        every { gitService.getCommitsSinceLastTag() } returns listOf(
                "7356414 fix: update gradle plugin publish due to security bugs",
                "45f65f6 fix: update version an changelog",
                "67f03b1 fix: Merge pull request #18 from ilovemilk/feature/support-multi-module",
                "fba5872 fix: add default tagPrefix behaviour",
                "2d03c4b fix: Merge pull request #17 from jgindin/support-multi-module",
                "f96697f fix: Merge remote-tracking branch 'origin/feature/add-more-tests' into develop",
                "73fc8b4 fix: Add support for multi-module projects.",
                "74e3eb1 fix: add test for kebab-case with numbers",
                "63ca60f fix: add more tests")

        Assertions.assertEquals(ConventionalCommitsVersionIncrementer.transform(Version(0, 0, 0, "", "", null)), Version(0, 0, 1, "", "", null))
    }

    @Test
    fun `major should be increased by 1`() {
        every { gitService.getCommitsSinceLastTag() } returns listOf(
                "7356414 fix: update gradle plugin publish due to security bugs",
                "45f65f6 BREAKING CHANGE: update version an changelog",
                "67f03b1 fix: Merge pull request #18 from ilovemilk/feature/support-multi-module",
                "fba5872 feat: add default tagPrefix behaviour",
                "2d03c4b fix: Merge pull request #17 from jgindin/support-multi-module",
                "f96697f feat: Merge remote-tracking branch 'origin/feature/add-more-tests' into develop",
                "73fc8b4 fix: Add support for multi-module projects.",
                "74e3eb1 feat: add test for kebab-case with numbers",
                "63ca60f fix: add more tests")

        Assertions.assertEquals(ConventionalCommitsVersionIncrementer.transform(Version(0, 0, 0, "", "", null)), Version(1, 0, 0, "", "", null))
    }
}
