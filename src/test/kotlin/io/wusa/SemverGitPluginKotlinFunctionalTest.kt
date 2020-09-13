package io.wusa

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.UnexpectedBuildFailure
import org.junit.jupiter.api.*
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SemverGitPluginKotlinFunctionalTest : FunctionalBaseTest() {

    private lateinit var gradleRunner: GradleRunner

    @BeforeEach
    fun setUp() {
        gradleRunner = GradleRunner.create()
    }

    @AfterEach
    fun tearDown() {
        gradleRunner.projectDir.deleteRecursively()
    }

    @Test
    fun `version formatter`() {
        val testProjectDirectory = createTempDir()
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
        Assertions.assertTrue(result.output.contains("Version: 0.1.0"))
    }

    @Test
    fun `custom version incrementer`() {
        val testProjectDirectory = createTempDir()
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
        val git = initializeGitWithBranch(testProjectDirectory, "0.0.1", "feature/test")
        git.commit().setMessage("").call()
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 1.1.2"))
    }

    @Test
    fun `version formatter for feature branches use specific`() {
        val testProjectDirectory = createTempDir()
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
                    }
                    branch {
                        regex = ".+"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
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
    fun `version formatter for feature branches with camelCase`() {
        val testProjectDirectory = createTempDir()
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
                    }
                    branch {
                        regex = "feature/.*"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+branch.${'$'}{info.branch.id}" }
                    }
                }
            }
        """)
        val git = initializeGitWithBranch(testProjectDirectory, "0.0.1", "feature/testAbc")
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
    fun `version formatter for feature branches with kebab-case`() {
        val testProjectDirectory = createTempDir()
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
                    }
                    branch {
                        regex = "feature/.*"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+branch.${'$'}{info.branch.id}" }
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
        Assertions.assertTrue(result.output.contains("Version: 0.1.0-SNAPSHOT"))
    }

    @Test
    fun `version formatter for feature branches with snake_case`() {
        val testProjectDirectory = createTempDir()
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
                    }
                    branch {
                        regex = "feature/.*"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+branch.${'$'}{info.branch.id}" }
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
        Assertions.assertTrue(result.output.contains("Version: 0.1.0-SNAPSHOT"))
    }

    @Test
    fun `version formatter for feature branches with PascalCase`() {
        val testProjectDirectory = createTempDir()
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
                    }
                    branch {
                        regex = "feature/.*"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+branch.${'$'}{info.branch.id}" }
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
        Assertions.assertTrue(result.output.contains("Version: 0.1.0-SNAPSHOT"))
    }

    @Test
    fun `version formatter for feature branches with UPPERCASE`() {
        val testProjectDirectory = createTempDir()
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
                    }
                    branch {
                        regex = "feature/.*"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+branch.${'$'}{info.branch.id}" }
                    }
                }
            }
        """)
        val git = initializeGitWithBranch(testProjectDirectory, "0.0.1", "feature/TESTABC10")
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
    fun `version formatter for feature branches use general`() {
        val testProjectDirectory = createTempDir()
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
                    }
                    branch {
                        regex = "feature/.*"
                        incrementer = MinorVersionIncrementer
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+branch.${'$'}{info.branch.id}" }
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

    @Test
    fun `version formatter with prefix`() {
        val testProjectDirectory = createTempDir()
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
                  }
              }
          }
      """)
        initializeGitWithoutBranchAnnotated(testProjectDirectory, "prj_0.1.0")
        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 0.1.0"))
    }

    @Test
    fun `version formatter with prefix and multiple tags not head`() {
        val testProjectDirectory = createTempDir()
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
                  }
              }
          }
      """)
        val git = initializeGitWithoutBranchAnnotated(testProjectDirectory, "prj_0.1.0")
        val commit = git.commit().setMessage("another commit").call()
        git.tag().setName("foo-1.0.0").setObjectId(commit).call()

        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 0.2.0-SNAPSHOT"))
    }

    @Test
    fun `version formatter with prefix and multiple tags from head`() {
        val testProjectDirectory = createTempDir()
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
                  }
              }
          }
      """)
        val git = initializeGitWithoutBranchAnnotated(testProjectDirectory, "prj_0.1.0")
        val commit = git.commit().setMessage("another commit").call()
        git.tag().setName("foo-1.0.0").setObjectId(commit).call()

        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 1.0.0"))
    }

    @Test
    fun `issue-47 increment minor by one with a lightweight tag`() {
        val testProjectDirectory = createTempDir()
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
        val git = initializeGitWithoutBranchLightweight(testProjectDirectory, "2.0.42")
        git.commit().setMessage("feat: added semver plugin incrementer parameter").call()
        git.commit().setMessage("feat: added semver plugin incrementer parameter").call()
        git.commit().setMessage("feat: added semver plugin incrementer parameter").call()

        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 2.1.0-SNAPSHOT"))
    }

    @Test
    fun `issue-47 increment minor by one with a annotated tag`() {
        val testProjectDirectory = createTempDir()
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
        val git = initializeGitWithoutBranchAnnotated(testProjectDirectory, "2.0.42")
        val commit = git.commit().setMessage("feat: another commit").call()
        git.tag().setName("2.2.0").setObjectId(commit).setAnnotated(false).call()
        git.commit().setMessage("feat: added semver plugin incrementer parameter").call()
        git.commit().setMessage("feat: added semver plugin incrementer parameter").call()
        git.commit().setMessage("feat: added semver plugin incrementer parameter").call()

        val result = gradleRunner
                .withProjectDir(testProjectDirectory)
                .withArguments("showVersion")
                .withPluginClasspath()
                .build()
        println(result.output)
        Assertions.assertTrue(result.output.contains("Version: 2.1.0-SNAPSHOT"))
    }
}
