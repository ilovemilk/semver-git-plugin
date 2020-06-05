package io.wusa

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.UnexpectedBuildFailure
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
            import io.wusa.Info

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".+"
                        incrementer = "MINOR_INCREMENTER"
                        formatter = Transformer<Any, Info>{ info:Info -> "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
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
        Assertions.assertTrue(result.output.contains("Version: 0.1.0"))
    }

    @Test
    fun `version formatter for feature branches use specific`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            import io.wusa.Info

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = "feature/.*"
                        incrementer = "MINOR_INCREMENTER"
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+branch.${'$'}{info.branch.id}" }
                    }
                    branch {
                        regex = ".+"
                        incrementer = "MINOR_INCREMENTER"
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

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".+"
                        incrementer = "MINOR_INCREMENTER"
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                    }
                    branch {
                        regex = "feature/.*"
                        incrementer = "MINOR_INCREMENTER"
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

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".+"
                        incrementer = "MINOR_INCREMENTER"
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                    }
                    branch {
                        regex = "feature/.*"
                        incrementer = "MINOR_INCREMENTER"
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

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".+"
                        incrementer = "MINOR_INCREMENTER"
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                    }
                    branch {
                        regex = "feature/.*"
                        incrementer = "MINOR_INCREMENTER"
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

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".+"
                        incrementer = "MINOR_INCREMENTER"
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                    }
                    branch {
                        regex = "feature/.*"
                        incrementer = "MINOR_INCREMENTER"
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

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".+"
                        incrementer = "MINOR_INCREMENTER"
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                    }
                    branch {
                        regex = "feature/.*"
                        incrementer = "MINOR_INCREMENTER"
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

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".+"
                        incrementer = "MINOR_INCREMENTER"
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                    }
                    branch {
                        regex = "feature/.*"
                        incrementer = "MINOR_INCREMENTER"
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
    fun `version wrong incrementer`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            import io.wusa.Info

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = "THIS_IS_NO_INCREMENTer"
                        formatter = Transformer<Any, Info>{ "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}+build.${'$'}{info.count}.sha.${'$'}{info.shortCommit}" }
                    }
                }
            }
        """)
        val git = initializeGitWithBranch(testProjectDirectory, "0.0.1")
        git.commit().setMessage("").call()
        Assertions.assertThrows(UnexpectedBuildFailure::class.java) {
            gradleRunner
                    .withProjectDir(testProjectDirectory)
                    .withArguments("showVersion")
                    .withPluginClasspath()
                    .build()
        }
    }

    @Test
    fun `version no increment`() {
        val testProjectDirectory = createTempDir()
        val buildFile = File(testProjectDirectory, "build.gradle.kts")
        buildFile.writeText("""
            import io.wusa.Info

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = "NO_VERSION_INCREMENTER"
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

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = "PATCH_INCREMENTER"
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

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = "MINOR_INCREMENTER"
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

            plugins {
                id("io.wusa.semver-git-plugin")
            }

            semver {
                branches {
                    branch {
                        regex = ".*"
                        incrementer = "MAJOR_INCREMENTER"
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

          plugins {
              id("io.wusa.semver-git-plugin")
          }

          semver {
              tagPrefix = "prj_"
              branches {
                  branch {
                      regex = ".+"
                      incrementer = "MINOR_INCREMENTER"
                      formatter = Transformer<Any, Info>{ info:Info -> "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                  }
              }
          }
      """)
      initializeGitWithoutBranch(testProjectDirectory, "prj_0.1.0")
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

          plugins {
              id("io.wusa.semver-git-plugin")
          }

          semver {
              tagPrefix = "prj_"
              branches {
                  branch {
                      regex = ".+"
                      incrementer = "MINOR_INCREMENTER"
                      formatter = Transformer<Any, Info>{ info:Info -> "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                  }
              }
          }
      """)
    val git = initializeGitWithoutBranch(testProjectDirectory, "prj_0.1.0")
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

          plugins {
              id("io.wusa.semver-git-plugin")
          }

          semver {
              tagPrefix = "foo-"
              branches {
                  branch {
                      regex = ".+"
                      incrementer = "MINOR_INCREMENTER"
                      formatter = Transformer<Any, Info>{ info:Info -> "${'$'}{info.version.major}.${'$'}{info.version.minor}.${'$'}{info.version.patch}" }
                  }
              }
          }
      """)
    val git = initializeGitWithoutBranch(testProjectDirectory, "prj_0.1.0")
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
}
