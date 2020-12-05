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
        val info = Info(semverGitPluginExtension)
        every { semverGitPluginExtension.info } returns info
        every { gitService.isDirty() } returns false
        startKoin {
            modules(modules)
        }
    }

    @AfterAll
    internal fun tearDown() {
        stopKoin()
    }

    @Test
    fun `minor should be increased by 1`() {
        every { gitService.getCommitsSinceLastTag() } returns listOf("fix: update gradle plugin publish due to security bugs\n",
                "fix: update version an changelog\n",
                "feat: Merge pull request #18 from ilovemilk/feature/support-multi-module\n",
                "fix: add default tagPrefix behaviour\n",
                "fix: Merge pull request #17 from jgindin/support-multi-module\n",
                "fix: Merge remote-tracking branch 'origin/feature/add-more-tests' into develop\n",
                "fix: Add support for multi-module projects.\n",
                "fix: add test for kebab-case with numbers\n",
                "fix: add more tests"
        )

        Assertions.assertEquals(ConventionalCommitsVersionIncrementer.transform(Version(0, 0, 0, "", "", null)), Version(0, 1, 0, "", "", null))
    }

    @Test
    fun `patch should be increased by 1`() {
        every { gitService.getCommitsSinceLastTag() } returns listOf("fix: update gradle plugin publish due to security bugs\n",
                "fix: update version an changelog\n",
                "fix: Merge pull request #18 from ilovemilk/feature/support-multi-module\n",
                "fix: add default tagPrefix behaviour\n",
                "fix: Merge pull request #17 from jgindin/support-multi-module\n",
                "fix: Merge remote-tracking branch 'origin/feature/add-more-tests' into develop\n",
                "fix: Add support for multi-module projects.\n",
                "fix: add test for kebab-case with numbers\n",
                "fix: add more tests")

        Assertions.assertEquals(ConventionalCommitsVersionIncrementer.transform(Version(0, 0, 0, "", "", null)), Version(0, 0, 1, "", "", null))
    }

    @Test
    fun `major should be increased by 1`() {
        every { gitService.getCommitsSinceLastTag() } returns listOf("fix: update gradle plugin publish due to security bugs\n",
                "BREAKING CHANGE: update version an changelog\n",
                "fix: Merge pull request #18 from ilovemilk/feature/support-multi-module\n",
                "feat: add default tagPrefix behaviour\n",
                "fix: Merge pull request #17 from jgindin/support-multi-module\n",
                "feat: Merge remote-tracking branch 'origin/feature/add-more-tests' into develop\n",
                "fix: Add support for multi-module projects.\n",
                "feat: add test for kebab-case with numbers\n",
                "fix: add more tests")

        Assertions.assertEquals(ConventionalCommitsVersionIncrementer.transform(Version(0, 0, 0, "", "", null)), Version(1, 0, 0, "", "", null))
    }

    @Test
    fun `issue-56 breaking change with ! after the feat scope`() {
        every { gitService.getCommitsSinceLastTag() } returns listOf("feat!: added semver plugin incrementer parameter\n",
                "feat: added semver plugin incrementer parameter")

        Assertions.assertEquals(ConventionalCommitsVersionIncrementer.transform(Version(0, 0, 0, "", "", null)), Version(1, 0, 0, "", "", null))
    }

    @Test
    fun `issue-56 breaking change with ! after the fix scope`() {
        every { gitService.getCommitsSinceLastTag() } returns listOf("fix!: added semver plugin incrementer parameter\n",
                "feat: added semver plugin incrementer parameter")

        Assertions.assertEquals(ConventionalCommitsVersionIncrementer.transform(Version(0, 0, 0, "", "", null)), Version(1, 0, 0, "", "", null))
    }
}
