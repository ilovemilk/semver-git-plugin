package io.wusa

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.*
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SemverGitPluginKotlinFunctionalTest : FunctionalBaseTest() {

    private lateinit var gradleRunner: GradleRunner

    @BeforeAll
    fun setUp() {
        gradleRunner = GradleRunner.create()
    }

    @AfterAll
    fun tearDown() {
        gradleRunner.projectDir.deleteRecursively()
    }

    @Test
    fun `defaults using version formatter`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branchVersionFormatter = mapOf(
                    ".*" to { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}" }
                )
            }
        """)
        initializeGitWithoutBranch(testProjectDirectory)
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 0.1.0"))
    }

    @Test
    fun `version formatter for feature branches`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                 branchVersionFormatter = mapOf(
                    "feature/.*" to { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}+branch.${'$'}{semver.info.branch.id}" },
                    ".*" to { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}" }
                )
            }
        """)
        val git = initializeGitWithBranch(testProjectDirectory, "0.0.1", "feature/test")
        git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 0.1.0+branch.feature-test-SNAPSHOT"))
    }
}