package io.wusa

import org.gradle.internal.impldep.org.eclipse.jgit.api.Git
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.UnexpectedBuildFailure
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
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
    fun defaults() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'io.wusa.semver-git-plugin'
            }
        """)
        initializeGitWithoutBranchAnnotated(testProjectDirectory)
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Version: 0.1.0"))
    }

    @Test
    fun `custom version incrementer`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            import io.wusa.incrementer.GroovyMinorVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = "feature/.*"
                        incrementer = { 
                                        it.major = it.major + 1
                                        it.minor = it.minor + 1
                                        it.patch = it.patch + 1
                                        it 
                                        }
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}+branch.${'$'}{it.branch.id}" }
                    }
                    branch {
                        regex = ".+"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}" }
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
        assertTrue(result.output.contains("Version: 1.1.2+branch.feature-test-SNAPSHOT"))
    }

    @Test
    fun `version formatter for all branches`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            import io.wusa.incrementer.GroovyMinorVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithoutBranchAnnotated(testProjectDirectory)
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
            import io.wusa.incrementer.GroovyMinorVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = "feature/.*"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}+branch.${'$'}{it.branch.id}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                    branch {
                        regex = ".+"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}" }
                        snapshotSuffix = "SNAPSHOT"
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
    fun `version formatter for feature branches with camelCase`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            import io.wusa.incrementer.GroovyMinorVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = "feature/.*"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}+branch.${'$'}{it.branch.id}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                    branch {
                        regex = ".+"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        val git = initializeGitWithBranch(testProjectDirectory, "0.0.1", "feature/testAbc10")
        git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Version: 0.1.0+branch.feature-testAbc10-SNAPSHOT"))
    }

    @Test
    fun `version formatter for feature branches with kebab-case`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            import io.wusa.incrementer.GroovyMinorVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = "feature/.*"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}+branch.${'$'}{it.branch.id}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                    branch {
                        regex = ".+"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        val git = initializeGitWithBranch(testProjectDirectory, "0.0.1", "feature/test-abc-10")
        git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Version: 0.1.0+branch.feature-test-abc-10-SNAPSHOT"))
    }

    @Test
    fun `version formatter for feature branches with PascalCase`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            import io.wusa.incrementer.GroovyMinorVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = "feature/.*"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}+branch.${'$'}{it.branch.id}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                    branch {
                        regex = ".+"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        val git = initializeGitWithBranch(testProjectDirectory, "0.0.1", "feature/TestAbc10")
        git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Version: 0.1.0+branch.feature-TestAbc10-SNAPSHOT"))
    }

    @Test
    fun `version formatter for feature branches with snake_case`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            import io.wusa.incrementer.GroovyMinorVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = "feature/.*"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}+branch.${'$'}{it.branch.id}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                    branch {
                        regex = ".+"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        val git = initializeGitWithBranch(testProjectDirectory, "0.0.1", "feature/test_abc_10")
        git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Version: 0.1.0+branch.feature-test_abc_10-SNAPSHOT"))
    }

    @Test
    fun `version formatter for feature branches with UPPER_CASE`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            import io.wusa.incrementer.GroovyMinorVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = "feature/.*"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}+branch.${'$'}{it.branch.id}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                    branch {
                        regex = ".+"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        val git = initializeGitWithBranch(testProjectDirectory, "0.0.1", "feature/TEST_ABC_10")
        git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Version: 0.1.0+branch.feature-TEST_ABC_10-SNAPSHOT"))
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
            import io.wusa.incrementer.GroovyNoVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = GroovyNoVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        Git.init().setDirectory(testProjectDirectory).call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue("""Version: 0\.1\.0-SNAPSHOT""".toRegex().containsMatchIn(result.output))
    }

    @Test
    fun `no existing tag with configuration`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            import io.wusa.incrementer.GroovyNoVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = GroovyNoVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}" }
                        snapshotSuffix = "TEST"
                    }
                }
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
            import io.wusa.incrementer.GroovyMinorVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}+build.${'$'}{it.count}.sha.${'$'}{it.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithoutBranchAnnotated(testProjectDirectory, "0.0.1")
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
            import io.wusa.incrementer.GroovyMinorVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}+build.${'$'}{it.count}.sha.${'$'}{it.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithoutBranchAnnotated(testProjectDirectory)
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
            import io.wusa.incrementer.GroovyMinorVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}+build.${'$'}{it.count}.sha.${'$'}{it.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithoutBranchAnnotated(testProjectDirectory, "1.0.0")
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
            import io.wusa.incrementer.GroovyMinorVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}+build.${'$'}{it.count}.sha.${'$'}{it.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithoutBranchAnnotated(testProjectDirectory, "0.1.0-alpha")
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
            import io.wusa.incrementer.GroovyMinorVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}+build.${'$'}{it.count}.sha.${'$'}{it.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithoutBranchAnnotated(testProjectDirectory, "0.1.0-alpha.beta")
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
            import io.wusa.incrementer.GroovyMinorVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}+build.${'$'}{it.count}.sha.${'$'}{it.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithoutBranchAnnotated(testProjectDirectory, "0.1.0-alpha.1")
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
            import io.wusa.incrementer.GroovyMinorVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}+build.${'$'}{it.count}.sha.${'$'}{it.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithoutBranchAnnotated(testProjectDirectory, "0.1.0-beta")
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
            import io.wusa.incrementer.GroovyMinorVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}+build.${'$'}{it.count}.sha.${'$'}{it.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithoutBranchAnnotated(testProjectDirectory, "0.1.0-rc")
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
            import io.wusa.incrementer.GroovyPatchVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = GroovyPatchVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}+build.${'$'}{it.count}.sha.${'$'}{it.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        val git = initializeGitWithoutBranchAnnotated(testProjectDirectory)
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
            import io.wusa.incrementer.GroovyMinorVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}+build.${'$'}{it.count}.sha.${'$'}{it.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        val git = initializeGitWithoutBranchAnnotated(testProjectDirectory)
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
            import io.wusa.incrementer.GroovyMajorVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = GroovyMajorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}+build.${'$'}{it.count}.sha.${'$'}{it.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        val git = initializeGitWithoutBranchAnnotated(testProjectDirectory)
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
            import io.wusa.incrementer.GroovyNoVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = GroovyNoVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}+build.${'$'}{it.count}.sha.${'$'}{it.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        val git = initializeGitWithoutBranchAnnotated(testProjectDirectory)
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
        val git = initializeGitWithoutBranchAnnotated(testProjectDirectory)
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
        val git = initializeGitWithoutBranchAnnotated(testProjectDirectory)
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

    @Test
    fun `issues-23 fix branch logic release branch`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            import io.wusa.incrementer.GroovyMinorVersionIncrementer
            import io.wusa.incrementer.GroovyPatchVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                dirtyMarker = "dirty"
                branches {
                    branch {
                        regex = "develop"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}-DEV.${'$'}{semver.info.count}.sha.${'$'}{semver.info.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                    branch {
                        regex = "release/.+"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}-RC.${'$'}{semver.info.count}.sha.${'$'}{semver.info.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                    branch {
                        regex = "hotfix/.+"
                        incrementer = GroovyPatchVersionIncrementer as Transformer
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}-HOTFIX.${'$'}{semver.info.count}.sha.${'$'}{semver.info.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                    branch {
                        regex = ".+"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}-BUILD.${'$'}{semver.info.count}.sha.${'$'}{semver.info.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        val git = initializeGitWithBranch(testProjectDirectory, "5.2.1", "release/5.3.0")
        val commit = git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showInfo")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Branch name: release/5.3.0"))
        assertTrue(result.output.contains("Branch group: release"))
        assertTrue(result.output.contains("Branch id: release-5.3.0"))
        assertTrue(result.output.contains("Commit: " + commit.id.name()))
        assertTrue(result.output.contains("Short commit: " + commit.id.abbreviate( 7 ).name()))
        assertTrue(result.output.contains("Tag: none"))
        assertTrue(result.output.contains("Last tag: 5.2.1-1-g" + commit.id.abbreviate( 7 ).name()))
        assertTrue(result.output.contains("Dirty: false"))
        assertTrue(result.output.contains("Version: 5.3.0-RC.2.sha." + commit.id.abbreviate( 7 ).name() + "-SNAPSHOT"))
        assertTrue(result.output.contains("Version major: 5"))
        assertTrue(result.output.contains("Version minor: 3"))
        assertTrue(result.output.contains("Version patch: 0"))
        assertTrue(result.output.contains("Version pre release: none"))
        assertTrue(result.output.contains("Version build: none"))
    }

    @Test
    fun `issues-23 fix branch logic hotfix branch`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            import io.wusa.incrementer.GroovyMinorVersionIncrementer
            import io.wusa.incrementer.GroovyPatchVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                dirtyMarker = "dirty"
                branches {
                    branch {
                        regex = "develop"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}-DEV.${'$'}{semver.info.count}.sha.${'$'}{semver.info.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                    branch {
                        regex = "release/.+"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}-RC.${'$'}{semver.info.count}.sha.${'$'}{semver.info.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                    branch {
                        regex = "hotfix/.+"
                        incrementer = GroovyPatchVersionIncrementer as Transformer
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}-HOTFIX.${'$'}{semver.info.count}.sha.${'$'}{semver.info.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                    branch {
                        regex = ".+"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}-BUILD.${'$'}{semver.info.count}.sha.${'$'}{semver.info.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        val git = initializeGitWithBranch(testProjectDirectory, "5.2.1", "hotfix/5.3.1")
        val commit = git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showInfo")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Branch name: hotfix/5.3.1"))
        assertTrue(result.output.contains("Branch group: hotfix"))
        assertTrue(result.output.contains("Branch id: hotfix-5.3.1"))
        assertTrue(result.output.contains("Commit: " + commit.id.name()))
        assertTrue(result.output.contains("Short commit: " + commit.id.abbreviate( 7 ).name()))
        assertTrue(result.output.contains("Tag: none"))
        assertTrue(result.output.contains("Last tag: 5.2.1-1-g" + commit.id.abbreviate( 7 ).name()))
        assertTrue(result.output.contains("Dirty: false"))
        assertTrue(result.output.contains("Version: 5.2.2-HOTFIX.2.sha." + commit.id.abbreviate( 7 ).name() + "-SNAPSHOT"))
        assertTrue(result.output.contains("Version major: 5"))
        assertTrue(result.output.contains("Version minor: 2"))
        assertTrue(result.output.contains("Version patch: 2"))
        assertTrue(result.output.contains("Version pre release: none"))
        assertTrue(result.output.contains("Version build: none"))
    }

    @Test
    fun `issues-35 fix branch regex`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            import io.wusa.incrementer.GroovyPatchVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                snapshotSuffix = "SNAPSHOT"
                dirtyMarker = "dirty"
                branches {
                    branch {
                        regex = "feature.+"
                        incrementer = GroovyPatchVersionIncrementer as Transformer
                        formatter = { "${'$'}{semver.info.version.major}.${'$'}{semver.info.version.minor}.${'$'}{semver.info.version.patch}-DEV.${'$'}{semver.info.count}.sha.${'$'}{semver.info.shortCommit}" }
                    }
                }
            }
        """)
        val git = initializeGitWithBranch(testProjectDirectory, "5.2.1", "feature/bellini/test-branch-version")
        val commit = git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showInfo")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Branch name: feature/bellini/test-branch-version"))
        assertTrue(result.output.contains("Branch group: feature"))
        assertTrue(result.output.contains("Branch id: feature-bellini-test-branch-version"))
        assertTrue(result.output.contains("Commit: " + commit.id.name()))
        assertTrue(result.output.contains("Short commit: " + commit.id.abbreviate( 7 ).name()))
        assertTrue(result.output.contains("Tag: none"))
        assertTrue(result.output.contains("Last tag: 5.2.1-1-g" + commit.id.abbreviate( 7 ).name()))
        assertTrue(result.output.contains("Dirty: false"))
        assertTrue(result.output.contains("Version: 5.2.2-DEV.2.sha." + commit.id.abbreviate( 7 ).name() + "-SNAPSHOT"))
        assertTrue(result.output.contains("Version major: 5"))
        assertTrue(result.output.contains("Version minor: 2"))
        assertTrue(result.output.contains("Version patch: 2"))
        assertTrue(result.output.contains("Version pre release: none"))
        assertTrue(result.output.contains("Version build: none"))
    }

    @Test
    fun `empty snapshotSuffix must not append a hyphen`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            import io.wusa.incrementer.GroovyMinorVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = GroovyMinorVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}" }
                        snapshotSuffix = ""
                    }
                }
            }
        """)
        val git = initializeGitWithBranch(testProjectDirectory, "4.2.0")
        git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Version: 4.3.0"))
        assertFalse(result.output.contains("Version: 4.3.0-"))
    }

    @Test
    fun `empty dirty marker must not append a hyphen`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle")
        buildFile.writeText("""
            import io.wusa.incrementer.GroovyNoVersionIncrementer
            import org.gradle.api.Transformer
            
            plugins {
                id 'io.wusa.semver-git-plugin'
            }

            semver {
                snapshotSuffix = ''
                dirtyMarker = ''
                branches {
                    branch {
                        regex = ".*"
                        incrementer = GroovyNoVersionIncrementer as Transformer
                        formatter = { "${'$'}{it.version.major}.${'$'}{it.version.minor}.${'$'}{it.version.patch}" }
                    }
                }
            }
        """)
        val git = initializeGitWithoutBranchAndWithoutTag(testProjectDirectory)
        git.commit().setMessage("").call()
        val dirtyFile = File(testProjectDirectory, "test.dirty")
        dirtyFile.writeText("""not dirty!""")
        git.add().addFilepattern(".").call()
        git.commit().setMessage("").call()
        dirtyFile.writeText("""dirty!""")
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showInfo")
                .withPluginClasspath()
                .build()
        println(result.output)
        assertTrue(result.output.contains("Dirty: true"))
        assertTrue(result.output.contains("Version: 0.1.0"))
        assertFalse(result.output.contains("Version: 0.1.0-"))
    }
}
