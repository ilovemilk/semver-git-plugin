package io.wusa

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkClass
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BranchTest {

    private lateinit var project: Project
    private lateinit var gitService: GitService

    private var modules = module {
        single { project }
        single { gitService }
    }

    @BeforeAll
    internal fun setUp() {
        project = ProjectBuilder.builder().build()
        gitService = mockk(relaxed = true)
        startKoin {
            modules(modules)
        }
    }

    @AfterAll
    internal fun tearDown() {
        stopKoin()
    }

    @Test
    fun `group of master should be master`() {
        val branch = Branch(gitService)
        every { gitService.currentBranch() } returns "master"
        Assertions.assertEquals(branch.group, "master")
    }

    @Test
    fun `group of develop should be develop`() {
        val branch = Branch(gitService)
        every { gitService.currentBranch() } returns "develop"
        Assertions.assertEquals(branch.group, "develop")
    }

    @Test
    fun `group of feature-test should be feature`() {
        val branch = Branch(gitService)
        every { gitService.currentBranch() } returns "feature/test"
        Assertions.assertEquals(branch.group, "feature")
    }

    @Test
    fun `group of feature-test-test should be feature`() {
        val branch = Branch(gitService)
        every { gitService.currentBranch() } returns "feature/test/test"
        Assertions.assertEquals(branch.group, "feature")
    }

    @Test
    fun `id of feature-test should be feature-test`() {
        val branch = Branch(gitService)
        every { gitService.currentBranch() } returns "feature/test"
        Assertions.assertEquals(branch.id, "feature-test")
    }

    @Test
    fun `id of feature-test_a! should be feature-test_a!`() {
        val branch = Branch(gitService)
        every { gitService.currentBranch() } returns "feature/test_a!"
        Assertions.assertEquals(branch.id, "feature-test_a!")
    }

    @Test
    fun `id of feature-special-test should be feature-special-test`() {
        val branch = Branch(gitService)
        every { gitService.currentBranch() } returns "feature/special-test"
        Assertions.assertEquals(branch.id, "feature-special-test")
    }

    @Test
    fun `branch group of hotfix branch should be hotfix`() {
        val branch = Branch(gitService)
        every { gitService.currentBranch() } returns "hotfix/5.3.1"
        Assertions.assertEquals(branch.group, "hotfix")
    }

    @Test
    fun `branch group of release branch should be release`() {
        val branch = Branch(gitService)
        every { gitService.currentBranch() } returns "release/5.3.0"
        Assertions.assertEquals(branch.group, "release")
    }
}