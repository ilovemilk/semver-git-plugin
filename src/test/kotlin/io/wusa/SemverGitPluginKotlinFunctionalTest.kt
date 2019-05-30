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
    fun `version formatter`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".+"
                        incrementer = "MINOR_INCREMENTER"
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}" }
                    }
                }
            }
        """)
        initializeGitWithoutBranch(testProjectDirectory)
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 0.2.0"))
    }

    @Test
    fun `version formatter for feature branches use specific`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = "feature/.*"
                        incrementer = "MINOR_INCREMENTER"
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}+branch.${'$'}{semver.info.branch.id}" }
                    }
                    branch {
                        regex = ".+"
                        incrementer = "MINOR_INCREMENTER"
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}" }
                    }
                }
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

    @Test
    fun `version formatter for feature branches use general`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".+"
                        incrementer = "MINOR_INCREMENTER"
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}" }
                    }
                    branch {
                        regex = "feature/.*"
                        incrementer = "MINOR_INCREMENTER"
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}+branch.${'$'}{semver.info.branch.id}" }
                    }
                }
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
        Assertions.assertTrue(result.output.contains("Version: 0.1.0-SNAPSHOT"))
    }

    @Test
    fun `version no increment`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = "NO_VERSION_INCREMENTER"
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}" }
                    }
                }
            }
        """)
        val git = initializeGitWithBranch(testProjectDirectory, "0.0.1")
        git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue("""Version: 0\.0\.1\+build\.2\.sha\.[0-9a-f]{7}-SNAPSHOT""".toRegex().containsMatchIn(result.output))
    }

    @Test
    fun `version patch increment`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = "PATCH_INCREMENTER"
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}" }
                    }
                }
            }
        """)
        val git = initializeGitWithBranch(testProjectDirectory, "0.0.1")
        git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue("""Version: 0\.0\.2\+build\.2\.sha\.[0-9a-f]{7}-SNAPSHOT""".toRegex().containsMatchIn(result.output))
    }

    @Test
    fun `version minor increment`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = "MINOR_INCREMENTER"
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}" }
                    }
                }
            }
        """)
        val git = initializeGitWithBranch(testProjectDirectory, "0.0.1")
        git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue("""Version: 0\.1\.0\+build\.2\.sha\.[0-9a-f]{7}-SNAPSHOT""".toRegex().containsMatchIn(result.output))
    }

    @Test
    fun `version major increment`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = "MAJOR_INCREMENTER"
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}" }
                    }
                }
            }
        """)
        val git = initializeGitWithBranch(testProjectDirectory, "0.0.1")
        git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue("""Version: 1\.0\.0\+build\.2\.sha\.[0-9a-f]{7}-SNAPSHOT""".toRegex().containsMatchIn(result.output))
    }
}