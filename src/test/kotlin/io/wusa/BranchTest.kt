package io.wusa

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BranchTest {
    @BeforeEach
    internal fun setUp() {
        mockkObject(GitService.Companion)
    }

    @AfterEach
    internal fun cleanUp() {
        unmockkObject(GitService.Companion)
    }

    @Test
    fun `group of master should be master`() {
        val branch = Branch(createTempDir())
        every { GitService.currentBranch(projectDir = any()) } returns "master"
        Assertions.assertEquals(branch.group, "master")
    }

    @Test
    fun `group of master should be develop`() {
        val branch = Branch(createTempDir())
        every { GitService.currentBranch(projectDir = any()) } returns "develop"
        Assertions.assertEquals(branch.group, "develop")
    }

    @Test
    fun `group of feature-test should be test`() {
        val branch = Branch(createTempDir())
        every { GitService.currentBranch(projectDir = any()) } returns "feature/test"
        Assertions.assertEquals(branch.group, "feature")
    }

    @Test
    fun `group of feature-test-test should be test`() {
        val branch = Branch(createTempDir())
        every { GitService.currentBranch(projectDir = any()) } returns "feature/test/test"
        Assertions.assertEquals(branch.group, "feature")
    }

    @Test
    fun `id of feature-test should be feature-test`() {
        val branch = Branch(createTempDir())
        every { GitService.currentBranch(projectDir = any()) } returns "feature/test"
        Assertions.assertEquals(branch.id, "feature-test")
    }

    @Test
    fun `id of feature-test_a! should be feature-test_a!`() {
        val branch = Branch(createTempDir())
        every { GitService.currentBranch(projectDir = any()) } returns "feature/test_a!"
        Assertions.assertEquals(branch.id, "feature-test_a!")
    }

    @Test
    fun `id of feature-special-test should be feature-special-test`() {
        val branch = Branch(createTempDir())
        every { GitService.currentBranch(projectDir = any()) } returns "feature/special-test"
        Assertions.assertEquals(branch.id, "feature-special-test")
    }
}