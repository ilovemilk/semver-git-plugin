package io.wusa

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BranchTest {

    private lateinit var project: Project

    @BeforeEach
    internal fun setUp() {
        project = ProjectBuilder.builder().build()
        mockkObject(GitService.Companion)
    }

    @AfterEach
    internal fun tearDown() {
        unmockkObject(GitService.Companion)
    }

    @Test
    fun `group of master should be master`() {
        val branch = Branch(project)
        every { GitService.currentBranch(project = any()) } returns "master"
        Assertions.assertEquals(branch.group, "master")
    }

    @Test
    fun `group of develop should be develop`() {
        val branch = Branch(project)
        every { GitService.currentBranch(project = any()) } returns "develop"
        Assertions.assertEquals(branch.group, "develop")
    }

    @Test
    fun `group of feature-test should be feature`() {
        val branch = Branch(project)
        every { GitService.currentBranch(project = any()) } returns "feature/test"
        Assertions.assertEquals(branch.group, "feature")
    }

    @Test
    fun `group of feature-test-test should be feature`() {
        val branch = Branch(project)
        every { GitService.currentBranch(project = any()) } returns "feature/test/test"
        Assertions.assertEquals(branch.group, "feature")
    }

    @Test
    fun `id of feature-test should be feature-test`() {
        val branch = Branch(project)
        every { GitService.currentBranch(project = any()) } returns "feature/test"
        Assertions.assertEquals(branch.id, "feature-test")
    }

    @Test
    fun `id of feature-test_a! should be feature-test_a!`() {
        val branch = Branch(project)
        every { GitService.currentBranch(project = any()) } returns "feature/test_a!"
        Assertions.assertEquals(branch.id, "feature-test_a!")
    }

    @Test
    fun `id of feature-special-test should be feature-special-test`() {
        val branch = Branch(project)
        every { GitService.currentBranch(project = any()) } returns "feature/special-test"
        Assertions.assertEquals(branch.id, "feature-special-test")
    }

    @Test
    fun `branch group of hotfix branch should be hotfix`() {
        val branch = Branch(project)
        every { GitService.currentBranch(project = any()) } returns "hotfix/5.3.1"
        Assertions.assertEquals(branch.group, "hotfix")
    }

    @Test
    fun `branch group of release branch should be release`() {
        val branch = Branch(project)
        every { GitService.currentBranch(project = any()) } returns "release/5.3.0"
        Assertions.assertEquals(branch.group, "release")
    }
}