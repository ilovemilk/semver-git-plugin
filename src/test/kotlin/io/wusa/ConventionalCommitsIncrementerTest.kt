package io.wusa

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.wusa.extension.SemverGitPluginExtension
import io.wusa.incrementer.ConventionalCommitsIncrementer
import io.wusa.incrementer.VersionIncrementer
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConventionalCommitsIncrementerTest {

    private lateinit var project: Project
    private lateinit var semverGitPluginExtension: SemverGitPluginExtension

    @BeforeEach
    internal fun setUp() {
        project = ProjectBuilder.builder().build()
        project.plugins.apply(SemverGitPlugin::class.java)
        semverGitPluginExtension = project.extensions.getByType(SemverGitPluginExtension::class.java)
        semverGitPluginExtension.tagType = TagType.ANNOTATED
        semverGitPluginExtension.tagPrefix = ""
        mockkObject(GitCommandRunner)
    }

    @AfterEach
    internal fun tearDown() {
        unmockkObject(GitCommandRunner)
    }

    @Test
    fun `patch should be increased by 1`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns
                "fix: update gradle plugin publish due to security bugs\n" +
                "fix: update version an changelog\n" +
                "feat: Merge pull request #18 from ilovemilk/feature/support-multi-module\n" +
                "fix: add default tagPrefix behaviour\n" +
                "fix: Merge pull request #17 from jgindin/support-multi-module\n" +
                "fix: Merge remote-tracking branch 'origin/feature/add-more-tests' into develop\n" +
                "fix: Add support for multi-module projects.\n" +
                "fix: add test for kebab-case with numbers\n" +
                "fix: add more tests"

        Assertions.assertEquals(VersionIncrementer.getVersionIncrementerByName("CONVENTIONAL_COMMITS_INCREMENTER").increment(Version(0, 0, 0, "", "", null), project), Version(0, 1, 0, "", "", null))
    }

    @Test
    fun `minor should be increased by 1`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns
                "fix: update gradle plugin publish due to security bugs\n" +
                "fix: update version an changelog\n" +
                "fix: Merge pull request #18 from ilovemilk/feature/support-multi-module\n" +
                "fix: add default tagPrefix behaviour\n" +
                "fix: Merge pull request #17 from jgindin/support-multi-module\n" +
                "fix: Merge remote-tracking branch 'origin/feature/add-more-tests' into develop\n" +
                "fix: Add support for multi-module projects.\n" +
                "fix: add test for kebab-case with numbers\n" +
                "fix: add more tests"

        Assertions.assertEquals(VersionIncrementer.getVersionIncrementerByName("CONVENTIONAL_COMMITS_INCREMENTER").increment(Version(0, 0, 0, "", "", null), project), Version(0, 0, 1, "", "", null))
    }

    @Test
    fun `major should be increased by 1`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns
                "fix: update gradle plugin publish due to security bugs\n" +
                "BREAKING CHANGE: update version an changelog\n" +
                "fix: Merge pull request #18 from ilovemilk/feature/support-multi-module\n" +
                "feat: add default tagPrefix behaviour\n" +
                "fix: Merge pull request #17 from jgindin/support-multi-module\n" +
                "feat: Merge remote-tracking branch 'origin/feature/add-more-tests' into develop\n" +
                "fix: Add support for multi-module projects.\n" +
                "feat: add test for kebab-case with numbers\n" +
                "fix: add more tests"

        Assertions.assertEquals(VersionIncrementer.getVersionIncrementerByName("CONVENTIONAL_COMMITS_INCREMENTER").increment(Version(0, 0, 0, "", "", null), project), Version(1, 0, 0, "", "", null))
    }

    @Test
    fun `issue-56 breaking change with ! after the feat scope`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns
                "feat!: added semver plugin incrementer parameter\n" +
                "feat: added semver plugin incrementer parameter"

        Assertions.assertEquals(VersionIncrementer.getVersionIncrementerByName("CONVENTIONAL_COMMITS_INCREMENTER").increment(Version(0, 0, 0, "", "", null), project), Version(1, 0, 0, "", "", null))
    }

    @Test
    fun `issue-56 breaking change with ! after the fix scope`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns
                "fix!: added semver plugin incrementer parameter\n" +
                "feat: added semver plugin incrementer parameter"

        Assertions.assertEquals(VersionIncrementer.getVersionIncrementerByName("CONVENTIONAL_COMMITS_INCREMENTER").increment(Version(0, 0, 0, "", "", null), project), Version(1, 0, 0, "", "", null))
    }
}
