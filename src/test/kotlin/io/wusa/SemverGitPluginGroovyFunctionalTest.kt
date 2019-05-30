package io.wusa

import org.gradle.internal.impldep.org.eclipse.jgit.api.Git
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.UnexpectedBuildFailure
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SemverGitPluginGroovyFunctionalTest : FunctionalBaseTest() {

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
    fun `defaults`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
            }
        """)
        initializeGitWithoutBranch(testProjectDirectory)
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Version: 0.1.0"))
    }

    @Test
    fun `version formatter for all branches`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
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
        initializeGitWithoutBranch(testProjectDirectory)
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Version: 0.1.0"))
    }

    @Test
    fun `version formatter for feature branches use specific`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
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
        assertTrue(result.output.contains("Version: 0.1.0+branch.feature-test-SNAPSHOT"))
    }

    @Test
    fun `no existing tag`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
            }
        """)
        val git = Git.init().setDirectory(testProjectDirectory).call()
        git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("-SNAPSHOT"))
        assertTrue(result.output.contains("Version: 0.1.0"))
    }

    @Test
    fun `no existing tag with custom initial version`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                initialVersion = '1.0.0'
            }
        """)
        val git = Git.init().setDirectory(testProjectDirectory).call()
        git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("-SNAPSHOT"))
        assertTrue(result.output.contains("Version: 1.0.0"))
    }

    @Test
    fun `no existing tag with configuration without commits`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                snapshotSuffix = 'SNAPSHOT'
            }
        """)
        Git.init().setDirectory(testProjectDirectory).call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue("""Version: 0\.1\.0\-SNAPSHOT""".toRegex().containsMatchIn(result.output))
    }

    @Test
    fun `no existing tag with configuration`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                snapshotSuffix = 'TEST'
            }
        """)
        val git = Git.init().setDirectory(testProjectDirectory).call()
        git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("-TEST"))
        assertTrue(result.output.contains("Version: 0.1.0"))
    }

    @Test
    fun `patch release with custom configuration`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = "MINOR_INCREMENTER"
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}+build.${'$'}{semver.info.count}.sha.${'$'}{semver.info.shortCommit}" }
                    }
                }
            }
        """)
        initializeGitWithoutBranch(testProjectDirectory, "0.0.1")
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Version: 0.0.1"))
    }

    @Test
    fun `minor release with custom configuration`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = "MINOR_INCREMENTER"
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}+build.${'$'}{semver.info.count}.sha.${'$'}{semver.info.shortCommit}" }
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
        assertTrue(result.output.contains("Version: 0.1.0"))
    }

    @Test
    fun `major release with custom configuration`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                snapshotSuffix = 'SNAPSHOT'
                branches {
                    branch {
                        regex = ".*"
                        incrementer = "MINOR_INCREMENTER"
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}+build.${'$'}{semver.info.count}.sha.${'$'}{semver.info.shortCommit}" }
                    }
                }
            }
        """)
        initializeGitWithoutBranch(testProjectDirectory, "1.0.0")
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Version: 1.0.0"))
    }

    @Test
    fun `release alpha with custom configuration`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = "MINOR_INCREMENTER"
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}+build.${'$'}{semver.info.count}.sha.${'$'}{semver.info.shortCommit}" }
                    }
                }
            }
        """)
        initializeGitWithoutBranch(testProjectDirectory, "0.1.0-alpha")
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Version: 0.1.0-alpha"))
    }

    @Test
    fun `release alpha beta with custom configuration`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = "MINOR_INCREMENTER"
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}+build.${'$'}{semver.info.count}.sha.${'$'}{semver.info.shortCommit}" }
                    }
                }
            }
        """)
        initializeGitWithoutBranch(testProjectDirectory, "0.1.0-alpha.beta")
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Version: 0.1.0-alpha.beta"))
    }

    @Test
    fun `release alpha 1 with custom configuration`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = "MINOR_INCREMENTER"
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}+build.${'$'}{semver.info.count}.sha.${'$'}{semver.info.shortCommit}" }
                    }
                }
            }
        """)
        initializeGitWithoutBranch(testProjectDirectory, "0.1.0-alpha.1")
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Version: 0.1.0-alpha.1"))
    }

    @Test
    fun `release beta with custom configuration`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = "MINOR_INCREMENTER"
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}+build.${'$'}{semver.info.count}.sha.${'$'}{semver.info.shortCommit}" }
                    }
                }
            }
        """)
        initializeGitWithoutBranch(testProjectDirectory, "0.1.0-beta")
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Version: 0.1.0-beta"))
    }

    @Test
    fun `release rc with custom configuration`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = "MINOR_INCREMENTER"
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}+build.${'$'}{semver.info.count}.sha.${'$'}{semver.info.shortCommit}" }
                    }
                }
            }
        """)
        initializeGitWithoutBranch(testProjectDirectory, "0.1.0-rc")
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Version: 0.1.0-rc"))
    }

    @Test
    fun `bump patch version`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = "PATCH_INCREMENTER"
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}+build.${'$'}{semver.info.count}.sha.${'$'}{semver.info.shortCommit}" }
                    }
                }
            }
        """)
        val git = initializeGitWithoutBranch(testProjectDirectory)
        git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Version: 0.1.1"))
    }

    @Test
    fun `bump minor version`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            branches {
                branch {
                    regex = ".*"
                    incrementer = "MINOR_INCREMENTER"
                    formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}+build.${'$'}{semver.info.count}.sha.${'$'}{semver.info.shortCommit}" }
                }
            }
        """)
        val git = initializeGitWithoutBranch(testProjectDirectory)
        git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Version: 0.2.0"))
    }

    @Test
    fun `bump major version`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = "MAJOR_INCREMENTER"
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}+build.${'$'}{semver.info.count}.sha.${'$'}{semver.info.shortCommit}" }
                    }
                }
            }
        """)
        val git = initializeGitWithoutBranch(testProjectDirectory)
        git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("-SNAPSHOT"))
        assertTrue(result.output.contains("Version: 1.0.0"))
    }

    @Test
    fun `don't bump version`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = "NO_VERSION_INCREMENTER"
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}+build.${'$'}{semver.info.count}.sha.${'$'}{semver.info.shortCommit}" }
                    }
                }
            }
        """)
        val git = initializeGitWithoutBranch(testProjectDirectory)
        git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Version: 0.1.0"))
    }

    @Test
    fun `non-semver tag`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
            }
        """)
        val git = initializeGitWithoutBranch(testProjectDirectory)
        val commit = git.commit().setMessage("").call()
        git.tag().setName("test-tag").setObjectId(commit).call()
        Assertions.assertThrows(UnexpectedBuildFailure::class.java) {
            gradleRunner
                    .withProjectDir(testProjectDirectory)
                    .withArguments("showVersion")
                    .withPluginClasspath()
                    .build()
        }
    }

    @Test
    fun `full info of master branch with one commit after the tag`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
            }
        """)
        val git = initializeGitWithoutBranch(testProjectDirectory)
        val commit = git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showInfo")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Branch name: master"))
        assertTrue(result.output.contains("Branch group: master"))
        assertTrue(result.output.contains("Branch id: master"))
        assertTrue(result.output.contains("Commit: " + commit.id.name()))
        assertTrue(result.output.contains("Short commit: " + commit.id.abbreviate( 7 ).name()))
        assertTrue(result.output.contains("Tag: none"))
        assertTrue(result.output.contains("Last tag: 0.1.0"))
        assertTrue(result.output.contains("Dirty: false"))
        assertTrue(result.output.contains("Version major: 0"))
        assertTrue(result.output.contains("Version minor: 2"))
        assertTrue(result.output.contains("Version patch: 0"))
        assertTrue(result.output.contains("Version pre release: none"))
        assertTrue(result.output.contains("Version build: none"))
    }

    @Test
    fun `full info of feature-test branch with one commit after the tag`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
            }
        """)
        val git = initializeGitWithBranch(testProjectDirectory, "0.0.1", "feature/test")
        val commit = git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showInfo")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Branch name: feature/test"))
        assertTrue(result.output.contains("Branch group: feature"))
        assertTrue(result.output.contains("Branch id: feature-test"))
        assertTrue(result.output.contains("Commit: " + commit.id.name()))
        assertTrue(result.output.contains("Short commit: " + commit.id.abbreviate( 7 ).name()))
        assertTrue(result.output.contains("Tag: none"))
        assertTrue(result.output.contains("Last tag: 0.0.1"))
        assertTrue(result.output.contains("Dirty: false"))
        assertTrue(result.output.contains("Version major: 0"))
        assertTrue(result.output.contains("Version minor: 1"))
        assertTrue(result.output.contains("Version patch: 0"))
        assertTrue(result.output.contains("Version pre release: none"))
        assertTrue(result.output.contains("Version build: none"))
    }

    @Test
    fun `full info of feature-test branch with no commit after the tag`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
            }
        """)
        val git = initializeGitWithBranch(testProjectDirectory, "0.0.1", "feature/test")
        val head = git.repository.allRefs["HEAD"]
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showInfo")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Branch name: feature/test"))
        assertTrue(result.output.contains("Branch group: feature"))
        assertTrue(result.output.contains("Branch id: feature-test"))
        assertTrue(result.output.contains("Commit: " + head?.objectId?.name))
        assertTrue(result.output.contains("Tag: 0.0.1"))
        assertTrue(result.output.contains("Last tag: 0.0.1"))
        assertTrue(result.output.contains("Dirty: false"))
        assertTrue(result.output.contains("Version major: 0"))
        assertTrue(result.output.contains("Version minor: 0"))
        assertTrue(result.output.contains("Version patch: 1"))
        assertTrue(result.output.contains("Version pre release: none"))
        assertTrue(result.output.contains("Version build: none"))
    }
}