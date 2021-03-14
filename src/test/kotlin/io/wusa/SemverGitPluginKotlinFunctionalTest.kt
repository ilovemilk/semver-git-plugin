package io.wusa

import org.gradle.internal.impldep.org.eclipse.jgit.api.Git
import org.gradle.internal.impldep.org.eclipse.jgit.lib.Repository
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.*
import org.junit.jupiter.api.io.TempDir
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class SemverGitPluginKotlinFunctionalTest : FunctionalBaseTest() {

    lateinit var testProjectDirectory: File
    private lateinit var gradleRunner: GradleRunner
    private lateinit var git: Git
    private lateinit var repository: Repository

    @BeforeEach
    fun setUp() {
        testProjectDirectory = createTempDir()
        gradleRunner = GradleRunner.create()
        gradleRunner.withProjectDir(testProjectDirectory)
        git = Git.init().setDirectory(testProjectDirectory).call()
        repository = Git.open(testProjectDirectory).repository
    }

    @AfterEach
    fun tearDown() {
        testProjectDirectory.deleteOnExit()
    }

    @Test
    fun `version formatter`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            import io.wusa.Info
            import io.wusa.incrementer.MinorVersionIncrementer

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".+"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ info:Info -> "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithoutBranchAnnotated(repository, testProjectDirectory)
        val result = gradleRunner

                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 0.1.0"))
    }

    @Test
    fun `version properties`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            import io.wusa.Info
            import io.wusa.incrementer.MinorVersionIncrementer

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".+"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ info:Info -> "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithoutBranchAnnotated(repository, testProjectDirectory)
        val result = gradleRunner

                .withArguments("createVersionProperties")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version properties file successfully created."))
        val propertiesFile = testProjectDirectory.resolve("build/generated/version.properties")
        val properties = propertiesFile.readText()
        Assertions.assertTrue(properties.contains("version=0.1.0"))
        Assertions.assertTrue(properties.contains("last.tag=0.1.0"))
        Assertions.assertTrue(properties.contains("tag=0.1.0"))
    }

    @Test
    fun `custom version incrementer`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            import io.wusa.Info
            import io.wusa.Version

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".+"
                        incrementer = 
                            object : Transformer<Version, Version> {
                                override fun transform(version: Version): Version {
                                    version.major += 1
                                    version.minor += 1
                                    version.patch += 1
                                    return version
                                }
                            }
                        formatter = Transformer<Any, Info>{ info:Info -> "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                    }
                }
            }
        """)
        initializeGitWithBranch(repository, testProjectDirectory, "0.0.1", "feature/test")
        Git(repository).commit().setMessage("").call()
        val result = gradleRunner

                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 1.1.2"))
    }

    @Test
    fun `version formatter for feature branches use specific`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            import io.wusa.Info
            import io.wusa.incrementer.MinorVersionIncrementer

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = "feature/.*"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+branch.${'$'}{info.branch.id}" }
                        snapshotSuffix = "SNAPSHOT-feature"
                    }
                    branch {
                        regex = ".+"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithBranch(repository, testProjectDirectory, "0.0.1", "feature/test")
        Git(repository).commit().setMessage("").call()
        val result = gradleRunner

                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 0.1.0+branch.feature-test-SNAPSHOT-feature"))
    }

    @Test
    fun `snapshot suffix for feature branches use specific`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            import io.wusa.Info
            import io.wusa.incrementer.MinorVersionIncrementer

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                snapshotSuffix = "SNAPSHOT-global"
                branches {
                    branch {
                        regex = "feature/.*"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+branch.${'$'}{info.branch.id}" }
                        snapshotSuffix = "SNAPSHOT-feature"
                    }
                    branch {
                        regex = ".+"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithBranch(repository, testProjectDirectory, "0.0.1", "feature/test")
        Git(repository).commit().setMessage("").call()
        val result = gradleRunner

                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 0.1.0+branch.feature-test-SNAPSHOT-feature"))
    }

    @Test
    fun `snapshot suffix for feature branches use specific which is empty`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            import io.wusa.Info
            import io.wusa.incrementer.MinorVersionIncrementer

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                snapshotSuffix = "SNAPSHOT-global"
                branches {
                    branch {
                        regex = "feature/.*"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+branch.${'$'}{info.branch.id}" }
                        snapshotSuffix = ""
                    }
                    branch {
                        regex = ".+"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithBranch(repository, testProjectDirectory, "0.0.1", "feature/test")
        Git(repository).commit().setMessage("").call()
        val result = gradleRunner

                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 0.1.0+branch.feature-test"))
    }

    @Test
    fun `snapshot suffix are both empty`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            import io.wusa.Info
            import io.wusa.incrementer.MinorVersionIncrementer

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                snapshotSuffix = ""
                branches {
                    branch {
                        regex = "feature/.*"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+branch.${'$'}{info.branch.id}" }
                        snapshotSuffix = ""
                    }
                    branch {
                        regex = ".+"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithBranch(repository, testProjectDirectory, "0.0.1", "feature/test")
        Git(repository).commit().setMessage("").call()
        val result = gradleRunner

                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 0.1.0+branch.feature-test"))
    }

    @Test
    fun `snapshot suffix for feature branches use specific which is not set`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            import io.wusa.Info
            import io.wusa.incrementer.MinorVersionIncrementer

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                snapshotSuffix = "SNAPSHOT-global"
                branches {
                    branch {
                        regex = "feature/.*"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+branch.${'$'}{info.branch.id}" }
                    }
                    branch {
                        regex = ".+"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithBranch(repository, testProjectDirectory, "0.0.1", "feature/test")
        Git(repository).commit().setMessage("").call()
        val result = gradleRunner

                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 0.1.0+branch.feature-test-SNAPSHOT-global"))
    }

    @Test
    fun `version formatter for feature branches with camelCase`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            import io.wusa.Info
            import io.wusa.incrementer.MinorVersionIncrementer

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".+"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                    branch {
                        regex = "feature/.*"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+branch.${'$'}{info.branch.id}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithBranch(repository, testProjectDirectory, "0.0.1", "feature/testAbc")
        Git(repository).commit().setMessage("").call()
        val result = gradleRunner

                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 0.1.0-SNAPSHOT"))
    }

    @Test
    fun `version formatter for feature branches with kebab-case`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            import io.wusa.Info
            import io.wusa.incrementer.MinorVersionIncrementer

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".+"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                    branch {
                        regex = "feature/.*"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+branch.${'$'}{info.branch.id}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithBranch(repository, testProjectDirectory, "0.0.1", "feature/test-abc-10")
        Git(repository).commit().setMessage("").call()
        val result = gradleRunner

                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 0.1.0-SNAPSHOT"))
    }

    @Test
    fun `version formatter for feature branches with snake_case`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            import io.wusa.Info
            import io.wusa.incrementer.MinorVersionIncrementer

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".+"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                    branch {
                        regex = "feature/.*"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+branch.${'$'}{info.branch.id}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithBranch(repository, testProjectDirectory, "0.0.1", "feature/test_abc_10")
        Git(repository).commit().setMessage("").call()
        val result = gradleRunner

                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 0.1.0-SNAPSHOT"))
    }

    @Test
    fun `version formatter for feature branches with PascalCase`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            import io.wusa.Info
            import io.wusa.incrementer.MinorVersionIncrementer

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".+"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                    branch {
                        regex = "feature/.*"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+branch.${'$'}{info.branch.id}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithBranch(repository, testProjectDirectory, "0.0.1", "feature/TestAbc10")
        Git(repository).commit().setMessage("").call()
        val result = gradleRunner

                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 0.1.0-SNAPSHOT"))
    }

    @Test
    fun `version formatter for feature branches with UPPERCASE`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            import io.wusa.Info
            import io.wusa.incrementer.MinorVersionIncrementer

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".+"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                    branch {
                        regex = "feature/.*"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+branch.${'$'}{info.branch.id}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithBranch(repository, testProjectDirectory, "0.0.1", "feature/TESTABC10")
        Git(repository).commit().setMessage("").call()
        val result = gradleRunner

                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 0.1.0-SNAPSHOT"))
    }

    @Test
    fun `version formatter for feature branches use general`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            import io.wusa.Info
            import io.wusa.incrementer.MinorVersionIncrementer

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".+"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                    branch {
                        regex = "feature/.*"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+branch.${'$'}{info.branch.id}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithBranch(repository, testProjectDirectory, "0.0.1", "feature/test")
        Git(repository).commit().setMessage("").call()
        val result = gradleRunner

                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 0.1.0-SNAPSHOT"))
    }

    @Test
    fun `version no increment`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            import io.wusa.Info
            import io.wusa.incrementer.NoVersionIncrementer

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = NoVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+build.${'$'}{info.count}.sha.${'$'}{info.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithBranch(repository, testProjectDirectory, "0.0.1")
        Git(repository).commit().setMessage("").call()
        val result = gradleRunner

                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue("""Version: 0\.0\.1\+build\.2\.sha\.[0-9a-f]{7}-SNAPSHOT""".toRegex().containsMatchIn(result.output))
    }

    @Test
    fun `version patch increment`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            import io.wusa.Info
            import io.wusa.incrementer.PatchVersionIncrementer

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = PatchVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+build.${'$'}{info.count}.sha.${'$'}{info.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithBranch(repository, testProjectDirectory, "0.0.1")
        Git(repository).commit().setMessage("").call()
        val result = gradleRunner

                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue("""Version: 0\.0\.2\+build\.2\.sha\.[0-9a-f]{7}-SNAPSHOT""".toRegex().containsMatchIn(result.output))
    }

    @Test
    fun `version minor increment`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            import io.wusa.Info
            import io.wusa.incrementer.MinorVersionIncrementer

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+build.${'$'}{info.count}.sha.${'$'}{info.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithBranch(repository, testProjectDirectory, "0.0.1")
        Git(repository).commit().setMessage("").call()
        val result = gradleRunner

                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue("""Version: 0\.1\.0\+build\.2\.sha\.[0-9a-f]{7}-SNAPSHOT""".toRegex().containsMatchIn(result.output))
    }

    @Test
    fun `version major increment`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            import io.wusa.Info
            import io.wusa.incrementer.MajorVersionIncrementer

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = MajorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+build.${'$'}{info.count}.sha.${'$'}{info.shortCommit}" }
                        snapshotSuffix = "SNAPSHOT"
                    }
                }
            }
        """)
        initializeGitWithBranch(repository, testProjectDirectory, "0.0.1")
        Git(repository).commit().setMessage("").call()
        val result = gradleRunner

                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue("""Version: 1\.0\.0\+build\.2\.sha\.[0-9a-f]{7}-SNAPSHOT""".toRegex().containsMatchIn(result.output))
    }

    @Test
    fun `version formatter with prefix`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
          import io.wusa.Info
          import io.wusa.incrementer.MinorVersionIncrementer

          plugins {
              id("io.wusa.semver-git-plugin")
          }

          semver {
              tagPrefix = "prj_"
              branches {
                  branch {
                      regex = ".+"
                      incrementer = MinorVersionIncrementer
                      formatter = Transformer<Any, Info>{ info:Info -> "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                      snapshotSuffix = "SNAPSHOT"
                  }
              }
          }
      """)
        initializeGitWithoutBranchAnnotated(repository, testProjectDirectory, "prj_0.1.0")
        val result = gradleRunner

                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 0.1.0"))
    }

    @Test
    fun `version formatter with prefix and multiple tags not head`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
          import io.wusa.Info
          import io.wusa.incrementer.MinorVersionIncrementer

          plugins {
              id("io.wusa.semver-git-plugin")
          }

          semver {
              tagPrefix = "prj_"
              branches {
                  branch {
                      regex = ".+"
                      incrementer = MinorVersionIncrementer
                      formatter = Transformer<Any, Info>{ info:Info -> "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                      snapshotSuffix = "SNAPSHOT"
                  }
              }
          }
      """)
        initializeGitWithoutBranchAnnotated(repository, testProjectDirectory, "prj_0.1.0")
        val commit = Git(repository).commit().setMessage("another commit").call()
        Git(repository).tag().setName("foo-1.0.0").setObjectId(commit).call()

        val result = gradleRunner

                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 0.2.0-SNAPSHOT"))
    }

    @Test
    fun `version formatter with prefix and multiple tags from head`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
          import io.wusa.Info
          import io.wusa.incrementer.MinorVersionIncrementer

          plugins {
              id("io.wusa.semver-git-plugin")
          }

          semver {
              tagPrefix = "foo-"
              branches {
                  branch {
                      regex = ".+"
                      incrementer = MinorVersionIncrementer
                      formatter = Transformer<Any, Info>{ info:Info -> "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                      snapshotSuffix = "SNAPSHOT"
                  }
              }
          }
      """)
        initializeGitWithoutBranchAnnotated(repository, testProjectDirectory, "prj_0.1.0")
        val commit = Git(repository).commit().setMessage("another commit").call()
        Git(repository).tag().setName("foo-1.0.0").setObjectId(commit).call()

        val result = gradleRunner

                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 1.0.0"))
    }

    @Test
    fun `issue-47 increment minor by one with a lightweight tag`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
          import io.wusa.Info
          import io.wusa.TagType
          import io.wusa.incrementer.ConventionalCommitsVersionIncrementer

          plugins {
              id("io.wusa.semver-git-plugin")
          }

          semver {
              tagPrefix = ""
              tagType = TagType.LIGHTWEIGHT
              branches {
                  branch {
                      regex = ".+"
                      incrementer = ConventionalCommitsVersionIncrementer
                      formatter = Transformer<Any, Info>{ info:Info -> "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                  }
              }
          }
      """)
        initializeGitWithoutBranchLightweight(repository, testProjectDirectory, "2.0.42")
        Git(repository).commit().setMessage("feat: added semver plugin incrementer parameter").call()
        Git(repository).commit().setMessage("feat: added semver plugin incrementer parameter").call()
        Git(repository).commit().setMessage("feat: added semver plugin incrementer parameter").call()

        val result = gradleRunner

                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 2.1.0-SNAPSHOT"))
    }

    @Test
    fun `issue-47 increment minor by one with a annotated tag`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
          import io.wusa.Info
          import io.wusa.TagType
          import io.wusa.incrementer.ConventionalCommitsVersionIncrementer

          plugins {
              id("io.wusa.semver-git-plugin")
          }

          semver {
              tagPrefix = ""
              tagType = TagType.ANNOTATED
              branches {
                  branch {
                      regex = ".+"
                      incrementer = ConventionalCommitsVersionIncrementer
                      formatter = Transformer<Any, Info>{ info:Info -> "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                  }
              }
          }
      """)
        initializeGitWithoutBranchAnnotated(repository, testProjectDirectory, "2.0.42")
        val commit = Git(repository).commit().setMessage("feat: another commit").call()
        Git(repository).tag().setName("2.2.0").setObjectId(commit).setAnnotated(false).call()
        Git(repository).commit().setMessage("feat: added semver plugin incrementer parameter").call()
        Git(repository).commit().setMessage("feat: added semver plugin incrementer parameter").call()
        Git(repository).commit().setMessage("feat: added semver plugin incrementer parameter").call()

        val result = gradleRunner

                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 2.1.0-SNAPSHOT"))
    }

    @Test
    fun `issue-59 increment minor by one with a dirty working tree`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
          import io.wusa.Info
          import io.wusa.TagType
          import io.wusa.incrementer.ConventionalCommitsVersionIncrementer

          plugins {
              id("io.wusa.semver-git-plugin")
          }

          semver {
              tagPrefix = ""
              tagType = TagType.ANNOTATED
              branches {
                  branch {
                      regex = ".+"
                      incrementer = ConventionalCommitsVersionIncrementer
                      formatter = Transformer<Any, Info>{ info:Info -> "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                  }
              }
          }
      """)
        initializeGitWithoutBranchAnnotated(repository, testProjectDirectory, "2.0.42")
        Git(repository).commit().setMessage("feat: another commit").call()
        Git(repository).commit().setMessage("feat: added semver plugin incrementer parameter").call()
        Git(repository).commit().setMessage("feat: added semver plugin incrementer parameter").call()
        Git(repository).commit().setMessage("feat: added semver plugin incrementer parameter").call()
        val dirty = File(testProjectDirectory, "dirty.file")
        dirty.writeText("dirty")
        Git(repository).add().addFilepattern(".").call()

        val result = gradleRunner

                .withArguments("showInfo")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 2.1.0-dirty-SNAPSHOT"))
    }

    /*@Test
    fun `issue-59 dirty working tree with no commits`() {
        
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
          import io.wusa.Info
          import io.wusa.TagType
          import io.wusa.incrementer.ConventionalCommitsVersionIncrementer

          plugins {
              id("io.wusa.semver-git-plugin")
          }

          semver {
              tagPrefix = ""
              tagType = TagType.ANNOTATED
              branches {
                  branch {
                      regex = ".+"
                      incrementer = ConventionalCommitsVersionIncrementer
                      formatter = Transformer<Any, Info>{ info:Info -> "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                  }
              }
          }
      """)
        initializeGitWithoutBranchAnnotated(repository, testProjectDirectory, "2.0.42")
        val dirty = File(testProjectDirectory, "dirty.file")
        dirty.writeText("dirty")
        Git(repository).add().addFilepattern(".").call()

        val result = gradleRunner

                .withArguments("showInfo")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 2.0.43-dirty-SNAPSHOT"))
    }*/
}
